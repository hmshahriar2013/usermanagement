# Workspace Structure Manager - Usage Examples

## Example 1: Automatic Update After Creating New Files

### Scenario
You create new service and repository files in a Spring Boot application.

### Before
```
src/main/java/com/konasl/user/management/
└── Application.java
```

### Action
```java
// Create UserService.java
// Create UserRepository.java
// Create UserController.java
```

### Skill Execution
```
1. Detect 3 new files created
2. Scan workspace to locate new files
3. Update workspace_structure.md incrementally:
   - Add UserService.java to services section
   - Add UserRepository.java to repositories section
   - Add UserController.java to controllers section
4. Update file counts in summary
5. Update timestamp
6. Sync both artifact and project copies
```

### After
```
src/main/java/com/konasl/user/management/
├── Application.java
├── controller/
│   └── UserController.java
├── service/
│   └── UserService.java
└── repository/
    └── UserRepository.java
```

---

## Example 2: Automatic Update After Deleting Files

### Scenario
You remove deprecated configuration files.

### Before
```
src/main/resources/
├── application.properties
├── application-dev.properties (deprecated)
└── application-old.yml (deprecated)
```

### Action
```
Delete application-dev.properties
Delete application-old.yml
```

### Skill Execution
```
1. Detect 2 files removed
2. Update workspace_structure.md incrementally:
   - Remove application-dev.properties entry
   - Remove application-old.yml entry
3. Update file counts in summary
4. Update timestamp
5. Sync both copies
```

### After
```
src/main/resources/
└── application.properties
```

---

## Example 3: Manual Invocation

### User Request
```
USER: Update the workspace structure documentation
```

### Skill Execution
```
ASSISTANT: [Reads workspace-structure-manager SKILL.md]
ASSISTANT: [Performs full workspace scan]
ASSISTANT: [Compares with existing documentation]
ASSISTANT: [Updates both documentation files]
ASSISTANT: "I've updated the workspace structure documentation."
```

---

## Example 4: After Scaffolding New Module

### Scenario
You scaffold a new authentication module.

### Action
```
Create src/main/java/com/konasl/user/management/auth/
Create src/main/java/com/konasl/user/management/auth/AuthController.java
Create src/main/java/com/konasl/user/management/auth/AuthService.java
Create src/main/java/com/konasl/user/management/auth/JwtUtil.java
Create src/test/java/com/konasl/user/management/auth/AuthServiceTest.java
```

### Skill Execution
```
1. Detect new directory and 4 new files
2. Update workspace_structure.md:
   - Add new "auth/" section
   - List all authentication-related files
   - Update package structure diagram
3. Update summary with new module information
4. Sync both copies
```

---

## Example 5: Silent Background Operation

### Scenario
During a refactoring task, multiple files are created and deleted.

### Task Flow
```
TASK: Refactor user management to use DTOs

ASSISTANT: [Creates UserDTO.java, UserMapper.java]
ASSISTANT: [Silently invokes workspace-structure-manager]
ASSISTANT: [Updates documentation in background]
ASSISTANT: [Deletes old User.java]
ASSISTANT: [Silently invokes workspace-structure-manager again]
ASSISTANT: [Updates documentation in background]
ASSISTANT: [Continues with refactoring]
ASSISTANT: "Refactoring complete. User management now uses DTOs."

# User never sees workspace structure update notifications
```

---

## Example 6: Handling Excluded Directories

### Scenario
Build generates output files that should be ignored.

### Action
```
Gradle build creates:
- build/classes/
- build/libs/
- .gradle/cache/
```

### Skill Execution
```
1. Scan workspace
2. Detect new files in build/ and .gradle/
3. Check exclusion list
4. Skip these directories (they match exclusion patterns)
5. No update to documentation
```

---

## Integration Example: With Git Commit Skill

### Scenario
Committing changes that include new files.

### Workflow
```
ASSISTANT: [Creates new feature files]
ASSISTANT: [Invokes workspace-structure-manager silently]
ASSISTANT: [Documentation updated]
ASSISTANT: [Invokes git-commit skill]
ASSISTANT: [Commits both code and updated documentation]
```

### Result
```
git commit -m "feat: add user authentication module

- Added AuthController, AuthService, JwtUtil
- Updated workspace structure documentation"
```

---

## Best Practice Example: Incremental Update

### Before Document (Partial)
```markdown
## ☕ `user.management/` - Java/Spring Boot Application

```
user.management/
└── user.management/
    └── src/
        └── main/
            └── java/
                └── com/konasl/user/management/
                    └── Application.java
```

**Package:** `com.konasl.user.management`
**Total Files:** 1
```

### After Adding UserController.java (Incremental Update)
```markdown
## ☕ `user.management/` - Java/Spring Boot Application

```
user.management/
└── user.management/
    └── src/
        └── main/
            └── java/
                └── com/konasl/user/management/
                    ├── Application.java
                    └── controller/
                        └── UserController.java
```

**Package:** `com.konasl.user.management`
**Total Files:** 2
```

**Note:** Only the affected section was updated, not the entire document.
