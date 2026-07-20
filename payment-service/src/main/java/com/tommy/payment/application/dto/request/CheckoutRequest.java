package com.tommy.payment.application.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.UUID;
import java.math.BigDecimal;

@Data
public class CheckoutRequest {
    @NotNull
    private UUID courseId;
    
    private String couponCode;
    
    @NotNull
    private BigDecimal originalAmount;
}
