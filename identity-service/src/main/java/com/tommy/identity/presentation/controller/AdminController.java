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

    // Remove Role
    @RequireRole("ADMIN")
    @DeleteMapping("/{userId}/roles/{roleName}")
    public ResponseEntity<ApiResponse<Void>> removeRole(
            @PathVariable("userId") UUID userId,
            @PathVariable("roleName") String roleName) {
        
        adminService.removeRole(userId, roleName);
        
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(200, "Role removed successfully", null));
    }

    // Suspend User
    @RequireRole("ADMIN")
    @PutMapping("/{userId}/suspend")
    public ResponseEntity<ApiResponse<Void>> suspendUser(@PathVariable("userId") UUID userId) {
        adminService.changeAccountStatus(userId, com.tommy.identity.domain.enums.AccountStatus.SUSPENDED);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(200, "User suspended successfully", null));
    }

    // Lock User
    @RequireRole("ADMIN")
    @PutMapping("/{userId}/lock")
    public ResponseEntity<ApiResponse<Void>> lockUser(@PathVariable("userId") UUID userId) {
        adminService.changeAccountStatus(userId, com.tommy.identity.domain.enums.AccountStatus.LOCKED);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(200, "User locked successfully", null));
    }

    // Activate User
    @RequireRole("ADMIN")
    @PutMapping("/{userId}/activate")
    public ResponseEntity<ApiResponse<Void>> activateUser(@PathVariable("userId") UUID userId) {
        adminService.changeAccountStatus(userId, com.tommy.identity.domain.enums.AccountStatus.ACTIVE);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(200, "User activated successfully", null));
    }
}
