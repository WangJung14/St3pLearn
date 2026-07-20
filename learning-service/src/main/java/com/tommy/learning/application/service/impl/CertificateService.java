package com.tommy.learning.application.service.impl;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.tommy.common.exception.AppException;
import com.tommy.common.exception.ErrorCode;
import com.tommy.learning.application.dto.request.IssueCertificateRequest;
import com.tommy.learning.application.dto.response.CertificateResponse;
import com.tommy.learning.application.dto.response.VerifyCertificateResponse;
import com.tommy.learning.application.service.ICertificateService;
import com.tommy.learning.domain.entity.Certificate;
import com.tommy.learning.domain.entity.Enrollment;
import com.tommy.learning.domain.entity.Exam;
import com.tommy.learning.domain.entity.LearningProgress;
import com.tommy.learning.infrastructure.client.CatalogClient;
import com.tommy.learning.infrastructure.client.IdentityClient;
import com.tommy.learning.infrastructure.persistence.repository.CertificateRepository;
import com.tommy.learning.infrastructure.persistence.repository.EnrollmentRepository;
import com.tommy.learning.infrastructure.persistence.repository.ExamAttemptRepository;
import com.tommy.learning.infrastructure.persistence.repository.ExamRepository;
import com.tommy.learning.infrastructure.persistence.repository.LearningProgressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class CertificateService implements ICertificateService {

    private final CertificateRepository certificateRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final LearningProgressRepository learningProgressRepository;
    private final ExamRepository examRepository;
    private final ExamAttemptRepository examAttemptRepository;
    private final CatalogClient catalogClient;
    private final IdentityClient identityClient;
    private final TemplateEngine templateEngine;

    @Override
    public CertificateResponse issueCertificate(UUID instructorId, IssueCertificateRequest request) {
        // Validate Course Ownership
        try {
            var courseResponse = catalogClient.getCourse(request.getCourseId()).getBody();
            if (courseResponse == null || courseResponse.getData() == null) {
                throw new AppException(ErrorCode.COURSE_NOT_FOUND);
            }
            if (!instructorId.equals(courseResponse.getData().getInstructorId())) {
                throw new AppException(ErrorCode.COURSE_ACCESS_DENIED);
            }
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to fetch course details from CatalogService", e);
            throw new AppException(ErrorCode.COURSE_NOT_FOUND);
        }

        // Check if certificate already exists
        Optional<Certificate> existingCert = certificateRepository
                .findByStudentIdAndCourseIdAndIsDeletedFalse(request.getStudentId(), request.getCourseId());
        if (existingCert.isPresent()) {
            return mapToResponse(existingCert.get());
        }

        // Validate Graduation Requirements
        Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseId(request.getStudentId(), request.getCourseId())
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_ENROLLED));
        
        LearningProgress progress = learningProgressRepository.findById(enrollment.getId())
                .orElseThrow(() -> new AppException(ErrorCode.REQUIREMENTS_NOT_MET));
        
        if (progress.getProgressPercent() == null || progress.getProgressPercent().compareTo(new BigDecimal("80")) < 0) {
            throw new AppException(ErrorCode.REQUIREMENTS_NOT_MET);
        }

        List<Exam> exams = examRepository.findByCourseIdAndIsDeletedFalse(request.getCourseId());
        for (Exam exam : exams) {
            boolean hasPassed = examAttemptRepository.existsByStudentIdAndExamIdAndPassedTrue(request.getStudentId(), exam.getId());
            if (!hasPassed) {
                throw new AppException(ErrorCode.REQUIREMENTS_NOT_MET);
            }
        }

        // Create new Certificate
        String code = "CERT-" + LocalDateTime.now().getYear() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Certificate certificate = Certificate.builder()
                .studentId(request.getStudentId())
                .courseId(request.getCourseId())
                .certificateCode(code)
                .build();
        
        certificate = certificateRepository.save(certificate);
        return mapToResponse(certificate);
    }

    @Override
    public byte[] downloadCertificate(UUID studentId, UUID certificateId) {
        Certificate certificate = certificateRepository.findByIdAndStudentIdAndIsDeletedFalse(certificateId, studentId)
                .orElseThrow(() -> new AppException(ErrorCode.CERTIFICATE_NOT_FOUND));

        if (certificate.getIsRevoked()) {
            throw new AppException(ErrorCode.CERTIFICATE_REVOKED);
        }

        String studentName = "Unknown Student";
        try {
            var userResponse = identityClient.getUserById(certificate.getStudentId()).getBody();
            if (userResponse != null && userResponse.getData() != null) {
                studentName = userResponse.getData().getFullName();
            }
        } catch (Exception e) {
            log.warn("Failed to fetch student details", e);
        }

        String courseName = "Unknown Course";
        try {
            var courseResponse = catalogClient.getCourse(certificate.getCourseId()).getBody();
            if (courseResponse != null && courseResponse.getData() != null) {
                courseName = courseResponse.getData().getTitle();
            }
        } catch (Exception e) {
            log.warn("Failed to fetch course details", e);
        }

        Context context = new Context();
        context.setVariable("studentName", studentName);
        context.setVariable("courseName", courseName);
        context.setVariable("issueDate", certificate.getIssueDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        context.setVariable("certificateCode", certificate.getCertificateCode());

        String html = templateEngine.process("certificate-template", context);

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, null);
            builder.toStream(os);
            builder.run();
            return os.toByteArray();
        } catch (Exception e) {
            log.error("Failed to generate PDF", e);
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    @Override
    public VerifyCertificateResponse verifyCertificate(String certificateCode) {
        Optional<Certificate> optCert = certificateRepository.findByCertificateCodeAndIsDeletedFalse(certificateCode);
        
        if (optCert.isEmpty()) {
            return VerifyCertificateResponse.builder()
                    .isValid(false)
                    .message("Chứng chỉ không tồn tại")
                    .build();
        }
        
        Certificate certificate = optCert.get();
        if (certificate.getIsRevoked()) {
            return VerifyCertificateResponse.builder()
                    .isValid(false)
                    .message("Chứng chỉ đã bị thu hồi")
                    .build();
        }

        String studentName = "Unknown Student";
        try {
            var userResponse = identityClient.getUserById(certificate.getStudentId()).getBody();
            if (userResponse != null && userResponse.getData() != null) {
                studentName = userResponse.getData().getFullName();
            }
        } catch (Exception e) {
            log.warn("Failed to fetch student details", e);
        }

        String courseName = "Unknown Course";
        try {
            var courseResponse = catalogClient.getCourse(certificate.getCourseId()).getBody();
            if (courseResponse != null && courseResponse.getData() != null) {
                courseName = courseResponse.getData().getTitle();
            }
        } catch (Exception e) {
            log.warn("Failed to fetch course details", e);
        }

        return VerifyCertificateResponse.builder()
                .isValid(true)
                .message("Chứng chỉ hợp lệ")
                .studentId(certificate.getStudentId())
                .studentName(studentName)
                .courseId(certificate.getCourseId())
                .courseName(courseName)
                .issueDate(certificate.getIssueDate())
                .build();
    }

    private CertificateResponse mapToResponse(Certificate certificate) {
        return CertificateResponse.builder()
                .id(certificate.getId())
                .studentId(certificate.getStudentId())
                .courseId(certificate.getCourseId())
                .certificateCode(certificate.getCertificateCode())
                .issueDate(certificate.getIssueDate())
                .build();
    }
}
