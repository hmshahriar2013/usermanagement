# High-Detail Implementation Plan: Phase 1.2 - Identity Persistence

This document provides the technical blueprint for the Identity domain's persistence layer. No code will be written until all details here are approved.

## 1. Domain Model: User Entity

The `User` entity is the core of the Identity context. I propose the following properties and logic:

### Proposed Lifecycle States (`UserStatus` Enum)
| State | Description |
| :--- | :--- |
| `PENDING` | Default state upon registration (Requires verification). |
| `ACTIVE` | Verified user with full system access. |
| `SUSPENDED` | Temporary access restriction (e.g., after failed login attempts). |
| `DEACTIVATED` | Permanent closure of account (Soft delete). |

### Proposed User Attributes
| Attribute | Type | Description |
| :--- | :--- | :--- |
| `username` | String | Unique login identifier (alphanumeric, 4-50 chars). |
| `email` | String | Unique contact and login identifier. |
| `firstName` | String | User's given name. |
| `lastName` | String | User's family name. |
| `status` | Enum | Current lifecycle state. |
| `failedLoginAttempts` | Integer | Counter for security/suspension logic. |
| `lastLoginAt` | DateTime | Timestamp of the last successful session. |

---

## 2. Database Schema Definition

**Database Name**: `user_management_db`  
**Schema Name**: `ums_core` (Logical separation for Identity components)

### Table: `ums_users`
| Column | Type | Constraints |
| :--- | :--- | :--- |
| `user_id` | UUID | **Primary Key** (Generated). |
| `username` | VARCHAR(50) | UNIQUE, NOT NULL. |
| `email` | VARCHAR(255) | UNIQUE, NOT NULL. |
| `first_name` | VARCHAR(100) | NOT NULL. |
| `last_name` | VARCHAR(100) | NOT NULL. |
| `status` | VARCHAR(20) | NOT NULL (Value of Enum). |
| `failed_login_count`| INT | NOT NULL, DEFAULT 0. |
| `last_login_at` | TIMESTAMP | NULL. |
| `version` | INT | NOT NULL (Optimistic locking). |
| `created_at` | TIMESTAMP | NOT NULL (Audit). |
| `updated_at` | TIMESTAMP | NOT NULL (Audit). |
| `created_by` | VARCHAR(50) | NOT NULL (Audit). |
| `updated_by` | VARCHAR(50) | NOT NULL (Audit). |

### Constraints & Indexes
- **UC_USERNAME**: Unique constraint on `username`.
- **UC_EMAIL**: Unique constraint on `email`.
- **IDX_USER_STATUS**: Index on `status` for filtering by admins.
- **IDX_USER_EMAIL**: Non-clustered index on `email` for lookup performance.

---

## 3. Implementation Steps

1. **Step 1: Liquibase Migration (If using)**: Define the DDL for the `ums_users` table in the `ums_core` schema.
2. **Step 2: UserStatus Enum**: Implement with states: `PENDING`, `ACTIVE`, `SUSPENDED`, `DEACTIVATED`.
3. **Step 3: User Entity**: Implement JPA entity with `@Table(name = "ums_users", schema = "ums_core")` and required audit listeners.
4. **Step 4: UserRepository**: Implement interface with existence and find-by logic.

---

## 4. Questions for User Clarification

> [!IMPORTANT]
> Please provide feedback on the following:
> 1. **Data Types**: Is `UUID` preferred for the `user_id` (recommended for distributed systems) or a standard `Long` sequence?
> 2. **Schema Name**: Is the schema name `ums_core` acceptable, or should all tables reside in the `public` schema?
> 3. **User Properties**: Do you want to include a `middleName` or `phoneNumber` in this foundational phase?
> 4. **Constraint Logic**: Should `username` allow special characters, or be restricted to alphanumeric?
> 5. **Audit Default**: For initial setup, `created_by` will be hardcoded to "SYSTEM". Does this meet your compliance needs?
