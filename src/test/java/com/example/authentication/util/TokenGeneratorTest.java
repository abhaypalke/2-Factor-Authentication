package com.example.authentication.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link TokenGenerator}.
 */
class TokenGeneratorTest {

    @Test
    void generateToken_shouldReturnParsableUuidString() {
        String token = TokenGenerator.generateToken();

        assertThat(token).isNotBlank();
        assertThatCode(() -> UUID.fromString(token)).doesNotThrowAnyException();
    }

    @Test
    void generateToken_shouldReturnUniqueValuesOnEachCall() {
        Set<String> tokens = new HashSet<>();

        for (int i = 0; i < 100; i++) {
            tokens.add(TokenGenerator.generateToken());
        }

        assertThat(tokens).hasSize(100);
    }
}
