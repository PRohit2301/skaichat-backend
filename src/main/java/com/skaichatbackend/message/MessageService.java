package com.skaichatbackend.message;

import com.skaichatbackend.conversation.ConversationService;
import com.skaichatbackend.exception.ResourceNotFoundException;
import com.skaichatbackend.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final ConversationService conversationService;

    // ── SEND MESSAGE ──────────────────────────────────
    public MessageResponse sendMessage(Long senderId, SendMessageRequest request) {

        // verify sender is participant in conversation
        if (!conversationService.isParticipant(request.getConversationId(), senderId)) {
            throw new UnauthorizedException("Not authorized to send message here");
        }

        // get the other participant as receiverId
        Long receiverId = conversationService
                .getOtherParticipant(request.getConversationId(), senderId);

        // build message document
        Message message = Message.builder()
                .conversationId(request.getConversationId())
                .senderId(senderId)
                .receiverId(receiverId)
                .content(request.getContent())
                .isDelivered(false)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        // save to MongoDB
        Message savedMessage = messageRepository.save(message);

        // update last message in conversation
        conversationService.updateLastMessage(
                request.getConversationId(),
                request.getContent()
        );

        return MessageResponse.fromMessage(savedMessage);
    }

    // ── FETCH ALL MESSAGES ────────────────────────────
    public List<MessageResponse> getMessages(Long conversationId, Long userId) {

        // verify user is participant
        if (!conversationService.isParticipant(conversationId, userId)) {
            throw new UnauthorizedException("Not authorized to view these messages");
        }

        List<Message> messages = messageRepository
                .findByConversationIdOrderByCreatedAtAsc(conversationId);

        return messages.stream()
                .map(MessageResponse::fromMessage)
                .collect(Collectors.toList());
    }

    // ── MARK MESSAGES AS READ ─────────────────────────
    public void markAsRead(Long conversationId, Long userId) {

        // get all unread messages for this user in this conversation
        List<Message> unreadMessages = messageRepository
                .findByConversationIdAndReceiverIdAndIsReadFalse(
                        conversationId, userId
                );

        // mark each as read — ✓✓ blue
        unreadMessages.forEach(message -> {
            message.setIsRead(true);
            message.setIsDelivered(true);
        });

        // save all updated messages
        messageRepository.saveAll(unreadMessages);
    }
}