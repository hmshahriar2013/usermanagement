package com.konasl.user.management.api.auth.controller;

import com.konasl.user.management.api.ApiResponse;
import com.konasl.user.management.api.auth.dto.ExternalLoginRequest;
import com.konasl.user.management.api.auth.dto.TokenResponse;
import com.konasl.user.management.domain.auth.service.AuthService;
import com.konasl.user.management.domain.auth.service.ExternalIdpService;
import com.konasl.user.management.domain.identity.model.User;
import com.konasl.user.management.domain.identity.model.UserStatus;
import com.konasl.user.management.domain.identity.service.IdentityService;
import com.konasl.user.management.api.identity.dto.UserRequest;
import com.konasl.user.management.exception.AuthException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for external authentication flows.
 */
@RestController
@RequestMapping("/api/v1/auth/external")
@RequiredArgsConstructor
public class ExternalAuthController {

    private final ExternalIdpService externalIdpService;
    private final IdentityService identityService;
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> externalLogin(@Valid @RequestBody ExternalLoginRequest request) {
        // 1. Fetch info from Mock IdP
        ExternalIdpService.ExternalUserInfo externalInfo = externalIdpService
                .getExternalUserInfo(request.getProvider(), request.getIdToken())
                .orElseThrow(() -> AuthException.authenticationFailed());

        // 2. Resolve or Create User
        User user;
        try {
            user = identityService.findByUsername(externalInfo.getEmail()); // Using email as username for simplicity
        } catch (Exception e) {
            // Create new user if not exists
            UserRequest userRequest = UserRequest.builder()
                    .username(externalInfo.getEmail())
                    .email(externalInfo.getEmail())
                    .firstName(externalInfo.getFirstName())
                    .lastName(externalInfo.getLastName())
                    .status(UserStatus.ACTIVE)
                    .build();
            user = identityService.createUser(userRequest);

            // For external users, we don't necessarily create a local password credential
            // But our current login logic expects a Credential.
            // In Phase 6, we should ideally allow AuthService.login to bypass password
            // check for external users.
        }

        // 3. Issue Token (Simulating a bypass or special grant)
        // Since this is a MOCK, we'll reuse the existing Token generation logic but we
        // might need
        // a specialized method in AuthService that doesn't check passwords.
        TokenResponse tokenResponse = authService.generateTokenForUser(user);

        return ResponseEntity.ok(ApiResponse.success(tokenResponse, "External login successful"));
    }
}
