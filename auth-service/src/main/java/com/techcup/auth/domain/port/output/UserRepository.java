package com.techcup.auth.domain.port.output;

import com.techcup.auth.domain.model.User;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    Optional<User> findById(UUID id);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByDocumentId(String documentId);
    User save(User user);
}
