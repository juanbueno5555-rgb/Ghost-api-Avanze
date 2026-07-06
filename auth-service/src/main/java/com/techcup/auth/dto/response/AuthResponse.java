package com.techcup.auth.dto.response;

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

    @Value
    @Builder
    public static class UserInfo {
        UUID id;
        String email;
        String fullName;
        List<String> roles;
        String userType;
        boolean verified;
    }
}
