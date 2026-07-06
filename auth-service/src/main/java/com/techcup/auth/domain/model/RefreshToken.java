package com.techcup.auth.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class RefreshToken {
    private final UUID id;
    private UUID userId;
    private String token;
    private LocalDateTime expiresAt;
    private boolean revoked;

    public RefreshToken(UUID id, UUID userId, String token,
                        LocalDateTime expiresAt, boolean revoked) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.userId = Objects.requireNonNull(userId, "userId must not be null");
        this.token = Objects.requireNonNull(token, "token must not be null");
        this.expiresAt = Objects.requireNonNull(expiresAt, "expiresAt must not be null");
        this.revoked = revoked;
    }

    public static RefreshToken createNew(UUID userId, String hashedToken, long ttlSeconds) {
        return new RefreshToken(
                UUID.randomUUID(), userId, hashedToken,
                LocalDateTime.now().plusSeconds(ttlSeconds),
                false
        );
    }

    public boolean isValid() {
        return !revoked && LocalDateTime.now().isBefore(expiresAt);
    }

    public void revoke() {
        this.revoked = true;
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public String getToken() { return token; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public boolean isRevoked() { return revoked; }
}
