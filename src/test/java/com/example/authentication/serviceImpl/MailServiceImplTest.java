package com.example.authentication.serviceImpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * Unit tests for {@link MailServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
class MailServiceImplTest {

    @Mock
    private JavaMailSender mailSender;

    private MailServiceImpl mailService;

    @BeforeEach
    void setUp() {
        mailService = new MailServiceImpl(mailSender);
    }

    @Test
    void sendRegistrationOtp_shouldBuildAndSendMessageWithOtpAndName() {
        mailService.sendRegistrationOtp("john@example.com", "John Doe", "123456");

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        SimpleMailMessage message = captor.getValue();
        assertThat(message.getTo()).containsExactly("john@example.com");
        assertThat(message.getSubject()).isEqualTo("Email Verification OTP");
        assertThat(message.getText()).contains("John Doe").contains("123456");
    }

    @Test
    void sendLoginOtp_shouldBuildAndSendMessageWithOtpAndName() {
        mailService.sendLoginOtp("jane@example.com", "Jane Doe", "654321");

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        SimpleMailMessage message = captor.getValue();
        assertThat(message.getTo()).containsExactly("jane@example.com");
        assertThat(message.getSubject()).isEqualTo("Login OTP");
        assertThat(message.getText()).contains("Jane Doe").contains("654321");
    }

    @Test
    void sendPasswordResetLink_shouldBuildAndSendMessageWithLinkAndName() {
        String link = "http://localhost:8080/reset-password?token=abc-123";

        mailService.sendPasswordResetLink("jane@example.com", "Jane Doe", link);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        SimpleMailMessage message = captor.getValue();
        assertThat(message.getTo()).containsExactly("jane@example.com");
        assertThat(message.getSubject()).isEqualTo("Reset Your Password");
        assertThat(message.getText()).contains("Jane Doe").contains(link);
    }
}
