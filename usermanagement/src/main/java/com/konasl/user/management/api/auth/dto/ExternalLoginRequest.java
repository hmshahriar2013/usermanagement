package com.konasl.user.management.api.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for external login simulation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExternalLoginRequest {

    @NotBlank(message = "Provider is required")
    private String provider;

    @NotBlank(message = "ID Token is required")
    private String idToken;
}
