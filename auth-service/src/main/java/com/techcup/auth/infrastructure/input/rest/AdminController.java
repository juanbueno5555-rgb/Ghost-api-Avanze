package com.techcup.auth.infrastructure.input.rest;

import com.techcup.auth.application.dto.request.UpdateRolesRequest;
import com.techcup.auth.application.dto.response.UserResponse;
import com.techcup.auth.domain.port.input.AdminUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth/users")
@RequiredArgsConstructor
public class AdminController {

    private final AdminUseCase adminUseCase;

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable UUID id) {
        var user = adminUseCase.getUserById(id);
        return ResponseEntity.ok(UserResponse.fromDomain(user));
    }

    @PutMapping("/{id}/roles")
    public ResponseEntity<UserResponse> updateRoles(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateRolesRequest request) {
        var user = adminUseCase.updateRoles(id, request.toDomainRoles());
        return ResponseEntity.ok(UserResponse.fromDomain(user));
    }
}
