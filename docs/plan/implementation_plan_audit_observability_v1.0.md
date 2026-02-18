# High-Detail Implementation Plan: Phase 4 - Audit & Observability

This plan outlines the implementation of system transparency and reliability through structured logging, audit trails, and health monitoring.

## 1. Requirements & Design
- **REQ-004**: Audit Trail (FR-AL-01) - Capture all security and administrative events.
- **NFR-OBS-01**: Structured Logging - Provide machine-readable logs with correlation IDs.
- **Health Checks**: Expose system health for monitoring.

---

## 2. Database Schema: `ums_audit`

**Schema Name**: `ums_audit`

### Table: `audit_logs`
| Column | Type | Constraints |
| :--- | :--- | :--- |
| `audit_id` | UUID | **Primary Key**. |
| `event_type` | VARCHAR(50) | NOT NULL. (e.g., LOGIN_SUCCESS, USER_CREATED) |
| `actor_id` | UUID | ID of the user performing the action. |
| `actor_name` | VARCHAR(100) | Username of the actor. |
| `resource_id` | VARCHAR(100) | ID of the affected resource. |
| `resource_type` | VARCHAR(50) | Type of resource (e.g., USER, ROLE). |
| `action_details` | TEXT | JSON or string summary of the change. |
| `status` | VARCHAR(20) | SUCCESS, FAILURE. |
| `ip_address` | VARCHAR(45) | Client IP. |
| `correlation_id` | VARCHAR(50) | Correlation ID for log tracing. |
| `timestamp` | TIMESTAMP | NOT NULL. |

---

## 3. Proposed Components

### Correlation ID Filter
- `CorrelationIdFilter`: Intercepts HTTP requests, extracts or generates a `X-Correlation-ID`, and adds it to the SLF4J MDC (Mapped Diagnostic Context).

### Audit Logging Context
- `AuditLog` Entity: Maps to `audit_logs` table.
- `AuditLogRepository`: Standard Spring Data JPA repository.
- `AuditService`: Service to persist audit events.
- `@Auditable` Annotation: Custom annotation to mark methods for automatic auditing.
- `AuditAspect`: Spring AOP aspect that intercepts `@Auditable` methods and records events.

### Observability Utilities
- `HealthConfig`: Configures Spring Boot Actuator.
- `LoggingAspect`: Standard aspect for logging service method entry/exit with execution time.

---

## 4. Implementation Roadmap

1. **Step 1: Correlation & Logging Infrastructure**:
    - Implement `CorrelationIdFilter`.
    - Configure Logback for structured output (including MDC fields).
2. **Step 2: Audit Domain**:
    - Create `ums_audit` schema in `schema-init.sql`.
    - Implement `AuditLog` entity and repository.
3. **Step 3: AOP Auditing**:
    - Implement `AuditService` and `@Auditable` annotation.
    - Create `AuditAspect` to capture events asynchronously.
4. **Step 4: Observability Wiring**:
    - Add `spring-boot-starter-actuator` to `build.gradle`.
    - Configure endpoints (Health, Info, Metrics).
5. **Step 5: Verification**:
    - verify correlation IDs in application logs.
    - Verify audit log persistence after login and admin actions.
    - Verify health check endpoint response.

---

## 5. Architectural Notes
> [!IMPORTANT]
> **Asynchronous Auditing**: Audit logging must not block the main transaction. We will use Spring's `@Async` or a dedicated event publisher to ensure auditing failures do not impact business logic.

> [!NOTE]
> **Privacy**: Ensure sensitive data (passwords, PII in some contexts) is not recorded in the `action_details`.
