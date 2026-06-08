package com.tommy.identity.presentation.controller;

import com.tommy.identity.application.dto.AuthResponse;
import com.tommy.identity.application.dto.LoginRequest;
import com.tommy.identity.application.dto.RegisterRequest;
import com.tommy.identity.application.service.IAuthService;
import com.tommy.identity.domain.entity.UserSecurityLog;
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

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {

        AuthResponse authResponse = authService.register(request);

        // Trả về data kèm bộ mã Token luôn cho mượt mà!
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(201, authResponse));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {

        AuthResponse authResponse = authService.login(request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(200,"Login successful", authResponse));
    }

}
