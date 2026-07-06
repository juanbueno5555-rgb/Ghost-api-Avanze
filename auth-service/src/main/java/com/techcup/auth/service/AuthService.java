package com.techcup.auth.service;

import com.techcup.auth.dto.request.*;
import com.techcup.auth.dto.response.AuthResponse;
import com.techcup.auth.model.*;
import com.techcup.auth.repository.UserRepository;
import com.techcup.common.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;
    private final TokenService tokenService;

    @Transactional
    public OtpService.OtpSendResult register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw ApiException.conflict("EMAIL_EXISTS", "Email is already registered");
        }
        if (userRepository.existsByDocumentId(request.getDocumentId())) {
            throw ApiException.conflict("DOCUMENT_EXISTS", "Document ID is already registered");
        }

        UserType userType = request.getUserType() != null ? request.getUserType() : UserType.GUEST;

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .documentId(request.getDocumentId())
                .phone(request.getPhone())
                .userType(userType)
                .roles(Set.of(Role.PLAYER))
                .verified(false)
                .build();

        userRepository.save(user);

        return otpService.generateAndSendOtp(user.getEmail());
    }

    @Transactional
    public AuthResponse verifyOtp(VerifyOtpRequest request, String email) {
        otpService.verifyOtp(request.getOtpToken(), request.getCode(), email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> ApiException.notFound("USER_NOT_FOUND", "User not found"));

        user.setVerified(true);
        userRepository.save(user);

        TokenService.TokenPair tokens = tokenService.generateTokenPair(user);

        return AuthResponse.builder()
                .accessToken(tokens.accessToken())
                .refreshToken(tokens.refreshToken())
                .expiresIn(tokens.expiresIn())
                .user(toUserInfo(user))
                .build();
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> ApiException.unauthorized("INVALID_CREDENTIALS", "Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw ApiException.unauthorized("INVALID_CREDENTIALS", "Invalid email or password");
        }

        if (!user.isVerified()) {
            throw ApiException.forbidden("NOT_VERIFIED", "Account not verified. Please verify your OTP code.");
        }

        TokenService.TokenPair tokens = tokenService.generateTokenPair(user);

        return AuthResponse.builder()
                .accessToken(tokens.accessToken())
                .refreshToken(tokens.refreshToken())
                .expiresIn(tokens.expiresIn())
                .user(toUserInfo(user))
                .build();
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {
        TokenService.TokenPair tokens = tokenService.refreshAccessToken(request.getRefreshToken());
        return AuthResponse.builder()
                .accessToken(tokens.accessToken())
                .refreshToken(tokens.refreshToken())
                .expiresIn(tokens.expiresIn())
                .build();
    }

    @Transactional
    public void logout(String rawRefreshToken) {
        tokenService.revokeRefreshToken(rawRefreshToken);
    }

    private AuthResponse.UserInfo toUserInfo(User user) {
        return AuthResponse.UserInfo.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .roles(user.getRoles().stream().map(Enum::name).toList())
                .userType(user.getUserType().name())
                .verified(user.isVerified())
                .build();
    }
}
