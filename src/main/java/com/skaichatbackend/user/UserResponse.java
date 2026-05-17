package com.skaichatbackend.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String username;
    private String phoneNumber;
    private String about;
    private String themePreference;
    private String accentColor;
    private LocalDateTime createdAt;

    // convert User entity to UserResponse DTO
    public static UserResponse fromUser(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .phoneNumber(user.getPhoneNumber())
                .about(user.getAbout())
                .themePreference(user.getThemePreference())
                .accentColor(user.getAccentColor())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
