package com.konasl.user.management.domain.identity.repository;

import com.konasl.user.management.domain.identity.model.User;
import com.konasl.user.management.domain.identity.model.UserStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Should save and find user by username with Unicode support")
    void saveAndFindByUsername() {
        // Given
        String koreanUsername = "사용자123"; // "user123" in Korean
        User user = User.builder()
                .username(koreanUsername)
                .email("korean.user@example.com")
                .firstName("Gildong")
                .lastName("Hong")
                .status(UserStatus.ACTIVE)
                .build();

        // When
        userRepository.save(user);
        Optional<User> found = userRepository.findByUsername(koreanUsername);

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo(koreanUsername);
        assertThat(found.get().getCreatedAt()).isNotNull();
        assertThat(found.get().getCreatedBy()).isEqualTo("SYSTEM");
    }

    @Test
    @DisplayName("Should return true when user exists by email")
    void existsByEmail() {
        // Given
        String email = "unique@example.com";
        User user = User.builder()
                .username("uniqueUser")
                .email(email)
                .firstName("Unique")
                .lastName("User")
                .status(UserStatus.PENDING)
                .build();
        userRepository.save(user);

        // When
        boolean exists = userRepository.existsByEmail(email);

        // Then
        assertThat(exists).isTrue();
    }
}
