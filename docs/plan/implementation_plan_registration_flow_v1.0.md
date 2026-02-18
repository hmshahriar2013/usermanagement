# Implementation Plan: Validation & Registration Flow (Phase 1.4)

This plan addresses the missing public registration flow, allowing new users to create accounts securely without administrative intervention.

## User Review Required

> [!IMPORTANT]
> - New users will be assigned the `USER` role by default.
> - Registration will be open to the public (unauthenticated).
> - Initial status will be `ACTIVE` for this demo, though in production, it might be `PENDING` until email verification.

## Proposed Changes

### Auth Context

#### [NEW] [RegistrationRequest.java](file:///d:/Projects/Projects/Projects/User-Management-from-scratch/usermanagement/src/main/java/com/konasl/user/management/api/auth/dto/RegistrationRequest.java)
- Create DTO for public signup.
- Include fields: `username`, `email`, `password`, `firstName`, `lastName`.
- Add JSR-303 validation constraints.

#### [MODIFY] [AuthService.java](file:///d:/Projects/Projects/Projects/User-Management-from-scratch/usermanagement/src/main/java/com/konasl/user/management/domain/auth/service/AuthService.java)
- Implement `registerUser(RegistrationRequest request)`.
- Coordinate between `IdentityService` (for user creation) and `AuthService` (for credential creation).
- Assign default `USER` role to new registrants.

#### [MODIFY] [AuthController.java](file:///d:/Projects/Projects/Projects/User-Management-from-scratch/usermanagement/src/main/java/com/konasl/user/management/api/auth/controller/AuthController.java)
- Add `POST /signup` endpoint.
- Ensure it is accessible without authentication in `SecurityConfig`.

### Access Control Context

#### [MODIFY] [AccessControlService.java](file:///d:/Projects/Projects/Projects/User-Management-from-scratch/usermanagement/src/main/java/com/konasl/user/management/domain/access/service/AccessControlService.java)
- Add a method to conveniently assign a role to a user by role name (e.g., "USER").

### Security Configuration

#### [MODIFY] [SecurityConfig.java](file:///d:/Projects/Projects/Projects/User-Management-from-scratch/usermanagement/src/main/java/com/konasl/user/management/security/SecurityConfig.java)
- Permit all access to `POST /api/v1/auth/signup`.

## Verification Plan

### Automated Tests
- Create `RegistrationIntegrationTest`.
- Verify successful signup and subsequent login.
- Verify validation failures (duplicate email, weak password).

### Manual Verification
- Test registration flow via Postman/Swagger.
- Verify user record in H2 console.
