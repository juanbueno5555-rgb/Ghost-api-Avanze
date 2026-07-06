package com.techcup.auth.infrastructure.input.rest;

import com.techcup.auth.application.dto.request.*;
import com.techcup.auth.application.dto.response.AuthResponse;
import com.techcup.auth.application.dto.response.MessageResponse;
import com.techcup.auth.domain.port.input.AuthUseCase;
import com.techcup.auth.domain.service.DomainException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthUseCase authUseCase;

    @PostMapping("/register")
    public ResponseEntity<MessageResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthUseCase.OtpResult result = authUseCase.register(
                request.getEmail(), request.getPassword(), request.getFullName(),
                request.getDocumentId(), request.getPhone(), request.getUserType()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MessageResponse("OTP sent to email", result.otpToken().toString()));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<AuthResponse> verifyOtp(@Valid @RequestBody VerifyOtpRequest request,
                                                   Authentication authentication) {
        String email = getEmail(authentication);
        AuthUseCase.AuthResult result = authUseCase.verifyOtp(
                request.getOtpToken(), request.getCode(), email);
        return ResponseEntity.ok(AuthResponse.from(result));
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<MessageResponse> resendOtp(@Valid @RequestBody ResendOtpRequest request) {
        return ResponseEntity.ok(new MessageResponse("Feature requires email context", null));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthUseCase.AuthResult result = authUseCase.login(
                request.getEmail(), request.getPassword());
        return ResponseEntity.ok(AuthResponse.from(result));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        AuthUseCase.AuthResult result = authUseCase.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(AuthResponse.from(result));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody RefreshTokenRequest request) {
        authUseCase.logout(request.getRefreshToken());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<AuthResponse.UserInfo> me(Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        return authUseCase.getCurrentUser(userId)
                .map(user -> ResponseEntity.ok(AuthResponse.UserInfo.from(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    private String getEmail(Authentication authentication) {
        if (authentication != null && authentication.getCredentials() instanceof String email) {
            return email;
        }
        return null;
    }
}
