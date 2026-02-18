# Phase 6 Implementation Plan: Integration & Final Polish

This final phase focuses on integrating a mock External Identity Provider (IdP) and reinforcing the system's security posture.

## User Review Required

> [!NOTE]
> - External IdP integration will be a **Mock Implementation** to demonstrate the pattern without requiring a live third-party service (like Google/GitHub).
> - Should we enforce a specific password complexity rule (e.g., minimum 10 chars, uppercase, special char)? (Recommended: Yes)

## Proposed Changes

### 1. Mock External IdP Integration
Demonstrate how the system can handle external authentication.

#### [NEW] [ExternalIdpService.java](file:///d:/Projects/Projects/Projects/User-Management-from-scratch/usermanagement/src/main/java/com/konasl/user/management/domain/auth/service/ExternalIdpService.java)
- A stub service that simulates an OIDC-like user profile retrieval.

#### [NEW] [ExternalAuthController.java](file:///d:/Projects/Projects/Projects/User-Management-from-scratch/usermanagement/src/main/java/com/konasl/user/management/api/auth/controller/ExternalAuthController.java)
- `POST /api/v1/auth/external/login`: Simulates a successful external callback.
- Logic: If external user (by email) doesn't exist, create a new Identity layer user with a unique external provider ID.

---

### 2. System Hardening

#### [MODIFY] [SecurityConfig.java](file:///d:/Projects/Projects/Projects/User-Management-from-scratch/usermanagement/src/main/java/com/konasl/user/management/security/SecurityConfig.java)
- Explicitly configure Security Headers (HSTS, Content Security Policy, Frame Options).
- Ensure actuator endpoints are properly secured if they weren't fully restricted (currently they are `permitAll` for demo, should be restricted).

#### [NEW] [PasswordPolicyValidator.java](file:///d:/Projects/Projects/Projects/User-Management-from-scratch/usermanagement/src/main/java/com/konasl/user/management/domain/auth/validator/PasswordPolicyValidator.java)
- Component to enforce minimum security standards for passwords during registration/change.

---

### 3. Final Polish
- Standardize all controller response messages.
- Final update to `docs/workspace_structure.md` via the Workspace Structure Manager skill.

## Verification Plan

### Automated Tests
- `ExternalAuthIntegrationTest`: Verify that external login correctly identifies/creates users and issues JWTs.
- `SecurityHardeningTest`: Verify that required security headers are present in responses.
- `PasswordPolicyTest`: Verify that weak passwords are rejected.

### Manual Verification
- Access `/swagger-ui.html` and test the simulated external login flow.
- Inspect HTTP headers using browser dev tools or `curl -v`.
