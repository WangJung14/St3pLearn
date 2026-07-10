package com.tommy.identity.application.dto.response;

import com.tommy.identity.domain.enums.AccountStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserListAdminResponse {
    private UUID id;
    private String username;
    private String email;
    private AccountStatus status;
    private Set<String> roles;
}
