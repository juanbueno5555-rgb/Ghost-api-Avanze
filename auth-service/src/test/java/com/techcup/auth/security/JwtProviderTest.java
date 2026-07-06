package com.techcup.auth.security;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtProviderTest {

    private JwtProvider jwtProvider;

    @BeforeEach
    void setUp() {
        jwtProvider = new JwtProvider(
                "test-secret-key-that-is-at-least-256-bits-long-for-hs256-algorithm",
                900000L,
                604800000L
        );
    }

    @Test
    void shouldGenerateAndValidateAccessToken() {
        UUID userId = UUID.randomUUID();
        String email = "test@example.com";
        List<String> roles = List.of("PLAYER", "CAPTAIN");

        String token = jwtProvider.generateAccessToken(userId, email, roles);
        assertNotNull(token);

        Claims claims = jwtProvider.validateToken(token);
        assertEquals(userId.toString(), claims.getSubject());
        assertEquals(email, claims.get("email"));
        assertEquals(roles, claims.get("roles"));
        assertFalse(jwtProvider.isRefreshToken(token));
    }

    @Test
    void shouldGenerateAndIdentifyRefreshToken() {
        UUID userId = UUID.randomUUID();
        String token = jwtProvider.generateRefreshToken(userId);
        assertNotNull(token);
        assertTrue(jwtProvider.isRefreshToken(token));
    }

    @Test
    void shouldRejectInvalidToken() {
        assertThrows(Exception.class, () ->
                jwtProvider.validateToken("invalid-token"));
    }

    @Test
    void shouldRejectExpiredToken() throws InterruptedException {
        JwtProvider shortLived = new JwtProvider(
                "test-secret-key-that-is-at-least-256-bits-long-for-hs256-algorithm",
                1L, // 1ms expiration
                604800000L
        );

        String token = shortLived.generateAccessToken(UUID.randomUUID(), "test@test.com", List.of("PLAYER"));
        Thread.sleep(10); // Ensure expiration

        assertThrows(Exception.class, () -> shortLived.validateToken(token));
    }
}
