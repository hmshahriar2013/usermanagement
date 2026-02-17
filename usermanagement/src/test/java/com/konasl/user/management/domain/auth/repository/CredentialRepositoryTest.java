package com.konasl.user.management.domain.auth.repository;

import com.konasl.user.management.domain.auth.model.Credential;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CredentialRepositoryTest {

    @Autowired
    private CredentialRepository credentialRepository;

    @Test
    @DisplayName("Should save and find credential by userId")
    void saveAndFindByUserId() {
        // Given
        UUID userId = UUID.randomUUID();
        Credential credential = Credential.builder()
                .userId(userId)
                .passwordHash("hashed-password")
                .build();

        // When
        credentialRepository.save(credential);
        Optional<Credential> found = credentialRepository.findByUserId(userId);

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getUserId()).isEqualTo(userId);
        assertThat(found.get().getPasswordHash()).isEqualTo("hashed-password");
        assertThat(found.get().getCreatedAt()).isNotNull();
    }
}
