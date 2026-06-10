package com.tommy.identity.presentation.controller;

import com.tommy.identity.application.dto.PublicProfileResponse;
import com.tommy.identity.application.dto.UserProfileResponse;
import com.tommy.identity.application.service.IUserService;
import com.tommy.identity.presentation.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
