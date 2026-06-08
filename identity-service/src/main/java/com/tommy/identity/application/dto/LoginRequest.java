package com.tommy.identity.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {

    @NotBlank(message = "Email can't be blank")
    @Email(message = "Email is not correct format")
    private String email;

    @NotBlank(message = "Mật khẩu không được để trống")
    private String password;
}