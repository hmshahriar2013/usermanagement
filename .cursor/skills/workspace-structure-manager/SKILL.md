---
name: workspace-structure-manager
description: Automatically maintains and updates workspace structure documentation when files are added or removed
---

# Workspace Structure Manager Skill

## Purpose

This skill enables Antigravity to automatically track and maintain comprehensive workspace structure documentation. It monitors the workspace for file additions, deletions, and structural changes, then incrementally updates the workspace structure document in both the artifact directory and the project documentation folder.

## When to Use This Skill

### Automatic Triggers (Silent Background Updates)
You should **automatically** invoke this skill when:
- Creating new files or directories in the workspace
- Deleting files or directories from the workspace
- Moving files between directories
- Completing a task that modified the workspace structure
- After scaffolding a new project or module

### Manual Triggers
Invoke this skill when the user explicitly requests:
- "Update workspace structure"
- "Scan workspace files"
- "Refresh project structure documentation"
- "Map the workspace"

## Monitored Directories

### Include
Monitor these directories for changes:
- `.agent/` - AI agent configuration and skills
- `docs/` - Project documentation
- `src/` or language-specific source directories
- Configuration files in the root directory
- Any custom project directories

### Exclude (Do NOT monitor)
Ignore these directories to avoid noise:
- `.git/` - Git internal files
- `node_modules/` - Node.js dependencies
- `build/`, `dist/`, `target/` - Build outputs
- `.gradle/`, `.maven/` - Build tool caches
- `venv/`, `env/`, `.venv/` - Python virtual environments
- `bin/`, `obj/` - Compiled binaries
- `.idea/`, `.vscode/` - IDE configuration
- `__pycache__/`, `*.pyc` - Python cache files
- `coverage/`, `.nyc_output/` - Test coverage reports
- Any directory matching `**/temp`, `**/tmp`, `**/cache`

## Implementation Instructions

### Step 1: Scan the Workspace

Use the `find_by_name` tool to discover files and directories:

```
find_by_name(
  SearchDirectory: <workspace_root>,
  Pattern: "*",
  MaxDepth: 10,
  Excludes: [".git", "node_modules", "build", "dist", "target", ".gradle", 
             "venv", ".venv", "bin", "obj", "__pycache__", "coverage"]
)
```

### Step 2: Identify Changes

Compare the current scan results with the existing workspace structure document to identify:
- **New files/directories** - Added since last update
- **Removed files/directories** - Deleted since last update
- **Moved files/directories** - Changed location

### Step 3: Update Documentation Incrementally

**DO NOT** regenerate the entire document. Instead:

1. **For new files/directories:**
   - Add entries to the appropriate section
   - Update file counts and summaries
   - Maintain the existing document structure

2. **For removed files/directories:**
   - Remove the corresponding entries
   - Update file counts and summaries

3. **For moved files:**
   - Remove from old location
   - Add to new location

### Step 4: Update Both Locations

Maintain the workspace structure document in **two locations**:

1. **Artifact location** (for Antigravity):
   ```
   <appDataDir>/brain/<conversation-id>/workspace_structure.md
   ```

2. **Project location** (for other AI agents and developers):
   ```
   <workspace_root>/docs/workspace_structure.md
   ```

**IMPORTANT:** Keep both files synchronized. Update both locations with identical content.

### Step 5: Silent Execution

- Do NOT notify the user about the update
- Do NOT create separate task boundaries for this skill
- Execute updates in the background as part of your current task
- Only log the update in your internal task summary

## Document Structure Template

Use this structure for the workspace documentation:

```markdown
# Workspace File Location Map

Complete directory structure and file locations for [Project Name].

---

## 📂 Root Directory
`<absolute_path>`

```
[ASCII tree structure]
```

---

## [Section for each major directory]

**Location:** `<absolute_path>`

```
[Detailed tree structure]
```

[Description of contents]

---

## 🗂️ Key File Locations Quick Reference

| File Type | Location |
|-----------|----------|
| ... | ... |

---

## 📋 Summary

- **Total Files:** X
- **Total Directories:** Y
- **Project Type:** [Type]
- **Last Updated:** [Timestamp]
```

## Example Usage

### Example 1: After Creating New Files

```
# User creates new controller files
ASSISTANT: [Creates UserController.java, ProductController.java]
ASSISTANT: [Silently invokes workspace-structure-manager skill]
ASSISTANT: [Scans workspace, detects 2 new files]
ASSISTANT: [Updates both workspace_structure.md files incrementally]
ASSISTANT: [Continues with main task without notifying user]
```

### Example 2: Manual Invocation

```
USER: Update the workspace structure documentation
ASSISTANT: [Invokes workspace-structure-manager skill]
ASSISTANT: [Performs full scan]
ASSISTANT: [Updates documentation]
ASSISTANT: "I've updated the workspace structure documentation in both locations."
```

## Best Practices

1. **Incremental Updates Only**: Never regenerate the entire document unless explicitly requested
2. **Preserve Formatting**: Maintain the existing document structure and formatting
3. **Accurate Paths**: Always use absolute paths in the documentation
4. **Timestamp Updates**: Update the "Last Updated" timestamp on every change
5. **Sync Both Locations**: Ensure artifact and project copies are identical
6. **Silent Operation**: Don't interrupt the user's workflow with notifications
7. **Efficient Scanning**: Use appropriate MaxDepth to avoid scanning too deep

## Error Handling

If errors occur:
- **File access errors**: Skip the problematic file and continue
- **Document parsing errors**: Regenerate the affected section only
- **Path resolution errors**: Log the error but don't fail the entire update

## Integration with Other Skills

This skill works seamlessly with:
- **git-commit**: Update structure before committing
- **logging**: Log structure changes for audit trails
- **error-handling**: Handle file system errors gracefully

## Notes

- This skill operates in the background and should not create separate task boundaries
- Updates should be performed after file operations, not before
- The skill should be invoked at the end of a task that modifies workspace structure
- For large workspaces (>1000 files), consider updating only affected sections
