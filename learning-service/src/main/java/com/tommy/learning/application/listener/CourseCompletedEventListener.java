package com.tommy.learning.application.listener;

import com.tommy.common.event.CourseCompletedEvent;
import com.tommy.common.response.ApiResponse;
import com.tommy.learning.infrastructure.messaging.RabbitMQConfig;
import com.tommy.learning.domain.entity.Enrollment;
import com.tommy.learning.domain.enums.EnrollmentStatus;
import com.tommy.learning.infrastructure.client.CatalogClient;
import com.tommy.learning.infrastructure.client.IdentityClient;
import com.tommy.learning.infrastructure.client.dto.CatalogCourseResponse;
import com.tommy.learning.infrastructure.client.dto.IdentityUserDetailResponse;
import com.tommy.learning.infrastructure.persistence.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class CourseCompletedEventListener {

    private final EnrollmentRepository enrollmentRepository;
    private final IdentityClient identityClient;
    private final CatalogClient catalogClient;
    private final RabbitTemplate rabbitTemplate;

    @EventListener
    @Transactional
    public void handleCourseCompletedEvent(CourseCompletedEvent event) {
        log.info("Handling CourseCompletedEvent for student {}, course {}", event.getStudentId(), event.getCourseId());

        // 1. Double check enrollment
        Optional<Enrollment> enrollmentOpt = enrollmentRepository.findById(event.getEnrollmentId());
        if (enrollmentOpt.isEmpty()) return;

        Enrollment enrollment = enrollmentOpt.get();
        if (enrollment.getStatus() == EnrollmentStatus.COMPLETED) {
            log.info("Enrollment {} is already COMPLETED. Ignoring.", enrollment.getId());
            return;
        }

        // 2. Change status to COMPLETED
        enrollment.setStatus(EnrollmentStatus.COMPLETED);
        enrollment.setCompletedAt(LocalDateTime.now());
        enrollmentRepository.save(enrollment);

        // 3. Fetch missing details via Feign for the event
        try {
            ResponseEntity<ApiResponse<IdentityUserDetailResponse>> userRes = identityClient.getUserById(event.getStudentId());
            if (userRes.getBody() != null && userRes.getBody().getData() != null) {
                event.setStudentEmail(userRes.getBody().getData().getEmail());
                event.setStudentName(userRes.getBody().getData().getFullName());
            }

            ResponseEntity<ApiResponse<CatalogCourseResponse>> courseRes = catalogClient.getCourse(event.getCourseId());
            if (courseRes.getBody() != null && courseRes.getBody().getData() != null) {
                event.setCourseTitle(courseRes.getBody().getData().getTitle());
            }
        } catch (Exception e) {
            log.error("Failed to fetch user/course info for CourseCompletedEvent: {}", e.getMessage());
            // Proceed anyway, the event might be missing some info but it's better than failing the transaction
        }

        // 4. Publish to RabbitMQ Fanout Exchange
        rabbitTemplate.convertAndSend(RabbitMQConfig.COURSE_COMPLETED_EXCHANGE, "", event);
        log.info("Published rich CourseCompletedEvent to RabbitMQ for student {}", event.getStudentId());
    }
}
