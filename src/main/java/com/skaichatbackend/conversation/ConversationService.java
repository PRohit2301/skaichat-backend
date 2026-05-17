package com.skaichatbackend.conversation;

import com.skaichatbackend.exception.ResourceNotFoundException;
import com.skaichatbackend.exception.UnauthorizedException;
import com.skaichatbackend.user.User;
import com.skaichatbackend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;

    // ── GET ALL CONVERSATIONS ─────────────────────────
    public List<ConversationResponse> getMyConversations(Long userId) {
        List<Conversation> conversations = conversationRepository
                .findAllByUserId(userId);

        return conversations.stream()
                .map(conv -> buildResponse(conv, userId))
                .collect(Collectors.toList());
    }

    // ── GET SINGLE CONVERSATION ───────────────────────
    public ConversationResponse getConversation(Long conversationId, Long userId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found"));

        // verify user is participant
        if (!conversation.getParticipantOneId().equals(userId) &&
                !conversation.getParticipantTwoId().equals(userId)) {
            throw new UnauthorizedException("Not authorized to view this conversation");
        }

        return buildResponse(conversation, userId);
    }

    // get the other user in a conversation
    public Long getOtherParticipant(Long conversationId, Long userId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found"));

        return conversation.getParticipantOneId().equals(userId)
                ? conversation.getParticipantTwoId()
                : conversation.getParticipantOneId();
    }

    // ── UPDATE LAST MESSAGE ───────────────────────────
    public void updateLastMessage(Long conversationId, String content) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found"));

        conversation.setLastMessage(content);
        conversation.setLastMessageAt(java.time.LocalDateTime.now());
        conversationRepository.save(conversation);
    }

    // ── VERIFY USER IS PARTICIPANT ────────────────────
    public boolean isParticipant(Long conversationId, Long userId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found"));

        return conversation.getParticipantOneId().equals(userId) ||
                conversation.getParticipantTwoId().equals(userId);
    }

    // ── HELPER: BUILD RESPONSE ────────────────────────
    private ConversationResponse buildResponse(Conversation conv, Long currentUserId) {
        // get the other user in conversation
        Long otherUserId = conv.getParticipantOneId().equals(currentUserId)
                ? conv.getParticipantTwoId()
                : conv.getParticipantOneId();

        User otherUser = userRepository.findById(otherUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return ConversationResponse.builder()
                .id(conv.getId())
                .otherUserId(otherUser.getId())
                .otherUsername(otherUser.getUsername())
                .otherUserPhone(otherUser.getPhoneNumber())
                .otherUserAbout(otherUser.getAbout())
                .lastMessage(conv.getLastMessage())
                .lastMessageAt(conv.getLastMessageAt())
                .createdAt(conv.getCreatedAt())
                .build();
    }
}
