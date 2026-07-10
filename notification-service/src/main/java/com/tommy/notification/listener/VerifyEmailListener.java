package com.tommy.notification.listener;

import com.tommy.common.event.VerifyEmailEvent;
import com.tommy.notification.config.RabbitMQConfig;
import com.tommy.notification.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class VerifyEmailListener {

    private final EmailService emailService;

    @RabbitListener(queues = RabbitMQConfig.VERIFY_EMAIL_QUEUE)
    public void handleVerifyEmailEvent(VerifyEmailEvent event) {
        log.info("Received VerifyEmailEvent for email: {}", event.getEmail());
        emailService.sendVerificationEmail(event.getEmail(), event.getOtp());
    }
}
