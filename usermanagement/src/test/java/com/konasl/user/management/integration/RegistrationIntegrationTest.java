package com.konasl.user.management.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.konasl.user.management.api.auth.dto.LoginRequest;
import com.konasl.user.management.api.auth.dto.RegistrationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class RegistrationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private com.konasl.user.management.domain.access.service.AccessControlService accessControlService;

    @Test
    void testRegistrationAndLoginFlow() throws Exception {
        // Seed Role
        try {
            accessControlService.createRole("ROLE_USER", "Default User Role");
        } catch (Exception e) {
            // Role might already exist
        }

        String username = "new_user_" + System.currentTimeMillis();
        RegistrationRequest regRequest = RegistrationRequest.builder()
                .username(username)
                .email(username + "@example.com")
                .password("Password123!")
                .firstName("New")
                .lastName("User")
                .build();

        // 1. Signup
        mockMvc.perform(post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(regRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.username").value(username));

        // 2. Login with new credentials
        LoginRequest loginRequest = new LoginRequest(username, "Password123!");
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").exists());
    }

    @Test
    void testRegistration_ValidationFailure() throws Exception {
        RegistrationRequest regRequest = RegistrationRequest.builder()
                .username("usr") // Too short
                .email("invalid-email")
                .password("weak")
                .firstName("")
                .lastName("")
                .build();

        mockMvc.perform(post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(regRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }
}
