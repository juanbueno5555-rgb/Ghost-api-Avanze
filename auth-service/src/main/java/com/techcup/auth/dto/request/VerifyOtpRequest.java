package com.techcup.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Value;
import java.util.UUID;

@Value
public class VerifyOtpRequest {
    @NotBlank
    UUID otpToken;

    @NotBlank @Size(min = 6, max = 6)
    String code;
}
