package com.tommy.identity.application.dto.response;

import com.tommy.identity.domain.enums.AccountStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDetailAdminResponse {
    private UUID id;
    private String username;
    private String email;
    private boolean emailVerified;
    private AccountStatus status;
    private Set<String> roles;
    private String fullName;
    private String avatarUrl;
    private String bio;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLoginAt;
}
