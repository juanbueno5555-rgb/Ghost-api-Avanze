package com.techcup.auth.domain.port.output;

public interface EmailSender {
    void sendOtp(String to, String code);
}
