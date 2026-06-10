package com.tommy.identity.application.dto;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PublicProfileResponse {
    private String username;
    private String fullName;
    private String avatarUrl;
    private String bio;
    private String country;
}
