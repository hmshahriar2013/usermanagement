package com.konasl.user.management.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception class for Authentication context related errors.
 */
public class AuthException extends BaseException {

    public AuthException(ErrorCode errorCode, HttpStatus httpStatus, Object... args) {
        super(errorCode, httpStatus, args);
    }

    public static AuthException authenticationFailed() {
        return new AuthException(ErrorCode.AUTHENTICATION_FAILED, HttpStatus.UNAUTHORIZED);
    }

    public static AuthException invalidToken() {
        return new AuthException(ErrorCode.INVALID_TOKEN, HttpStatus.UNAUTHORIZED);
    }

    public static AuthException tokenExpired() {
        return new AuthException(ErrorCode.TOKEN_EXPIRED, HttpStatus.UNAUTHORIZED);
    }
}
