package com.skaichatbackend.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    // JWT token returned after signup or login
    private String token;

    // user details returned after signup or login
    private Long id;
    private String username;
    private String phoneNumber;
    private String about;
    private String themePreference;
    private String accentColor;
}
