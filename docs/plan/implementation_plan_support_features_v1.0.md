# Phase 5 Implementation Plan: Support Features & Polish

This phase focuses on enhancing the user experience and providing administrative tools for bulk operations, as well as final polishing and documentation.

## User Review Required

> [!IMPORTANT]
> - Bulk import will support JSON format initially. Should CSV support be prioritized?
> - For "Change Password", do we require the old password as a security measure? (Recommended: Yes)

## Proposed Changes

### 1. Foundational User CRUD (Admin)
Address missing REQ-001 by implementing formal administrative user management.

#### [NEW] [AdminUserController.java](file:///d:/Projects/Projects/Projects/User-Management-from-scratch/usermanagement/src/main/java/com/konasl/user/management/api/identity/controller/AdminUserController.java)
- `POST /api/v1/admin/users`: Manual user creation.
- `GET /api/v1/admin/users/{userId}`: Fetch specific user details.
- `PUT /api/v1/admin/users/{userId}`: Update user (Admin).
- `PATCH /api/v1/admin/users/{userId}/status`: Lifecycle management (Activate/Suspend).

#### [MODIFY] [IdentityService.java](file:///d:/Projects/Projects/Projects/User-Management-from-scratch/usermanagement/src/main/java/com/konasl/user/management/domain/identity/service/IdentityService.java)
- Add `createUser(UserRequest request)`
- Add `updateUser(UUID userId, UserRequest request)`
- Add `updateStatus(UUID userId, UserStatus status)`

---

### 2. User Self-Service & Profiles
Empower users to manage their own information.

#### [NEW] [UserController.java](file:///d:/Projects/Projects/Projects/User-Management-from-scratch/usermanagement/src/main/java/com/konasl/user/management/api/identity/controller/UserController.java)
- `GET /api/v1/users/me`: Returns current user's profile.
- `PUT /api/v1/users/me`: Updates current user's profile (First/Last Name).
- `POST /api/v1/users/me/change-password`: Self-service password change.

#### [MODIFY] [AuthService.java](file:///d:/Projects/Projects/Projects/User-Management-from-scratch/usermanagement/src/main/java/com/konasl/user/management/domain/auth/service/AuthService.java)
- Add `changePassword(UUID userId, String oldPassword, String newPassword)`
- Integrate with `PasswordEncoder` for verification.

---

### 3. Administrative Bulk Operations

#### [NEW] [BulkOperationController.java](file:///d:/Projects/Projects/Projects/User-Management-from-scratch/usermanagement/src/main/java/com/konasl/user/management/api/admin/controller/BulkOperationController.java)
- `POST /api/v1/admin/users/import`: Bulk import users (JSON array).
- `GET /api/v1/admin/users/export`: Export all users as JSON.

---

### 4. API Documentation (OpenAPI)

#### [MODIFY] [build.gradle](file:///d:/Projects/Projects/Projects/User-Management-from-scratch/usermanagement/build.gradle)
- Add `org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.4` dependency.

---

## Verification Plan

### Automated Tests
- `AdminUserIntegrationTest`: Verify Admin CRUD and lifecycle status changes.
- `UserProfileIntegrationTest`: Verify "Me" API and profile updates.
- `BulkOperationIntegrationTest`: Verify bulk import/export functionality.
- `SecurityIntegrationTest`: Ensure `/api/v1/admin/**` requires `admin` authority.

---

### Manual Verification
- Access `/swagger-ui.html` to verify all endpoints are documented.
- Verify JWT principal extraction in `UserController.me`.
