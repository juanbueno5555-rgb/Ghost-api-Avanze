package com.techcup.auth.service;

import com.techcup.auth.dto.request.LoginRequest;
import com.techcup.auth.dto.request.RegisterRequest;
import com.techcup.auth.dto.request.VerifyOtpRequest;
import com.techcup.auth.dto.response.AuthResponse;
import com.techcup.auth.model.User;
import com.techcup.auth.model.UserType;
import com.techcup.auth.repository.UserRepository;
import com.techcup.common.exception.ApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private OtpService otpService;
    @Mock
    private TokenService tokenService;

    private AuthService authService;
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        authService = new AuthService(userRepository, passwordEncoder, otpService, tokenService);
    }

    @Test
    void shouldRegisterUserAndSendOtp() {
        RegisterRequest request = new RegisterRequest(
                "test@example.com", "Pass1234", "Test User",
                "DOC123", "123456789", UserType.STUDENT, null
        );

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByDocumentId(anyString())).thenReturn(false);
        when(otpService.generateAndSendOtp(anyString()))
                .thenReturn(new OtpService.OtpSendResult(UUID.randomUUID(), 10));

        OtpService.OtpSendResult result = authService.register(request);

        assertNotNull(result);
        verify(userRepository).save(any(User.class));
        verify(otpService).generateAndSendOtp("test@example.com");
    }

    @Test
    void shouldRejectDuplicateEmail() {
        RegisterRequest request = new RegisterRequest(
                "existing@test.com", "Pass1234", "Test",
                "DOC456", "123456789", UserType.STUDENT, null
        );

        when(userRepository.existsByEmail("existing@test.com")).thenReturn(true);

        assertThrows(ApiException.class, () -> authService.register(request));
    }

    @Test
    void shouldLoginVerifiedUser() {
        String email = "test@example.com";
        String password = "Pass1234";
        User user = User.builder()
                .id(UUID.randomUUID())
                .email(email)
                .password(passwordEncoder.encode(password))
                .verified(true)
                .userType(UserType.STUDENT)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(tokenService.generateTokenPair(any(User.class)))
                .thenReturn(new TokenService.TokenPair("access", "refresh", 900));

        LoginRequest request = new LoginRequest(email, password);
        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("access", response.getAccessToken());
    }

    @Test
    void shouldRejectUnverifiedUserLogin() {
        String email = "unverified@test.com";
        User user = User.builder()
                .id(UUID.randomUUID())
                .email(email)
                .password(passwordEncoder.encode("Pass1234"))
                .verified(false)
                .userType(UserType.STUDENT)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        LoginRequest request = new LoginRequest(email, "Pass1234");
        assertThrows(ApiException.class, () -> authService.login(request));
    }

    @Test
    void shouldRejectInvalidPassword() {
        String email = "test@example.com";
        User user = User.builder()
                .id(UUID.randomUUID())
                .email(email)
                .password(passwordEncoder.encode("CorrectPass1"))
                .verified(true)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        LoginRequest request = new LoginRequest(email, "WrongPass1");
        assertThrows(ApiException.class, () -> authService.login(request));
    }
}
