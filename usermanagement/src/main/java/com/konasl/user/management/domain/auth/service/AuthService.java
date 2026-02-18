package com.konasl.user.management.domain.auth.service;

import com.konasl.user.management.api.auth.dto.TokenResponse;
import com.konasl.user.management.domain.auth.model.Credential;
import com.konasl.user.management.domain.auth.model.RefreshToken;
import com.konasl.user.management.domain.auth.repository.CredentialRepository;
import com.konasl.user.management.domain.auth.repository.RefreshTokenRepository;
import com.konasl.user.management.domain.auth.validator.PasswordPolicyValidator;
import com.konasl.user.management.domain.identity.model.User;
import com.konasl.user.management.domain.identity.service.IdentityService;
import com.konasl.user.management.exception.AuthException;
import com.konasl.user.management.exception.ErrorCode;
import com.konasl.user.management.exception.IdentityException;
import com.konasl.user.management.security.jwt.JwtService;
import com.konasl.user.management.api.auth.dto.RegistrationRequest;
import com.konasl.user.management.api.identity.dto.UserRequest;
import com.konasl.user.management.domain.access.service.AccessControlService;
import com.konasl.user.management.domain.identity.model.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

        private final CredentialRepository credentialRepository;
        private final RefreshTokenRepository refreshTokenRepository;
        private final IdentityService identityService;
        private final AccessControlService accessControlService;
        private final JwtService jwtService;
        private final PasswordEncoder passwordEncoder;
        private final PasswordPolicyValidator passwordPolicyValidator;

        @Transactional
        @com.konasl.user.management.domain.audit.annotation.Auditable(eventType = com.konasl.user.management.domain.audit.model.AuditEventType.USER_CREATED)
        public User registerUser(RegistrationRequest request) {
                // 1. Create Identity
                UserRequest identityRequest = UserRequest.builder()
                                .username(request.getUsername())
                                .email(request.getEmail())
                                .firstName(request.getFirstName())
                                .lastName(request.getLastName())
                                .status(UserStatus.ACTIVE)
                                .build();
                User user = identityService.createUser(identityRequest);

                // 2. Create Credential
                createCredential(user.getUserId(), request.getPassword());

                // 3. Assign Default Role (USER)
                try {
                        accessControlService.assignRoleToUser(user.getUserId(), "ROLE_USER");
                } catch (Exception e) {
                        // In case the role doesn't exist yet (e.g. first run), we might want to log it
                        // but for this implementation we assume the role exists or will be created.
                }

                return user;
        }

        @Transactional
        public void createCredential(UUID userId, String rawPassword) {
                passwordPolicyValidator.validate(rawPassword);
                Credential credential = Credential.builder()
                                .userId(userId)
                                .passwordHash(passwordEncoder.encode(rawPassword))
                                .lastChangedAt(LocalDateTime.now())
                                .build();
                credentialRepository.save(credential);
        }

        @Transactional
        @com.konasl.user.management.domain.audit.annotation.Auditable(eventType = com.konasl.user.management.domain.audit.model.AuditEventType.LOGIN_SUCCESS)
        public TokenResponse login(String username, String password) {
                // 1. Fetch User (Identity Context)
                User user = identityService.findByUsername(username);

                // 2. Fetch Credential (Auth Context)
                Credential credential = credentialRepository.findByUserId(user.getUserId())
                                .orElseThrow(AuthException::authenticationFailed);

                // 3. Verify Password
                if (!passwordEncoder.matches(password, credential.getPasswordHash())) {
                        throw AuthException.authenticationFailed();
                }

                // 4. Generate Tokens
                var userDetails = new org.springframework.security.core.userdetails.User(
                                user.getUsername(),
                                credential.getPasswordHash(),
                                Collections.emptyList());

                String accessToken = jwtService.generateAccessToken(userDetails);
                String refreshToken = jwtService.generateRefreshToken(userDetails);

                // 5. Save Refresh Token
                saveRefreshToken(user.getUserId(), refreshToken);

                return TokenResponse.builder()
                                .accessToken(accessToken)
                                .refreshToken(refreshToken)
                                .expiresInMs(3600000) // TODO: Get from property
                                .tokenType("Bearer")
                                .build();
        }

        @Transactional
        public TokenResponse generateTokenForUser(User user) {
                // Since it's an external user or already authenticated, we just issue tokens
                var userDetails = new org.springframework.security.core.userdetails.User(
                                user.getUsername(),
                                "", // No password for external principal
                                Collections.emptyList());

                String accessToken = jwtService.generateAccessToken(userDetails);
                String refreshToken = jwtService.generateRefreshToken(userDetails);

                saveRefreshToken(user.getUserId(), refreshToken);

                return TokenResponse.builder()
                                .accessToken(accessToken)
                                .refreshToken(refreshToken)
                                .expiresInMs(3600000)
                                .tokenType("Bearer")
                                .build();
        }

        @Transactional
        @com.konasl.user.management.domain.audit.annotation.Auditable(eventType = com.konasl.user.management.domain.audit.model.AuditEventType.TOKEN_REFRESH)
        public TokenResponse refreshToken(String refreshTokenValue) {
                // 1. Find and validate the refresh token
                RefreshToken refreshToken = refreshTokenRepository.findByTokenValue(refreshTokenValue)
                                .orElseThrow(AuthException::authenticationFailed);

                if (refreshToken.isRevoked() || refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
                        throw AuthException.tokenExpired();
                }

                // 2. Load User Details
                User user = identityService.findById(refreshToken.getUserId());
                Credential credential = credentialRepository.findByUserId(user.getUserId())
                                .orElseThrow(AuthException::authenticationFailed);

                var userDetails = new org.springframework.security.core.userdetails.User(
                                user.getUsername(),
                                credential.getPasswordHash(),
                                Collections.emptyList());

                // 3. Generate new tokens
                String newAccessToken = jwtService.generateAccessToken(userDetails);

                return TokenResponse.builder()
                                .accessToken(newAccessToken)
                                .refreshToken(refreshTokenValue)
                                .expiresInMs(3600000)
                                .tokenType("Bearer")
                                .build();
        }

        @Transactional
        @com.konasl.user.management.domain.audit.annotation.Auditable(eventType = com.konasl.user.management.domain.audit.model.AuditEventType.PASSWORD_CHANGED)
        public void changePassword(UUID userId, String oldPassword, String newPassword) {
                passwordPolicyValidator.validate(newPassword);
                Credential credential = credentialRepository.findByUserId(userId)
                                .orElseThrow(AuthException::authenticationFailed);

                if (!passwordEncoder.matches(oldPassword, credential.getPasswordHash())) {
                        throw new AuthException(ErrorCode.AUTHENTICATION_FAILED, HttpStatus.UNAUTHORIZED,
                                        "Invalid old password");
                }

                credential.setPasswordHash(passwordEncoder.encode(newPassword));
                credential.setLastChangedAt(LocalDateTime.now());
                credentialRepository.save(credential);
        }

        private void saveRefreshToken(UUID userId, String tokenValue) {
                RefreshToken refreshToken = RefreshToken.builder()
                                .userId(userId)
                                .tokenValue(tokenValue)
                                .expiryDate(LocalDateTime.now().plusDays(7)) // TODO: Get from property
                                .build();
                refreshTokenRepository.save(refreshToken);
        }
}
