package com.konasl.user.management.api.auth.controller;

import com.konasl.user.management.api.ApiResponse;
import com.konasl.user.management.api.auth.dto.LoginRequest;
import com.konasl.user.management.api.auth.dto.RefreshRequest;
import com.konasl.user.management.api.auth.dto.RegistrationRequest;
import com.konasl.user.management.api.auth.dto.TokenResponse;
import com.konasl.user.management.domain.auth.service.AuthService;
import com.konasl.user.management.domain.identity.model.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<User>> signup(@Valid @RequestBody RegistrationRequest request) {
        User user = authService.registerUser(request);
        return ResponseEntity.ok(ApiResponse.success(user, "User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@Valid @RequestBody LoginRequest request) {
        TokenResponse tokenResponse = authService.login(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(ApiResponse.success(tokenResponse, "Login successful"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> refresh(@Valid @RequestBody RefreshRequest request) {
        TokenResponse tokenResponse = authService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success(tokenResponse, "Token refreshed successfully"));
    }
}
