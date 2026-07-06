package com.techcup.auth.infrastructure.output.adapter;

import com.techcup.auth.domain.model.Role;
import com.techcup.auth.domain.port.output.JwtTokenService;
import com.techcup.auth.infrastructure.input.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtTokenServiceAdapter implements JwtTokenService {

    private final JwtProvider jwtProvider;

    @Override
    public String generateAccessToken(UUID userId, String email, Set<Role> roles) {
        return jwtProvider.generateAccessToken(
                userId, email,
                roles.stream().map(Enum::name).toList()
        );
    }

    @Override
    public String generateRefreshToken(UUID userId) {
        return jwtProvider.generateRefreshToken(userId);
    }

    @Override
    public TokenClaims validateRefreshToken(String token) {
        var claims = jwtProvider.validateToken(token);
        return new TokenClaims(claims.getSubject(), claims.get("email", String.class));
    }
}
