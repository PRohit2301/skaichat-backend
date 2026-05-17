package com.skaichatbackend.settings;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ChangePhoneRequest {

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "New phone number is required")
    @Pattern(regexp = "^[0-9]{10,15}$", message = "Phone number must be 10-15 digits")
    private String newPhone;

    @NotBlank(message = "Confirm phone number is required")
    private String confirmPhone;
}
