package com.example.authentication.serviceImpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.authentication.dto.LoginRequest;
import com.example.authentication.dto.RegisterRequest;
import com.example.authentication.entity.OtpPurpose;
import com.example.authentication.entity.OtpVerification;
import com.example.authentication.entity.User;
import com.example.authentication.exception.UserAlreadyExistsException;
import com.example.authentication.repository.OtpVerificationRepository;
import com.example.authentication.repository.UserRepository;
import com.example.authentication.service.MailService;
import com.example.authentication.util.OtpGenerator;

/**
 * Unit tests for {@link AuthServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private OtpVerificationRepository otpRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private MailService mailService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private OtpGenerator otpGenerator;

    private AuthServiceImpl authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        authService = new AuthServiceImpl(
                userRepository,
                otpRepository,
                passwordEncoder,
                mailService,
                authenticationManager,
                otpGenerator
        );

        registerRequest = new RegisterRequest();
        registerRequest.setName("John Doe");
        registerRequest.setEmail("john@example.com");
        registerRequest.setPassword("Password123");
        registerRequest.setConfirmPassword("Password123");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("john@example.com");
        loginRequest.setPassword("Password123");
    }

    @Test
    void register_shouldSaveUserGenerateOtpAndSendMail_whenEmailNotRegistered() {
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(1L);
            return u;
        });
        when(otpGenerator.generateOtp()).thenReturn("123456");

        authService.register(registerRequest);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getName()).isEqualTo("John Doe");
        assertThat(savedUser.getEmail()).isEqualTo("john@example.com");
        assertThat(savedUser.getPassword()).isEqualTo("encodedPassword");

        ArgumentCaptor<OtpVerification> otpCaptor = ArgumentCaptor.forClass(OtpVerification.class);
        verify(otpRepository).save(otpCaptor.capture());
        OtpVerification savedOtp = otpCaptor.getValue();
        assertThat(savedOtp.getOtp()).isEqualTo("123456");
        assertThat(savedOtp.getPurpose()).isEqualTo(OtpPurpose.REGISTER);
        assertThat(savedOtp.getUser()).isEqualTo(savedUser);

        verify(mailService).sendRegistrationOtp("john@example.com", "John Doe", "123456");
    }

    @Test
    void register_shouldThrowUserAlreadyExistsException_whenEmailAlreadyRegistered() {
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessage("Email already registered.");

        verify(userRepository, never()).save(any(User.class));
        verify(otpRepository, never()).save(any(OtpVerification.class));
        verify(mailService, never()).sendRegistrationOtp(anyString(), anyString(), anyString());
    }

    @Test
    void login_shouldAuthenticateGenerateOtpAndSendMail_whenCredentialsValid() {
        User user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setPassword("encodedPassword");

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(otpGenerator.generateOtp()).thenReturn("654321");

        authService.login(loginRequest);

        verify(authenticationManager).authenticate(any());

        ArgumentCaptor<OtpVerification> otpCaptor = ArgumentCaptor.forClass(OtpVerification.class);
        verify(otpRepository).save(otpCaptor.capture());
        assertThat(otpCaptor.getValue().getOtp()).isEqualTo("654321");
        assertThat(otpCaptor.getValue().getPurpose()).isEqualTo(OtpPurpose.LOGIN);
        assertThat(otpCaptor.getValue().getUser()).isEqualTo(user);

        verify(mailService).sendLoginOtp("john@example.com", "John Doe", "654321");
    }

    @Test
    void login_shouldThrowUsernameNotFoundException_whenUserMissingAfterAuthentication() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found");

        verify(otpRepository, never()).save(any(OtpVerification.class));
        verify(mailService, never()).sendLoginOtp(anyString(), anyString(), anyString());
    }

    @Test
    void login_shouldPropagateAuthenticationException_whenCredentialsInvalid() {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(BadCredentialsException.class);

        verify(userRepository, never()).findByEmail(anyString());
        verify(otpRepository, never()).save(any(OtpVerification.class));
    }
}
