package com.techcup.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Value;

@Value
public class RefreshTokenRequest {
    @NotBlank
    String refreshToken;
}
