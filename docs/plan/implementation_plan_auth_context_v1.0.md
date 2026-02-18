# High-Detail Implementation Plan: Phase 2 - Authentication Context

This document provides the technical specification for the Authentication Context, focusing on security infrastructure, credential management, and JWT-based session handling.

## 1. Requirements Recap & User Feedback
- ISS-001: JWT and Refresh Token expirations must be configurable.
- ISS-002: Credentials must be separate from Identity (`User` entity).
- ISS-003: Refresh tokens must be implemented for session longevity.

---

## 2. Infrastructure & Properties

### `application.properties` (Security Section)
```properties
# JWT Configuration
ums.security.jwt.secret=${JWT_SECRET:very-secret-key-32-chars-minimum-length}
ums.security.jwt.access-token-expiration=3600000 # 1 Hour in ms
ums.security.jwt.refresh-token-expiration=604800000 # 7 Days in ms

# Password Policy
ums.security.password.min-length=8
ums.security.password.max-retry-attempts=5
```

---

## 3. Database Schema: `ums_auth`

**Database Name**: `user_management`  
**Schema Name**: `ums_auth`

### Table: `ums_credentials`
| Column | Type | Constraints |
| :--- | :--- | :--- |
| `credential_id` | UUID | **Primary Key**. |
| `user_id` | UUID | Foreign Key ref `ums_core.ums_users(user_id)`. |
| `password_hash` | VARCHAR(255) | NOT NULL. |
| `is_expired` | BOOLEAN | DEFAULT FALSE. |
| `last_changed_at` | TIMESTAMP | NOT NULL. |
| `version` | INT | NOT NULL (Audit). |
| `created_at` | TIMESTAMP | NOT NULL (Audit). |
| `updated_at` | TIMESTAMP | NOT NULL (Audit). |

### Table: `ums_refresh_tokens`
| Column | Type | Constraints |
| :--- | :--- | :--- |
| `token_id` | UUID | **Primary Key**. |
| `user_id` | UUID | Foreign Key ref `ums_core.ums_users(user_id)`. |
| `token_value` | VARCHAR(255)| UNIQUE, NOT NULL. |
| `expiry_date` | TIMESTAMP | NOT NULL. |
| `revoked` | BOOLEAN | DEFAULT FALSE. |
| `created_at` | TIMESTAMP | NOT NULL (Audit). |

---

## 4. Proposed Components

### Security Core
- `SecurityConfig`: Configures `WebSecurity` and `SecurityFilterChain`.
- `JwtService`: Handles JWT creation, parsing, and validation.
- `JWTAuthenticationFilter`: Intercepts requests to validate Bearer tokens.

### Domain Layer (Auth Context)
- `Credential` Entity: Maps to `ums_credentials`.
- `RefreshToken` Entity: Maps to `ums_refresh_tokens`.
- `AuthService`: Coordinates login logic (BCrypt comparison, token issuance).

### API Layer
- `AuthController`:
    - `POST /api/v1/auth/login`: Issues Access + Refresh Tokens.
    - `POST /api/v1/auth/refresh`: Renews Access Token.
    - `POST /api/v1/auth/logout`: Revokes Refresh Token.

---

## 5. Implementation Roadmap

1. **Step 1: Security Dependencies**: Add `jjwt` and `spring-boot-starter-security` to `build.gradle`.
2. **Step 2: Credential & Token Models**: Implement persistence layer for auth schema.
3. **Step 3: Security Configuration**: Implementation of stateless filter chain.
4. **Step 4: Auth Flow**: Implementation of login and refresh services.
5. **Step 5: Verification**: Integration tests for the full login/refresh lifecycle.

## 4. Proposed Architectural Separation

> [!IMPORTANT]
> **Context Isolation Strategy (Approved):**
> - **Identity Context (`ums_core`)**: Owns `User` entity and profile data.
> - **Authentication Context (`ums_auth`)**: Owns `Credential` and `RefreshToken` entities.
> - **Repository Isolation**: The `auth` context will NOT join tables with `ums_core`. It will fetch credentials by `user_id` (the common correlation ID).
> - **Transaction Management**: Auth operations involving identity verification will be coordinated at the Service layer using IDs.

---

## 5. Proposed Components
...
[Rest of components as previously planned, but emphasizing local service lookups instead of JPA joins]
