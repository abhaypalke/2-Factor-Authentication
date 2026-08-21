package com.example.authentication.serviceImpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.authentication.dto.OtpRequest;
import com.example.authentication.entity.OtpPurpose;
import com.example.authentication.entity.OtpVerification;
import com.example.authentication.entity.User;
import com.example.authentication.exception.InvalidOtpException;
import com.example.authentication.exception.ResourceNotFoundException;
import com.example.authentication.repository.OtpVerificationRepository;
import com.example.authentication.repository.UserRepository;
import com.example.authentication.security.CustomUserDetailsService;
import com.example.authentication.service.MailService;
import com.example.authentication.util.OtpGenerator;

/**
 * Unit tests for {@link OtpServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
class OtpServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private OtpVerificationRepository otpVerificationRepository;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private MailService mailService;

    @Mock
    private OtpGenerator otpGenerator;

    private OtpServiceImpl otpService;

    private User user;

    @BeforeEach
    void setUp() {
        otpService = new OtpServiceImpl(
                userRepository,
                otpVerificationRepository,
                userDetailsService,
                mailService,
                otpGenerator
        );

        user = new User();
        user.setId(1L);
        user.setName("Jane Doe");
        user.setEmail("jane@example.com");
        user.setPassword("encodedPassword");
        user.setEnabled(false);
        user.setEmailVerified(false);
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    private OtpVerification buildVerification(String otp, OtpPurpose purpose, LocalDateTime expiry) {
        OtpVerification verification = new OtpVerification();
        verification.setOtp(otp);
        verification.setPurpose(purpose);
        verification.setExpiryTime(expiry);
        verification.setUser(user);
        verification.setVerified(false);
        return verification;
    }

    @Test
    void verifyOtp_shouldEnableUser_whenPurposeIsRegisterAndOtpValid() {
        OtpRequest request = new OtpRequest();
        request.setEmail("jane@example.com");
        request.setOtp("123456");
        request.setPurpose(OtpPurpose.REGISTER);

        OtpVerification verification = buildVerification(
                "123456", OtpPurpose.REGISTER, LocalDateTime.now().plusMinutes(5));

        when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(user));
        when(otpVerificationRepository.findByUserAndPurposeAndVerifiedFalse(user, OtpPurpose.REGISTER))
                .thenReturn(Optional.of(verification));

        otpService.verifyOtp(request);

        assertThat(verification.getVerified()).isTrue();
        verify(otpVerificationRepository).save(verification);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue().getEnabled()).isTrue();
        assertThat(userCaptor.getValue().getEmailVerified()).isTrue();

        verify(userDetailsService, never()).loadUserByUsername(anyString());
    }

    @Test
    void verifyOtp_shouldSetAuthentication_whenPurposeIsLoginAndOtpValid() {
        OtpRequest request = new OtpRequest();
        request.setEmail("jane@example.com");
        request.setOtp("654321");
        request.setPurpose(OtpPurpose.LOGIN);

        OtpVerification verification = buildVerification(
                "654321", OtpPurpose.LOGIN, LocalDateTime.now().plusMinutes(5));

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("jane@example.com")
                .password("encodedPassword")
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                .build();

        when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(user));
        when(otpVerificationRepository.findByUserAndPurposeAndVerifiedFalse(user, OtpPurpose.LOGIN))
                .thenReturn(Optional.of(verification));
        when(userDetailsService.loadUserByUsername("jane@example.com")).thenReturn(userDetails);

        otpService.verifyOtp(request);

        assertThat(verification.getVerified()).isTrue();
        verify(otpVerificationRepository).save(verification);
        verify(userRepository, never()).save(any(User.class));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNotNull();
        assertThat(authentication.isAuthenticated()).isTrue();
        assertThat(authentication.getPrincipal()).isEqualTo(userDetails);
    }

    @Test
    void verifyOtp_shouldThrowResourceNotFoundException_whenUserNotFound() {
        OtpRequest request = new OtpRequest();
        request.setEmail("missing@example.com");
        request.setOtp("123456");
        request.setPurpose(OtpPurpose.REGISTER);

        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> otpService.verifyOtp(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found");

        verify(otpVerificationRepository, never()).save(any(OtpVerification.class));
    }

    @Test
    void verifyOtp_shouldThrowInvalidOtpException_whenNoActiveOtpFound() {
        OtpRequest request = new OtpRequest();
        request.setEmail("jane@example.com");
        request.setOtp("123456");
        request.setPurpose(OtpPurpose.REGISTER);

        when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(user));
        when(otpVerificationRepository.findByUserAndPurposeAndVerifiedFalse(user, OtpPurpose.REGISTER))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> otpService.verifyOtp(request))
                .isInstanceOf(InvalidOtpException.class)
                .hasMessage("OTP not found");
    }

    @Test
    void verifyOtp_shouldThrowInvalidOtpException_whenOtpExpired() {
        OtpRequest request = new OtpRequest();
        request.setEmail("jane@example.com");
        request.setOtp("123456");
        request.setPurpose(OtpPurpose.REGISTER);

        OtpVerification verification = buildVerification(
                "123456", OtpPurpose.REGISTER, LocalDateTime.now().minusMinutes(1));

        when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(user));
        when(otpVerificationRepository.findByUserAndPurposeAndVerifiedFalse(user, OtpPurpose.REGISTER))
                .thenReturn(Optional.of(verification));

        assertThatThrownBy(() -> otpService.verifyOtp(request))
                .isInstanceOf(InvalidOtpException.class)
                .hasMessage("OTP expired");

        verify(otpVerificationRepository, never()).save(any(OtpVerification.class));
    }

    @Test
    void verifyOtp_shouldThrowInvalidOtpException_whenOtpDoesNotMatch() {
        OtpRequest request = new OtpRequest();
        request.setEmail("jane@example.com");
        request.setOtp("000000");
        request.setPurpose(OtpPurpose.REGISTER);

        OtpVerification verification = buildVerification(
                "123456", OtpPurpose.REGISTER, LocalDateTime.now().plusMinutes(5));

        when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(user));
        when(otpVerificationRepository.findByUserAndPurposeAndVerifiedFalse(user, OtpPurpose.REGISTER))
                .thenReturn(Optional.of(verification));

        assertThatThrownBy(() -> otpService.verifyOtp(request))
                .isInstanceOf(InvalidOtpException.class)
                .hasMessage("Invalid OTP");

        verify(otpVerificationRepository, never()).save(any(OtpVerification.class));
    }

    @Test
    void resendOtp_shouldRegenerateOtpAndSendRegistrationMail_whenPurposeIsRegister() {
        OtpVerification verification = buildVerification(
                "111111", OtpPurpose.REGISTER, LocalDateTime.now().minusMinutes(1));

        when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(user));
        when(otpVerificationRepository.findByUserAndPurposeAndVerifiedFalse(user, OtpPurpose.REGISTER))
                .thenReturn(Optional.of(verification));
        when(otpGenerator.generateOtp()).thenReturn("222222");

        otpService.resendOtp("jane@example.com", OtpPurpose.REGISTER);

        assertThat(verification.getOtp()).isEqualTo("222222");
        assertThat(verification.getVerified()).isFalse();
        assertThat(verification.getExpiryTime()).isAfter(LocalDateTime.now());
        verify(otpVerificationRepository).save(verification);
        verify(mailService).sendRegistrationOtp("jane@example.com", "Jane Doe", "222222");
        verify(mailService, never()).sendLoginOtp(anyString(), anyString(), anyString());
    }

    @Test
    void resendOtp_shouldRegenerateOtpAndSendLoginMail_whenPurposeIsLogin() {
        OtpVerification verification = buildVerification(
                "111111", OtpPurpose.LOGIN, LocalDateTime.now().minusMinutes(1));

        when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(user));
        when(otpVerificationRepository.findByUserAndPurposeAndVerifiedFalse(user, OtpPurpose.LOGIN))
                .thenReturn(Optional.of(verification));
        when(otpGenerator.generateOtp()).thenReturn("333333");

        otpService.resendOtp("jane@example.com", OtpPurpose.LOGIN);

        assertThat(verification.getOtp()).isEqualTo("333333");
        verify(mailService).sendLoginOtp("jane@example.com", "Jane Doe", "333333");
        verify(mailService, never()).sendRegistrationOtp(anyString(), anyString(), anyString());
    }

    @Test
    void resendOtp_shouldThrowResourceNotFoundException_whenUserNotFound() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> otpService.resendOtp("missing@example.com", OtpPurpose.REGISTER))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found");

        verify(otpVerificationRepository, never()).save(any(OtpVerification.class));
    }

    @Test
    void resendOtp_shouldThrowInvalidOtpException_whenNoActiveOtpFound() {
        when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(user));
        when(otpVerificationRepository.findByUserAndPurposeAndVerifiedFalse(user, OtpPurpose.REGISTER))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> otpService.resendOtp("jane@example.com", OtpPurpose.REGISTER))
                .isInstanceOf(InvalidOtpException.class)
                .hasMessage("No active OTP found");
    }
}
