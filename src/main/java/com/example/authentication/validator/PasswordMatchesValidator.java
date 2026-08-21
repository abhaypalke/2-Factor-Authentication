package com.example.authentication.validator;

import com.example.authentication.dto.RegisterRequest;
import com.example.authentication.dto.ResetPasswordRequest;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator
        implements ConstraintValidator<PasswordMatches, Object> {

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {

        if (value instanceof RegisterRequest request) {
            return request.getPassword().equals(request.getConfirmPassword());
        }

        if (value instanceof ResetPasswordRequest request) {
            return request.getPassword().equals(request.getConfirmPassword());
        }

        return true;
    }
}