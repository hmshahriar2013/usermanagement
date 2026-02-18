# High-Detail Implementation Plan: Phase 1.2 - Identity Persistence (FINAL)

This document provides the finalized technical blueprint for the Identity domain's persistence layer.

## 1. Domain Model: User Entity

The `User` entity is the core of the Identity context.

### Lifecycle States (`UserStatus` Enum)
| State | Description |
| :--- | :--- |
| `PENDING` | Default state upon registration (Requires verification). |
| `ACTIVE` | Verified user with full system access. |
| `SUSPENDED` | Temporary access restriction (e.g., after failed login attempts). |
| `DEACTIVATED` | Permanent closure of account (Soft delete pattern). |

### User Attributes
| Attribute | Type | Description |
| :--- | :--- | :--- |
| `userId` | UUID | **Primary Key** (Randomly generated). |
| `username` | String | Unique login identifier. Supports Unicode (Korean, Latin, etc.). |
| `email` | String | Unique contact and login identifier. |
| `firstName` | String | User's given name. |
| `lastName` | String | User's family name. |
| `status` | Enum | Current lifecycle state. |
| `failedLoginAttempts` | Integer | Counter for security/suspension logic. |
| `lastLoginAt` | DateTime | Timestamp of the last successful session. |

---

## 2. Database Schema Definition

**Database Name**: `user_management`  
**Schema Name**: `ums_core`

### Table: `ums_users`
| Column | Type | Constraints |
| :--- | :--- | :--- |
| `user_id` | UUID | **Primary Key**. |
| `username` | VARCHAR(100) | UNIQUE, NOT NULL. Collation: `C` or `en_US.UTF-8` (UTF-8 by default). |
| `email` | VARCHAR(255) | UNIQUE, NOT NULL. |
| `first_name` | VARCHAR(100) | NOT NULL. |
| `last_name` | VARCHAR(100) | NOT NULL. |
| `status` | VARCHAR(20) | NOT NULL. |
| `failed_login_count`| INT | NOT NULL, DEFAULT 0. |
| `last_login_at` | TIMESTAMP | NULL. |
| `version` | INT | NOT NULL (Optimistic locking). |
| `created_at` | TIMESTAMP | NOT NULL (Audit). |
| `updated_at` | TIMESTAMP | NOT NULL (Audit). |
| `created_by` | VARCHAR(50) | NOT NULL (Audit, Default: "SYSTEM"). |
| `updated_by` | VARCHAR(50) | NOT NULL (Audit, Default: "SYSTEM"). |

### Constraints & Indexes
- **UC_USERNAME**: Unique constraint on `username`.
- **UC_EMAIL**: Unique constraint on `email`.
- **IDX_USER_STATUS**: Index on `status` for admin filtering.
- **IDX_USER_EMAIL**: Non-clustered index on `email` for lookup performance.

---

## 3. Implementation Steps

1. **Step 1: UserStatus Enum**: Implement in `com.konasl.user.management.domain.identity.model`.
2. **Step 2: User Entity**: Implement JPA entity with `@Table(name = "ums_users", schema = "ums_core")`.
3. **Step 3: Audit Listener**: Create or use a BaseEntity for common audit fields.
4. **Step 4: UserRepository**: Implement interface in `com.konasl.user.management.domain.identity.repository`.
5. **Step 5: Verification**: Unit and DataJPA tests.
