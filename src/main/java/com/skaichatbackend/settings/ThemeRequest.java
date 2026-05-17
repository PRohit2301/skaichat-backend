package com.skaichatbackend.settings;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ThemeRequest {

    @NotBlank(message = "Theme is required")
    @Pattern(
            regexp = "^(light|dark|custom)$",
            message = "Theme must be light, dark or custom"
    )
    private String themePreference;

    @NotBlank(message = "Accent color is required")
    @Pattern(
            regexp = "^#([A-Fa-f0-9]{6})$",
            message = "Accent color must be a valid hex color e.g. #007AFF"
    )
    private String accentColor;
}
