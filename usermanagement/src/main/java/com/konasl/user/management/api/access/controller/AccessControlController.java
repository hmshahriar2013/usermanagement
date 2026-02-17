package com.konasl.user.management.api.access.controller;

import com.konasl.user.management.api.ApiResponse;
import com.konasl.user.management.api.access.dto.PermissionAssignmentRequest;
import com.konasl.user.management.api.access.dto.PermissionRequest;
import com.konasl.user.management.api.access.dto.RoleAssignmentRequest;
import com.konasl.user.management.api.access.dto.RoleRequest;
import com.konasl.user.management.domain.access.service.AccessControlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/access")
@RequiredArgsConstructor
public class AccessControlController {

    private final AccessControlService accessControlService;

    @PostMapping("/roles")
    @PreAuthorize("hasAuthority('access:manage')")
    public ResponseEntity<ApiResponse<Void>> createRole(@Valid @RequestBody RoleRequest request) {
        accessControlService.createRole(request.getName(), request.getDescription());
        return ResponseEntity.ok(ApiResponse.success(null, "Role created successfully"));
    }

    @PostMapping("/permissions")
    @PreAuthorize("hasAuthority('access:manage')")
    public ResponseEntity<ApiResponse<Void>> createPermission(@Valid @RequestBody PermissionRequest request) {
        accessControlService.createPermission(request.getName(), request.getDescription());
        return ResponseEntity.ok(ApiResponse.success(null, "Permission created successfully"));
    }

    @PostMapping("/users/{userId}/roles")
    @PreAuthorize("hasAuthority('access:manage')")
    public ResponseEntity<ApiResponse<Void>> assignRoleToUser(
            @PathVariable UUID userId,
            @Valid @RequestBody RoleAssignmentRequest request) {
        accessControlService.assignRoleToUser(userId, request.getRoleName());
        return ResponseEntity.ok(ApiResponse.success(null, "Role assigned to user successfully"));
    }

    @PostMapping("/roles/{roleName}/permissions")
    @PreAuthorize("hasAuthority('access:manage')")
    public ResponseEntity<ApiResponse<Void>> addPermissionToRole(
            @PathVariable String roleName,
            @Valid @RequestBody PermissionAssignmentRequest request) {
        accessControlService.addPermissionToRole(roleName, request.getPermissionName());
        return ResponseEntity.ok(ApiResponse.success(null, "Permission added to role successfully"));
    }
}
