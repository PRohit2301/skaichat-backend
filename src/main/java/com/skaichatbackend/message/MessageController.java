package com.skaichatbackend.message;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    // POST /api/messages/send
    @PostMapping("/send")
    public ResponseEntity<MessageResponse> sendMessage(
            @Valid @RequestBody SendMessageRequest request,
            Authentication authentication
    ) {
        Long senderId = (Long) authentication.getPrincipal();
        MessageResponse response = messageService.sendMessage(senderId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET /api/messages/:convId
    @GetMapping("/{convId}")
    public ResponseEntity<List<MessageResponse>> getMessages(
            @PathVariable Long convId,
            Authentication authentication
    ) {
        Long userId = (Long) authentication.getPrincipal();
        List<MessageResponse> messages = messageService.getMessages(convId, userId);
        return ResponseEntity.ok(messages);
    }

    // PUT /api/messages/read/:convId
    @PutMapping("/read/{convId}")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long convId,
            Authentication authentication
    ) {
        Long userId = (Long) authentication.getPrincipal();
        messageService.markAsRead(convId, userId);
        return ResponseEntity.ok().build();
    }
}
