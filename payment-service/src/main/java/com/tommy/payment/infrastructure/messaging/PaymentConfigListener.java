package com.tommy.payment.infrastructure.messaging;

import com.tommy.common.event.SystemConfigUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentConfigListener {

    @RabbitListener(queues = "payment.config.events.queue")
    public void handleConfigUpdatedEvent(SystemConfigUpdatedEvent event) {
        log.info("Payment service received config update: {} = {}", event.getConfigKey(), event.getConfigValue());
        
        // In a real application, you might update a local cache or database table
        if ("MOMO_ENABLED".equals(event.getConfigKey())) {
            boolean isMomoEnabled = Boolean.parseBoolean(event.getConfigValue());
            log.info("Momo Gateway Enabled: {}", isMomoEnabled);
        } else if ("TEACHER_COMMISSION_RATE".equals(event.getConfigKey())) {
            double commissionRate = Double.parseDouble(event.getConfigValue());
            log.info("Teacher Commission Rate updated to: {}", commissionRate);
        }
    }
}
