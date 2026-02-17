package com.konasl.user.management.domain.audit.aspect;

import com.konasl.user.management.domain.audit.annotation.Auditable;
import com.konasl.user.management.domain.audit.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Aspect to intercept methods annotated with @Auditable and record audit events.
 */
@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {

    private final AuditService auditService;

    @AfterReturning(value = "@annotation(auditable)", returning = "result")
    public void auditAfterReturning(JoinPoint joinPoint, Auditable auditable, Object result) {
        recordEntry(auditable, "SUCCESS");
    }

    @AfterThrowing(value = "@annotation(auditable)", throwing = "ex")
    public void auditAfterThrowing(JoinPoint joinPoint, Auditable auditable, Throwable ex) {
        recordEntry(auditable, "FAILURE: " + ex.getMessage());
    }

    private void recordEntry(Auditable auditable, String status) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID actorId = null;
        String actorName = "SYSTEM";

        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            actorName = auth.getName();
            // In a real application, we would extract the userId from the authentication principal
        }

        auditService.recordEvent(
                auditable.eventType(),
                actorId,
                actorName,
                null, // resourceId (could be pulled from arguments via joinPoint)
                null, // resourceType
                null, // details
                status
        );
    }
}
