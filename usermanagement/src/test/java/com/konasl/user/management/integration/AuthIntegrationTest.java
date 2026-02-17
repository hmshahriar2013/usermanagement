package com.konasl.user.management.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.konasl.user.management.api.auth.dto.LoginRequest;
import com.konasl.user.management.api.auth.dto.RefreshRequest;
import com.konasl.user.management.api.auth.dto.TokenResponse;
import com.konasl.user.management.domain.auth.service.AuthService;
import com.konasl.user.management.domain.identity.model.User;
import com.konasl.user.management.domain.identity.model.UserStatus;
import com.konasl.user.management.domain.identity.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should successfully login and return tokens")
    void shouldLoginSuccessfully() throws Exception {
        // 1. Create a user (Identity Context)
        User user = User.builder()
                .userId(UUID.randomUUID())
                .username("auth_test_user")
                .email("auth@test.com")
                .firstName("Auth")
                .lastName("Test")
                .status(UserStatus.ACTIVE)
                .build();
        userRepository.save(user);

        // 2. Create credentials (Auth Context)
        authService.createCredential(user.getUserId(), "password123");

        // 3. Attempt Login
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("auth_test_user");
        loginRequest.setPassword("password123");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.data.refreshToken").isNotEmpty())
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"));
    }

    @Test
    @DisplayName("Should fail with wrong password")
    void shouldFailWithWrongPassword() throws Exception {
        // 1. Create a user and credential
        User user = User.builder()
                .userId(UUID.randomUUID())
                .username("wrong_pass_user")
                .email("wrong@test.com")
                .firstName("Wrong")
                .lastName("Pass")
                .status(UserStatus.ACTIVE)
                .build();
        userRepository.save(user);
        authService.createCredential(user.getUserId(), "password123");

        // 2. Attempt Login with wrong password
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("wrong_pass_user");
        loginRequest.setPassword("wrongpassword");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized()) // Should return 401 for bad credentials
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should successfully refresh token")
    void shouldRefreshTokenSuccessfully() throws Exception {
        // 1. Setup user and login to get refresh token
        User user = User.builder()
                .userId(UUID.randomUUID())
                .username("refresh_test_user")
                .email("refresh@test.com")
                .firstName("Refresh")
                .lastName("Test")
                .status(UserStatus.ACTIVE)
                .build();
        userRepository.save(user);
        authService.createCredential(user.getUserId(), "password123");

        TokenResponse loginResponse = authService.login("refresh_test_user", "password123");
        String refreshToken = loginResponse.getRefreshToken();

        // 2. Attempt Refresh
        RefreshRequest refreshRequest = new RefreshRequest();
        refreshRequest.setRefreshToken(refreshToken);

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.data.refreshToken").value(refreshToken));
    }
}
