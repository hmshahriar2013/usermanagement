package com.konasl.user.management.domain.auth.service;

import lombok.Builder;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Mock service simulating an External Identity Provider (e.g., Google, GitHub).
 */
@Service
public class ExternalIdpService {

    @Data
    @Builder
    public static class ExternalUserInfo {
        private String externalId;
        private String email;
        private String firstName;
        private String lastName;
        private String provider;
    }

    /**
     * Simulates fetching user info from an external provider using an ID token.
     */
    public Optional<ExternalUserInfo> getExternalUserInfo(String provider, String idToken) {
        // In a real implementation, this would validate the token with the provider
        if ("invalid-token".equals(idToken)) {
            return Optional.empty();
        }

        // Simulating different users based on token content
        String email = idToken.contains("@") ? idToken : idToken + "@external.com";

        return Optional.of(ExternalUserInfo.builder()
                .externalId("ext_" + idToken.hashCode())
                .email(email)
                .firstName("External")
                .lastName("User")
                .provider(provider)
                .build());
    }
}
