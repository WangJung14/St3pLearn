package com.tommy.admin.infrastructure.messaging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tommy.admin.domain.entity.CourseReplica;
import com.tommy.admin.domain.entity.DailyRevenue;
import com.tommy.admin.domain.entity.UserReplica;
import com.tommy.admin.infrastructure.persistence.repository.CourseReplicaRepository;
import com.tommy.admin.infrastructure.persistence.repository.DailyRevenueRepository;
import com.tommy.admin.infrastructure.persistence.repository.UserReplicaRepository;
import com.tommy.common.event.CourseCompletedEvent;
import com.tommy.common.event.UserLoggedInEvent;
import com.tommy.common.event.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import com.tommy.admin.application.service.ModerationService;
import com.tommy.common.event.ReportSubmittedEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminEventListener {

    private final UserReplicaRepository userReplicaRepository;
    private final CourseReplicaRepository courseReplicaRepository;
    private final DailyRevenueRepository dailyRevenueRepository;
    private final ModerationService moderationService;
    private final com.tommy.admin.application.service.AuditLogService auditLogService;
    private final ObjectMapper objectMapper;

    // --- User Events ---
    @RabbitListener(queues = RabbitMQConfig.ADMIN_USER_EVENTS_QUEUE)
    @Transactional
    public void handleUserEvents(Object event) {
        log.info("Received User Event: {}", event);
        if (event instanceof UserRegisteredEvent) {
            UserRegisteredEvent registeredEvent = (UserRegisteredEvent) event;
            UserReplica user = UserReplica.builder()
                    .id(registeredEvent.getUserId())
                    .email(registeredEvent.getEmail())
                    .fullName(registeredEvent.getFullName())
                    .role(registeredEvent.getRole())
                    .createdAt(registeredEvent.getRegisteredAt())
                    .build();
            userReplicaRepository.save(user);
            log.info("Saved UserReplica for ID: {}", user.getId());
        } else if (event instanceof UserLoggedInEvent) {
            UserLoggedInEvent loggedInEvent = (UserLoggedInEvent) event;
            userReplicaRepository.findById(loggedInEvent.getUserId()).ifPresent(user -> {
                user.setLastLogin(loggedInEvent.getLoginAt());
                userReplicaRepository.save(user);
                log.info("Updated last login for UserReplica: {}", user.getId());
            });
        }
    }

    // --- Course Events ---
    @RabbitListener(queues = RabbitMQConfig.ADMIN_COURSE_EVENTS_QUEUE)
    @Transactional
    public void handleCourseEvents(Object event) {
        log.info("Received Course Event: {}", event);
        if (event instanceof CourseCompletedEvent) {
            CourseCompletedEvent completedEvent = (CourseCompletedEvent) event;
            courseReplicaRepository.findById(completedEvent.getCourseId()).ifPresent(course -> {
                course.setCompletionCount(course.getCompletionCount() + 1);
                courseReplicaRepository.save(course);
            });
        }
    }
    
    @RabbitListener(bindings = @org.springframework.amqp.rabbit.annotation.QueueBinding(
                            value = @org.springframework.amqp.rabbit.annotation.Queue("admin.course.enrolled.queue"),
                            exchange = @org.springframework.amqp.rabbit.annotation.Exchange(value = RabbitMQConfig.COURSE_EXCHANGE, type = "topic"),
                            key = "course.enrolled.key"
                    ))
    @Transactional
    public void handleCourseEnrolledEvent(String payload) {
        // Since StudentEnrolledEvent is not in common-library, we parse JSON directly
        try {
            JsonNode jsonNode = objectMapper.readTree(payload);
            if (jsonNode.has("courseId")) {
                UUID courseId = UUID.fromString(jsonNode.get("courseId").asText());
                courseReplicaRepository.findById(courseId).ifPresent(course -> {
                    course.setEnrollmentCount(course.getEnrollmentCount() + 1);
                    courseReplicaRepository.save(course);
                    log.info("Incremented enrollment count for course {}", courseId);
                });
            }
        } catch (Exception e) {
            log.error("Failed to process course enrolled event: {}", payload, e);
        }
    }

    // --- Payment Events ---
    @RabbitListener(bindings = @org.springframework.amqp.rabbit.annotation.QueueBinding(
            value = @org.springframework.amqp.rabbit.annotation.Queue("admin.payment.completed.queue"),
            exchange = @org.springframework.amqp.rabbit.annotation.Exchange(value = "payment.exchange", type = "direct"),
            key = "payment.order.completed"
    ))
    @Transactional
    public void handlePaymentCompletedEvent(String payload) {
        log.info("Received PaymentCompletedEvent string in Admin: {}", payload);
        try {
            JsonNode jsonNode = objectMapper.readTree(payload);
            UUID courseId = UUID.fromString(jsonNode.get("courseId").asText());
            BigDecimal amount = new BigDecimal(jsonNode.get("amount").asText());

            // 1. Update Daily Revenue
            LocalDate today = LocalDate.now();
            Optional<DailyRevenue> revenueOpt = dailyRevenueRepository.findByDate(today);
            DailyRevenue dailyRevenue;
            if (revenueOpt.isPresent()) {
                dailyRevenue = revenueOpt.get();
                dailyRevenue.setTotalRevenue(dailyRevenue.getTotalRevenue().add(amount));
            } else {
                dailyRevenue = DailyRevenue.builder()
                        .date(today)
                        .totalRevenue(amount)
                        .build();
            }
            dailyRevenueRepository.save(dailyRevenue);

            // 2. Update Course Revenue
            courseReplicaRepository.findById(courseId).ifPresent(course -> {
                course.setTotalRevenue(course.getTotalRevenue().add(amount));
                courseReplicaRepository.save(course);
            });

            log.info("Updated DailyRevenue & CourseRevenue with amount: {}", amount);
        } catch (Exception e) {
            log.error("Failed to parse PaymentCompletedEvent for admin", e);
        }
    }

    // --- Report Events ---
    @RabbitListener(queues = RabbitMQConfig.ADMIN_REPORT_EVENTS_QUEUE)
    @Transactional
    public void handleReportEvents(Object event) {
        log.info("Received Report Event: {}", event);
        if (event instanceof ReportSubmittedEvent) {
            ReportSubmittedEvent reportEvent = (ReportSubmittedEvent) event;
            moderationService.openCaseManual(
                    reportEvent.getReportId(),
                    reportEvent.getReporterId(),
                    reportEvent.getTargetType(),
                    reportEvent.getTargetId(),
                    reportEvent.getReason(),
                    reportEvent.getDescription()
            );
        }
    }
    // --- System Audit Events ---
    @RabbitListener(queues = RabbitMQConfig.ADMIN_AUDIT_EVENTS_QUEUE)
    @Transactional
    public void handleSystemAuditEvents(Object event) {
        log.info("Received System Audit Event: {}", event);
        if (event instanceof com.tommy.common.event.SystemAuditEvent) {
            com.tommy.common.event.SystemAuditEvent auditEvent = (com.tommy.common.event.SystemAuditEvent) event;
            com.tommy.admin.domain.entity.AuditLog logEntry = com.tommy.admin.domain.entity.AuditLog.builder()
                    .actorId(auditEvent.getActorId())
                    .action(auditEvent.getAction())
                    .targetType(auditEvent.getTargetType())
                    .targetId(auditEvent.getTargetId())
                    .oldValue(auditEvent.getOldValue())
                    .newValue(auditEvent.getNewValue())
                    .description(auditEvent.getDescription())
                    .timestamp(auditEvent.getTimestamp() != null ? auditEvent.getTimestamp() : java.time.LocalDateTime.now())
                    .build();
            
            auditLogService.saveLog(logEntry);
            log.info("Saved SystemAuditEvent to AuditLog");
        }
    }
}
