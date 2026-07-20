package com.tommy.admin.presentation.controller;

import com.tommy.admin.application.service.AuditLogService;
import com.tommy.admin.application.service.SystemConfigService;
import com.tommy.admin.domain.entity.AuditLog;
import com.tommy.admin.domain.entity.SystemConfig;
import com.tommy.common.response.ApiResponse;
import com.tommy.common.security.RequireRole;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/system")
@RequiredArgsConstructor
public class SystemAdminController {

    private final SystemConfigService configService;
    private final AuditLogService auditLogService;

    @RequireRole("ADMIN")
    @GetMapping("/configs")
    public ResponseEntity<ApiResponse<List<SystemConfig>>> getAllConfigs() {
        return ResponseEntity.ok(ApiResponse.success(200, "Success", configService.getAllConfigs()));
    }

    @RequireRole("ADMIN")
    @PutMapping("/configs/{key}")
    public ResponseEntity<ApiResponse<SystemConfig>> updateConfig(
            @PathVariable String key,
            @RequestBody ConfigUpdateRequest request) {
        
        UUID adminId = request.getAdminId() != null ? request.getAdminId() : UUID.randomUUID();
        SystemConfig config = configService.updateConfig(key, request.getValue(), request.getDescription(), adminId.toString());
        
        // Save audit log for config update
        AuditLog auditLog = AuditLog.builder()
                .actorId(adminId)
                .action("UPDATE_CONFIG")
                .targetType("SYSTEM_CONFIG")
                .targetId(key)
                .newValue(request.getValue())
                .description("Admin updated config " + key)
                .build();
        auditLogService.saveLog(auditLog);

        return ResponseEntity.ok(ApiResponse.success(200, "Config updated", config));
    }

    @RequireRole("ADMIN")
    @GetMapping("/audit-logs")
    public ResponseEntity<ApiResponse<List<AuditLog>>> getAuditLogs() {
        return ResponseEntity.ok(ApiResponse.success(200, "Success", auditLogService.getAllLogs()));
    }

    @Data
    public static class ConfigUpdateRequest {
        private UUID adminId;
        private String value;
        private String description;
    }
}
