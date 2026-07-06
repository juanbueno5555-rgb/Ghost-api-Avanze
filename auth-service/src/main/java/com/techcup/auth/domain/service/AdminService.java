package com.techcup.auth.domain.service;

import com.techcup.auth.domain.model.Role;
import com.techcup.auth.domain.model.User;
import com.techcup.auth.domain.port.input.AdminUseCase;
import com.techcup.auth.domain.port.output.UserRepository;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class AdminService implements AdminUseCase {

    private final UserRepository userRepository;

    public AdminService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> listUsers(int page, int size) {
        throw new UnsupportedOperationException("Pagination not implemented yet at domain level");
    }

    @Override
    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new DomainException("USER_NOT_FOUND", "User not found", 404));
    }

    @Override
    public User updateRoles(UUID userId, Set<Role> newRoles) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DomainException("USER_NOT_FOUND", "User not found", 404));
        user.updateRoles(newRoles);
        return userRepository.save(user);
    }
}
