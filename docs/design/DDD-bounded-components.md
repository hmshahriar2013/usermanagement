Good. Now we stop talking about “the system” and start talking about multiple models that must not bleed into each other. Identity systems collapse when everything shares the same language and database.

We’ll do this in a disciplined DDD way:

Identify bounded contexts

Define their responsibilities and boundaries

Classify each as Core, Supporting, or Generic

Call out explicitly what must not live together

This is not about microservices yet. It’s about meaning.

1. Core Domain Bounded Contexts

These are the parts of the system that encode your unique business rules and deserve the most care.

1.1 Identity Context (CORE)

Purpose
Defines who a user is and their lifecycle inside the organization.

Owns

User

Identity

User Status

User Lifecycle (create, activate, suspend, deactivate)

Identity uniqueness rules

Key Responsibilities

Maintain user identity as a first-class concept

Enforce lifecycle invariants

Prevent duplicate or inconsistent identities

Explicit Boundaries

Does NOT know about permissions

Does NOT make authorization decisions

Does NOT issue tokens

Why Core?
Identity lifecycle and governance are business-specific and legally sensitive. Mistakes here are expensive and visible.

1.2 Access Control Context (CORE)

Purpose
Defines what an authenticated identity is allowed to do.

Owns

Role

Permission

Access Policy

Authorization decision logic

Key Responsibilities

Evaluate access requests

Enforce RBAC and future policy rules

Produce deterministic authorization outcomes

Explicit Boundaries

Does NOT authenticate users

Does NOT manage user lifecycle

Does NOT store credentials

Why Core?
Authorization rules encode organizational structure, compliance rules, and risk posture. This is not a commodity decision engine.

2. Supporting Domain Bounded Contexts

These support the core but do not define the business’s unique value.

2.1 Authentication Context (SUPPORTING)

Purpose
Proves that a user is who they claim to be.

Owns

Credentials

Authentication flows

MFA challenges

Token issuance

Key Responsibilities

Authenticate identities

Issue and validate tokens

Manage credential policies

Explicit Boundaries

Does NOT decide authorization

Does NOT own user lifecycle rules

Treats Identity as read-only input

Why Supporting?
Authentication mechanisms evolve rapidly and often align with standards. The rules are important, but not unique.

2.2 Provisioning Context (SUPPORTING)

Purpose
Manages onboarding, offboarding, and bulk identity operations.

Owns

User creation workflows

Bulk imports

Deprovisioning orchestration

Key Responsibilities

Coordinate lifecycle events

Trigger access revocation

Integrate with HR or external systems

Explicit Boundaries

Does NOT own identity data

Does NOT define access rules

Why Supporting?
Provisioning is workflow-heavy and integration-driven, not conceptually core.

3. Generic Domain Bounded Contexts

These are necessary, but you should never reinvent them unless forced.

3.1 Audit & Compliance Context (GENERIC)

Purpose
Provides traceability and evidence.

Owns

Audit Logs

Retention rules

Audit reporting

Key Responsibilities

Append-only event storage

Compliance reporting

Log integrity

Explicit Boundaries

Does NOT enforce business rules

Consumes events from other contexts

Why Generic?
Audit is universal. The value is in correctness, not novelty.

3.2 Configuration Context (GENERIC)

Purpose
Manages system-level behavior without redeployment.

Owns

Security policies

Password rules

Feature flags

Key Responsibilities

Provide configuration values

Version and audit changes

Explicit Boundaries

Does NOT enforce rules itself

Provides inputs to other contexts

Why Generic?
Configuration is infrastructural, not domain-specific.

3.3 Integration Context (GENERIC)

Purpose
Handles communication with external systems.

Owns

External IdP adapters

API contracts

Protocol translations

Key Responsibilities

Translate external assertions into internal meaning

Protect trust boundaries

Explicit Boundaries

Does NOT own identity semantics

Does NOT bypass core contexts

Why Generic?
This is plumbing. Important plumbing, but plumbing nonetheless.

4. Bounded Context Map (Textual)
[External Systems]
        |
        v
[Integration Context]
        |
        v
[Authentication Context] ---> [Identity Context]
        |                         |
        v                         v
[Access Control Context] <--- [Provisioning Context]
        |
        v
[Applications]

(All contexts emit events to)
        v
[Audit & Compliance Context]

5. Critical Separation Rules (Non-Negotiable)

Identity and Access Control must never share a data model

Authentication must never make authorization decisions

Audit must never be mutable

Provisioning orchestrates but does not own

If you violate these, the system will still compile — and slowly destroy itself in production.