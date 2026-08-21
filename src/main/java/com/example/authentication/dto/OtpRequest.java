package com.example.authentication.dto;

import com.example.authentication.entity.OtpPurpose;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OtpRequest {

    @Email(message = "Invalid email")
    @NotBlank(message = "Email is required")
    private String email;

    @Size(min = 6, max = 6, message = "OTP must be 6 digits")
    private String otp;
    
    private OtpPurpose purpose;
}