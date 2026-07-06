package com.techcup.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Value;
import java.util.UUID;

@Value
public class ResendOtpRequest {
    @NotBlank
    UUID otpToken;
}
