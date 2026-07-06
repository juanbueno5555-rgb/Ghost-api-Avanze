package com.techcup.auth.domain.port.input;

import com.techcup.auth.domain.model.User;
import com.techcup.auth.domain.model.UserType;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface AuthUseCase {
    OtpResult register(String email, String password, String fullName,
                       String documentId, String phone, UserType userType);

    AuthResult verifyOtp(UUID otpToken, String code, String email);

    AuthResult login(String email, String password);

    AuthResult refreshToken(String refreshToken);

    void logout(String refreshToken);

    Optional<User> getCurrentUser(UUID userId);

    record OtpResult(UUID otpToken, int expiresInMinutes) {}

    record AuthResult(String accessToken, String refreshToken, int expiresIn, User user) {}
}
