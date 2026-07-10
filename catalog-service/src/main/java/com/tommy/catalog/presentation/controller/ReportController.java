package com.tommy.catalog.presentation.controller;

import com.tommy.catalog.application.dto.request.CreateReportRequest;
import com.tommy.catalog.application.dto.response.ReportTicketResponse;
import com.tommy.catalog.application.service.IReportService;
import com.tommy.common.security.RequireRole;
import com.tommy.catalog.application.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final IReportService reportService;

    @RequireRole({"STUDENT", "TEACHER"})
    @PostMapping
    public ResponseEntity<ApiResponse<ReportTicketResponse>> createReport(
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader,
            @Valid @RequestBody CreateReportRequest request) {
        
        UUID reporterId = UUID.fromString(userIdHeader);
        ReportTicketResponse response = reportService.createReport(reporterId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(201, "Report created successfully", response));
    }
}
