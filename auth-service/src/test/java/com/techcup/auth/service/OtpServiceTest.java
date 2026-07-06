package com.techcup.auth.service;

import com.techcup.auth.model.OtpCode;
import com.techcup.auth.repository.OtpCodeRepository;
import com.techcup.common.exception.ApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OtpServiceTest {

    @Mock
    private OtpCodeRepository otpCodeRepository;
    @Mock
    private EmailService emailService;

    private OtpService otpService;

    @BeforeEach
    void setUp() {
        otpService = new OtpService(otpCodeRepository, emailService);
        ReflectionTestUtils.setField(otpService, "otpLength", 6);
        ReflectionTestUtils.setField(otpService, "expirationMinutes", 10);
        ReflectionTestUtils.setField(otpService, "maxAttempts", 5);
        ReflectionTestUtils.setField(otpService, "resendCooldownSeconds", 60);
    }

    @Test
    void shouldGenerateAndSendOtp() {
        when(otpCodeRepository.save(any(OtpCode.class))).thenAnswer(i -> {
            OtpCode saved = i.getArgument(0);
            // Simulate ID generation by JPA
            if (saved.getId() == null) {
                java.lang.reflect.Field idField = OtpCode.class.getDeclaredField("id");
                idField.setAccessible(true);
                idField.set(saved, UUID.randomUUID());
            }
            return saved;
        });

        OtpService.OtpSendResult result = otpService.generateAndSendOtp("test@example.com");

        assertNotNull(result);
        assertNotNull(result.otpToken());
        assertEquals(10, result.expiresInMinutes());

        verify(emailService).sendOtp(eq("test@example.com"), anyString());
        verify(otpCodeRepository).save(any(OtpCode.class));
    }

    @Test
    void shouldVerifyValidOtp() {
        UUID otpToken = UUID.randomUUID();
        String email = "test@example.com";
        String plainCode = "123456";

        OtpCode otpCode = OtpCode.builder()
                .id(otpToken)
                .email(email)
                .code(hashCode(plainCode))
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .attempts(0)
                .used(false)
                .build();

        when(otpCodeRepository.findByIdAndEmailAndUsedFalse(otpToken, email))
                .thenReturn(Optional.of(otpCode));
        when(otpCodeRepository.save(any(OtpCode.class))).thenAnswer(i -> i.getArgument(0));

        assertDoesNotThrow(() -> otpService.verifyOtp(otpToken, plainCode, email));
        assertTrue(otpCode.isUsed());
    }

    @Test
    void shouldRejectExpiredOtp() {
        UUID otpToken = UUID.randomUUID();
        String email = "test@example.com";

        OtpCode expired = OtpCode.builder()
                .id(otpToken)
                .email(email)
                .code("hash")
                .expiresAt(LocalDateTime.now().minusMinutes(1))
                .attempts(0)
                .used(false)
                .build();

        when(otpCodeRepository.findByIdAndEmailAndUsedFalse(otpToken, email))
                .thenReturn(Optional.of(expired));
        when(otpCodeRepository.save(any(OtpCode.class))).thenAnswer(i -> i.getArgument(0));

        assertThrows(ApiException.class, () ->
                otpService.verifyOtp(otpToken, "123456", email));
    }

    @Test
    void shouldRejectWrongCode() {
        UUID otpToken = UUID.randomUUID();
        String email = "test@example.com";

        OtpCode otpCode = OtpCode.builder()
                .id(otpToken)
                .email(email)
                .code(hashCode("000000"))
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .attempts(0)
                .used(false)
                .build();

        when(otpCodeRepository.findByIdAndEmailAndUsedFalse(otpToken, email))
                .thenReturn(Optional.of(otpCode));
        when(otpCodeRepository.save(any(OtpCode.class))).thenAnswer(i -> i.getArgument(0));

        assertThrows(ApiException.class, () ->
                otpService.verifyOtp(otpToken, "999999", email));
        assertEquals(1, otpCode.getAttempts());
    }

    private String hashCode(String code) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(code.getBytes());
            return java.util.HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
