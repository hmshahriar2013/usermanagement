package com.konasl.user.management.domain.audit.model;

/**
 * Supported audit event types.
 */
public enum AuditEventType {
    LOGIN_SUCCESS,
    LOGIN_FAILURE,
    LOGOUT,
    TOKEN_REFRESH,
    USER_CREATED,
    USER_ACTIVATED,
    USER_SUSPENDED,
    USER_DEACTIVATED,
    ROLE_CREATED,
    PERMISSION_CREATED,
    ROLE_ASSIGNED_TO_USER,
    PERMISSION_ADDED_TO_ROLE,
    ACCESS_DENIED,
    PASSWORD_CHANGED
}
