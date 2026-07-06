package com.techcup.auth.dto.response;

import com.techcup.auth.model.User;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Value
@Builder
public class UserResponse {
    UUID id;
    String email;
    String fullName;
    String documentId;
    String phone;
    String userType;
    List<String> roles;
    boolean verified;
    boolean enabled;
    LocalDateTime createdAt;

    public static UserResponse fromEntity(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .documentId(user.getDocumentId())
                .phone(user.getPhone())
                .userType(user.getUserType().name())
                .roles(user.getRoles().stream().map(Enum::name).toList())
                .verified(user.isVerified())
                .enabled(user.isEnabled())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
