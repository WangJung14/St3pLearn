package com.tommy.notification.listener;

import com.tommy.common.event.PasswordChangedEvent;
import com.tommy.notification.config.RabbitMQConfig;
import com.tommy.notification.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PasswordChangedListener {

    private final EmailService emailService;

    @RabbitListener(queues = RabbitMQConfig.PASSWORD_CHANGED_QUEUE)
    public void handlePasswordChangedEvent(PasswordChangedEvent event) {
        log.info("Received PasswordChangedEvent for email: {}", event.getEmail());
        emailService.sendPasswordChangedEmail(event.getEmail());
    }
}
