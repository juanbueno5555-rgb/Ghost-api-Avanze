package com.techcup.auth.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class OtpCode {
    private final UUID id;
    private String email;
    private String code;
    private LocalDateTime expiresAt;
    private int attempts;
    private boolean used;

    public OtpCode(UUID id, String email, String code, LocalDateTime expiresAt,
                   int attempts, boolean used) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.email = Objects.requireNonNull(email, "email must not be null");
        this.code = Objects.requireNonNull(code, "code must not be null");
        this.expiresAt = Objects.requireNonNull(expiresAt, "expiresAt must not be null");
        this.attempts = attempts;
        this.used = used;
    }

    public static OtpCode createNew(String email, String hashedCode, int expirationMinutes) {
        return new OtpCode(
                UUID.randomUUID(), email, hashedCode,
                LocalDateTime.now().plusMinutes(expirationMinutes),
                0, false
        );
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean canRetry(int maxAttempts) {
        return !used && !isExpired() && attempts < maxAttempts;
    }

    public void incrementAttempts() {
        this.attempts++;
    }

    public void markUsed() {
        this.used = true;
    }

    // Getters
    public UUID getId() { return id; }
    public String getEmail() { return email; }
    public String getCode() { return code; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public int getAttempts() { return attempts; }
    public boolean isUsed() { return used; }
}
