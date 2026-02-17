package com.konasl.user.management.domain.auth.repository;

import com.konasl.user.management.domain.auth.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for RefreshToken persistence.
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    
    /**
     * Finds a refresh token by its raw string value.
     */
    Optional<RefreshToken> findByTokenValue(String tokenValue);

    /**
     * Finds all tokens for a user (useful for revocation).
     */
    void deleteByUserId(UUID userId);
}
