package com.konasl.user.management.domain.auth.validator;

import com.konasl.user.management.exception.AuthException;
import com.konasl.user.management.exception.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

/**
 * Validator for enforcing password complexity policies.
 */
@Component
public class PasswordPolicyValidator {

    private static final int MIN_LENGTH = 10;
    private static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{10,}$";

    private static final Pattern PATTERN = Pattern.compile(PASSWORD_PATTERN);

    /**
     * Validates a raw password against the security policy.
     * Policy: Min 10 chars, at least one digit, one lowercase, one uppercase, one
     * special char, no whitespace.
     */
    public void validate(String password) {
        if (!StringUtils.hasText(password)) {
            throw new AuthException(ErrorCode.INVALID_INPUT, HttpStatus.BAD_REQUEST, "Password cannot be empty");
        }

        if (password.length() < MIN_LENGTH) {
            throw new AuthException(ErrorCode.INVALID_INPUT, HttpStatus.BAD_REQUEST,
                    "Password must be at least " + MIN_LENGTH + " characters long");
        }

        if (!PATTERN.matcher(password).matches()) {
            throw new AuthException(ErrorCode.INVALID_INPUT, HttpStatus.BAD_REQUEST,
                    "Password must contain at least one digit, one lowercase, one uppercase, and one special character (@#$%^&+=!)");
        }
    }
}
