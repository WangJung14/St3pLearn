package com.tommy.identity.presentation.controller;

import com.tommy.identity.application.dto.request.ForgotPasswordRequest;
import com.tommy.identity.application.dto.request.LoginRequest;
import com.tommy.identity.application.dto.request.LogoutRequest;
import com.tommy.identity.application.dto.request.RefreshTokenRequest;
import com.tommy.identity.application.dto.request.RegisterRequest;
import com.tommy.identity.application.dto.request.ResetPasswordRequest;
import com.tommy.identity.application.dto.request.VerifyEmailRequest;
import com.tommy.identity.application.dto.request.ResendVerifyEmailRequest;
import com.tommy.identity.application.dto.response.AuthResponse;
import com.tommy.identity.application.service.IAuthService;
import com.tommy.identity.presentation.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    //DI injection
    private final IAuthService authService;

    // Register Account
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {

        AuthResponse authResponse = authService.register(request);

        // Trả về data kèm bộ mã Token luôn cho mượt mà!
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(201, authResponse));
    }

    // Login Account
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {

        AuthResponse authResponse = authService.login(request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(200,"Login successful", authResponse));
    }

    // Refresh Token
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse authResponse = authService.refreshToken(request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(200,"Refresh successful", authResponse));
    }

    // Logout
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@Valid @RequestBody LogoutRequest request) {
        authService.logout(request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(200, "Logout successful", null));
    }

    // Forgot Password
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(200, "OTP sent to email successfully", null));
    }

    // Reset Password
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(200, "Password reset successfully", null));
    }

    // Verify Email
    @PostMapping("/verify-email")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {
        authService.verifyEmail(request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(200, "Email verified successfully", null));
    }

    // Resend Verification Email
    @PostMapping("/resend-verification-email")
    public ResponseEntity<ApiResponse<Void>> resendVerificationEmail(@Valid @RequestBody ResendVerifyEmailRequest request) {
        authService.resendVerificationEmail(request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(200, "Verification email sent successfully", null));
    }
}
