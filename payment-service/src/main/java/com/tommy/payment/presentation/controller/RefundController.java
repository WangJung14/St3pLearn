package com.tommy.payment.presentation.controller;

import com.tommy.common.response.ApiResponse;
import com.tommy.common.security.RequireRole;
import com.tommy.payment.application.dto.request.RefundRequestDto;
import com.tommy.payment.application.service.impl.RefundService;
import com.tommy.payment.domain.entity.RefundRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/payment/refunds")
@RequiredArgsConstructor
@Slf4j
public class RefundController {

    private final RefundService refundService;

    @PostMapping
    @RequireRole({"STUDENT"})
    public ResponseEntity<ApiResponse<RefundRequest>> requestRefund(
            @RequestHeader("X-User-Id") UUID studentId,
            @Valid @RequestBody RefundRequestDto request) {
        log.info("Student {} requesting refund for order {}", studentId, request.getPaymentOrderId());
        RefundRequest refundRequest = refundService.requestRefund(studentId, request);
        return ResponseEntity.ok(ApiResponse.success(200, "Refund requested successfully", refundRequest));
    }

    @PostMapping("/{id}/approve")
    @RequireRole({"ADMIN"})
    public ResponseEntity<ApiResponse<RefundRequest>> approveRefund(@PathVariable UUID id) {
        log.info("Admin approving refund {}", id);
        RefundRequest refundRequest = refundService.approveRefund(id);
        return ResponseEntity.ok(ApiResponse.success(200, "Refund approved", refundRequest));
    }
}
