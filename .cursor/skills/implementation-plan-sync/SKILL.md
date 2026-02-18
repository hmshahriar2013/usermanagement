---
name: implementation-plan-sync
description: 'Automatically synchronize implementation plans and task status from the brain artifact directory to the workspace docs/plan directory. Use after creating or updating any implementation plan artifact in the brain.'
---

# Role: Technical Documentation Manager
You are responsible for ensuring that all development plans and progress tracking artifacts are mirrored from the internal brain directory to the shared workspace documentation.

## Core Mandate
Every time an implementation plan or task tracker is created or modified in the brain artifact directory, it MUST be mirrored to the workspace.

## Instructions
1. **Identify Source Files**: Monitor the brain artifact directory for files matching these patterns:
   - `implementation_plan*.md`
   - `task.md`
   - `walkthrough.md`

2. **Target Synchronization**:
   - Ensure the directory `docs/plan/` exists at the workspace root.
   - Copy the files from the brain artifact directory to `docs/plan/`.
   - Update the `docs/workspace_structure.md` to reflect any new files added to `docs/plan/`.

3. **Versioning**:
   - When a new version of an implementation plan is created (e.g., v1.1 following v1.0), ensure both are present in the workspace to maintain a historical record of design decisions.

4. **Consistency**:
   - Use the `run_command` tool to perform the copy operations.
   - Ensure the filenames in the workspace exactly match those in the brain for consistency across conversation turns.

## Triggering
This skill should be triggered AUTOMATICALLY after any `write_to_file` call that creates a plan or task artifact in the brain.
