package com.techcup.auth.infrastructure.output.email;

import com.techcup.auth.domain.port.output.EmailSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ConsoleEmailAdapter implements EmailSender {

    @Override
    public void sendOtp(String to, String code) {
        log.info("=== EMAIL (MOCK) ===");
        log.info("To: {}", to);
        log.info("OTP Code: {}", code);
        log.info("====================");
    }
}
