package com.techcup.auth.application.dto.request;

import com.techcup.auth.domain.model.Role;
import jakarta.validation.constraints.NotEmpty;
import lombok.Value;
import java.util.Set;
import java.util.stream.Collectors;

@Value
public class UpdateRolesRequest {
    @NotEmpty
    Set<String> roles;

    public Set<Role> toDomainRoles() {
        return getRoles().stream()
                .map(r -> Role.valueOf(r.toUpperCase()))
                .collect(Collectors.toSet());
    }
}
