package com.konasl.user.management.domain.auth.model;

import com.konasl.user.management.domain.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing user credentials, logically separate from Identity.
 */
@Entity
@Table(name = "ums_credentials", schema = "ums_auth")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Credential extends BaseEntity {

    @Id
    @Column(name = "credential_id", updatable = false, nullable = false)
    @Builder.Default
    private UUID credentialId = UUID.randomUUID();

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "is_expired", nullable = false)
    @Builder.Default
    private boolean expired = false;

    @Column(name = "last_changed_at", nullable = false)
    @Builder.Default
    private LocalDateTime lastChangedAt = LocalDateTime.now();
}
