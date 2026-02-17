package com.konasl.user.management.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception class for Identity context related errors.
 */
public class IdentityException extends BaseException {

    public IdentityException(ErrorCode errorCode, HttpStatus httpStatus, Object... args) {
        super(errorCode, httpStatus, args);
    }

    public static IdentityException userNotFound(Object identifier) {
        return new IdentityException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND, identifier);
    }

    public static IdentityException userAlreadyExists(String field, Object value) {
        return new IdentityException(ErrorCode.USER_ALREADY_EXISTS, HttpStatus.CONFLICT, field, value);
    }

    public static IdentityException invalidRegistration(String reason) {
        return new IdentityException(ErrorCode.USER_REGISTRATION_FAILED, HttpStatus.BAD_REQUEST, reason);
    }
}
