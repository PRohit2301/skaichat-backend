package com.skaichatbackend.settings;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
public class SettingsController {

    private final SettingsService settingsService;

    // PUT /api/settings/password
    @PutMapping("/password")
    public ResponseEntity<Void> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            Authentication authentication
    ) {
        Long userId = (Long) authentication.getPrincipal();
        settingsService.changePassword(userId, request);
        return ResponseEntity.ok().build();
    }

    // PUT /api/settings/phone
    @PutMapping("/phone")
    public ResponseEntity<Void> changePhone(
            @Valid @RequestBody ChangePhoneRequest request,
            Authentication authentication
    ) {
        Long userId = (Long) authentication.getPrincipal();
        settingsService.changePhone(userId, request);
        return ResponseEntity.ok().build();
    }

    // PUT /api/settings/theme
    @PutMapping("/theme")
    public ResponseEntity<Void> saveTheme(
            @Valid @RequestBody ThemeRequest request,
            Authentication authentication
    ) {
        Long userId = (Long) authentication.getPrincipal();
        settingsService.saveTheme(userId, request);
        return ResponseEntity.ok().build();
    }

    // DELETE /api/settings/account
    @DeleteMapping("/account")
    public ResponseEntity<Void> deleteAccount(
            @RequestBody Map<String, String> body,
            Authentication authentication
    ) {
        Long userId = (Long) authentication.getPrincipal();
        String password = body.get("password");
        String confirmation = body.get("confirmation");
        settingsService.deleteAccount(userId, password, confirmation);
        return ResponseEntity.ok().build();
    }
}
