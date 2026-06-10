package com.tommy.identity.application.service.serviceimpl;
import com.tommy.identity.application.dto.request.UpdateProfileRequest;
import com.tommy.identity.application.dto.response.PublicProfileResponse;
import com.tommy.identity.application.dto.response.UserProfileResponse;
import com.tommy.identity.application.service.IUserService;
import com.tommy.identity.domain.entity.Account;
import com.tommy.identity.domain.entity.RefreshToken;
import com.tommy.identity.domain.entity.UserProfile;
import com.tommy.identity.domain.entity.UserSecurityLog;
import com.tommy.identity.domain.enums.AccountStatus;
import com.tommy.identity.domain.exception.AppException;
import com.tommy.identity.domain.exception.ErrorCode;
import com.tommy.identity.infrastructure.persistence.repository.AccountRepository;
import com.tommy.identity.infrastructure.persistence.repository.RefreshTokenRepository;
import com.tommy.identity.infrastructure.persistence.repository.UserProfileRepository;
import com.tommy.identity.infrastructure.persistence.repository.UserSecurityLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements IUserService {
    private final AccountRepository accountRepository;
    private final UserProfileRepository userProfileRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserSecurityLogRepository  securityLogRepository;

    /*
    * Get my profile
    * */
    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getMyProfile(UUID userId) {
        // Find user information
        Account account = accountRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        log.info("Fetching profile details for verified user: {}", account.getUsername());

        UserProfile profile = account.getProfile();

        return UserProfileResponse.builder()
                .userId(account.getId())
                .username(account.getUsername())
                .email(account.getEmail())
                .fullName(profile != null && profile.getFullName() != null ? profile.getFullName() : account.getUsername())
                .avatarUrl(profile != null ? profile.getAvatarUrl() : null)
                .bio(profile != null ? profile.getBio() : null)
                .country(profile != null ? profile.getCountry() : null)
                .timezone(profile != null ? profile.getTimezone() : null)
                .englishLevel(profile != null && profile.getEnglishLevel() != null ? profile.getEnglishLevel().name() : null)
                .birthDate(profile != null ? profile.getBirthDate() : null)
                .build();
    }

    /*
    * Get profile public
    * */
    @Override
    @Transactional(readOnly = true)
    public PublicProfileResponse getPublicProfile(String publicId) {
        // 1. Query database to get publicId
        UserProfile profile = userProfileRepository.findByPublicId(publicId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        // 2. Return information public
        return PublicProfileResponse.builder()
                .username(profile.getAccount().getUsername())
                .fullName(profile.getFullName())
                .avatarUrl(profile.getAvatarUrl())
                .bio(profile.getBio())
                .country(profile.getCountry())
                .build();
    }

    /*
    * Update my profile
    * */
    @Override
    @Transactional
    public UserProfileResponse updateMyProfile(UUID userId, UpdateProfileRequest request) {
        // 1. Find account by Id
        Account account = accountRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // 2. Get profile for update
        // Create new profile if user doesn't have profile
        UserProfile profile = account.getProfile();
        if (profile == null) {
            profile = UserProfile.builder().account(account).build();
            account.setProfile(profile);
        }

        // 3. Update new data from request to entity
        if (request.getFullName() != null) profile.setFullName(request.getFullName());
        if (request.getAvatarUrl() != null) profile.setAvatarUrl(request.getAvatarUrl());
        if (request.getBio() != null) profile.setBio(request.getBio());
        if (request.getCountry() != null) profile.setCountry(request.getCountry());
        if (request.getTimezone() != null) profile.setTimezone(request.getTimezone());
        if (request.getBirthDate() != null) profile.setBirthDate(request.getBirthDate());

        // 4. Safe Enum data type casting for English level
        if (request.getEnglishLevel() != null && !request.getEnglishLevel().isBlank()) {
            try {
                profile.setEnglishLevel(com.tommy.identity.domain.enums.EnglishLevel.valueOf(request.getEnglishLevel().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new AppException(ErrorCode.INVALID_ROLE);
            }
        }

        // 5. Save update to database
        Account savedAccount = accountRepository.save(account);
        log.info("Successfully updated profile for user: {}", savedAccount.getUsername());

        // 6. Return new user profile
        UserProfile updatedProfile = savedAccount.getProfile();
        return UserProfileResponse.builder()
                .userId(savedAccount.getId())
                .username(savedAccount.getUsername())
                .email(savedAccount.getEmail())
                .fullName(updatedProfile.getFullName())
                .avatarUrl(updatedProfile.getAvatarUrl())
                .bio(updatedProfile.getBio())
                .country(updatedProfile.getCountry())
                .timezone(updatedProfile.getTimezone())
                .englishLevel(updatedProfile.getEnglishLevel() != null ? updatedProfile.getEnglishLevel().name() : null)
                .birthDate(updatedProfile.getBirthDate())
                .build();
    }

    /*
    * Deactive user account
    * */

    @Override
    @Transactional
    public void deactivateMyAccount(UUID userId) {
        // 1. Find user account by id
        Account account = accountRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // 2. Change status to soft DELETED. It won't be deleted in database
        account.setStatus(AccountStatus.DELETED);
        accountRepository.save(account);

        // 3. Revoke all active Refresh token
        List<RefreshToken> activeTokens = refreshTokenRepository.findAllByUserIdAndRevokedAtIsNull(userId);
        if (!activeTokens.isEmpty()) {
            activeTokens.forEach(token -> token.setRevokedAt(LocalDateTime.now()));
            refreshTokenRepository.saveAll(activeTokens);
        }

        // 4. Save audit log
        UserSecurityLog securityLog = UserSecurityLog.builder()
                .userId(account.getId())
                .eventType("ACCOUNT_DEACTIVATED")
                .metadata(Map.of("action", "Users voluntarily request account deletion/deactivation"))
                .build();
        securityLogRepository.save(securityLog);

        log.info("User {} has successfully deactivated their account.", account.getUsername());
    }
}
