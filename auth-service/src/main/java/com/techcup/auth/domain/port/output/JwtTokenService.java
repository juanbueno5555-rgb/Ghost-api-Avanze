package com.techcup.auth.domain.port.output;

import com.techcup.auth.domain.model.Role;
import java.util.Set;
import java.util.UUID;

public interface JwtTokenService {
    String generateAccessToken(UUID userId, String email, Set<Role> roles);
    String generateRefreshToken(UUID userId);
    TokenClaims validateRefreshToken(String token);

    record TokenClaims(String subject, String email) {}
}
