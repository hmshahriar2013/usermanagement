package com.konasl.user.management.domain.audit.service;

import com.konasl.user.management.domain.audit.model.AuditEventType;
import com.konasl.user.management.domain.audit.model.AuditLog;
import com.konasl.user.management.domain.audit.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service for managing audit logs with asynchronous persistence.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    /**
     * Records an audit event asynchronously in a new transaction.
     */
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordEvent(AuditEventType eventType, UUID actorId, String actorName, 
                             String resourceId, String resourceType, String details, String status) {
        
        try {
            AuditLog auditLog = AuditLog.builder()
                    .eventType(eventType)
                    .actorId(actorId)
                    .actorName(actorName)
                    .resourceId(resourceId)
                    .resourceType(resourceType)
                    .actionDetails(details)
                    .status(status)
                    .correlationId(MDC.get("correlationId"))
                    .build();

            auditLogRepository.save(auditLog);
            log.trace("Audit log recorded: type={}, status={}, correlationId={}", 
                    eventType, status, MDC.get("correlationId"));
        } catch (Exception e) {
            log.error("Failed to record audit log: type={}, status={}", eventType, status, e);
        }
    }
}
