package com.techcup.auth.infrastructure.output.jpa.mapper;

import com.techcup.auth.domain.model.User;
import com.techcup.auth.infrastructure.output.jpa.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toDomain(UserEntity entity) {
        return new User(
                entity.getId(),
                entity.getEmail(),
                entity.getPassword(),
                entity.getFullName(),
                entity.getDocumentId(),
                entity.getPhone(),
                entity.getUserType(),
                entity.getRoles(),
                entity.isVerified(),
                entity.isEnabled(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public UserEntity toEntity(User domain) {
        return UserEntity.builder()
                .id(domain.getId())
                .email(domain.getEmail())
                .password(domain.getPassword())
                .fullName(domain.getFullName())
                .documentId(domain.getDocumentId())
                .phone(domain.getPhone())
                .userType(domain.getUserType())
                .roles(domain.getRoles())
                .verified(domain.isVerified())
                .enabled(domain.isEnabled())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}
