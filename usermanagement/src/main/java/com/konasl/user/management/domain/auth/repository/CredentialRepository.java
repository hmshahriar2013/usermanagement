package com.konasl.user.management.domain.auth.repository;

import com.konasl.user.management.domain.auth.model.Credential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Credential persistence.
 */
@Repository
public interface CredentialRepository extends JpaRepository<Credential, UUID> {
    
    /**
     * Finds the credential associated with a specific user.
     */
    Optional<Credential> findByUserId(UUID userId);
}
