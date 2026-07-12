package com.tommy.notification.listener;

import com.tommy.common.event.CourseViolationEvent;
import com.tommy.notification.config.RabbitMQConfig;
import com.tommy.notification.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CourseViolationListener {

    private final EmailService emailService;

    @RabbitListener(queues = RabbitMQConfig.COURSE_VIOLATION_QUEUE)
    public void handleCourseViolationEvent(CourseViolationEvent event) {
        log.info("Received CourseViolationEvent for course: {}, reason: {}", event.getCourseTitle(), event.getReason());
        
        emailService.sendCourseViolationEmail(
                event.getInstructorEmail(),
                event.getCourseTitle(),
                event.getReason()
        );
        
        log.info("Course violation email sent successfully to {}", event.getInstructorEmail());
    }
}
