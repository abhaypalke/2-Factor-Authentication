package com.example.authentication.service;

import com.example.authentication.dto.ForgotPasswordRequest;
import com.example.authentication.dto.ResetPasswordRequest;

public interface PasswordResetService {

    void sendResetLink(ForgotPasswordRequest request);

    void resetPassword(ResetPasswordRequest request);
}