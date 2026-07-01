package com.tommy.learning.application.service.impl;

import com.tommy.learning.application.service.IEnrollmentService;
import com.tommy.learning.application.dto.request.EnrollCourseRequest;
import com.tommy.learning.application.dto.response.EnrollmentResponse;
import com.tommy.learning.domain.entity.Enrollment;
import com.tommy.learning.domain.entity.CourseReplica;
import com.tommy.learning.infrastructure.messaging.event.StudentEnrolledEvent;
import com.tommy.learning.infrastructure.persistence.repository.CourseReplicaRepository;
import com.tommy.learning.infrastructure.persistence.repository.EnrollmentRepository;
import com.tommy.common.exception.AppException;
import com.tommy.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
@Service
@RequiredArgsConstructor
@Slf4j
public class EnrollmentService implements IEnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final CourseReplicaRepository courseReplicaRepository;
    private final RabbitTemplate rabbitTemplate;

    // Exchange and Routing Key Hard code
    private static final String EXCHANGE_NAME = "course.events.exchange";
    private static final String ROUTING_KEY = "course.enrolled.key";


    /*
    * EnrollCourse
    * */
    @Override
    @Transactional
    public EnrollmentResponse enrollCourse(UUID studentId, EnrollCourseRequest request) {
        log.info("Student {} is trying to enroll in course {}", studentId, request.getCourseId());

        CourseReplica courseReplica = courseReplicaRepository.findById(request.getCourseId())
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        if (!"PUBLISHED".equals(courseReplica.getStatus())) {
            throw new AppException(ErrorCode.COURSE_NOT_PUBLISHED);
        }

        // 1. Check duplicate
        if (enrollmentRepository.existsByStudentIdAndCourseId(studentId, request.getCourseId())) {
            log.warn("Student {} is already enrolled in course {}", studentId, request.getCourseId());
            throw new AppException(ErrorCode.ENROLLMENT_EXISTS);
        }

        // Create new record
        Enrollment enrollment = Enrollment.builder()
                .studentId(studentId)
                .courseId(request.getCourseId())
                .build();

        enrollmentRepository.save(enrollment);

        // 3. Push the event via RabbitMQ
        StudentEnrolledEvent event = new StudentEnrolledEvent(studentId, request.getCourseId());
        try {
            rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, event);
            log.info("Successfully published StudentEnrolledEvent for student {} and course {}", studentId, request.getCourseId());
        } catch (Exception e) {
            log.error("Failed to publish RabbitMQ event: {}", e.getMessage());
        }

        return EnrollmentResponse.builder()
                .id(enrollment.getId())
                .studentId(enrollment.getStudentId())
                .courseId(enrollment.getCourseId())
                .status(enrollment.getStatus())
                .progressPercent(enrollment.getProgressPercent())
                .enrolledAt(enrollment.getEnrolledAt())
                .build();
    }

    /*
    * View all course enrolled
    * */
    @Override
    @Transactional(readOnly = true)
    public Page<EnrollmentResponse> getMyEnrolledCourses(UUID studentId, int page, int size) {
        log.info("Fetching enrolled courses for student: {}", studentId);
        Pageable pageable = PageRequest.of(page, size, Sort.by("enrolledAt").descending());

        Page<Enrollment> enrollments = enrollmentRepository.findByStudentId(studentId, pageable);

        return enrollments.map(enrollment -> EnrollmentResponse.builder()
                .id(enrollment.getId())
                .studentId(enrollment.getStudentId())
                .courseId(enrollment.getCourseId())
                .status(enrollment.getStatus())
                .progressPercent(enrollment.getProgressPercent())
                .enrolledAt(enrollment.getEnrolledAt())
                .build());
    }
}
