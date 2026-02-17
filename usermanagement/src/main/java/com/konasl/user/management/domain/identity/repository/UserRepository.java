package com.konasl.user.management.domain.identity.repository;

import com.konasl.user.management.domain.identity.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for User persistence operations.
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Finds a user by their unique username.
     */
    Optional<User> findByUsername(String username);

    /**
     * Finds a user by their unique email.
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks if a user already exists with the given username.
     */
    boolean existsByUsername(String username);

    /**
     * Checks if a user already exists with the given email.
     */
    boolean existsByEmail(String email);
}
