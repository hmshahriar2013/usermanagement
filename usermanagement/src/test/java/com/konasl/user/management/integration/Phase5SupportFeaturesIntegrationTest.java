package com.konasl.user.management.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.konasl.user.management.api.auth.dto.LoginRequest;
import com.konasl.user.management.api.auth.dto.PasswordChangeRequest;
import com.konasl.user.management.api.identity.dto.UserRequest;
import com.konasl.user.management.domain.auth.model.Credential;
import com.konasl.user.management.domain.auth.repository.CredentialRepository;
import com.konasl.user.management.domain.identity.model.User;
import com.konasl.user.management.domain.identity.model.UserStatus;
import com.konasl.user.management.domain.identity.repository.UserRepository;
import com.konasl.user.management.domain.identity.service.IdentityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class Phase5SupportFeaturesIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CredentialRepository credentialRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private IdentityService identityService;

    @BeforeEach
    void setup() {
        credentialRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"identity:manage", "identity:view"})
    void testAdminUserCRUD() throws Exception {
        UserRequest request = UserRequest.builder()
                .username("newadmin")
                .email("admin@test.com")
                .firstName("Admin")
                .lastName("User")
                .status(UserStatus.ACTIVE)
                .build();

        // Create
        byte[] response = mockMvc.perform(post("/api/v1/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("newadmin"))
                .andReturn().getResponse().getContentAsByteArray();

        User createdUser = objectMapper.readValue(response, com.fasterxml.jackson.databind.JsonNode.class)
                .get("data").traverse(objectMapper).readValueAs(User.class);

        // Get
        mockMvc.perform(get("/api/v1/admin/users/" + createdUser.getUserId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.firstName").value("Admin"));

        // Update status
        mockMvc.perform(patch("/api/v1/admin/users/" + createdUser.getUserId() + "/status")
                        .param("status", "SUSPENDED"))
                .andExpect(status().isOk());

        User updatedUser = identityService.findById(createdUser.getUserId());
        assertThat(updatedUser.getStatus()).isEqualTo(UserStatus.SUSPENDED);
    }

    @Test
    @WithMockUser(username = "user1")
    void testUserSelfService() throws Exception {
        User user = User.builder()
                .username("user1")
                .email("user1@test.com")
                .firstName("User")
                .lastName("One")
                .status(UserStatus.ACTIVE)
                .build();
        user = userRepository.save(user);

        // Get Profile
        mockMvc.perform(get("/api/v1/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("user1"));

        // Update Profile
        UserRequest updateRequest = UserRequest.builder()
                .firstName("Updated")
                .lastName("Name")
                .build();
        mockMvc.perform(put("/api/v1/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.firstName").value("Updated"));
    }

    @Test
    @WithMockUser(username = "passuser")
    void testChangePassword() throws Exception {
        String username = "passuser";
        String oldPass = "OldPass123!";
        String newPass = "NewPass123!";

        User user = User.builder()
                .username(username)
                .email("pass@test.com")
                .firstName("Pass")
                .lastName("User")
                .status(UserStatus.ACTIVE)
                .build();
        user = userRepository.save(user);

        Credential credential = Credential.builder()
                .userId(user.getUserId())
                .passwordHash(passwordEncoder.encode(oldPass))
                .lastChangedAt(java.time.LocalDateTime.now())
                .build();
        credentialRepository.save(credential);

        PasswordChangeRequest request = new PasswordChangeRequest(oldPass, newPass);

        mockMvc.perform(post("/api/v1/users/me/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        Credential updatedCred = credentialRepository.findByUserId(user.getUserId()).get();
        assertThat(passwordEncoder.matches(newPass, updatedCred.getPasswordHash())).isTrue();
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"identity:manage", "identity:view"})
    void testBulkOperations() throws Exception {
        UserRequest u1 = UserRequest.builder().username("b1").email("b1@test.com").firstName("B1").lastName("U1").build();
        UserRequest u2 = UserRequest.builder().username("b2").email("b2@test.com").firstName("B2").lastName("U2").build();
        List<UserRequest> requests = List.of(u1, u2);

        // Import
        mockMvc.perform(post("/api/v1/admin/users/import")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requests)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2));

        // Export
        mockMvc.perform(get("/api/v1/admin/users/export"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2));
    }
}
