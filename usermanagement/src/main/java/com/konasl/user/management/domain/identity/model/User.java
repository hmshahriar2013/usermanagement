package com.konasl.user.management.domain.identity.model;

import com.konasl.user.management.domain.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Root entity for the Identity Context.
 * Manages user identification and lifecycle metadata.
 */
@Entity
@Table(
    name = "ums_users", 
    schema = "ums_core",
    indexes = {
        @Index(name = "idx_user_status", columnList = "status"),
        @Index(name = "idx_user_email", columnList = "email")
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {

    @Id
    @Column(name = "user_id", updatable = false, nullable = false)
    @Builder.Default
    private UUID userId = UUID.randomUUID();

    @Column(name = "username", unique = true, nullable = false, length = 100)
    private String username;

    @Column(name = "email", unique = true, nullable = false, length = 255)
    private String email;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private UserStatus status;

    @Column(name = "failed_login_count", nullable = false)
    @Builder.Default
    private Integer failedLoginCount = 0;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;
}
