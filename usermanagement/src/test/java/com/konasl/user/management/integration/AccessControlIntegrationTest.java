package com.konasl.user.management.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.konasl.user.management.api.auth.dto.LoginRequest;
import com.konasl.user.management.domain.access.service.AccessControlService;
import com.konasl.user.management.domain.auth.service.AuthService;
import com.konasl.user.management.domain.identity.model.User;
import com.konasl.user.management.domain.identity.model.UserStatus;
import com.konasl.user.management.domain.identity.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(AccessTestController.class)
@Transactional
class AccessControlIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private AccessControlService accessControlService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should allow access to role-protected endpoint when user has role")
    void shouldAllowAccessWithCorrectRole() throws Exception {
        // 1. Setup user with ROLE_ADMIN
        User user = createUser("admin_user");
        authService.createCredential(user.getUserId(), "password123");
        accessControlService.createRole("ROLE_ADMIN", "Administrator");
        accessControlService.assignRoleToUser(user.getUserId(), "ROLE_ADMIN");

        // 2. Login to get token
        String token = loginAndGetToken("admin_user", "password123");

        // 3. Access endpoint requiring ROLE_ADMIN
        mockMvc.perform(get("/api/v1/test/admin-only")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("Admin Content"));
    }

    @Test
    @DisplayName("Should deny access to role-protected endpoint when user lacks role")
    void shouldDenyAccessWithoutCorrectRole() throws Exception {
        // 1. Setup user with ROLE_USER
        User user = createUser("regular_user");
        authService.createCredential(user.getUserId(), "password123");
        accessControlService.createRole("ROLE_USER", "Regular User");
        accessControlService.assignRoleToUser(user.getUserId(), "ROLE_USER");

        // 2. Login to get token
        String token = loginAndGetToken("regular_user", "password123");

        // 3. Access endpoint requiring ROLE_ADMIN
        mockMvc.perform(get("/api/v1/test/admin-only")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should allow access to permission-protected endpoint when user has permission")
    void shouldAllowAccessWithCorrectPermission() throws Exception {
        // 1. Setup user, role and permission
        User user = createUser("perm_user");
        authService.createCredential(user.getUserId(), "password123");
        accessControlService.createRole("ROLE_MANAGER", "Manager");
        accessControlService.createPermission("user:read", "Read User Data");
        accessControlService.addPermissionToRole("ROLE_MANAGER", "user:read");
        accessControlService.assignRoleToUser(user.getUserId(), "ROLE_MANAGER");

        // 2. Login
        String token = loginAndGetToken("perm_user", "password123");

        // 3. Access endpoint requiring user:read
        mockMvc.perform(get("/api/v1/test/perm-read")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("Readable Data"));
    }

    @Test
    @DisplayName("Should allow creating role when user has access:manage permission")
    void shouldAllowRoleCreationWithAccessManage() throws Exception {
        // 1. Setup user with access:manage
        User user = createUser("manager_user");
        authService.createCredential(user.getUserId(), "password123");
        accessControlService.createRole("ROLE_MANAGER", "Manager");
        accessControlService.createPermission("access:manage", "Manage Access Control");
        accessControlService.addPermissionToRole("ROLE_MANAGER", "access:manage");
        accessControlService.assignRoleToUser(user.getUserId(), "ROLE_MANAGER");

        // 2. Login
        String token = loginAndGetToken("manager_user", "password123");

        // 3. Create a new role via API
        String roleJson = "{\"name\":\"ROLE_NEW\",\"description\":\"New Role\"}";
        mockMvc.perform(post("/api/v1/access/roles")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(roleJson))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should deny creating role when user lacks access:manage permission")
    void shouldDenyRoleCreationWithoutAccessManage() throws Exception {
        // 1. Setup regular user
        User user = createUser("unprivileged_user");
        authService.createCredential(user.getUserId(), "password123");

        // 2. Login
        String token = loginAndGetToken("unprivileged_user", "password123");

        // 3. Attempt to create a role via API
        String roleJson = "{\"name\":\"ROLE_NEW\",\"description\":\"New Role\"}";
        mockMvc.perform(post("/api/v1/access/roles")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(roleJson))
                .andExpect(status().isForbidden());
    }

    private User createUser(String username) {
        User user = User.builder()
                .userId(UUID.randomUUID())
                .username(username)
                .email(username + "@test.com")
                .firstName("Test")
                .lastName("User")
                .status(UserStatus.ACTIVE)
                .build();
        return userRepository.save(user);
    }

    private String loginAndGetToken(String username, String password) throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(username);
        loginRequest.setPassword(password);

        String response = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andReturn().getResponse().getContentAsString();
        
        return objectMapper.readTree(response).get("data").get("accessToken").asText();
    }
}
