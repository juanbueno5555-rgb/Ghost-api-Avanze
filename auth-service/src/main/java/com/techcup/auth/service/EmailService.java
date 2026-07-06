package com.techcup.auth.service;

public interface EmailService {
    void sendOtp(String to, String code);
}
