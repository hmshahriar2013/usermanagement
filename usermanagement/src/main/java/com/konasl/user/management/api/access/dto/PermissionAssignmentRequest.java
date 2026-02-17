package com.konasl.user.management.api.access.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PermissionAssignmentRequest {

    @NotBlank(message = "Permission name is required")
    private String permissionName;
}
