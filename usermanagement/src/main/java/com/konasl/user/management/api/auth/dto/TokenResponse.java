package com.konasl.user.management.api.auth.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenResponse {
    private String accessToken;
    private String refreshToken;
    private long expiresInMs;
    private long refreshExpiresInMs;
    private String tokenType;
}
