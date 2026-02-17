package com.konasl.user.management.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.text.MessageFormat;

/**
 * Base exception class for all custom application exceptions.
 */
@Getter
public abstract class BaseException extends RuntimeException {

    private final ErrorCode errorCode;
    private final HttpStatus httpStatus;
    private final Object[] args;

    protected BaseException(ErrorCode errorCode, HttpStatus httpStatus, Object... args) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.args = args;
    }

    @Override
    public String getMessage() {
        if (args != null && args.length > 0) {
            return MessageFormat.format(errorCode.getMessage(), args);
        }
        return errorCode.getMessage();
    }
}
