package com.example.authentication.service;

public interface MailService {

    void sendRegistrationOtp(String to, String name, String otp);

    void sendLoginOtp(String to, String name, String otp);

    void sendPasswordResetLink(String to, String name, String link);
}