package com.konasl.user.management.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.konasl.user.management.api.auth.dto.ExternalLoginRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class Phase6IntegrationTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @Test
        void testExternalLogin_Success() throws Exception {
                ExternalLoginRequest request = ExternalLoginRequest.builder()
                                .provider("google")
                                .idToken("test-user@example.com")
                                .build();

                mockMvc.perform(post("/api/v1/auth/external/login")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.data.accessToken").exists())
                                .andExpect(jsonPath("$.data.refreshToken").exists());
        }

        @Test
        void testSecurityHeaders() throws Exception {
                mockMvc.perform(get("/api/v1/auth/external/login")) // Just to get a response
                                .andExpect(header().string("Content-Security-Policy", "default-src 'self'"))
                                .andExpect(header().string("X-Frame-Options", "DENY"));
        }

        @Test
        void testActuatorSecurity() throws Exception {
                // Actuator endpoints should be restricted
                mockMvc.perform(get("/actuator/beans"))
                                .andExpect(status().isForbidden());

                // Health should be public
                mockMvc.perform(get("/actuator/health"))
                                .andExpect(status().isOk());
        }
}
