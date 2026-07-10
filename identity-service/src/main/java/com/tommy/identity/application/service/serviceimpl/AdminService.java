package com.tommy.identity.application.service.serviceimpl;

import com.tommy.identity.application.dto.request.AssignRoleRequest;
import com.tommy.identity.application.dto.response.UserListAdminResponse;
import com.tommy.identity.application.service.IAdminService;
import com.tommy.identity.domain.entity.Account;
import com.tommy.identity.domain.entity.Role;
import com.tommy.identity.domain.enums.AccountStatus;
import com.tommy.identity.domain.exception.AppException;
import com.tommy.identity.domain.exception.ErrorCode;
import com.tommy.identity.infrastructure.persistence.repository.AccountRepository;
import com.tommy.identity.infrastructure.persistence.repository.RoleRepository;
import com.tommy.identity.infrastructure.persistence.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService implements IAdminService {

    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    @Transactional
    public void assignRole(UUID targetUserId, AssignRoleRequest request) {
        Account account = accountRepository.findById(targetUserId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Role role = roleRepository.findByName(request.getRoleName())
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

        account.getRoles().add(role);
        accountRepository.save(account);

        log.info("Assigned role {} to user {}", request.getRoleName(), targetUserId);
    }

    @Override
    @Transactional
    public void removeRole(UUID targetUserId, String roleName) {
        Account account = accountRepository.findById(targetUserId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

        account.getRoles().remove(role);
        accountRepository.save(account);

        log.info("Removed role {} from user {}", roleName, targetUserId);
    }

    @Override
    @Transactional
    public void changeAccountStatus(UUID targetUserId, AccountStatus status) {
        Account account = accountRepository.findById(targetUserId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        account.setStatus(status);
        accountRepository.save(account);

        // Force logout if suspending or locking
        if (status == AccountStatus.SUSPENDED || status == AccountStatus.LOCKED) {
            refreshTokenRepository.deleteByUserId(targetUserId);
        }

        log.info("Changed account status to {} for user {}", status, targetUserId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserListAdminResponse> searchUsers(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Account> accounts = accountRepository.searchUsers(keyword, pageable);

        return accounts.map(account -> UserListAdminResponse.builder()
                .id(account.getId())
                .username(account.getUsername())
                .email(account.getEmail())
                .status(account.getStatus())
                .roles(account.getRoles().stream().map(Role::getName).collect(java.util.stream.Collectors.toSet()))
                .build());
    }
}
