package com.tommy.payment.application.service.impl;

import com.tommy.payment.domain.entity.PaymentOutboxEvent;
import com.tommy.payment.domain.enums.OutboxStatus;
import com.tommy.payment.infrastructure.messaging.RabbitMQConfig;
import com.tommy.payment.infrastructure.persistence.repository.PaymentOutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxScheduler {

    private final PaymentOutboxEventRepository outboxEventRepository;
    private final RabbitTemplate rabbitTemplate;

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void processOutboxEvents() {
        List<PaymentOutboxEvent> pendingEvents = outboxEventRepository.findByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING);

        for (PaymentOutboxEvent event : pendingEvents) {
            try {
                // Send to RabbitMQ
                rabbitTemplate.convertAndSend(
                        RabbitMQConfig.PAYMENT_EXCHANGE,
                        RabbitMQConfig.PAYMENT_ORDER_COMPLETED_ROUTING_KEY,
                        event.getPayload()
                );
                event.setStatus(OutboxStatus.PROCESSED);
                log.info("Successfully processed outbox event id: {}", event.getId());
            } catch (Exception e) {
                log.error("Failed to process outbox event id: {}", event.getId(), e);
            }
        }
        outboxEventRepository.saveAll(pendingEvents);
    }
}
