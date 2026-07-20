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
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
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

    @GetMapping("/callback")
    @Transactional
    public ResponseEntity<String> vnpayCallback(@RequestParam Map<String, String> queryParams) {
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
            return ResponseEntity.badRequest().body("Signature mismatch");
        }

        String txnRef = queryParams.get("vnp_TxnRef");
        String vnpResponseCode = queryParams.get("vnp_ResponseCode");

        Optional<PaymentTransaction> transactionOpt = transactionRepository.findByRequestIdempotencyKey(txnRef);
        if (transactionOpt.isEmpty()) {
            log.warn("Transaction not found for ref: {}", txnRef);
            return ResponseEntity.badRequest().body("Transaction not found");
        }

        PaymentTransaction transaction = transactionOpt.get();

        // Idempotency check
        if (transaction.getStatus() != TransactionStatus.PENDING) {
            log.info("Transaction {} already processed", txnRef);
            return ResponseEntity.ok("Already processed");
        }

        PaymentOrder order = orderRepository.findById(transaction.getPaymentOrderId()).orElseThrow();

        if ("00".equals(vnpResponseCode)) {
            // Success
            transaction.setStatus(TransactionStatus.SUCCESS);
            transaction.setCompletedAt(LocalDateTime.now());
            
            order.setStatus(OrderStatus.PAID);
            
            // Create outbox event
            String payload = String.format("{\"studentId\":\"%s\", \"courseId\":\"%s\", \"orderId\":\"%s\"}", 
                order.getStudentId(), order.getCourseId(), order.getId());
                
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

        // Redirect user to frontend success/fail page (Mocked here)
        return ResponseEntity.ok("Payment processed. Status: " + transaction.getStatus());
    }
}
