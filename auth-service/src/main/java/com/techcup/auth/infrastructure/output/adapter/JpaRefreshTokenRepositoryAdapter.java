package com.techcup.auth.infrastructure.output.adapter;

import com.techcup.auth.domain.model.RefreshToken;
import com.techcup.auth.domain.port.output.RefreshTokenRepository;
import com.techcup.auth.infrastructure.output.jpa.mapper.RefreshTokenMapper;
import com.techcup.auth.infrastructure.output.jpa.repository.SpringDataRefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaRefreshTokenRepositoryAdapter implements RefreshTokenRepository {

    private final SpringDataRefreshTokenRepository springRepo;
    private final RefreshTokenMapper mapper;

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return springRepo.findByToken(token).map(mapper::toDomain);
    }

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        var entity = mapper.toEntity(refreshToken);
        var saved = springRepo.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public void deleteByUserId(UUID userId) {
        springRepo.deleteByUserId(userId);
    }
}
