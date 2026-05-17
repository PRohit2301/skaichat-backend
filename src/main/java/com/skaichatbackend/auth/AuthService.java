package com.skaichatbackend.auth;

import com.skaichatbackend.user.User;
import com.skaichatbackend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    // ── SIGNUP ────────────────────────────────────────
    public AuthResponse signup(SignupRequest request) {

        // check if phone number already exists
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new RuntimeException("Phone number already registered");
        }

        // hash the password — never store plain text
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        // build user entity
        User user = User.builder()
                .username(request.getUsername())
                .phoneNumber(request.getPhoneNumber())
                .passwordHash(hashedPassword)
                .about("Hey there, I am using Skai Chat")
                .themePreference("light")
                .accentColor("#007AFF")
                .isActive(true)
                .build();

        // save to PostgreSQL
        User savedUser = userRepository.save(user);

        // generate JWT token
        String token = jwtService.generateToken(savedUser.getId(), savedUser.getPhoneNumber());

        // return token + user details
        return AuthResponse.builder()
                .token(token)
                .id(savedUser.getId())
                .username(savedUser.getUsername())
                .phoneNumber(savedUser.getPhoneNumber())
                .about(savedUser.getAbout())
                .themePreference(savedUser.getThemePreference())
                .accentColor(savedUser.getAccentColor())
                .build();
    }

    // ── LOGIN ─────────────────────────────────────────
    public AuthResponse login(LoginRequest request) {

        // find user by phone number
        User user = userRepository.findByPhoneNumber(request.getPhoneNumber())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        // check if account is active
        if (!user.getIsActive()) {
            throw new RuntimeException("Account has been deleted");
        }

        // verify password — BCrypt compare
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            // same error message — never reveal which field is wrong
            throw new RuntimeException("Invalid credentials");
        }

        // generate JWT token
        String token = jwtService.generateToken(user.getId(), user.getPhoneNumber());

        // return token + user details
        return AuthResponse.builder()
                .token(token)
                .id(user.getId())
                .username(user.getUsername())
                .phoneNumber(user.getPhoneNumber())
                .about(user.getAbout())
                .themePreference(user.getThemePreference())
                .accentColor(user.getAccentColor())
                .build();
    }
}
