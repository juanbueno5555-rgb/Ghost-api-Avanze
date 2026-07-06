package com.techcup.auth.domain.service;

public class DomainException extends RuntimeException {
    private final String errorCode;
    private final int statusCode;

    public DomainException(String errorCode, String message, int statusCode) {
        super(message);
        this.errorCode = errorCode;
        this.statusCode = statusCode;
    }

    public String getErrorCode() { return errorCode; }
    public int getStatusCode() { return statusCode; }
}
