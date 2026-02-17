package com.konasl.user.management.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enum for application-wide error codes.
 * Format: ERR-[CONTEXT]-[NUMBER]
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // General Errors (0000-0999)
    INTERNAL_SERVER_ERROR("ERR-GEN-0001", "An unexpected error occurred"),
    INVALID_INPUT("ERR-GEN-0002", "Invalid request input: {0}"),
    RESOURCE_NOT_FOUND("ERR-GEN-0003", "Requested resource not found"),

    // Identity/User Errors (1000-1999)
    USER_NOT_FOUND("ERR-ID-1001", "User not found with identifier: {0}"),
    USER_ALREADY_EXISTS("ERR-ID-1002", "User already exists with {0}: {1}"),
    INVALID_USER_STATUS("ERR-ID-1003", "Invalid user status transition from {0} to {1}"),
    USER_REGISTRATION_FAILED("ERR-ID-1004", "User registration failed: {0}"),

    // Authentication Errors (2000-2999)
    AUTHENTICATION_FAILED("ERR-AUTH-2001", "Authentication failed"),
    UNAUTHORIZED_ACCESS("ERR-AUTH-2002", "Unauthorized access"),
    TOKEN_EXPIRED("ERR-AUTH-2003", "Security token has expired"),
    INVALID_TOKEN("ERR-AUTH-2004", "Invalid security token"),

    // Authorization Errors (3000-3999)
    ACCESS_DENIED("ERR-AZ-3001", "Access denied: missing required permissions");

    private final String code;
    private final String message;
}
