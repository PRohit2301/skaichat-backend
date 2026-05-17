package com.skaichatbackend.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignupRequest {

    @NotBlank(message = "Username is required")
    @Size(min = 2, max = 50, message = "Username must be between 2 and 50 characters")
    private String username;

    @NotBlank(message = "Phone number is required")
    //@Pattern(regexp = "^[0-9]{10,15}$", message = "Phone number must be 10-15 digits")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits")

    private String phoneNumber;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%!&*^]).{8,}$",
            message = "Password must contain uppercase, lowercase, number and special character"
    )
    private String password;
}
