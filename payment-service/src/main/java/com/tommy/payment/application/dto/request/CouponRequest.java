package com.tommy.payment.application.dto.request;

import com.tommy.payment.domain.enums.DiscountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CouponRequest {
    private UUID courseId;
    
    @NotBlank
    private String code;

    @NotNull
    private DiscountType discountType;

    @NotNull
    private BigDecimal discountValue;

    private BigDecimal maxDiscount;
    private Integer usageLimit;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
