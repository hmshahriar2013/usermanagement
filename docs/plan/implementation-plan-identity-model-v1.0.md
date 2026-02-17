# Implementation Plan: Phase 1.2 - Domain & Persistence Layer

This plan covers the implementation of the core domain model and persistence layer for the Identity Context.

## User Review Required

> [!IMPORTANT]
> I need clarification on the following items before proceeding to implementation:
> 1. **User Attributes**: Are `firstName` and `lastName` sufficient for the initial version? Should we include `middleName` or `phoneNumber`?
> 2. **Lifecycle States**: The DDD document mentions ACTIVE, SUSPENDED, and DEACTIVATED. Do we need a `PENDING_VERIFICATION` state for new registrations?
> 3. **Database Naming**: I propose naming the table `ums_users` to avoid potential reserved word conflicts and for better clarity. Is this acceptable?
> 4. **Audit Fields**: I will implement `createdAt`, `updatedAt`, `createdBy`, and `updatedBy`. Initially, `createdBy`/`updatedBy` will default to "SYSTEM".

## Proposed Changes

### [Identity Context]

#### [NEW] [UserStatus.java](file:///d:/Projects/Projects/Projects/User-Management-from-scratch/usermanagement/src/main/java/com/konasl/user/management/domain/identity/model/UserStatus.java)
Enum representing the lifecycle states of a user.
- `ACTIVE`, `SUSPENDED`, `DEACTIVATED`, (Optional: `PENDING`)

#### [NEW] [User.java](file:///d:/Projects/Projects/Projects/User-Management-from-scratch/usermanagement/src/main/java/com/konasl/user/management/domain/identity/model/User.java)
The root entity for the Identity context.
- [ ] Map to table `ums_users`.
- [ ] Unique constraints on `username` and `email`.
- [ ] Audit fields via JPA listeners.

#### [NEW] [UserRepository.java](file:///d:/Projects/Projects/Projects/User-Management-from-scratch/usermanagement/src/main/java/com/konasl/user/management/domain/identity/repository/UserRepository.java)
Standard Spring Data JPA interface.
- [ ] `findByUsername(String username)`
- [ ] `findByEmail(String email)`
- [ ] `existsByUsername(String username)`
- [ ] `existsByEmail(String email)`

## Verification Plan

### Automated Tests
- Unit tests for `User` entity validation and status transitions.
- DataJPA integration tests for `UserRepository` to verify constraints and query methods.

### Manual Verification
- Verify table creation in the H2/PostgreSQL console.
