package com.skaichatbackend.conversation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationResponse {

    private Long id;

    // the other user in the conversation
    private Long otherUserId;
    private String otherUsername;
    private String otherUserPhone;
    private String otherUserAbout;

    // last message preview for chat list
    private String lastMessage;
    private LocalDateTime lastMessageAt;
    private LocalDateTime createdAt;
}
