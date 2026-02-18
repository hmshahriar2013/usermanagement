---
description: follow this workflow to implement a new feature
---

# Feature Implementation Workflow

This document defines the standardized procedure for implementing features and plans in the project using agentic AI. All future implementations must adhere to this lifecycle.

## 1. Preparation Phase

### Step 1: Plan Selection
Identify the specific feature plan in `docs/plan/` (e.g., `feature-identity-foundation-v1.0.md`).

### Step 2: Task Context
Review the atomic tasks (TASK-001, TASK-002, etc.) within the selected phase. Ensure technical requirements and constraints are understood. Discuss about the business logic, constrains, corner cases, input parameters, output parameters, definition of done, architecture implementation, DB design, etc.

### Step 3: Global Task Tracker Update
Update `docs/plan/task.md` to mark the upcoming phase or task as "In Progress" `[/]`.

## 2. Iterative Implementation Cycle

For each **Atomic Task** in the plan, follow these steps:

### Step A: Planning [Mode: PLANNING]
- Call `task_boundary` with `Mode: PLANNING`.
- Analyze the specific code items to be created or modified.
- Verify existing patterns in the codebase to ensure consistency (e.g., package structure, naming).

### Step B: Execution [Mode: EXECUTION]
- Call `task_boundary` with `Mode: EXECUTION`.
- Implement the code changes (Entities, Services, Controllers, etc.).
- Follow the architectural standards defined in `.agent/instructions/springboot.instructions.md`.

### Step C: Verification [Mode: VERIFICATION]
- Call `task_boundary` with `Mode: VERIFICATION`.
- Execute automated tests (Unit and Integration).
- Perform manual verification (e.g., API calls via `curl`, DB checks).
- If bugs are found, return to Step B.

## 3. Completion Phase

### Step 4: Documentation Update
- Mark the task as completed in the feature plan file (e.g., `feature-identity-foundation-v1.0.md`) using an 'X'.
- Update the detailed Task Summary in the `task_boundary`.

### Step 5: Artifact Sync
- Ensure all plan files and `task.md` are synchronized between the `.gemini` brain and the project's `docs/plan/` directory.

### Step 6: User Notification
- Use `notify_user` to provide a summary of work, test results, and provide a clickable link to the updated `docs/plan/task.md`.

---

## Example Traceability
Every implementation tool call should ideally reference the Task ID (e.g., "Implementing TASK-001 from feature-identity-foundation-v1.0.md").
