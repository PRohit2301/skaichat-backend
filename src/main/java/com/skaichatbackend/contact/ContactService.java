package com.skaichatbackend.contact;

import com.skaichatbackend.conversation.Conversation;
import com.skaichatbackend.conversation.ConversationRepository;
import com.skaichatbackend.exception.ResourceNotFoundException;
import com.skaichatbackend.exception.UnauthorizedException;
import com.skaichatbackend.user.User;
import com.skaichatbackend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContactService {

    private final FriendRequestRepository friendRequestRepository;
    private final UserRepository userRepository;
    private final ConversationRepository conversationRepository;

    // ── SEND FRIEND REQUEST ───────────────────────────
    public ContactResponse sendRequest(Long senderId, Long receiverId) {

        // cannot send request to yourself
        if (senderId.equals(receiverId)) {
            throw new RuntimeException("Cannot send request to yourself");
        }

        // check receiver exists
        userRepository.findById(receiverId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // check if already friends
        if (friendRequestRepository.areAlreadyFriends(senderId, receiverId)) {
            throw new RuntimeException("Already friends");
        }

        // check if request already exists
        if (friendRequestRepository.findBySenderIdAndReceiverId(senderId, receiverId).isPresent()) {
            throw new RuntimeException("Friend request already sent");
        }

        // save friend request
        FriendRequest request = FriendRequest.builder()
                .senderId(senderId)
                .receiverId(receiverId)
                .status("pending")
                .build();

        FriendRequest saved = friendRequestRepository.save(request);

        return buildContactResponse(saved, receiverId);
    }

    // ── ACCEPT FRIEND REQUEST ─────────────────────────
    @Transactional
    public ContactResponse acceptRequest(Long requestId, Long currentUserId) {

        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));

        // only receiver can accept
        if (!request.getReceiverId().equals(currentUserId)) {
            throw new UnauthorizedException("Not authorized to accept this request");
        }

        // update status to accepted
        request.setStatus("accepted");
        friendRequestRepository.save(request);

        // create conversation between the two users
        Conversation conversation = Conversation.builder()
                .participantOneId(request.getSenderId())
                .participantTwoId(request.getReceiverId())
                .build();
        conversationRepository.save(conversation);

        return buildContactResponse(request, request.getSenderId());
    }

    // ── DECLINE / CANCEL REQUEST ──────────────────────
    public void declineRequest(Long requestId, Long currentUserId) {

        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));

        // only sender or receiver can decline/cancel
        if (!request.getReceiverId().equals(currentUserId) &&
                !request.getSenderId().equals(currentUserId)) {
            throw new UnauthorizedException("Not authorized");
        }

        friendRequestRepository.delete(request);
    }

    // ── REMOVE CONTACT ────────────────────────────────
    public void removeContact(Long requestId, Long currentUserId) {

        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found"));

        // only participants can remove
        if (!request.getSenderId().equals(currentUserId) &&
                !request.getReceiverId().equals(currentUserId)) {
            throw new UnauthorizedException("Not authorized");
        }

        friendRequestRepository.delete(request);
    }

    // ── GET MY CONTACTS ───────────────────────────────
    public List<ContactResponse> getMyContacts(Long userId) {
        List<FriendRequest> accepted = friendRequestRepository
                .findAcceptedContactsByUserId(userId);

        return accepted.stream()
                .map(req -> {
                    Long otherUserId = req.getSenderId().equals(userId)
                            ? req.getReceiverId()
                            : req.getSenderId();
                    return buildContactResponse(req, otherUserId);
                })
                .collect(Collectors.toList());
    }

    // ── GET PENDING REQUESTS ──────────────────────────
    public List<ContactResponse> getPendingRequests(Long userId) {
        List<FriendRequest> incoming = friendRequestRepository
                .findByReceiverIdAndStatus(userId, "pending");

        return incoming.stream()
                .map(req -> buildContactResponse(req, req.getSenderId()))
                .collect(Collectors.toList());
    }

    // ── HELPER: BUILD RESPONSE ────────────────────────
    private ContactResponse buildContactResponse(FriendRequest request, Long otherUserId) {
        User otherUser = userRepository.findById(otherUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return ContactResponse.builder()
                .requestId(request.getId())
                .userId(otherUser.getId())
                .username(otherUser.getUsername())
                .phoneNumber(otherUser.getPhoneNumber())
                .about(otherUser.getAbout())
                .status(request.getStatus())
                .createdAt(request.getCreatedAt())
                .build();
    }
}
