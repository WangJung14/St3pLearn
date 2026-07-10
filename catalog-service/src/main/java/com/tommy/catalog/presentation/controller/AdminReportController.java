package com.tommy.catalog.presentation.controller;

import com.tommy.catalog.application.dto.request.ProcessReportRequest;
import com.tommy.catalog.application.dto.response.ReportTicketResponse;
import com.tommy.catalog.application.service.IReportService;
import com.tommy.catalog.domain.enums.ReportStatus;
import com.tommy.common.security.RequireRole;
import com.tommy.catalog.application.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/reports")
@RequiredArgsConstructor
public class AdminReportController {

    private final IReportService reportService;

    @RequireRole("ADMIN")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ReportTicketResponse>>> getReports(
            @RequestParam(value = "status", required = false) ReportStatus status,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        Page<ReportTicketResponse> response = reportService.getReports(status, page, size);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(200, "Reports retrieved successfully", response));
    }

    @RequireRole("ADMIN")
    @PostMapping("/{reportId}/process")
    public ResponseEntity<ApiResponse<ReportTicketResponse>> processReport(
            @PathVariable("reportId") UUID reportId,
            @Valid @RequestBody ProcessReportRequest request) {

        ReportTicketResponse response = reportService.processReport(reportId, request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(200, "Report processed successfully", response));
    }
}
