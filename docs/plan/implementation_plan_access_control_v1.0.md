# High-Detail Implementation Plan: Phase 3 - Access Control Context (RBAC)

This document specifies the technical design for the Access Control module, implementing Role-Based Access Control (RBAC) to secure the User Management System.

## 1. Requirements & Design
- **REQ-003**: Implement RBAC (FR-AZ-01).
- **Hierarchical Design**: Permissions are assigned to Roles, and Roles are assigned to Users.
- **Stateless Verification**: User permissions will be embedded in the JWT (optional/Phase 4) or fetched during authentication and cached in the security context.

---

## 2. Database Schema: `ums_access`

**Database Name**: `user_management`  
**Schema Name**: `ums_access`

### Table: `ums_roles`
| Column | Type | Constraints |
| :--- | :--- | :--- |
| `role_id` | UUID | **Primary Key**. |
| `name` | VARCHAR(50) | UNIQUE, NOT NULL. (e.g., ROLE_ADMIN, ROLE_USER) |
| `description` | VARCHAR(255) | |
| `version` | INT | NOT NULL (Audit). |
| `created_at` | TIMESTAMP | NOT NULL (Audit). |
| `updated_at` | TIMESTAMP | NOT NULL (Audit). |

### Table: `ums_permissions`
| Column | Type | Constraints |
| :--- | :--- | :--- |
| `permission_id` | UUID | **Primary Key**. |
| `name` | VARCHAR(50) | UNIQUE, NOT NULL. (e.g., identity:create, identity:view) |
| `description` | VARCHAR(255) | |
| `version` | INT | NOT NULL (Audit). |
| `created_at` | TIMESTAMP | NOT NULL (Audit). |
| `updated_at` | TIMESTAMP | NOT NULL (Audit). |

### Table: `ums_user_roles`
| Column | Type | Constraints |
| :--- | :--- | :--- |
| `id` | UUID | **Primary Key**. |
| `user_id` | UUID | Foreign Key ref `ums_core.ums_users(user_id)`. |
| `role_id` | UUID | Foreign Key ref `ums_access.ums_roles(role_id)`. |

### Table: `ums_role_permissions`
| Column | Type | Constraints |
| :--- | :--- | :--- |
| `id` | UUID | **Primary Key**. |
| `role_id` | UUID | Foreign Key ref `ums_access.ums_roles(role_id)`. |
| `permission_id` | UUID | Foreign Key ref `ums_access.ums_permissions(permission_id)`. |

---

## 3. Proposed Components

### Domain Layer
- `Role` Entity: Maps to `ums_roles`.
- `Permission` Entity: Maps to `ums_permissions`.
- `UserRole` Entity: Linkage between Users and Roles.
- `RolePermission` Entity: Linkage between Roles and Permissions.

### Service Layer
- `AccessControlService`: Manages roles, permissions, and assignments.
    - `assignRoleToUser(UUID userId, String roleName)`
    - `addPermissionToRole(String roleName, String permissionName)`
    - `getUserAuthorities(UUID userId)`: Returns a collection of roles and permissions.

### Security Integration
- `CustomUserDetailsService`: Update to load roles and permissions via `AccessControlService`.
- `SecurityConfig`: Enable `@EnableMethodSecurity`.

---

## 4. Implementation Roadmap

1. **Step 1: Schema & Entities**: Create `ums_access` schema and JPA entities.
2. **Step 2: Repositories & Service**: Implement the persistence and logic for role/permission management.
3. **Step 3: Security Wiring**: Integrate roles/permissions into `UserDetails` and enable method-level security.
4. **Step 4: API Layer**: Implement `AccessControlController` for administrative management.
5. **Step 5: Verification**: Integration tests for unauthorized access (403 Forbidden) and authorized access.

## 5. Architectural Notes
> [!NOTE]
> **Context Isolation**:
> - The Access Control context (`ums_access`) will refer to `user_id` from `ums_core`.
> - We will avoid cross-schema JPA joins by using service-level lookups for correlation.
