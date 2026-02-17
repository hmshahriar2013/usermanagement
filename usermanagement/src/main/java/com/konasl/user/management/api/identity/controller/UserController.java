package com.konasl.user.management.api.identity.controller;

import com.konasl.user.management.api.ApiResponse;
import com.konasl.user.management.api.auth.dto.PasswordChangeRequest;
import com.konasl.user.management.api.identity.dto.UserRequest;
import com.konasl.user.management.domain.auth.service.AuthService;
import com.konasl.user.management.domain.identity.model.User;
import com.konasl.user.management.domain.identity.service.IdentityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for user self-service operations.
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final IdentityService identityService;
    private final AuthService authService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<User>> getMyProfile() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = identityService.findByUsername(username);
        return ResponseEntity.ok(ApiResponse.success(user, "Profile retrieved successfully"));
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<User>> updateMyProfile(@Valid @RequestBody UserRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = identityService.findByUsername(username);
        User updatedUser = identityService.updateUser(user.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.success(updatedUser, "Profile updated successfully"));
    }

    @PostMapping("/me/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(@Valid @RequestBody PasswordChangeRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = identityService.findByUsername(username);
        authService.changePassword(user.getUserId(), request.getOldPassword(), request.getNewPassword());
        return ResponseEntity.ok(ApiResponse.success(null, "Password changed successfully"));
    }
}
