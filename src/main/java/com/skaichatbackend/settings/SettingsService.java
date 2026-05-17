package com.skaichatbackend.settings;

import com.skaichatbackend.conversation.ConversationRepository;
import com.skaichatbackend.contact.FriendRequestRepository;
import com.skaichatbackend.exception.ResourceNotFoundException;
import com.skaichatbackend.exception.UnauthorizedException;
import com.skaichatbackend.message.MessageRepository;
import com.skaichatbackend.user.User;
import com.skaichatbackend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SettingsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final FriendRequestRepository friendRequestRepository;

    // ── CHANGE PASSWORD ───────────────────────────────
    public void changePassword(Long userId, ChangePasswordRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // verify old password
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("Current password is incorrect");
        }

        // check new password matches confirm
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("New passwords do not match");
        }

        // hash new password and save
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    // ── CHANGE PHONE NUMBER ───────────────────────────
    public void changePhone(Long userId, ChangePhoneRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // verify password first
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("Password is incorrect");
        }

        // check new phone matches confirm
        if (!request.getNewPhone().equals(request.getConfirmPhone())) {
            throw new RuntimeException("Phone numbers do not match");
        }

        // check new phone not already taken
        if (userRepository.existsByPhoneNumber(request.getNewPhone())) {
            throw new RuntimeException("Phone number already in use");
        }

        // update phone number
        user.setPhoneNumber(request.getNewPhone());
        userRepository.save(user);
    }

    // ── SAVE THEME ────────────────────────────────────
    public void saveTheme(Long userId, ThemeRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setThemePreference(request.getThemePreference());
        user.setAccentColor(request.getAccentColor());
        userRepository.save(user);
    }

    // ── DELETE ACCOUNT (HARD DELETE) ──────────────────
    @Transactional
    public void deleteAccount(Long userId, String password, String confirmation) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // verify password
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new UnauthorizedException("Password is incorrect");
        }

        // verify confirmation word
        if (!"DELETE".equals(confirmation)) {
            throw new RuntimeException("Please type DELETE to confirm");
        }

        // Step 1 — delete all messages from MongoDB
        messageRepository.deleteBySenderIdOrReceiverId(userId, userId);

        // Step 2 — delete all friend requests from PostgreSQL
        friendRequestRepository.deleteAllByUserId(userId);

        // Step 3 — delete all conversations from PostgreSQL
        conversationRepository.deleteAllByUserId(userId);

        // Step 4 — delete user from PostgreSQL
        userRepository.deleteById(userId);
    }
}
