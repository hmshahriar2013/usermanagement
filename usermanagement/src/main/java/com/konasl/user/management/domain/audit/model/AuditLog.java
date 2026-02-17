package com.konasl.user.management.domain.audit.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing an audit log entry.
 */
@Entity
@Table(name = "ums_audit_logs", schema = "ums_audit")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID auditId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuditEventType eventType;

    private UUID actorId;
    private String actorName;
    private String resourceId;
    private String resourceType;
    
    @Column(columnDefinition = "TEXT")
    private String actionDetails;
    
    private String status;
    private String ipAddress;
    private String correlationId;
    
    @Column(nullable = false)
    private LocalDateTime timestamp;

    @PrePersist
    protected void onCreate() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }
}
