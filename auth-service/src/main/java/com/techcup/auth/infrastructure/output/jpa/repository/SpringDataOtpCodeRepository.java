package com.techcup.auth.infrastructure.output.jpa.repository;

import com.techcup.auth.infrastructure.output.jpa.entity.OtpCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataOtpCodeRepository extends JpaRepository<OtpCodeEntity, UUID> {
    Optional<OtpCodeEntity> findByIdAndEmailAndUsedFalse(UUID id, String email);
}
