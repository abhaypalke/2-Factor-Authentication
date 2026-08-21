package com.example.authentication.dto;

import com.example.authentication.validator.PasswordMatches;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@PasswordMatches
public class ResetPasswordRequest {

    @NotBlank(message = "Token is required")
    private String token;

    @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
    private String password;

    @NotBlank(message = "Confirm Password is required")
    private String confirmPassword;
}