package com.techcup.auth.infrastructure.output.adapter;

import com.techcup.auth.domain.model.OtpCode;
import com.techcup.auth.domain.port.output.OtpCodeRepository;
import com.techcup.auth.infrastructure.output.jpa.mapper.OtpCodeMapper;
import com.techcup.auth.infrastructure.output.jpa.repository.SpringDataOtpCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaOtpCodeRepositoryAdapter implements OtpCodeRepository {

    private final SpringDataOtpCodeRepository springRepo;
    private final OtpCodeMapper mapper;

    @Override
    public Optional<OtpCode> findByIdAndEmailAndNotUsed(UUID id, String email) {
        return springRepo.findByIdAndEmailAndUsedFalse(id, email)
                .map(mapper::toDomain);
    }

    @Override
    public OtpCode save(OtpCode otpCode) {
        var entity = mapper.toEntity(otpCode);
        var saved = springRepo.save(entity);
        return mapper.toDomain(saved);
    }
}
