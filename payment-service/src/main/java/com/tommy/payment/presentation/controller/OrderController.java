package com.tommy.payment.presentation.controller;

import com.tommy.common.response.ApiResponse;
import com.tommy.common.security.RequireRole;
import com.tommy.payment.application.dto.request.CheckoutRequest;
import com.tommy.payment.application.dto.response.CheckoutResponse;
import com.tommy.payment.application.dto.response.PaymentOrderResponse;
import com.tommy.payment.application.service.impl.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/payment/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    @RequireRole({"STUDENT"})
    public ResponseEntity<ApiResponse<Page<PaymentOrderResponse>>> getMyOrders(
            @RequestHeader("X-User-Id") UUID studentId,
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
                200,
                "Payment history retrieved",
                orderService.getStudentOrders(studentId, pageable)
        ));
    }

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
