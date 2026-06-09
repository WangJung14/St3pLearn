package com.tommy.identity.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LogoutRequest {
    @NotBlank(message = "Refresh Token can not be blank")
    private String refreshToken;
}
