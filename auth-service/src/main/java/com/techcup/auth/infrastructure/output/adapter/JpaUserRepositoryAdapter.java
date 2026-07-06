package com.techcup.auth.infrastructure.output.adapter;

import com.techcup.auth.domain.model.User;
import com.techcup.auth.domain.port.output.UserRepository;
import com.techcup.auth.infrastructure.output.jpa.mapper.UserMapper;
import com.techcup.auth.infrastructure.output.jpa.repository.SpringDataUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaUserRepositoryAdapter implements UserRepository {

    private final SpringDataUserRepository springRepo;
    private final UserMapper mapper;

    @Override
    public Optional<User> findById(UUID id) {
        return springRepo.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return springRepo.findByEmail(email).map(mapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return springRepo.existsByEmail(email);
    }

    @Override
    public boolean existsByDocumentId(String documentId) {
        return springRepo.existsByDocumentId(documentId);
    }

    @Override
    public User save(User user) {
        var entity = mapper.toEntity(user);
        var saved = springRepo.save(entity);
        return mapper.toDomain(saved);
    }
}
