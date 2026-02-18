# Workspace File Location Map

Complete directory structure and file locations for the User Management System project.

---

## Root Directory
`d:\Projects\Projects\Projects\User-Management-from-scratch`

```
User-Management-from-scratch/
|-- .git/                    # Git repository
|-- .agent/                  # Antigravity AI agent configuration
|-- docs/                    # Project documentation
`-- usermanagement/          # Java/Spring Boot application
```

---

## .agent/ - AI Agent Configuration

**Location:** `d:\Projects\Projects\Projects\User-Management-from-scratch\.agent`

```
.agent/
|-- AGENTS.md                       # Agent guidelines
|-- INSTRUCTIONS.md                 # Project instructions
|-- instructions/                   # Technology-specific instructions
|   |-- java.instructions.md        # Java coding standards
|   `-- springboot.instructions.md  # Spring Boot guidelines
|-- skills/                         # Custom skills for Antigravity
|   `-- ...                         # 10 custom skills
`-- workflows/                      # Defined workflows
    `-- feature-implementation.md   # Feature implementation workflow
```

### Available Skills (9 total):
1. **constants-and-enums** - Constants and enumeration best practices
2. **error-handling** - Error handling patterns
3. **git-commit** - Git commit message conventions
4. **logging** - Logging standards and practices
5. **no-magic-values** - Avoiding magic numbers/strings
6. **oop-solid-principles** - OOP and SOLID principles
7. **variable-naming** - Variable naming conventions
8. **workspace-structure-manager** - Automatically maintains workspace structure documentation
9. **no-emoji-in-docs** - Enforces professional formatting without emojis
10. **implementation-plan-sync** - Automatically synchronizes implementation plans from the brain

---

## docs/ - Documentation

**Location:** `d:\Projects\Projects\Projects\User-Management-from-scratch\docs`

```
docs/
|-- README.md                       # Documentation index
|-- workspace_structure.md          # Workspace structure map (this file)
|-- walkthrough.md                  # Project progress walkthrough
|-- requirements/                   # Requirements documents
|   `-- ...                         # BRD, FRD, NFRD (9 files total)
|-- design/                         # Architecture & design
|   `-- DDD-bounded-components.md   # DDD bounded contexts
`-- plan/                           # Project management & tasks
    |-- task.md                     # Current task progress & roadmap
    |-- implementation_plan.md      # Master implementation plan
    |-- implementation_plan_identity_model_v1.0.md
    |-- implementation_plan_identity_model_v1.1.md
    |-- implementation_plan_identity_model_v1.2.md
    |-- implementation_plan_auth_context_v1.0.md
    |-- implementation_plan_access_control_v1.0.md
    |-- implementation_plan_audit_observability_v1.0.md
    |-- implementation_plan_support_features_v1.0.md
    |-- implementation_plan_final_polish_v1.0.md
    `-- implementation_plan_registration_flow_v1.0.md
```

---

## usermanagement/ - Java/Spring Boot Application

**Location:** `d:\Projects\Projects\Projects\User-Management-from-scratch\usermanagement`

```
usermanagement/
|-- build.gradle                # Gradle build configuration
|-- settings.gradle             # Gradle settings
|-- src/
|   |-- main/
|   |   |-- java/com/konasl/user/management/
|   |   |   |-- Application.java            # Main Entry Point
|   |   |   |-- api/                        # API Versioning & Controllers
|   |   |   |   |-- ApiResponse.java        # Standard Response Wrapper
|   |   |   |   |-- access/                 # Access Control (RBAC) APIs
|   |   |   |   |-- auth/                   # Authentication APIs
|   |   |   |   |-- identity/               # Identity Management APIs (Pending)
|   |   |   |   `-- test/                   # Integration Test Controllers
|   |   |   |-- domain/                     # Bounded Contexts
|   |   |   |   |-- access/                 # RBAC Domain (Roles, Perms)
|   |   |   |   |-- auth/                   # Auth Domain (Credentials, Tokens)
|   |   |   |   |-- identity/               # Identity Domain (User Lifecycle)
|   |   |   |   `-- model/                  # Shared Domain Models (BaseEntity)
|   |   |   |-- exception/                  # Error Handling Architecture
|   |   |   |-- security/                   # Spring Security & JWT
|   |   |   |-- util/                       # Shared Utilities
|   |   |-- resources/
|   |   |   |-- application.properties      # Config
|   |   |   `-- schema-init.sql             # DB Initialization
|   `-- test/
|       `-- java/com/konasl/user/management/
|           |-- ApplicationTests.java
|           |-- integration/                # Integration Tests (Auth, Access)
|           `-- unit/                       # Unit Tests (Repositories, Services)
`-- ...                             # Logs and Gradle wrapper
```

**Package:** `com.konasl.user.management`

---

## Key File Locations Quick Reference

| File Type | Location |
|-----------|----------|
| **AI Agent Docs** | `.agent/AGENTS.md`, `.agent/INSTRUCTIONS.md` |
| **Custom Skills** | `.agent/skills/` |
| **Requirements** | `docs/requirements/` |
| **Implementation Plans** | `docs/plan/` |
| **Walkthrough** | `docs/walkthrough.md` |
| **Workspace Map** | `docs/workspace_structure.md` |
| **Main Config** | `usermanagement/src/main/resources/application.properties` |
| **Security Setup** | `usermanagement/src/main/java/com/konasl/user/management/security/SecurityConfig.java` |

---

## Summary

- **Total Skills:** 10 custom Antigravity skills
- **Documentation:** 25+ files (Requirements, Plans, Design, Walkthrough)
- **Bounded Contexts:** Identity, Authentication, Access Control (Implemented)
- **Project Type:** Java 21 / Spring Boot 3.4.2 / Gradle
- **Last Updated:** 2026-02-18 10:10
