package com.tommy.identity.application.dto.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {
    @NotBlank(message = "Email address can't be blank")
    @Email(message = "Email address is not correct format")
    private String email;

    @NotBlank(message = "Username can't be blank")
    @Size(min = 4, max = 50, message = "Username must contain between 4 and 50 characters! ")
    private String username;

    @NotBlank(message = "Password can't be blank")
    @Size(min = 8, message = "Password must contain at least 8")
    private String password;

    private String fullName;
}
