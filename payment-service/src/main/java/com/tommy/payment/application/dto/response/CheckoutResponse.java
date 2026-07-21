package com.tommy.payment.application.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class CheckoutResponse {
    private UUID orderId;
    private String orderNumber;
    private String paymentUrl;
    private String status;
    private BigDecimal originalAmount;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
}
