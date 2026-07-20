package com.tommy.payment.presentation.controller;

import com.tommy.common.response.ApiResponse;
import com.tommy.common.security.RequireRole;
import com.tommy.payment.application.dto.request.CheckoutRequest;
import com.tommy.payment.application.dto.response.CheckoutResponse;
import com.tommy.payment.application.service.impl.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/payment/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/checkout")
    @RequireRole({"STUDENT"})
    public ResponseEntity<ApiResponse<CheckoutResponse>> checkout(
            @RequestHeader("X-User-Id") UUID studentId,
            @Valid @RequestBody CheckoutRequest request,
            HttpServletRequest servletRequest) {
        
        String ipAddress = servletRequest.getRemoteAddr();
        log.info("Student {} creating order for course {} with IP {}", studentId, request.getCourseId(), ipAddress);
        
        CheckoutResponse response = orderService.checkout(studentId, request, ipAddress);
        return ResponseEntity.ok(ApiResponse.success(200, "Order created", response));
    }
}
