package com.techcup.auth.service;

import com.techcup.auth.model.RefreshToken;
import com.techcup.auth.model.User;
import com.techcup.auth.repository.RefreshTokenRepository;
import com.techcup.auth.security.JwtProvider;
import com.techcup.common.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${app.jwt.refresh-token-expiration}")
    private long refreshTokenExpirationMs;

    public TokenPair generateTokenPair(User user) {
        String accessToken = jwtProvider.generateAccessToken(
                user.getId(),
                user.getEmail(),
                user.getRoles().stream().map(Enum::name).toList()
        );

        String rawRefreshToken = jwtProvider.generateRefreshToken(user.getId());
        String hashedRefreshToken = hashToken(rawRefreshToken);

        RefreshToken entity = RefreshToken.builder()
                .userId(user.getId())
                .token(hashedRefreshToken)
                .expiresAt(LocalDateTime.now().plusSeconds(refreshTokenExpirationMs / 1000))
                .build();

        refreshTokenRepository.save(entity);

        return new TokenPair(accessToken, rawRefreshToken, 900);
    }

    @Transactional
    public TokenPair refreshAccessToken(String rawRefreshToken) {
        String hashedToken = hashToken(rawRefreshToken);

        RefreshToken entity = refreshTokenRepository.findByToken(hashedToken)
                .orElseThrow(() -> ApiException.badRequest("REFRESH_INVALID", "Invalid refresh token"));

        if (!entity.isValid()) {
            refreshTokenRepository.delete(entity);
            throw ApiException.badRequest("REFRESH_EXPIRED", "Refresh token has expired");
        }

        String accessToken = jwtProvider.generateAccessToken(
                entity.getUserId(),
                null,
                List.of()
        );

        return new TokenPair(accessToken, rawRefreshToken, 900);
    }

    @Transactional
    public void revokeRefreshToken(String rawRefreshToken) {
        String hashedToken = hashToken(rawRefreshToken);
        refreshTokenRepository.findByToken(hashedToken)
                .ifPresent(token -> {
                    token.setRevoked(true);
                    refreshTokenRepository.save(token);
                });
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes());
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new RuntimeException("Failed to hash token", e);
        }
    }

    public record TokenPair(String accessToken, String refreshToken, int expiresIn) {}
}
