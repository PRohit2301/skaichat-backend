package com.skaichatbackend.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "messages")
@CompoundIndex(def = "{'conversationId': 1, 'createdAt': 1}")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    @Id
    private String id;

    // references PostgreSQL conversations.id
    @Field("conversation_id")
    private Long conversationId;

    // references PostgreSQL users.id
    @Field("sender_id")
    private Long senderId;

    // references PostgreSQL users.id
    @Field("receiver_id")
    private Long receiverId;

    @Field("content")
    private String content;

    // ✓ grey — delivered to receiver device
    @Field("is_delivered")
    private Boolean isDelivered = false;

    // ✓✓ blue — receiver has read the message
    @Field("is_read")
    private Boolean isRead = false;

    @Field("created_at")
    private LocalDateTime createdAt;

    public void preSave() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
