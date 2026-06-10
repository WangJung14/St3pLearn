package com.tommy.identity.presentation.controller;

import com.tommy.identity.application.dto.request.UpdateProfileRequest;
import com.tommy.identity.application.dto.response.PublicProfileResponse;
import com.tommy.identity.application.dto.response.UserProfileResponse;
import com.tommy.identity.application.service.IUserService;
import com.tommy.identity.presentation.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final IUserService userService;

    // Get user profile
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getMyProfile() {
        // Extract userId form security filter context
        String userIdStr = SecurityContextHolder.getContext().getAuthentication().getName();
        UUID userId = UUID.fromString(userIdStr);

        // Gọi Service lấy thông tin hồ sơ
        UserProfileResponse profileResponse = userService.getMyProfile(userId);

        // Đóng gói trả về JSON chuẩn hóa
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(200,profileResponse));
    }

    // Get public user profile
    @GetMapping("/p/{publicId}")
    public ResponseEntity<ApiResponse<PublicProfileResponse>> getPublicProfile(@PathVariable("publicId") String publicId) {
        PublicProfileResponse publicProfile = userService.getPublicProfile(publicId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(200,publicProfile));
    }

    // Update user profile
    @PostMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateMyProfile(
            @jakarta.validation.Valid @RequestBody UpdateProfileRequest request) {

        // Get id by Security context
        String userIdStr = SecurityContextHolder.getContext().getAuthentication().getName();
        UUID userId = UUID.fromString(userIdStr);

        UserProfileResponse updatedProfile = userService.updateMyProfile(userId, request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(200,"Update profile successfully",updatedProfile));
    }
}
