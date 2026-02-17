package com.konasl.user.management.domain.access.model;

import com.konasl.user.management.domain.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.util.UUID;

/**
 * Entity representing a system permission (e.g., identity:view, role:write).
 */
@Entity
@Table(name = "ums_permissions", schema = "ums_access")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Permission extends BaseEntity {

    @Id
    @Column(name = "permission_id", updatable = false, nullable = false)
    @Builder.Default
    private UUID permissionId = UUID.randomUUID();

    @Column(name = "name", unique = true, nullable = false, length = 50)
    private String name;

    @Column(name = "description")
    private String description;
}
