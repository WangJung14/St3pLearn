package com.tommy.identity.application.dto.response;


import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileResponse {
    private UUID userId;
    private String username;
    private String email;
    private String fullName;
    private String avatarUrl;
    private String bio;
    private String country;
    private String timezone;
    private String englishLevel;
    private LocalDate birthDate;
}
