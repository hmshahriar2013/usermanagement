package com.konasl.user.management.domain.access.model;

import com.konasl.user.management.domain.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.util.UUID;

/**
 * Entity representing a system role (e.g., ROLE_ADMIN, ROLE_USER).
 */
@Entity
@Table(name = "ums_roles", schema = "ums_access")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Role extends BaseEntity {

    @Id
    @Column(name = "role_id", updatable = false, nullable = false)
    @Builder.Default
    private UUID roleId = UUID.randomUUID();

    @Column(name = "name", unique = true, nullable = false, length = 50)
    private String name;

    @Column(name = "description")
    private String description;
}
