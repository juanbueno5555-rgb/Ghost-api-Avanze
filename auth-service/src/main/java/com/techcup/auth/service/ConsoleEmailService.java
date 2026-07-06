package com.techcup.auth.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ConsoleEmailService implements EmailService {

    @Override
    public void sendOtp(String to, String code) {
        log.info("=== EMAIL (MOCK) ===");
        log.info("To: {}", to);
        log.info("OTP Code: {}", code);
        log.info("====================");
    }
}
