package com.skaichatbackend.user;

import com.skaichatbackend.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // ── GET CURRENT USER PROFILE ──────────────────────
    public UserResponse getMyProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return UserResponse.fromUser(user);
    }

    // ── UPDATE PROFILE ────────────────────────────────
    public UserResponse updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // update only allowed fields
        user.setUsername(request.getUsername());
        user.setAbout(request.getAbout());

        User updatedUser = userRepository.save(user);
        return UserResponse.fromUser(updatedUser);
    }

    // ── SEARCH USERS ──────────────────────────────────
    public List<UserResponse> searchUsers(String query, Long currentUserId) {
        List<User> users = userRepository
                .findByUsernameContainingIgnoreCaseOrPhoneNumberContaining(query, query);

        // exclude current user from search results
        return users.stream()
                .filter(user -> !user.getId().equals(currentUserId))
                .filter(User::getIsActive)
                .map(UserResponse::fromUser)
                .collect(Collectors.toList());
    }
}
