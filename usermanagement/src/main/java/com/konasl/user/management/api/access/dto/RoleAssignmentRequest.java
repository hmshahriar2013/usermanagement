package com.konasl.user.management.api.access.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RoleAssignmentRequest {

    @NotBlank(message = "Role name is required")
    private String roleName;
}
