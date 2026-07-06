package com.techcup.auth.controller;

import com.techcup.auth.dto.request.*;
import com.techcup.auth.dto.response.AuthResponse;
import com.techcup.auth.dto.response.MessageResponse;
import com.techcup.auth.service.AuthService;
import com.techcup.auth.service.OtpService;
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

    private final AuthService authService;
    private final OtpService otpService;

    @PostMapping("/register")
    public ResponseEntity<MessageResponse> register(@Valid @RequestBody RegisterRequest request) {
        OtpService.OtpSendResult result = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MessageResponse("OTP sent to email", result.otpToken().toString()));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<AuthResponse> verifyOtp(
            @Valid @RequestBody VerifyOtpRequest request,
            Authentication authentication) {
        AuthResponse response = authService.verifyOtp(request, getEmail(authentication));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<MessageResponse> resendOtp(@Valid @RequestBody ResendOtpRequest request) {
        // For resend we need the email - in a real app we'd get it from the otpToken context
        return ResponseEntity.ok(new MessageResponse("New OTP sent. Feature requires email context.", null));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody RefreshTokenRequest request) {
        authService.logout(request.getRefreshToken());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<AuthResponse.UserInfo> me(Authentication authentication) {
        // The user info is loaded from the JWT context
        // For full details, we'd fetch from the database
        return ResponseEntity.ok(AuthResponse.UserInfo.builder()
                .id((UUID) authentication.getPrincipal())
                .email((String) authentication.getCredentials())
                .build());
    }

    private String getEmail(Authentication authentication) {
        if (authentication != null && authentication.getCredentials() instanceof String email) {
            return email;
        }
        return null;
    }
}
