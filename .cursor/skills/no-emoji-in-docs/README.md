# No Emoji in Documentation Skill

Enforces professional formatting by preventing emoji usage in markdown files, skills, and documentation.

## Overview

This skill ensures all project documentation maintains a professional appearance by avoiding emoji usage. It provides clear guidelines for creating clean, professional documentation using standard markdown formatting instead of emojis.

## Purpose

Maintain professional, universally readable documentation that:
- Renders correctly across all systems and environments
- Is suitable for formal/corporate settings
- Maintains consistency in version control
- Focuses on content over visual decoration

## Rules

**DO NOT use emojis in:**
- Markdown files (.md)
- Skill files (SKILL.md, README.md)
- Documentation (docs/ directory)
- Technical specifications
- Requirements and design documents

**Use instead:**
- Bold text for emphasis
- Standard markdown formatting
- Text labels: [NEW], [UPDATED], [DEPRECATED]
- Symbols: ✓, ✗, -, *, >
- Section numbers: 1., 2., 3.

## Examples

### Incorrect (With Emojis)
```markdown
## 🚀 Features
- ✅ Authentication
- 📦 User Management
```

### Correct (Without Emojis)
```markdown
## Features
- Authentication
- User Management
```

## Files

- **SKILL.md** - Complete skill instructions with examples and guidelines

## Application

This skill applies to:
- All markdown files
- Documentation in docs/ directory
- Skill files in .agent/skills/
- Technical specifications
- Requirements documents

## Exception

Emojis MAY be acceptable in conversational chat responses to users, but should be used sparingly and professionally.
