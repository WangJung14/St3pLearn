package com.tommy.identity.application.service.serviceimpl;

import com.tommy.identity.application.dto.request.LoginRequest;
import com.tommy.identity.application.dto.request.LogoutRequest;
import com.tommy.identity.application.dto.request.RefreshTokenRequest;
import com.tommy.identity.application.dto.request.RegisterRequest;
import com.tommy.identity.application.dto.response.AuthResponse;
import com.tommy.identity.application.service.IAuthService;
import com.tommy.identity.domain.entity.*;
import com.tommy.identity.domain.enums.AccountStatus;
import com.tommy.identity.domain.exception.AppException;
import com.tommy.identity.domain.exception.ErrorCode;
import com.tommy.identity.infrastructure.persistence.repository.*;
import com.tommy.identity.infrastructure.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
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
                .publicId(UUID.randomUUID().toString().replace("-","").substring(0, 10))
                .build();

        account.setProfile(userProfile);
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

    /*
    * Refresh Token
    * */

    @Override
    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request){

        String rawRefreshToken = request.getRefreshToken();

        // 1. Check is valid and expiration date JWT
        boolean isValidToken = jwtTokenProvider.isTokenValid(rawRefreshToken);
        if(!isValidToken) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }

        // 2. Extract userId from Token
        UUID userId = jwtTokenProvider.extractUserId(rawRefreshToken);

        // 3. Get user information for verify if user is valid and not locked out
        Account account = accountRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if(account.getStatus() == AccountStatus.SUSPENDED || account.getStatus() == AccountStatus.LOCKED) {
            throw new AppException(ErrorCode.ACCOUNT_LOCKED);
        }

        // 4. Find All refresh Token is active
        List<RefreshToken> activeTokens = refreshTokenRepository.findAllByUserIdAndRevokedAtIsNull(userId);

        // 5. BCrypt Matching check if user submitted rawRefreshToken matches any existing entries in database

        RefreshToken matchedToken = activeTokens.stream()
                .filter(token -> passwordEncoder.matches(rawRefreshToken, token.getTokenHash()))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_TOKEN));
        // 6. Revoked old token
        matchedToken.setRevokedAt(LocalDateTime.now());
        refreshTokenRepository.save(matchedToken);

        //7. Generate new token
        String newAccessToken = jwtTokenProvider.generateAccessToken(account.getId(), account.getUsername());
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(account.getId(), account.getUsername());

        //8. Save refresh token to database
        RefreshToken newRefreshTokenEntity = RefreshToken.builder()
                .userId(account.getId())
                .tokenHash(passwordEncoder.encode(newRefreshToken))
                .expiresAt(LocalDateTime.now().plusDays(7))
                .ipAddress("127.0.0.1")
                .deviceInfo("Linux Ubuntu")
                .build();
        refreshTokenRepository.save(newRefreshTokenEntity);

        // 9. Save audit log
        UserSecurityLog securityLog = UserSecurityLog.builder()
                .userId(account.getId())
                .eventType("TOKEN_REFRESHED")
                .metadata(Map.of("action", "User refresh login session"))
                .build();
        securityLogRepository.save(securityLog);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .userId(account.getId())
                .username(account.getUsername())
                .email(account.getEmail())
                .build();
    }


    /*
    * Logout account
    * */

    @Override
    @Transactional
    public void logout(LogoutRequest request){

        String rawRefreshToken = request.getRefreshToken();

        // 1.If token is not valid , do nothing

        boolean isValidToken = jwtTokenProvider.isTokenValid(rawRefreshToken);
        if(!isValidToken) return;

        // 2. Extract user id from token
        UUID userId = jwtTokenProvider.extractUserId(rawRefreshToken);

        // 3. Find active tokens of user
        List<RefreshToken> activeTokens = refreshTokenRepository.findAllByUserIdAndRevokedAtIsNull(userId);

        //4. Compare token that are being request to check which one is from database
        activeTokens.stream()
                .filter(token -> passwordEncoder.matches(rawRefreshToken, token.getTokenHash()))
                .findFirst()
                .ifPresent(matchedToken -> {
                    // 5. Remove token
                    matchedToken.setRevokedAt(LocalDateTime.now());
                    refreshTokenRepository.save(matchedToken);

                    // 6. Save audit log
                    UserSecurityLog securityLog = UserSecurityLog.builder()
                            .userId(userId)
                            .eventType("USER_LOGGED_OUT")
                            .metadata(Map.of("action", "User logout login session"))
                            .build();
                    securityLogRepository.save(securityLog);

                    log.info("User {} successfully logged out", userId);
                });

    }
}