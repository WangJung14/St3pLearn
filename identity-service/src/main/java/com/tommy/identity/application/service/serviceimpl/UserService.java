package com.tommy.identity.application.service.serviceimpl;
import com.tommy.identity.application.dto.PublicProfileResponse;
import com.tommy.identity.application.dto.UserProfileResponse;
import com.tommy.identity.application.service.IUserService;
import com.tommy.identity.domain.entity.Account;
import com.tommy.identity.domain.entity.UserProfile;
import com.tommy.identity.domain.exception.AppException;
import com.tommy.identity.domain.exception.ErrorCode;
import com.tommy.identity.infrastructure.persistence.repository.AccountRepository;
import com.tommy.identity.infrastructure.persistence.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements IUserService {
    private final AccountRepository accountRepository;
    private final UserProfileRepository userProfileRepository;

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
}
