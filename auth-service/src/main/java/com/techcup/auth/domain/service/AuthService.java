package com.techcup.auth.domain.service;

import com.techcup.auth.domain.model.*;
import com.techcup.auth.domain.port.input.AuthUseCase;
import com.techcup.auth.domain.port.output.*;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.HexFormat;
import java.util.Optional;
import java.util.UUID;

public class AuthService implements AuthUseCase {

    private final UserRepository userRepository;
    private final OtpCodeRepository otpCodeRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailSender emailSender;
    private final JwtTokenService jwtTokenService;

    private final int otpLength = 6;
    private final int otpExpirationMinutes = 10;
    private final int otpMaxAttempts = 5;

    public AuthService(UserRepository userRepository, OtpCodeRepository otpCodeRepository,
                       RefreshTokenRepository refreshTokenRepository,
                       PasswordEncoder passwordEncoder, EmailSender emailSender,
                       JwtTokenService jwtTokenService) {
        this.userRepository = userRepository;
        this.otpCodeRepository = otpCodeRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailSender = emailSender;
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    public OtpResult register(String email, String password, String fullName,
                              String documentId, String phone, UserType userType) {
        if (userRepository.existsByEmail(email)) {
            throw new DomainException("EMAIL_EXISTS", "Email is already registered", 409);
        }
        if (userRepository.existsByDocumentId(documentId)) {
            throw new DomainException("DOCUMENT_EXISTS", "Document ID is already registered", 409);
        }

        UserType type = userType != null ? userType : UserType.GUEST;
        User user = User.createNew(email, passwordEncoder.encode(password), fullName,
                documentId, phone, type);
        userRepository.save(user);

        OtpCode otp = generateOtp(email);
        otpCodeRepository.save(otp);
        emailSender.sendOtp(email, otp.getCode());

        return new OtpResult(otp.getId(), otpExpirationMinutes);
    }

    @Override
    public AuthResult verifyOtp(UUID otpToken, String code, String email) {
        OtpCode otpCode = otpCodeRepository.findByIdAndEmailAndNotUsed(otpToken, email)
                .orElseThrow(() -> new DomainException("OTP_INVALID", "Invalid or expired OTP token", 400));

        if (otpCode.isExpired()) {
            otpCode.markUsed();
            otpCodeRepository.save(otpCode);
            throw new DomainException("OTP_EXPIRED", "OTP code has expired", 400);
        }

        if (otpCode.getAttempts() >= otpMaxAttempts) {
            otpCode.markUsed();
            otpCodeRepository.save(otpCode);
            throw new DomainException("OTP_MAX_ATTEMPTS", "Maximum OTP attempts exceeded", 400);
        }

        otpCode.incrementAttempts();

        if (!hashCode(code).equals(otpCode.getCode())) {
            otpCodeRepository.save(otpCode);
            throw new DomainException("OTP_MISMATCH", "Invalid OTP code", 400);
        }

        otpCode.markUsed();
        otpCodeRepository.save(otpCode);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new DomainException("USER_NOT_FOUND", "User not found", 404));
        user.verify();
        userRepository.save(user);

        return generateAuthResult(user);
    }

    @Override
    public AuthResult login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new DomainException("INVALID_CREDENTIALS", "Invalid email or password", 401));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new DomainException("INVALID_CREDENTIALS", "Invalid email or password", 401);
        }

        if (!user.isVerified()) {
            throw new DomainException("NOT_VERIFIED", "Account not verified", 403);
        }

        return generateAuthResult(user);
    }

    @Override
    public AuthResult refreshToken(String refreshToken) {
        var claims = jwtTokenService.validateRefreshToken(refreshToken);
        UUID userId = UUID.fromString(claims.subject());

        String hashedToken = hashToken(refreshToken);
        var storedToken = refreshTokenRepository.findByToken(hashedToken)
                .orElseThrow(() -> new DomainException("REFRESH_INVALID", "Invalid refresh token", 401));

        if (!storedToken.isValid()) {
            refreshTokenRepository.deleteByUserId(userId);
            throw new DomainException("REFRESH_EXPIRED", "Refresh token has expired", 401);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DomainException("USER_NOT_FOUND", "User not found", 404));

        return generateAuthResult(user);
    }

    @Override
    public void logout(String refreshToken) {
        String hashedToken = hashToken(refreshToken);
        refreshTokenRepository.findByToken(hashedToken)
                .ifPresent(token -> {
                    token.revoke();
                    refreshTokenRepository.save(token);
                });
    }

    @Override
    public Optional<User> getCurrentUser(UUID userId) {
        return userRepository.findById(userId);
    }

    private OtpCode generateOtp(String email) {
        String plainCode = generatePlainCode();
        String hashedCode = hashCode(plainCode);
        OtpCode otp = OtpCode.createNew(email, hashedCode, otpExpirationMinutes);

        // Store the plain code temporarily for sending via email
        // In a real app, this would be handled differently
        try {
            var field = OtpCode.class.getDeclaredField("code");
            field.setAccessible(true);
            field.set(otp, plainCode);
        } catch (Exception ignored) {}

        return otp;
    }

    private String generatePlainCode() {
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < otpLength; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

    private String hashCode(String plain) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(plain.getBytes()));
        } catch (Exception e) {
            throw new RuntimeException("Failed to hash", e);
        }
    }

    private String hashToken(String token) {
        return hashCode(token);
    }

    private AuthResult generateAuthResult(User user) {
        String accessToken = jwtTokenService.generateAccessToken(
                user.getId(), user.getEmail(), user.getRoles());
        String refreshToken = jwtTokenService.generateRefreshToken(user.getId());

        String hashedRefresh = hashToken(refreshToken);
        var rt = RefreshToken.createNew(user.getId(), hashedRefresh, 604800L);
        refreshTokenRepository.save(rt);

        return new AuthResult(accessToken, refreshToken, 900, user);
    }
}
