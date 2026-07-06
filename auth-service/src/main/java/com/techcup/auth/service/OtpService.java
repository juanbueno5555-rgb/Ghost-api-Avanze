package com.techcup.auth.service;

import com.techcup.auth.model.OtpCode;
import com.techcup.auth.repository.OtpCodeRepository;
import com.techcup.common.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final OtpCodeRepository otpCodeRepository;
    private final EmailService emailService;

    @Value("${app.otp.length}")
    private int otpLength;

    @Value("${app.otp.expiration-minutes}")
    private int expirationMinutes;

    @Value("${app.otp.max-attempts}")
    private int maxAttempts;

    @Value("${app.otp.resend-cooldown-seconds}")
    private int resendCooldownSeconds;

    public OtpSendResult generateAndSendOtp(String email) {
        String plainCode = generatePlainCode();
        String hashedCode = hashCode(plainCode);

        OtpCode otpCode = OtpCode.builder()
                .email(email)
                .code(hashedCode)
                .expiresAt(LocalDateTime.now().plusMinutes(expirationMinutes))
                .build();

        otpCodeRepository.save(otpCode);
        emailService.sendOtp(email, plainCode);

        return new OtpSendResult(otpCode.getId(), expirationMinutes);
    }

    @Transactional
    public void verifyOtp(UUID otpToken, String code, String email) {
        OtpCode otpCode = otpCodeRepository.findByIdAndEmailAndUsedFalse(otpToken, email)
                .orElseThrow(() -> ApiException.badRequest("OTP_INVALID", "Invalid or expired OTP token"));

        if (otpCode.isExpired()) {
            otpCode.setUsed(true);
            otpCodeRepository.save(otpCode);
            throw ApiException.badRequest("OTP_EXPIRED", "OTP code has expired");
        }

        if (otpCode.getAttempts() >= maxAttempts) {
            otpCode.setUsed(true);
            otpCodeRepository.save(otpCode);
            throw ApiException.badRequest("OTP_MAX_ATTEMPTS", "Maximum OTP attempts exceeded");
        }

        otpCode.setAttempts(otpCode.getAttempts() + 1);

        if (!hashCode(code).equals(otpCode.getCode())) {
            otpCodeRepository.save(otpCode);
            throw ApiException.badRequest("OTP_MISMATCH", "Invalid OTP code");
        }

        otpCode.setUsed(true);
        otpCodeRepository.save(otpCode);
    }

    private String generatePlainCode() {
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < otpLength; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

    private String hashCode(String plainCode) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(plainCode.getBytes());
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new RuntimeException("Failed to hash OTP code", e);
        }
    }

    public record OtpSendResult(UUID otpToken, int expiresInMinutes) {}
}
