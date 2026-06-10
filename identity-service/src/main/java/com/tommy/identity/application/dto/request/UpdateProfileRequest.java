package com.tommy.identity.application.dto.request;


import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateProfileRequest {
    @Size(max = 255, message = "The full name must not exceed 255 characters")
    private String fullName;

    private String avatarUrl;

    @Size(max = 1000, message = "The biography must not exceed 1000 characters")
    private String bio;

    @Size(max = 100, message = "Country names must not exceed 100 characters")
    private String country;

    @Size(max = 100, message = "Time zone codes must not exceed 100 characters.")
    private String timezone;

    private String englishLevel; // It will identify the String from the Client and map it to an Enum in the Service.

    @Past(message = "The date of birth must be a date in the past")
    private LocalDate birthDate;
}
