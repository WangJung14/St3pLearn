package com.tommy.learning.presentation.controller;

import com.tommy.common.response.ApiResponse;
import com.tommy.learning.application.dto.request.IssueCertificateRequest;
import com.tommy.learning.application.dto.response.CertificateResponse;
import com.tommy.learning.application.dto.response.VerifyCertificateResponse;
import com.tommy.learning.application.service.ICertificateService;
import com.tommy.common.security.RequireRole;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/learning/certificates")
@RequiredArgsConstructor
@Slf4j
public class CertificateController {

    private final ICertificateService certificateService;

    @PostMapping("/issue")
    @RequireRole({"INSTRUCTOR", "TEACHER"})
    public ResponseEntity<ApiResponse<CertificateResponse>> issueCertificate(
            @RequestHeader("X-User-Id") UUID instructorId,
            @Valid @RequestBody IssueCertificateRequest request) {

        log.info("Instructor {} issuing certificate for student {} in course {}", instructorId, request.getStudentId(), request.getCourseId());
        CertificateResponse response = certificateService.issueCertificate(instructorId, request);
        return ResponseEntity.ok(ApiResponse.success(200, "Certificate issued successfully", response));
    }

    @GetMapping("/{certificateId}/download")
    @RequireRole({"STUDENT"})
    public ResponseEntity<byte[]> downloadCertificate(
            @RequestHeader("X-User-Id") UUID studentId,
            @PathVariable UUID certificateId) {

        log.info("Student {} downloading certificate {}", studentId, certificateId);
        byte[] pdfBytes = certificateService.downloadCertificate(studentId, certificateId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "certificate.pdf");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    @GetMapping("/verify/{certificateCode}")
    public ResponseEntity<ApiResponse<VerifyCertificateResponse>> verifyCertificate(
            @PathVariable String certificateCode) {

        log.info("Public verifying certificate code {}", certificateCode);
        VerifyCertificateResponse response = certificateService.verifyCertificate(certificateCode);
        return ResponseEntity.ok(ApiResponse.success(200, "Certificate verification completed", response));
    }
}
