package com.techcup.auth.repository;

import com.techcup.auth.model.OtpCode;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface OtpCodeRepository extends JpaRepository<OtpCode, UUID> {
    Optional<OtpCode> findByIdAndEmailAndUsedFalse(UUID id, String email);
}
