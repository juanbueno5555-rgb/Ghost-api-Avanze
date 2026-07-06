package com.techcup.auth.infrastructure.input.config;

import com.techcup.auth.domain.port.input.AdminUseCase;
import com.techcup.auth.domain.port.input.AuthUseCase;
import com.techcup.auth.domain.port.output.*;
import com.techcup.auth.domain.service.AdminService;
import com.techcup.auth.domain.service.AuthService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainServiceConfig {

    @Bean
    public AuthUseCase authUseCase(UserRepository userRepository,
                                   OtpCodeRepository otpCodeRepository,
                                   RefreshTokenRepository refreshTokenRepository,
                                   PasswordEncoder passwordEncoder,
                                   EmailSender emailSender,
                                   JwtTokenService jwtTokenService) {
        return new AuthService(userRepository, otpCodeRepository, refreshTokenRepository,
                passwordEncoder, emailSender, jwtTokenService);
    }

    @Bean
    public AdminUseCase adminUseCase(UserRepository userRepository) {
        return new AdminService(userRepository);
    }
}
