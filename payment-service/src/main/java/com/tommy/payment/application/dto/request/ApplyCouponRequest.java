package com.tommy.payment.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.UUID;
import java.math.BigDecimal;

@Data
public class ApplyCouponRequest {
    private UUID courseId; // For validation if coupon is course specific

    @NotBlank
    private String code;
    
    private BigDecimal originalAmount;
}
