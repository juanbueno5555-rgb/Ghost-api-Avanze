package com.techcup.auth.domain.port.output;

import com.techcup.auth.domain.model.OtpCode;
import java.util.Optional;
import java.util.UUID;

public interface OtpCodeRepository {
    Optional<OtpCode> findByIdAndEmailAndNotUsed(UUID id, String email);
    OtpCode save(OtpCode otpCode);
}
