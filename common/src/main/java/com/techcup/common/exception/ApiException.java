package com.techcup.common.exception;

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {

    private final int statusCode;
    private final String errorCode;

    public ApiException(int statusCode, String errorCode, String message) {
        super(message);
        this.statusCode = statusCode;
        this.errorCode = errorCode;
    }

    public ApiException(int statusCode, String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
        this.errorCode = errorCode;
    }

    public static ApiException badRequest(String errorCode, String message) {
        return new ApiException(400, errorCode, message);
    }

    public static ApiException unauthorized(String errorCode, String message) {
        return new ApiException(401, errorCode, message);
    }

    public static ApiException forbidden(String errorCode, String message) {
        return new ApiException(403, errorCode, message);
    }

    public static ApiException notFound(String errorCode, String message) {
        return new ApiException(404, errorCode, message);
    }

    public static ApiException conflict(String errorCode, String message) {
        return new ApiException(409, errorCode, message);
    }

    public static ApiException tooManyRequests(String errorCode, String message) {
        return new ApiException(429, errorCode, message);
    }
}
