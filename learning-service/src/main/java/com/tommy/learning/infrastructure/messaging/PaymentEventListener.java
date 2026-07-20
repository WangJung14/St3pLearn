package com.tommy.learning.infrastructure.messaging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

import com.tommy.learning.application.service.impl.EnrollmentService;
import com.tommy.learning.application.dto.request.EnrollCourseRequest;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventListener {

    private final ObjectMapper objectMapper;
    private final EnrollmentService enrollmentService;

    @RabbitListener(queues = "payment.order.completed.queue")
    @Transactional
    public void handlePaymentCompletedEvent(String message) {
        log.info("Received PaymentCompletedEvent: {}", message);
        try {
            JsonNode payload = objectMapper.readTree(message);
            UUID studentId = UUID.fromString(payload.get("studentId").asText());
            UUID courseId = UUID.fromString(payload.get("courseId").asText());

            // Enroll student logic
            EnrollCourseRequest request = new EnrollCourseRequest();
            request.setCourseId(courseId);
            enrollmentService.enrollCourse(studentId, request);
            log.info("Successfully enrolled student {} into course {}", studentId, courseId);
            
        } catch (Exception e) {
            log.error("Error processing PaymentCompletedEvent", e);
            // In a real system, you might want to use DLQ (Dead Letter Queue) or nack the message
            // throw new AmqpRejectAndDontRequeueException(e);
        }
    }
}
