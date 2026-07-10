package com.tommy.identity.application.service.serviceimpl;

import com.tommy.identity.application.dto.request.AssignRoleRequest;
import com.tommy.identity.application.service.IAdminService;
import com.tommy.identity.domain.entity.Account;
import com.tommy.identity.domain.entity.Role;
import com.tommy.identity.domain.exception.AppException;
import com.tommy.identity.domain.exception.ErrorCode;
import com.tommy.identity.infrastructure.persistence.repository.AccountRepository;
import com.tommy.identity.infrastructure.persistence.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService implements IAdminService {

    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;

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
}
