package com.tommy.notification.listener;

import com.tommy.common.event.ForgotPasswordEvent;
import com.tommy.notification.config.RabbitMQConfig;
import com.tommy.notification.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ForgotPasswordListener {

    private final EmailService emailService;

    @RabbitListener(queues = RabbitMQConfig.FORGOT_PASSWORD_QUEUE)
    public void handleForgotPasswordEvent(ForgotPasswordEvent event) {
        log.info("Received ForgotPasswordEvent for email: {}", event.getEmail());
        emailService.sendForgotPasswordEmail(event.getEmail(), event.getOtp());
    }
}
