package com.example.authentication.serviceImpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.authentication.dto.ForgotPasswordRequest;
import com.example.authentication.dto.ResetPasswordRequest;
import com.example.authentication.entity.PasswordResetToken;
import com.example.authentication.entity.User;
import com.example.authentication.exception.InvalidTokenException;
import com.example.authentication.exception.ResourceNotFoundException;
import com.example.authentication.repository.PasswordResetTokenRepository;
import com.example.authentication.repository.UserRepository;
import com.example.authentication.service.MailService;

/**
 * Unit tests for {@link PasswordResetServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
class PasswordResetServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordResetTokenRepository tokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private MailService mailService;

    private PasswordResetServiceImpl passwordResetService;

    private User user;

    @BeforeEach
    void setUp() {
        passwordResetService = new PasswordResetServiceImpl(
                userRepository,
                tokenRepository,
                passwordEncoder,
                mailService
        );

        user = new User();
        user.setId(1L);
        user.setName("Jane Doe");
        user.setEmail("jane@example.com");
        user.setPassword("oldEncodedPassword");
    }

    @Test
    void sendResetLink_shouldSaveTokenAndSendMail_whenUserExists() {
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail("jane@example.com");

        when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(user));

        passwordResetService.sendResetLink(request);

        ArgumentCaptor<PasswordResetToken> tokenCaptor = ArgumentCaptor.forClass(PasswordResetToken.class);
        verify(tokenRepository).save(tokenCaptor.capture());
        PasswordResetToken savedToken = tokenCaptor.getValue();
        assertThat(savedToken.getToken()).isNotBlank();
        assertThat(savedToken.getUser()).isEqualTo(user);
        assertThat(savedToken.getExpiryTime()).isAfter(LocalDateTime.now());

        ArgumentCaptor<String> linkCaptor = ArgumentCaptor.forClass(String.class);
        verify(mailService).sendPasswordResetLink(
                org.mockito.ArgumentMatchers.eq("jane@example.com"),
                org.mockito.ArgumentMatchers.eq("Jane Doe"),
                linkCaptor.capture());
        assertThat(linkCaptor.getValue()).contains(savedToken.getToken());
    }

    @Test
    void sendResetLink_shouldThrowResourceNotFoundException_whenUserMissing() {
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail("missing@example.com");

        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> passwordResetService.sendResetLink(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found");

        verify(tokenRepository, never()).save(any(PasswordResetToken.class));
        verify(mailService, never()).sendPasswordResetLink(anyString(), anyString(), anyString());
    }

    private PasswordResetToken buildToken(String tokenValue, boolean used, LocalDateTime expiry) {
        PasswordResetToken token = new PasswordResetToken();
        token.setToken(tokenValue);
        token.setUsed(used);
        token.setExpiryTime(expiry);
        token.setUser(user);
        return token;
    }

    @Test
    void resetPassword_shouldUpdatePasswordAndMarkTokenUsed_whenTokenValid() {
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setToken("valid-token");
        request.setPassword("NewPassword123");
        request.setConfirmPassword("NewPassword123");

        PasswordResetToken token = buildToken("valid-token", false, LocalDateTime.now().plusMinutes(10));

        when(tokenRepository.findByToken("valid-token")).thenReturn(Optional.of(token));
        when(passwordEncoder.encode("NewPassword123")).thenReturn("newEncodedPassword");

        passwordResetService.resetPassword(request);

        assertThat(user.getPassword()).isEqualTo("newEncodedPassword");
        assertThat(token.getUsed()).isTrue();
        verify(userRepository).save(user);
        verify(tokenRepository).save(token);
    }

    @Test
    void resetPassword_shouldThrowInvalidTokenException_whenTokenNotFound() {
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setToken("unknown-token");
        request.setPassword("NewPassword123");
        request.setConfirmPassword("NewPassword123");

        when(tokenRepository.findByToken("unknown-token")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> passwordResetService.resetPassword(request))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessage("Invalid token");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void resetPassword_shouldThrowInvalidTokenException_whenTokenAlreadyUsed() {
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setToken("used-token");
        request.setPassword("NewPassword123");
        request.setConfirmPassword("NewPassword123");

        PasswordResetToken token = buildToken("used-token", true, LocalDateTime.now().plusMinutes(10));

        when(tokenRepository.findByToken("used-token")).thenReturn(Optional.of(token));

        assertThatThrownBy(() -> passwordResetService.resetPassword(request))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessage("Token already used");

        verify(userRepository, never()).save(any(User.class));
        verify(tokenRepository, never()).save(any(PasswordResetToken.class));
    }

    @Test
    void resetPassword_shouldThrowInvalidTokenException_whenTokenExpired() {
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setToken("expired-token");
        request.setPassword("NewPassword123");
        request.setConfirmPassword("NewPassword123");

        PasswordResetToken token = buildToken("expired-token", false, LocalDateTime.now().minusMinutes(1));

        when(tokenRepository.findByToken("expired-token")).thenReturn(Optional.of(token));

        assertThatThrownBy(() -> passwordResetService.resetPassword(request))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessage("Token expired");

        verify(userRepository, never()).save(any(User.class));
        verify(tokenRepository, never()).save(any(PasswordResetToken.class));
    }
}
