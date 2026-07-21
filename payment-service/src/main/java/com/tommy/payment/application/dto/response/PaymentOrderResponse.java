package com.tommy.payment.application.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class PaymentOrderResponse {
    private UUID id;
    private UUID courseId;
    private String orderNumber;
    private BigDecimal originalAmount;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    private String currency;
    private String status;
    private String gateway;
    private String transactionStatus;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
}
