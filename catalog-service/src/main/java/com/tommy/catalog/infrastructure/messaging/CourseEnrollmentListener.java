package com.tommy.catalog.infrastructure.messaging;

import com.tommy.catalog.domain.entity.StudentEnrolledCourse;
import com.tommy.catalog.infrastructure.persistence.repository.StudentEnrolledCourseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class CourseEnrollmentListener {

    private final StudentEnrolledCourseRepository enrolledCourseRepository;

    // TODO: Bỏ comment khi tích hợp Spring AMQP (RabbitMQ)
    // @org.springframework.amqp.rabbit.annotation.RabbitListener(queues = "course.enrollment.catalog.queue")
    public void handleCourseEnrolledEvent(CourseEnrolledEvent event) {
        log.info("Received enrollment event for Student {} and Course {}", event.studentId(), event.courseId());

        boolean exists = enrolledCourseRepository.existsByStudentIdAndCourseId(event.studentId(), event.courseId());

        if (!exists) {
            StudentEnrolledCourse record = StudentEnrolledCourse.builder()
                    .studentId(event.studentId())
                    .courseId(event.courseId())
                    .build();
            enrolledCourseRepository.save(record);
            log.info("Successfully synced enrollment data to Catalog DB");
        }
    }

    // DTO hứng data từ RabbitMQ
    public record CourseEnrolledEvent(UUID studentId, UUID courseId) {}
}