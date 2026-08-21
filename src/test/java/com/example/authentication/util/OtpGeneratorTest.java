package com.example.authentication.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link OtpGenerator}.
 *
 * <p>The class has a private no-arg constructor (it is instantiated by Spring as a bean),
 * so reflection is used here to obtain an instance for testing without altering
 * production code visibility.</p>
 */
class OtpGeneratorTest {

    private OtpGenerator otpGenerator;

    @BeforeEach
    void setUp() throws Exception {
        Constructor<OtpGenerator> constructor = OtpGenerator.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        otpGenerator = constructor.newInstance();
    }

    @Test
    void generateOtp_shouldReturnSixDigitNumericString() {
        String otp = otpGenerator.generateOtp();

        assertThat(otp).isNotNull();
        assertThat(otp).hasSize(6);
        assertThat(otp).matches("\\d{6}");
    }

    @Test
    void generateOtp_shouldReturnDifferentValuesAcrossMultipleInvocations() {
        Set<String> generatedOtps = new HashSet<>();

        for (int i = 0; i < 50; i++) {
            generatedOtps.add(otpGenerator.generateOtp());
        }

        // With a 6-digit random space, 50 draws should very likely produce more than one
        // distinct value; this guards against a hard-coded or degenerate implementation.
        assertThat(generatedOtps.size()).isGreaterThan(1);
    }
}
