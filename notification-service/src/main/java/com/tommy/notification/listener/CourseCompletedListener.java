package com.tommy.notification.listener;

import com.tommy.common.event.CourseCompletedEvent;
import com.tommy.notification.config.RabbitMQConfig;
import com.tommy.notification.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CourseCompletedListener {

    private final EmailService emailService;

    @RabbitListener(queues = RabbitMQConfig.COURSE_COMPLETED_QUEUE)
    public void handleCourseCompletedEvent(CourseCompletedEvent event) {
        log.info("Received CourseCompletedEvent for student: {}, course: {}", event.getStudentId(), event.getCourseId());
        
        if (event.getStudentEmail() != null && event.getCourseTitle() != null) {
            emailService.sendCourseCompletedEmail(
                    event.getStudentEmail(),
                    event.getStudentName(),
                    event.getCourseTitle()
            );
            log.info("Course completion email sent successfully to {}", event.getStudentEmail());
        } else {
            log.warn("Cannot send email: missing studentEmail or courseTitle in CourseCompletedEvent");
        }
    }
}
