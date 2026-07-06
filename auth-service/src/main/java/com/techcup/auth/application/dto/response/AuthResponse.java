package com.techcup.auth.application.dto.response;

import com.techcup.auth.domain.model.User;
import com.techcup.auth.domain.port.input.AuthUseCase;
import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.UUID;

@Value
@Builder
public class AuthResponse {
    String accessToken;
    String refreshToken;
    int expiresIn;
    UserInfo user;

    public static AuthResponse from(AuthUseCase.AuthResult result) {
        return AuthResponse.builder()
                .accessToken(result.accessToken())
                .refreshToken(result.refreshToken())
                .expiresIn(result.expiresIn())
                .user(result.user() != null ? UserInfo.from(result.user()) : null)
                .build();
    }

    @Value
    @Builder
    public static class UserInfo {
        UUID id;
        String email;
        String fullName;
        List<String> roles;
        String userType;
        boolean verified;

        public static UserInfo from(User user) {
            return UserInfo.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .fullName(user.getFullName())
                    .roles(user.getRoles().stream().map(Enum::name).toList())
                    .userType(user.getUserType().name())
                    .verified(user.isVerified())
                    .build();
        }
    }
}
