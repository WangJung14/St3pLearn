package com.tommy.payment.application.service.impl;

import com.tommy.common.exception.AppException;
import com.tommy.common.exception.ErrorCode;
import com.tommy.payment.application.dto.request.ApplyCouponRequest;
import com.tommy.payment.application.dto.request.CheckoutRequest;
import com.tommy.payment.application.dto.response.CheckoutResponse;
import com.tommy.payment.domain.entity.PaymentOrder;
import com.tommy.payment.domain.entity.PaymentTransaction;
import com.tommy.payment.domain.entity.PaymentOutboxEvent;
import com.tommy.payment.domain.enums.OrderStatus;
import com.tommy.payment.domain.enums.OutboxStatus;
import com.tommy.payment.domain.enums.TransactionStatus;
import com.tommy.payment.infrastructure.persistence.repository.PaymentOrderRepository;
import com.tommy.payment.infrastructure.persistence.repository.PaymentOutboxEventRepository;
import com.tommy.payment.infrastructure.persistence.repository.PaymentTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final PaymentOrderRepository orderRepository;
    private final PaymentTransactionRepository transactionRepository;
    private final PaymentOutboxEventRepository outboxEventRepository;
    private final CouponService couponService;
    private final VNPayService vnPayService;

    @Transactional
    public CheckoutResponse checkout(UUID studentId, CheckoutRequest request, String ipAddress) {
        BigDecimal finalAmount = request.getOriginalAmount();
        BigDecimal discountAmount = BigDecimal.ZERO;

        if (request.getCouponCode() != null && !request.getCouponCode().isEmpty()) {
            ApplyCouponRequest applyReq = new ApplyCouponRequest();
            applyReq.setCourseId(request.getCourseId());
            applyReq.setCode(request.getCouponCode());
            applyReq.setOriginalAmount(request.getOriginalAmount());
            discountAmount = couponService.calculateDiscount(studentId, applyReq);
            finalAmount = request.getOriginalAmount().subtract(discountAmount);
        }

        String orderNumber = "ORD-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 4);

        PaymentOrder order = PaymentOrder.builder()
                .studentId(studentId)
                .courseId(request.getCourseId())
                .orderNumber(orderNumber)
                .originalAmount(request.getOriginalAmount())
                .discountAmount(discountAmount)
                .finalAmount(finalAmount)
                .currency("VND")
                .status(OrderStatus.PENDING_PAYMENT)
                .build();
        order = orderRepository.save(order);

        // If amount is 0 (100% discount), skip payment gateway
        if (finalAmount.compareTo(BigDecimal.ZERO) == 0) {
            order.setStatus(OrderStatus.PAID);
            orderRepository.save(order);
            
            // Create outbox event
            createPaymentSuccessEvent(order);
            
            return CheckoutResponse.builder()
                    .orderNumber(orderNumber)
                    .paymentUrl(null)
                    .build();
        }

        // Create Transaction
        String txnRef = UUID.randomUUID().toString();
        PaymentTransaction transaction = PaymentTransaction.builder()
                .paymentOrderId(order.getId())
                .gateway("VNPAY")
                .amount(finalAmount)
                .status(TransactionStatus.PENDING)
                .requestIdempotencyKey(txnRef)
                .build();
        transactionRepository.save(transaction);

        String paymentUrl = vnPayService.createPaymentUrl(
                "Thanh toan don hang " + orderNumber,
                finalAmount,
                txnRef,
                ipAddress
        );

        return CheckoutResponse.builder()
                .orderNumber(orderNumber)
                .paymentUrl(paymentUrl)
                .build();
    }

    private void createPaymentSuccessEvent(PaymentOrder order) {
        String payload = String.format("{\"studentId\":\"%s\", \"courseId\":\"%s\", \"orderId\":\"%s\"}", 
            order.getStudentId(), order.getCourseId(), order.getId());
            
        PaymentOutboxEvent event = PaymentOutboxEvent.builder()
                .aggregateId(order.getId().toString())
                .eventType("PaymentSucceeded")
                .payload(payload)
                .status(OutboxStatus.PENDING)
                .build();
        outboxEventRepository.save(event);
    }
}
