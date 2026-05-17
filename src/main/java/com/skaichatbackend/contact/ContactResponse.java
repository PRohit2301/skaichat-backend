package com.skaichatbackend.contact;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactResponse {

    private Long requestId;
    private Long userId;
    private String username;
    private String phoneNumber;
    private String about;
    private String status;
    private LocalDateTime createdAt;
}
