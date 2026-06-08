package com.tommy.identity.application.service.serviceimpl;

import com.tommy.identity.application.dto.AuthResponse;
import com.tommy.identity.application.dto.LoginRequest;
import com.tommy.identity.application.dto.RegisterRequest;
import com.tommy.identity.application.service.IAuthService;
import com.tommy.identity.domain.entity.*;
import com.tommy.identity.domain.enums.AccountStatus;
import com.tommy.identity.domain.exception.AppException;
import com.tommy.identity.domain.exception.ErrorCode;
import com.tommy.identity.infrastructure.persistence.repository.*;
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
    private final LoginHistoryRepository loginHistoryRepository;

    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;


    /*
    * Register account function
    * */
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


    /*
    * Login account Function
    * */

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        // 1. Find account by email
        Account account = accountRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_CREDENTIALS));
        // 2. Check password
        boolean isPasswordValid = passwordEncoder.matches(request.getPassword(), account.getPasswordHash());
        if(!isPasswordValid) {
            // future develop : If the user enters incorrect information 5 times, they will not be allowed to enter more
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }

        // 3. Check account status
        if(account.getStatus() == AccountStatus.SUSPENDED || account.getStatus() == AccountStatus.LOCKED) {
            throw new AppException(ErrorCode.ACCOUNT_LOCKED);
        }

        // 4. Generate token
        String accessToken = jwtTokenProvider.generateAccessToken(account.getId(), account.getUsername());
        String refreshToken = jwtTokenProvider.generateRefreshToken(account.getId(), account.getUsername());

        // 5. Save Refresh Token to database
        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .userId(account.getId())
                .tokenHash(passwordEncoder.encode(refreshToken))
                .expiresAt(LocalDateTime.now().plusDays(7))
                .ipAddress("127.0.0.1") // Hardcode, IP address will get from HttpServletRequest
                .deviceInfo("Postman/Browser")
                .build();
        refreshTokenRepository.save(refreshTokenEntity);

        // 6. Update last login and write login log
        account.setLastLoginAt(LocalDateTime.now());
        accountRepository.save(account);

        LoginHistory loginHistory = LoginHistory.builder()
                .userId(account.getId())
                .loginAt(LocalDateTime.now())
                .ipAddress("127.0.0.1")
                .deviceInfo("Postman/Browser")
                .success(true)
                .build();
        loginHistoryRepository.save(loginHistory);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(account.getId())
                .username(account.getUsername())
                .email(account.getEmail())
                .build();
    }
}