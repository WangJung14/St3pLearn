package com.tommy.identity.application.service.serviceimpl;

import com.tommy.identity.application.dto.AuthResponse;
import com.tommy.identity.application.dto.RegisterRequest;
import com.tommy.identity.application.service.IAuthService;
import com.tommy.identity.domain.entity.*;
import com.tommy.identity.domain.enums.AccountStatus;
import com.tommy.identity.domain.exception.AppException;
import com.tommy.identity.domain.exception.ErrorCode;
import com.tommy.identity.infrastructure.persistence.repository.AccountRepository;
import com.tommy.identity.infrastructure.persistence.repository.RefreshTokenRepository;
import com.tommy.identity.infrastructure.persistence.repository.RoleRepository;
import com.tommy.identity.infrastructure.persistence.repository.UserSecurityLogRepository;
import com.tommy.identity.infrastructure.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.config.authentication.UserServiceBeanDefinitionParser;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService implements IAuthService {
    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserSecurityLogRepository securityLogRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // 1. check duplicate by email
        if(accountRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }
        // 2. check duplicate by userName
        if(accountRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        // 3. Get default role
        Role studentRole = roleRepository.findByName("STUDENT")
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));


        String encodePassword = passwordEncoder.encode(request.getPassword());

        // 4. Create Account with hashed password
        Account account = Account.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .passwordHash(encodePassword)
                .status(AccountStatus.UNVERIFIED)
                .emailVerified(false)
                .roles(Set.of(studentRole))
                .build();


        // 5. Init Profile (Primary key shared)
        UserProfile userProfile = UserProfile.builder()
                .account(account)
                .fullName(request.getFullName() != null ? request.getFullName() : request.getUsername())
                .build();

        // 6. Save database
        Account savedAccount = accountRepository.save(account);
        log.info("Successfully registered new user: {}", savedAccount.getUsername());

        // 7. Generate token
        String accessToken = jwtTokenProvider.generateAccessToken(savedAccount.getId(), savedAccount.getUsername());
        String refreshToken = jwtTokenProvider.generateRefreshToken(savedAccount.getId(), savedAccount.getUsername());

        // 8. Save hash refresh-token  to database
        String encodeRefreshToken = passwordEncoder.encode(refreshToken);
        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .userId(savedAccount.getId())
                .tokenHash(encodeRefreshToken)
                .expiresAt(LocalDateTime.now().plusDays(7)) // 7 days
                .ipAddress("127.0.0.1") // hardcode get IP address
                .deviceInfo("Linux Ubuntu")
                .build();
        refreshTokenRepository.save(refreshTokenEntity);

        // 9. Write Audit Log into user_security_logs table
        UserSecurityLog userSecurityLog = UserSecurityLog.builder()
                .userId(savedAccount.getId())
                .eventType("USER_REGISTERED")
                .metadata(Map.of("action", "Register an account and grant login access directly."))
                .build();
        securityLogRepository.save(userSecurityLog);
        // 10. Return data for controller
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(savedAccount.getId())
                .username(savedAccount.getUsername())
                .email(savedAccount.getEmail())
                .build();
    }
}