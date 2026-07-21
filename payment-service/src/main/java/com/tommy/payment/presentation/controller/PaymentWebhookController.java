package com.tommy.payment.presentation.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tommy.payment.application.service.impl.VNPayService;
import com.tommy.payment.domain.entity.PaymentGatewayLog;
import com.tommy.payment.domain.entity.PaymentOrder;
import com.tommy.payment.domain.entity.PaymentOutboxEvent;
import com.tommy.payment.domain.entity.PaymentTransaction;
import com.tommy.payment.domain.enums.OrderStatus;
import com.tommy.payment.domain.enums.OutboxStatus;
import com.tommy.payment.domain.enums.TransactionStatus;
import com.tommy.payment.infrastructure.persistence.repository.PaymentGatewayLogRepository;
import com.tommy.payment.infrastructure.persistence.repository.PaymentOrderRepository;
import com.tommy.payment.infrastructure.persistence.repository.PaymentOutboxEventRepository;
import com.tommy.payment.infrastructure.persistence.repository.PaymentTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/payment/vnpay")
@RequiredArgsConstructor
@Slf4j
public class PaymentWebhookController {

    private final VNPayService vnPayService;
    private final PaymentTransactionRepository transactionRepository;
    private final PaymentOrderRepository orderRepository;
    private final PaymentOutboxEventRepository outboxEventRepository;
    private final PaymentGatewayLogRepository gatewayLogRepository;
    private final ObjectMapper objectMapper;

    @Value("${app.frontend-url:http://localhost:3000}")
    private String frontendUrl;

    @GetMapping("/callback")
    @Transactional
    public ResponseEntity<Void> vnpayCallback(@RequestParam Map<String, String> queryParams) {
        log.info("Received VNPay Callback: {}", queryParams);

        // Save log
        try {
            PaymentGatewayLog gatewayLog = PaymentGatewayLog.builder()
                    .gateway("VNPAY")
                    .responsePayload(objectMapper.writeValueAsString(queryParams))
                    .build();
            gatewayLogRepository.save(gatewayLog);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse payload", e);
        }

        // Verify signature
        if (!vnPayService.verifySignature(queryParams)) {
            log.warn("VNPay Signature mismatch");
            return redirectToHistory("failed", null, "Chữ ký VNPay không hợp lệ");
        }

        String txnRef = queryParams.get("vnp_TxnRef");
        String vnpResponseCode = queryParams.get("vnp_ResponseCode");

        Optional<PaymentTransaction> transactionOpt = transactionRepository.findByRequestIdempotencyKey(txnRef);
        if (transactionOpt.isEmpty()) {
            log.warn("Transaction not found for ref: {}", txnRef);
            return redirectToHistory("failed", null, "Không tìm thấy giao dịch");
        }

        PaymentTransaction transaction = transactionOpt.get();

        // Idempotency check
        if (transaction.getStatus() != TransactionStatus.PENDING) {
            log.info("Transaction {} already processed", txnRef);
            PaymentOrder processedOrder = orderRepository.findById(transaction.getPaymentOrderId()).orElse(null);
            return redirectToHistory(
                    transaction.getStatus() == TransactionStatus.SUCCESS ? "success" : "failed",
                    processedOrder != null ? processedOrder.getOrderNumber() : null,
                    null
            );
        }

        PaymentOrder order = orderRepository.findById(transaction.getPaymentOrderId()).orElseThrow();
        transaction.setGatewayTransactionId(queryParams.get("vnp_TransactionNo"));

        if ("00".equals(vnpResponseCode)) {
            // Success
            transaction.setStatus(TransactionStatus.SUCCESS);
            transaction.setCompletedAt(LocalDateTime.now());
            
            order.setStatus(OrderStatus.PAID);
            
            // Create outbox event
            String payload = String.format("{\"studentId\":\"%s\", \"courseId\":\"%s\", \"orderId\":\"%s\", \"amount\":%s}", 
                order.getStudentId(), order.getCourseId(), order.getId(), order.getFinalAmount());
                
            PaymentOutboxEvent event = PaymentOutboxEvent.builder()
                    .aggregateId(order.getId().toString())
                    .eventType("PaymentSucceeded")
                    .payload(payload)
                    .status(OutboxStatus.PENDING)
                    .build();
            outboxEventRepository.save(event);
            
        } else {
            // Failed
            transaction.setStatus(TransactionStatus.FAILED);
            transaction.setCompletedAt(LocalDateTime.now());
            order.setStatus(OrderStatus.FAILED);
        }

        transactionRepository.save(transaction);
        orderRepository.save(order);

        return redirectToHistory(
                transaction.getStatus() == TransactionStatus.SUCCESS ? "success" : "failed",
                order.getOrderNumber(),
                null
        );
    }

    private ResponseEntity<Void> redirectToHistory(String paymentStatus, String orderNumber, String message) {
        StringBuilder target = new StringBuilder(frontendUrl)
                .append("/student/payments?payment=")
                .append(URLEncoder.encode(paymentStatus, StandardCharsets.UTF_8));
        if (orderNumber != null) {
            target.append("&orderNumber=")
                    .append(URLEncoder.encode(orderNumber, StandardCharsets.UTF_8));
        }
        if (message != null) {
            target.append("&message=")
                    .append(URLEncoder.encode(message, StandardCharsets.UTF_8));
        }
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(target.toString())).build();
    }
}
