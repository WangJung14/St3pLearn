package com.tommy.payment.application.service.impl;

import com.tommy.common.exception.AppException;
import com.tommy.common.exception.ErrorCode;
import com.tommy.payment.application.dto.request.ApplyCouponRequest;
import com.tommy.payment.application.dto.request.CheckoutRequest;
import com.tommy.payment.application.dto.response.CheckoutResponse;
import com.tommy.payment.application.dto.response.PaymentOrderResponse;
import com.tommy.payment.domain.entity.PaymentOrder;
import com.tommy.payment.domain.entity.PaymentTransaction;
import com.tommy.payment.domain.entity.PaymentOutboxEvent;
import com.tommy.payment.domain.enums.OrderStatus;
import com.tommy.payment.domain.enums.OutboxStatus;
import com.tommy.payment.domain.enums.TransactionStatus;
import com.tommy.payment.infrastructure.persistence.repository.PaymentOrderRepository;
import com.tommy.payment.infrastructure.persistence.repository.PaymentOutboxEventRepository;
import com.tommy.payment.infrastructure.persistence.repository.PaymentTransactionRepository;
import com.tommy.payment.infrastructure.client.CatalogClient;
import com.tommy.payment.infrastructure.client.dto.CatalogCourseResponse;
import com.tommy.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
    private final CatalogClient catalogClient;

    @Transactional
    public CheckoutResponse checkout(UUID studentId, CheckoutRequest request, String ipAddress) {
        ApiResponse<CatalogCourseResponse> catalogResponse = catalogClient.getCourse(request.getCourseId());
        CatalogCourseResponse course = catalogResponse != null ? catalogResponse.getData() : null;
        if (course == null || course.getPrice() == null || !"PUBLISHED".equals(course.getStatus())) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION); // COURSE_NOT_AVAILABLE_FOR_PURCHASE
        }

        BigDecimal originalAmount = course.getPrice();
        BigDecimal finalAmount = originalAmount;
        BigDecimal discountAmount = BigDecimal.ZERO;

        if (request.getCouponCode() != null && !request.getCouponCode().isEmpty()) {
            ApplyCouponRequest applyReq = new ApplyCouponRequest();
            applyReq.setCourseId(request.getCourseId());
            applyReq.setCode(request.getCouponCode());
            applyReq.setOriginalAmount(originalAmount);
            discountAmount = couponService.calculateDiscount(studentId, applyReq);
            finalAmount = originalAmount.subtract(discountAmount);
        }

        String orderNumber = "ORD-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 4);

        PaymentOrder order = PaymentOrder.builder()
                .studentId(studentId)
                .courseId(request.getCourseId())
                .orderNumber(orderNumber)
                .originalAmount(originalAmount)
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
                    .orderId(order.getId())
                    .orderNumber(orderNumber)
                    .paymentUrl(null)
                    .status(order.getStatus().name())
                    .originalAmount(order.getOriginalAmount())
                    .discountAmount(order.getDiscountAmount())
                    .finalAmount(order.getFinalAmount())
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
                .orderId(order.getId())
                .orderNumber(orderNumber)
                .paymentUrl(paymentUrl)
                .status(order.getStatus().name())
                .originalAmount(order.getOriginalAmount())
                .discountAmount(order.getDiscountAmount())
                .finalAmount(order.getFinalAmount())
                .build();
    }

    @Transactional(readOnly = true)
    public Page<PaymentOrderResponse> getStudentOrders(UUID studentId, Pageable pageable) {
        return orderRepository.findByStudentIdOrderByCreatedAtDesc(studentId, pageable)
                .map(order -> {
                    PaymentTransaction transaction = transactionRepository
                            .findFirstByPaymentOrderIdOrderByCreatedAtDesc(order.getId())
                            .orElse(null);

                    return PaymentOrderResponse.builder()
                            .id(order.getId())
                            .courseId(order.getCourseId())
                            .orderNumber(order.getOrderNumber())
                            .originalAmount(order.getOriginalAmount())
                            .discountAmount(order.getDiscountAmount())
                            .finalAmount(order.getFinalAmount())
                            .currency(order.getCurrency())
                            .status(order.getStatus().name())
                            .gateway(transaction != null ? transaction.getGateway() : null)
                            .transactionStatus(transaction != null ? transaction.getStatus().name() : null)
                            .createdAt(order.getCreatedAt())
                            .completedAt(transaction != null ? transaction.getCompletedAt() : null)
                            .build();
                });
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
