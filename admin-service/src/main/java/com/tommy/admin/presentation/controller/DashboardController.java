package com.tommy.admin.presentation.controller;

import com.tommy.admin.application.dto.response.DashboardMetricsResponse;
import com.tommy.admin.application.service.AnalyticsService;
import com.tommy.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final AnalyticsService analyticsService;

    @GetMapping
    public ResponseEntity<ApiResponse<DashboardMetricsResponse>> getDashboard() {
        DashboardMetricsResponse metrics = analyticsService.getDashboardMetrics();
        return ResponseEntity.ok(ApiResponse.success(200, "Dashboard metrics retrieved successfully", metrics));
    }
}
