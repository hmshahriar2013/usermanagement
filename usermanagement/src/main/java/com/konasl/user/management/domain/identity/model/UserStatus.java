package com.konasl.user.management.domain.identity.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enum representing the lifecycle states of a user identity.
 */
@Getter
@RequiredArgsConstructor
public enum UserStatus {
    /**
     * Account created but email/phone verification is pending.
     */
    PENDING("PENDING"),

    /**
     * Account verified and active for system use.
     */
    ACTIVE("ACTIVE"),

    /**
     * Account temporarily restricted (e.g., due to security policy or admin action).
     */
    SUSPENDED("SUSPENDED"),

    /**
     * Account permanently disabled or logically deleted.
     */
    DEACTIVATED("DEACTIVATED");

    private final String value;
}
