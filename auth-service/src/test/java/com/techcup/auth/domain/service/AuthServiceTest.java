package com.techcup.auth.domain.service;

import com.techcup.auth.domain.model.*;
import com.techcup.auth.domain.port.input.AuthUseCase;
import com.techcup.auth.domain.port.output.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private UserRepository userRepository;
    private OtpCodeRepository otpCodeRepository;
    private RefreshTokenRepository refreshTokenRepository;
    private PasswordEncoder passwordEncoder;
    private EmailSender emailSender;
    private JwtTokenService jwtTokenService;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        otpCodeRepository = mock(OtpCodeRepository.class);
        refreshTokenRepository = mock(RefreshTokenRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        emailSender = mock(EmailSender.class);
        jwtTokenService = mock(JwtTokenService.class);
        authService = new AuthService(userRepository, otpCodeRepository, refreshTokenRepository,
                passwordEncoder, emailSender, jwtTokenService);
    }

    @Test
    void shouldRegisterUser() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByDocumentId(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(otpCodeRepository.save(any(OtpCode.class))).thenAnswer(i -> i.getArgument(0));

        AuthUseCase.OtpResult result = authService.register(
                "test@test.com", "Pass1234", "Test User",
                "DOC001", "3001234567", UserType.STUDENT
        );

        assertNotNull(result);
        assertNotNull(result.otpToken());
        assertEquals(10, result.expiresInMinutes());
        verify(userRepository).save(any(User.class));
        verify(emailSender).sendOtp(anyString(), anyString());
    }

    @Test
    void shouldRejectDuplicateEmail() {
        when(userRepository.existsByEmail("existing@test.com")).thenReturn(true);

        assertThrows(DomainException.class, () ->
                authService.register("existing@test.com", "Pass1234", "Test",
                        "DOC002", "3001234568", UserType.STUDENT));
    }

    @Test
    void shouldLoginVerifiedUser() {
        User user = new User(UUID.randomUUID(), "test@test.com", "encoded",
                "Test", "DOC003", "3001234569", UserType.STUDENT,
                Set.of(Role.PLAYER), true, true, null, null);

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Pass1234", "encoded")).thenReturn(true);
        when(jwtTokenService.generateAccessToken(any(), anyString(), any()))
                .thenReturn("access-token");
        when(jwtTokenService.generateRefreshToken(any())).thenReturn("refresh-token");
        when(refreshTokenRepository.save(any(RefreshToken.class)))
                .thenAnswer(i -> i.getArgument(0));

        AuthUseCase.AuthResult result = authService.login("test@test.com", "Pass1234");

        assertNotNull(result);
        assertEquals("access-token", result.accessToken());
    }

    @Test
    void shouldRejectUnverifiedUserLogin() {
        User user = new User(UUID.randomUUID(), "test@test.com", "encoded",
                "Test", "DOC004", "3001234570", UserType.STUDENT,
                Set.of(Role.PLAYER), false, true, null, null);

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Pass1234", "encoded")).thenReturn(true);

        assertThrows(DomainException.class, () ->
                authService.login("test@test.com", "Pass1234"));
    }

    @Test
    void shouldRejectInvalidPassword() {
        User user = new User(UUID.randomUUID(), "test@test.com", "encoded",
                "Test", "DOC005", "3001234571", UserType.STUDENT,
                Set.of(Role.PLAYER), true, true, null, null);

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("WrongPass", "encoded")).thenReturn(false);

        assertThrows(DomainException.class, () ->
                authService.login("test@test.com", "WrongPass"));
    }
}
