package com.tommy.identity.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshTokenRequest {
    @NotBlank(message = "Refresh token can not be blank")
    private String refreshToken;
}
