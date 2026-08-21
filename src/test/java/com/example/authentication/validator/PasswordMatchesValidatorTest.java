package com.example.authentication.validator;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.authentication.dto.RegisterRequest;
import com.example.authentication.dto.ResetPasswordRequest;

/**
 * Unit tests for {@link PasswordMatchesValidator}.
 */
class PasswordMatchesValidatorTest {

    private PasswordMatchesValidator validator;

    @BeforeEach
    void setUp() {
        validator = new PasswordMatchesValidator();
    }

    @Test
    void isValid_shouldReturnTrue_whenRegisterRequestPasswordsMatch() {
        RegisterRequest request = new RegisterRequest();
        request.setPassword("Password123");
        request.setConfirmPassword("Password123");

        assertThat(validator.isValid(request, null)).isTrue();
    }

    @Test
    void isValid_shouldReturnFalse_whenRegisterRequestPasswordsDoNotMatch() {
        RegisterRequest request = new RegisterRequest();
        request.setPassword("Password123");
        request.setConfirmPassword("Different123");

        assertThat(validator.isValid(request, null)).isFalse();
    }

    @Test
    void isValid_shouldReturnTrue_whenResetPasswordRequestPasswordsMatch() {
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setPassword("NewPassword123");
        request.setConfirmPassword("NewPassword123");

        assertThat(validator.isValid(request, null)).isTrue();
    }

    @Test
    void isValid_shouldReturnFalse_whenResetPasswordRequestPasswordsDoNotMatch() {
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setPassword("NewPassword123");
        request.setConfirmPassword("Mismatch123");

        assertThat(validator.isValid(request, null)).isFalse();
    }

    @Test
    void isValid_shouldReturnTrue_whenValueIsUnrelatedType() {
        assertThat(validator.isValid("some other object", null)).isTrue();
    }
}
