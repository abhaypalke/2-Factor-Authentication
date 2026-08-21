package com.example.authentication.serviceImpl;

import java.time.LocalDateTime;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.authentication.dto.LoginRequest;
import com.example.authentication.dto.RegisterRequest;
import com.example.authentication.entity.OtpPurpose;
import com.example.authentication.entity.OtpVerification;
import com.example.authentication.entity.User;
import com.example.authentication.exception.UserAlreadyExistsException;
import com.example.authentication.repository.OtpVerificationRepository;
import com.example.authentication.repository.UserRepository;
import com.example.authentication.service.AuthService;
import com.example.authentication.service.MailService;
import com.example.authentication.util.OtpGenerator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final OtpVerificationRepository otpRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final AuthenticationManager authenticationManager;
    private final OtpGenerator otpGenerator;

    @Override
    public void register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email already registered.");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        user = userRepository.save(user);

        String otp = otpGenerator.generateOtp();

        OtpVerification verification = new OtpVerification();
        verification.setOtp(otp);
        verification.setPurpose(OtpPurpose.REGISTER);
        verification.setExpiryTime(LocalDateTime.now().plusMinutes(5));
        verification.setUser(user);
         
        otpRepository.save(verification);

        mailService.sendRegistrationOtp(
                user.getEmail(),
                user.getName(),
                otp
        );

    }

    @Override
    public void login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        
        String otp = otpGenerator.generateOtp();

        OtpVerification verification = new OtpVerification();
        verification.setOtp(otp);
        verification.setPurpose(OtpPurpose.LOGIN);
        verification.setExpiryTime(LocalDateTime.now().plusMinutes(5));
        verification.setUser(user);

        otpRepository.save(verification);

        mailService.sendLoginOtp(
                user.getEmail(),
                user.getName(),
                otp
        );
    }
}