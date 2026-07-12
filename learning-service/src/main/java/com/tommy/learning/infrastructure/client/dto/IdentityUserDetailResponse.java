package com.tommy.learning.infrastructure.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IdentityUserDetailResponse {
    private UUID id;
    private String email;
    private String fullName;
}
