package com.example.authentication.serviceImpl;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.authentication.dto.ForgotPasswordRequest;
import com.example.authentication.dto.ResetPasswordRequest;
import com.example.authentication.entity.PasswordResetToken;
import com.example.authentication.entity.User;
import com.example.authentication.exception.InvalidTokenException;
import com.example.authentication.exception.ResourceNotFoundException;
import com.example.authentication.repository.PasswordResetTokenRepository;
import com.example.authentication.repository.UserRepository;
import com.example.authentication.service.MailService;
import com.example.authentication.service.PasswordResetService;
import com.example.authentication.util.TokenGenerator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;

    @Override
    public void sendResetLink(ForgotPasswordRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        String token = TokenGenerator.generateToken();

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setExpiryTime(LocalDateTime.now().plusMinutes(15));
        resetToken.setUser(user);

        tokenRepository.save(resetToken);

        String link =
                "http://localhost:8080/reset-password?token=" + token;

        mailService.sendPasswordResetLink(
                user.getEmail(),
                user.getName(),
                link
        );
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {

        PasswordResetToken token = tokenRepository.findByToken(request.getToken())
                .orElseThrow(() ->
                        new InvalidTokenException("Invalid token"));

        if (token.getUsed()) {
            throw new InvalidTokenException("Token already used");
        }

        if (token.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException("Token expired");
        }

        User user = token.getUser();

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        token.setUsed(true);
        tokenRepository.save(token);
    }
}