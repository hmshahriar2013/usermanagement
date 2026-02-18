# Workspace Structure Manager Skill

Automatically maintains and updates workspace structure documentation when files are added or removed.

## Overview

This skill enables Antigravity to track workspace changes and keep structure documentation up-to-date automatically. It monitors selected directories, excludes build artifacts and dependencies, and performs incremental updates to documentation in both the artifact directory and project docs folder.

## Features

✅ **Automatic Monitoring** - Detects file additions, deletions, and moves  
✅ **Incremental Updates** - Updates only changed sections, not entire document  
✅ **Dual Location Sync** - Maintains docs in artifact and project locations  
✅ **Smart Exclusions** - Ignores .git, node_modules, build outputs, etc.  
✅ **Silent Operation** - Updates in background without user interruption  
✅ **Manual Trigger** - Can be invoked on-demand by user request  

## Files

- **SKILL.md** - Main skill instructions for Antigravity
- **examples/usage_examples.md** - Detailed usage scenarios and examples
- **scripts/scan-workspace.ps1** - PowerShell helper script for workspace scanning

## Usage

### Automatic (Silent)
The skill automatically activates when:
- Creating or deleting files/directories
- Moving files between directories
- Completing tasks that modify workspace structure

### Manual
User can request updates with:
- "Update workspace structure"
- "Scan workspace files"
- "Refresh project structure documentation"

## Monitored Directories

**Included:**
- `.agent/` - AI configuration
- `docs/` - Documentation
- `src/` - Source code
- Root configuration files

**Excluded:**
- `.git/`, `node_modules/`, `build/`, `dist/`, `target/`
- `.gradle/`, `venv/`, `.venv/`, `bin/`, `obj/`
- `__pycache__/`, `coverage/`, `.idea/`, `.vscode/`

## Documentation Locations

1. **Artifact:** `<appDataDir>/brain/<conversation-id>/workspace_structure.md`
2. **Project:** `<workspace_root>/docs/workspace_structure.md`

Both locations are kept synchronized.

## Integration

Works seamlessly with other skills:
- **git-commit** - Update structure before commits
- **logging** - Log structure changes
- **error-handling** - Handle file system errors

## Notes

- Operates in background without creating separate task boundaries
- Updates performed after file operations, not before
- For large workspaces (>1000 files), updates only affected sections
- Preserves existing document formatting and structure
