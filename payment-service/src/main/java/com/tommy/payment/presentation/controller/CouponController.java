package com.tommy.payment.presentation.controller;

import com.tommy.common.response.ApiResponse;
import com.tommy.common.security.RequireRole;
import com.tommy.payment.application.dto.request.ApplyCouponRequest;
import com.tommy.payment.application.dto.request.CouponRequest;
import com.tommy.payment.application.service.impl.CouponService;
import com.tommy.payment.domain.entity.Coupon;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/payment/coupons")
@RequiredArgsConstructor
@Slf4j
public class CouponController {

    private final CouponService couponService;

    @PostMapping
    @RequireRole({"ADMIN", "INSTRUCTOR"})
    public ResponseEntity<ApiResponse<Coupon>> createCoupon(@Valid @RequestBody CouponRequest request) {
        log.info("Creating new coupon: {}", request.getCode());
        Coupon coupon = couponService.createCoupon(request);
        return ResponseEntity.ok(ApiResponse.success(200, "Coupon created successfully", coupon));
    }

    @PostMapping("/calculate")
    @RequireRole({"STUDENT"})
    public ResponseEntity<ApiResponse<BigDecimal>> calculateDiscount(
            @RequestHeader("X-User-Id") UUID studentId,
            @Valid @RequestBody ApplyCouponRequest request) {
        log.info("Student {} calculating discount for code {}", studentId, request.getCode());
        BigDecimal discount = couponService.calculateDiscount(studentId, request);
        return ResponseEntity.ok(ApiResponse.success(200, "Discount calculated", discount));
    }
}
