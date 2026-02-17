package com.konasl.user.management.api.identity.controller;

import com.konasl.user.management.api.ApiResponse;
import com.konasl.user.management.api.identity.dto.UserRequest;
import com.konasl.user.management.domain.identity.model.User;
import com.konasl.user.management.domain.identity.model.UserStatus;
import com.konasl.user.management.domain.identity.service.IdentityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Administrative controller for user management.
 */
@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final IdentityService identityService;

    @PostMapping
    @PreAuthorize("hasAuthority('identity:manage')")
    public ResponseEntity<ApiResponse<User>> createUser(@Valid @RequestBody UserRequest request) {
        User user = identityService.createUser(request);
        return ResponseEntity.ok(ApiResponse.success(user, "User created successfully"));
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasAuthority('identity:view')")
    public ResponseEntity<ApiResponse<User>> getUser(@PathVariable UUID userId) {
        User user = identityService.findById(userId);
        return ResponseEntity.ok(ApiResponse.success(user, "User retrieved successfully"));
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasAuthority('identity:manage')")
    public ResponseEntity<ApiResponse<User>> updateUser(@PathVariable UUID userId, @Valid @RequestBody UserRequest request) {
        User user = identityService.updateUser(userId, request);
        return ResponseEntity.ok(ApiResponse.success(user, "User updated successfully"));
    }

    @PatchMapping("/{userId}/status")
    @PreAuthorize("hasAuthority('identity:manage')")
    public ResponseEntity<ApiResponse<Void>> updateStatus(@PathVariable UUID userId, @RequestParam UserStatus status) {
        identityService.updateStatus(userId, status);
        return ResponseEntity.ok(ApiResponse.success(null, "User status updated to " + status));
    }
}
