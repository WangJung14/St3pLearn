package com.tommy.payment.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class RefundRequestDto {
    @NotNull
    private UUID paymentOrderId;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal refundAmount;

    @NotBlank
    private String reason;
}
