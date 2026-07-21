package com.tommy.payment.application.service.impl;

import com.tommy.common.exception.AppException;
import com.tommy.common.exception.ErrorCode;
import com.tommy.payment.application.dto.request.RefundRequestDto;
import com.tommy.payment.domain.entity.PaymentOrder;
import com.tommy.payment.domain.entity.PaymentOutboxEvent;
import com.tommy.payment.domain.entity.RefundRequest;
import com.tommy.payment.domain.enums.OrderStatus;
import com.tommy.payment.domain.enums.OutboxStatus;
import com.tommy.payment.domain.enums.RefundStatus;
import com.tommy.payment.infrastructure.persistence.repository.PaymentOrderRepository;
import com.tommy.payment.infrastructure.persistence.repository.PaymentOutboxEventRepository;
import com.tommy.payment.infrastructure.persistence.repository.RefundRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefundService {

    private final RefundRequestRepository refundRepository;
    private final PaymentOrderRepository orderRepository;
    private final PaymentOutboxEventRepository outboxEventRepository;

    @Transactional
    public RefundRequest requestRefund(UUID studentId, RefundRequestDto request) {
        PaymentOrder order = orderRepository.findById(request.getPaymentOrderId())
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION)); // ORDER_NOT_FOUND

        if (!order.getStudentId().equals(studentId)) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION); // FORBIDDEN
        }

        if (order.getStatus() != OrderStatus.PAID) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION); // INVALID_ORDER_STATUS
        }

        if (request.getRefundAmount().compareTo(order.getFinalAmount()) > 0) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION); // REFUND_AMOUNT_EXCEEDS_PAID_AMOUNT
        }

        RefundRequest refundRequest = RefundRequest.builder()
                .paymentOrderId(order.getId())
                .studentId(studentId)
                .refundAmount(request.getRefundAmount())
                .reason(request.getReason())
                .status(RefundStatus.REQUESTED)
                .build();
                
        return refundRepository.save(refundRequest);
    }

    @Transactional
    public RefundRequest approveRefund(UUID refundId) {
        RefundRequest refundRequest = refundRepository.findById(refundId)
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION));
                
        if (refundRequest.getStatus() != RefundStatus.REQUESTED) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }

        PaymentOrder order = orderRepository.findById(refundRequest.getPaymentOrderId()).orElseThrow();
        
        // Mock VNPay Refund call here...
        // vnPayService.refund(...)

        refundRequest.setStatus(RefundStatus.COMPLETED);
        refundRequest.setProcessedAt(LocalDateTime.now());
        
        order.setStatus(OrderStatus.REFUNDED);
        orderRepository.save(order);
        
        // Emit RefundCompleted Event so Learning Service un-enrolls
        String payload = String.format("{\"studentId\":\"%s\", \"courseId\":\"%s\", \"orderId\":\"%s\"}", 
            order.getStudentId(), order.getCourseId(), order.getId());
            
        PaymentOutboxEvent event = PaymentOutboxEvent.builder()
                .aggregateId(order.getId().toString())
                .eventType("RefundCompleted")
                .payload(payload)
                .status(OutboxStatus.PENDING)
                .build();
        outboxEventRepository.save(event);

        return refundRepository.save(refundRequest);
    }
}
