package com.tommy.notification.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    public void sendForgotPasswordEmail(String to, String otp) {
        try {
            Context context = new Context();
            context.setVariable("email", to);
            context.setVariable("otp", otp);

            String process = templateEngine.process("forgot-password", context);
            
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject("St3pLearn - Mã khôi phục mật khẩu");
            helper.setText(process, true);
            
            javaMailSender.send(mimeMessage);
            log.info("Sent forgot password email to {}", to);
        } catch (MessagingException e) {
            log.error("Error while sending email", e);
        }
    }

    public void sendVerificationEmail(String to, String otp) {
        try {
            Context context = new Context();
            context.setVariable("email", to);
            context.setVariable("otp", otp);

            String process = templateEngine.process("verify-email", context);
            
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject("St3pLearn - Mã xác nhận tài khoản");
            helper.setText(process, true);
            
            javaMailSender.send(mimeMessage);
            log.info("Sent verification email to {}", to);
        } catch (MessagingException e) {
            log.error("Error while sending email", e);
        }
    }

    public void sendPasswordChangedEmail(String to) {
        try {
            Context context = new Context();
            context.setVariable("email", to);

            String process = templateEngine.process("password-changed", context);
            
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject("St3pLearn - Cảnh báo bảo mật: Thay đổi mật khẩu");
            helper.setText(process, true);
            
            javaMailSender.send(mimeMessage);
            log.info("Sent password changed email to {}", to);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void sendCourseViolationEmail(String to, String courseTitle, String reason) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setTo(to);
            helper.setSubject("St3pLearn - Cảnh báo vi phạm khóa học: " + courseTitle);
            
            Context context = new Context();
            context.setVariable("courseTitle", courseTitle);
            context.setVariable("reason", reason);
            
            String html = templateEngine.process("course-violation", context);
            helper.setText(html, true);
            
            javaMailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
