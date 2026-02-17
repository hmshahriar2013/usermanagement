package com.konasl.user.management.domain.audit.annotation;

import com.konasl.user.management.domain.audit.model.AuditEventType;
import java.lang.annotation.*;

/**
 * Annotation to mark methods for automated audit logging.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Auditable {
    /**
     * The type of event being audited.
     */
    AuditEventType eventType();
}
