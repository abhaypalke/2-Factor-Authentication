package com.example.authentication.util;

import org.springframework.stereotype.Component;

@Component
public class EmailUtil {

    private EmailUtil() {
    }

    public static String registrationOtpBody(String name, String otp) {
        return String.format("""
                Hello %s,

                Your registration OTP is: %s

                It is valid for 5 minutes.

                Regards,
                Authentication Team
                """, name, otp);
    }

    public static String loginOtpBody(String name, String otp) {
        return String.format("""
                Hello %s,

                Your login OTP is: %s

                It is valid for 5 minutes.

                Regards,
                Authentication Team
                """, name, otp);
    }

    public static String resetPasswordBody(String name, String link) {
        return String.format("""
                Hello %s,

                Click the link below to reset your password:

                %s

                This link is valid for 15 minutes.

                Regards,
                Authentication Team
                """, name, link);
    }
}