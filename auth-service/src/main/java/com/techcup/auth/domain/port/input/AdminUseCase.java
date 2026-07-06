package com.techcup.auth.domain.port.input;

import com.techcup.auth.domain.model.Role;
import com.techcup.auth.domain.model.User;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface AdminUseCase {
    List<User> listUsers(int page, int size);
    User getUserById(UUID id);
    User updateRoles(UUID userId, Set<Role> newRoles);
}
