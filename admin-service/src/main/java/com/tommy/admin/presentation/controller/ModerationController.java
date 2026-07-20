package com.tommy.admin.presentation.controller;

import com.tommy.admin.application.service.ModerationService;
import com.tommy.admin.domain.entity.ModerationCase;
import com.tommy.admin.domain.entity.ModerationLog;
import com.tommy.common.response.ApiResponse;
import com.tommy.common.security.RequireRole;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/moderation/cases")
@RequiredArgsConstructor
public class ModerationController {

    private final ModerationService moderationService;

    @RequireRole("ADMIN")
    @GetMapping
    public ResponseEntity<ApiResponse<List<ModerationCase>>> getAllCases() {
        return ResponseEntity.ok(ApiResponse.success(200, "Success", moderationService.getAllCases()));
    }

    @RequireRole("ADMIN")
    @PostMapping("/{caseId}/dismiss")
    public ResponseEntity<ApiResponse<ModerationCase>> dismissCase(
            @PathVariable UUID caseId,
            @RequestBody ActionRequest request) {
        // Mock adminId for now, normally from SecurityContext
        UUID adminId = request.getAdminId() != null ? request.getAdminId() : UUID.randomUUID();
        ModerationCase modCase = moderationService.dismissCase(caseId, adminId, request.getNotes());
        return ResponseEntity.ok(ApiResponse.success(200, "Case dismissed", modCase));
    }

    @RequireRole("ADMIN")
    @PostMapping("/{caseId}/resolve")
    public ResponseEntity<ApiResponse<ModerationCase>> resolveCase(
            @PathVariable UUID caseId,
            @RequestBody ActionRequest request) {
        UUID adminId = request.getAdminId() != null ? request.getAdminId() : UUID.randomUUID();
        ModerationCase modCase = moderationService.resolveCase(caseId, adminId, request.getAction(), request.getNotes());
        return ResponseEntity.ok(ApiResponse.success(200, "Case resolved", modCase));
    }

    @RequireRole("ADMIN")
    @GetMapping("/{caseId}/logs")
    public ResponseEntity<ApiResponse<List<ModerationLog>>> getLogs(@PathVariable UUID caseId) {
        return ResponseEntity.ok(ApiResponse.success(200, "Success", moderationService.getLogsForCase(caseId)));
    }

    @Data
    public static class ActionRequest {
        private UUID adminId;
        private String action;
        private String notes;
    }
}
