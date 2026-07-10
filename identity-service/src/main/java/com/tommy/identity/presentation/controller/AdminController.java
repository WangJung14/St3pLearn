package com.tommy.identity.presentation.controller;

import com.tommy.common.security.RequireRole;
import com.tommy.identity.application.dto.request.AssignRoleRequest;
import com.tommy.identity.application.service.IAdminService;
import com.tommy.identity.presentation.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminController {

    private final IAdminService adminService;

    // Assign Role
    @RequireRole("ADMIN")
    @PostMapping("/{userId}/roles")
    public ResponseEntity<ApiResponse<Void>> assignRole(
            @PathVariable("userId") UUID userId,
            @jakarta.validation.Valid @RequestBody AssignRoleRequest request) {
        
        adminService.assignRole(userId, request);
        
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(200, "Role assigned successfully", null));
    }
}
