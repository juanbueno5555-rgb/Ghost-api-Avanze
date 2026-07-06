package com.techcup.auth.domain.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class User {
    private final UUID id;
    private String email;
    private String password;
    private String fullName;
    private String documentId;
    private String phone;
    private UserType userType;
    private Set<Role> roles;
    private boolean verified;
    private boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public User(UUID id, String email, String password, String fullName,
                String documentId, String phone, UserType userType,
                Set<Role> roles, boolean verified, boolean enabled,
                LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.email = Objects.requireNonNull(email, "email must not be null");
        this.password = Objects.requireNonNull(password, "password must not be null");
        this.fullName = Objects.requireNonNull(fullName, "fullName must not be null");
        this.documentId = Objects.requireNonNull(documentId, "documentId must not be null");
        this.phone = Objects.requireNonNull(phone, "phone must not be null");
        this.userType = Objects.requireNonNull(userType, "userType must not be null");
        this.roles = roles != null ? new HashSet<>(roles) : new HashSet<>();
        this.verified = verified;
        this.enabled = enabled;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.updatedAt = updatedAt != null ? updatedAt : LocalDateTime.now();
    }

    public static User createNew(String email, String password, String fullName,
                                  String documentId, String phone, UserType userType) {
        return new User(
                UUID.randomUUID(), email, password, fullName, documentId, phone,
                userType, Set.of(Role.PLAYER), false, true,
                LocalDateTime.now(), LocalDateTime.now()
        );
    }

    public void verify() {
        this.verified = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateRoles(Set<Role> newRoles) {
        this.roles = new HashSet<>(newRoles);
        this.updatedAt = LocalDateTime.now();
    }

    public void markUpdated() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters
    public UUID getId() { return id; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getFullName() { return fullName; }
    public String getDocumentId() { return documentId; }
    public String getPhone() { return phone; }
    public UserType getUserType() { return userType; }
    public Set<Role> getRoles() { return new HashSet<>(roles); }
    public boolean isVerified() { return verified; }
    public boolean isEnabled() { return enabled; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
