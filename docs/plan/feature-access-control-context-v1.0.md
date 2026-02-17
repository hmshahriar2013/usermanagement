---
goal: "Implement Access Control Context (Phase 3)"
version: 1.0
date_created: 2026-02-17
status: 'Planned'
tags: ["feature", "authorization", "rbac", "security"]
---

# Introduction

![Status: Planned](https://img.shields.io/badge/status-Planned-blue)

This phase implements the **Access Control Context**, defining the authorization model based on roles and permissions (RBAC). It ensures that authenticated identities can only perform actions permitted by their assigned roles.

## 1. Requirements & Constraints

- **REQ-001**: Support Role-Based Access Control (RBAC) (FR-AZ-01).
- **REQ-002**: Map multiple permissions to single or multiple roles (FR-RP-03).
- **REQ-003**: Assign one or more roles to a user (FR-AZ-02).
- **REQ-004**: Evaluate permissions dynamically during request processing (FR-AZ-04).
- **GUD-001**: Permissions should be granular (e.g., `user:create`, `role:view`).
- **GUD-002**: Roles should be organizational entities (e.g., `ADMIN`, `MANAGER`).

## 2. Implementation Steps

### Phase 3.1: Authorization Model

- GOAL-001: Define the Role and Permission entities.

| Task     | Description                                                                 | Completed | Date       |
| -------- | --------------------------------------------------------------------------- | --------- | ---------- |
| TASK-001 | Create `Role` and `Permission` entities with unique constraints             |           | 2026-02-17 |
| TASK-002 | Set up many-to-many relationships giữa User, Role, và Permission            |           | 2026-02-17 |
| TASK-003 | Implement `RoleRepository` and `PermissionRepository`                       |           | 2026-02-17 |

### Phase 3.2: RBAC Service Layer

- GOAL-002: Build the logic for access evaluation.

| Task     | Description                                                                 | Completed | Date       |
| -------- | --------------------------------------------------------------------------- | --------- | ---------- |
| TASK-004 | Implement `AccessControlService` to fetch user permissions                  |           | 2026-02-17 |
| TASK-005 | Implement `RoleManagementService` for CRUD operations on roles/perms        |           | 2026-02-17 |

### Phase 3.3: Enforcement & Integration

- GOAL-003: Secure the application using the authorization model.

| Task     | Description                                                                 | Completed | Date       |
| -------- | --------------------------------------------------------------------------- | --------- | ---------- |
| TASK-006 | Integrate User authorities into `JWTAuthenticationFilter`                   |           | 2026-02-17 |
| TASK-007 | Apply `@PreAuthorize` annotations on existing API endpoints                 |           | 2026-02-17 |
| TASK-008 | Create `RoleManagementController` for administrative management             |           | 2026-02-17 |

## 3. Alternatives

- **ALT-001**: Simple String-based roles (e.g., `ROLE_ADMIN`). *Rejected* because we need granular permissions mapping for compliance and scalability (FR-RP-03).
- **ALT-002**: External Authorization Service (OPA). *Rejected* for the initial phase to minimize architectural complexity.

## 4. Dependencies

- **DEP-001**: Spring Security Expression Language (SpEL)
- **DEP-002**: Hibernate Envers (Optional: for auditing changes to roles)

## 5. Files

- **FILE-001**: `com.konasl.user.management.domain.access.model.Role`
- **FILE-002**: `com.konasl.user.management.domain.access.model.Permission`
- **FILE-003**: `com.konasl.user.management.domain.access.service.AccessControlService`

## 6. Testing

- **TEST-001**: Unit tests for Permission aggregation logic.
- **TEST-002**: Integration tests for API endpoints verifying 403 Forbidden for unauthorized roles.

## 7. Risks & Assumptions

- **RISK-001**: Performance overhead of permission fetching. *Mitigation*: Cache user permissions in the JWT or a side-cache.
- **ASSUMPTION-001**: Initial roles will be pre-seeded via database migrations.

## 8. Related Specifications / Further Reading

- [FRD - Authorization Section](file:///d:/Projects/Projects/Projects/User-Management-from-scratch/docs/requirements/user_management_system_functional_requirements_document_frd.md#43-authorization)
