---
description: follow this workflow to implement a new feature or plan
---

1. **Business Logic & Clarification**
   - Identify the business logic of the feature/plan.
   - Discuss with the user to understand the feature completely.
   - **Properties Clarification**: Explicitly list and ask the user about all proposed properties, data types, and business rules before defining the model.
   - Clarify all business cases properly. 
   - **DO NOT GUESS**. Always clarify requirements from the user before proceeding.

2. **Database & Storage Planning**
   - Plan for new database implementations, modifications, or removals.
   - **Detailed Schema Specification**: You MUST share the following details for user approval:
     - Database name and Schema name.
     - Table names and Column definitions (Data types, Nullability, Defaults).
     - Primary Keys, Foreign Keys, and Constraints (Unique, Check).
     - Indexes and their purpose.
   - **DO NOT CHANGE CODE YET**. Discuss the database/storage plan with the user and obtain explicit clarification/approval first.

3. **Implementation**
   - Once requirements and plans are clarified, follow the feature implementation guidelines defined in:
     - [.agent/instructions/springboot.instructions.md](file:///d:/Projects/Projects/Projects/User-Management-from-scratch/.agent/instructions/springboot.instructions.md)
     - [.agent/AGENTS.md](file:///d:/Projects/Projects/Projects/User-Management-from-scratch/.agent/AGENTS.md)
     - [.agent/instructions/implementation-workflow.instructions.md](file:///d:/Projects/Projects/Projects/User-Management-from-scratch/.agent/instructions/implementation-workflow.instructions.md)
