package com.konasl.user.management.api.admin.controller;

import com.konasl.user.management.api.ApiResponse;
import com.konasl.user.management.api.identity.dto.UserRequest;
import com.konasl.user.management.domain.identity.model.User;
import com.konasl.user.management.domain.identity.service.IdentityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for administrative bulk operations.
 */
@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
public class BulkOperationController {

    private final IdentityService identityService;

    @PostMapping("/import")
    @PreAuthorize("hasAuthority('identity:manage')")
    public ResponseEntity<ApiResponse<List<User>>> importUsers(@Valid @RequestBody List<UserRequest> requests) {
        List<User> users = identityService.createUsersBulk(requests);
        return ResponseEntity.ok(ApiResponse.success(users, "Users imported successfully: " + users.size()));
    }

    @GetMapping("/export")
    @PreAuthorize("hasAuthority('identity:view')")
    public ResponseEntity<ApiResponse<List<User>>> exportUsers() {
        List<User> users = identityService.findAll();
        return ResponseEntity.ok(ApiResponse.success(users, "Users exported successfully: " + users.size()));
    }
}
