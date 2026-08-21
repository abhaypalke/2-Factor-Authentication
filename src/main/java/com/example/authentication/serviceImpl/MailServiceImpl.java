package com.example.authentication.serviceImpl;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.example.authentication.service.MailService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendRegistrationOtp(String to, String name, String otp) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Email Verification OTP");
        message.setText("""
                Hello %s,

                Your registration OTP is: %s

                This OTP is valid for 5 minutes.

                Regards,
                Authentication Team
                """.formatted(name, otp));

        mailSender.send(message);
    }

    @Override
    public void sendLoginOtp(String to, String name, String otp) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Login OTP");
        message.setText("""
                Hello %s,

                Your login OTP is: %s

                This OTP is valid for 5 minutes.

                Regards,
                Authentication Team
                """.formatted(name, otp));

        mailSender.send(message);
    }

    @Override
    public void sendPasswordResetLink(String to, String name, String link) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Reset Your Password");
        message.setText("""
                Hello %s,

                Click the link below to reset your password:

                %s

                This link is valid for 15 minutes.

                Regards,
                Authentication Team
                """.formatted(name, link));

        mailSender.send(message);
    }
}