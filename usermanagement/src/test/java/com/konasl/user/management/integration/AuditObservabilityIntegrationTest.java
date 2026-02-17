package com.konasl.user.management.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.konasl.user.management.api.auth.dto.LoginRequest;
import com.konasl.user.management.domain.audit.repository.AuditLogRepository;
import com.konasl.user.management.domain.auth.service.AuthService;
import com.konasl.user.management.domain.identity.model.User;
import com.konasl.user.management.domain.identity.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuditObservabilityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        auditLogRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testCorrelationIdPropagation() throws Exception {
        String correlationId = UUID.randomUUID().toString();

        mockMvc.perform(get("/actuator/health")
                        .header("X-Correlation-ID", correlationId))
                .andExpect(status().isOk())
                .andExpect(header -> assertThat(header.getResponse().getHeader("X-Correlation-ID")).isEqualTo(correlationId));
    }

    @Test
    void testAuditLogPersistenceOnLogin() throws Exception {
        // 1. Create a user and credentials
        String username = "audit_user";
        String password = "Password123!";
        
        User user = User.builder()
                .username(username)
                .email("audit@example.com")
                .firstName("Audit")
                .lastName("Tester")
                .status(com.konasl.user.management.domain.identity.model.UserStatus.ACTIVE)
                .build();
        user = userRepository.save(user);
        authService.createCredential(user.getUserId(), password);

        // 2. Perform login
        LoginRequest loginRequest = new LoginRequest(username, password);
        
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk());

        // 3. Verify audit log (Async, might need a small wait but H2 is fast in-memory)
        // In a real scenario, we might use Awaitility
        Thread.sleep(200); // Small wait for async audit log
        
        assertThat(auditLogRepository.findAll()).isNotEmpty();
        assertThat(auditLogRepository.findAll().get(0).getEventType().name()).isEqualTo("LOGIN_SUCCESS");
    }

    @Test
    void testActuatorHealthEndpoint() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }
}
