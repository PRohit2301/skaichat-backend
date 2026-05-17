package com.skaichatbackend.conversation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conversations")
@RequiredArgsConstructor
public class ConversationController {

    private final ConversationService conversationService;

    // GET /api/conversations
    @GetMapping
    public ResponseEntity<List<ConversationResponse>> getMyConversations(
            Authentication authentication
    ) {
        Long userId = (Long) authentication.getPrincipal();
        List<ConversationResponse> conversations = conversationService
                .getMyConversations(userId);
        return ResponseEntity.ok(conversations);
    }

    // GET /api/conversations/:id
    @GetMapping("/{id}")
    public ResponseEntity<ConversationResponse> getConversation(
            @PathVariable Long id,
            Authentication authentication
    ) {
        Long userId = (Long) authentication.getPrincipal();
        ConversationResponse response = conversationService
                .getConversation(id, userId);
        return ResponseEntity.ok(response);
    }
}
