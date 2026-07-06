package com.techcup.auth.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class MessageResponse {
    String message;
    String otpToken;
}
