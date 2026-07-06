package com.techcup.auth.infrastructure.output.jpa.mapper;

import com.techcup.auth.domain.model.OtpCode;
import com.techcup.auth.infrastructure.output.jpa.entity.OtpCodeEntity;
import org.springframework.stereotype.Component;

@Component
public class OtpCodeMapper {

    public OtpCode toDomain(OtpCodeEntity entity) {
        return new OtpCode(
                entity.getId(),
                entity.getEmail(),
                entity.getCode(),
                entity.getExpiresAt(),
                entity.getAttempts(),
                entity.isUsed()
        );
    }

    public OtpCodeEntity toEntity(OtpCode domain) {
        return OtpCodeEntity.builder()
                .id(domain.getId())
                .email(domain.getEmail())
                .code(domain.getCode())
                .expiresAt(domain.getExpiresAt())
                .attempts(domain.getAttempts())
                .used(domain.isUsed())
                .build();
    }
}
