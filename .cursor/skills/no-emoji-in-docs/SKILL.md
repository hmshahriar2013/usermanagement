---
name: no-emoji-in-docs
description: Enforces professional formatting by preventing emoji usage in markdown files, skills, and documentation
---

# No Emoji in Documentation Skill

## Purpose

This skill ensures that all documentation, markdown files, skills, and technical documents maintain a professional appearance by avoiding emoji usage. Emojis should not be used in any project documentation or skill files.

## When to Apply This Skill

Apply this skill when creating or editing:
- **Markdown files** (`.md`)
- **Skill files** (`SKILL.md`, `README.md`)
- **Documentation** (`docs/` directory)
- **Technical specifications**
- **Requirements documents**
- **Design documents**
- **Code comments** (when applicable)
- **Commit messages** (when applicable)

## Rules

### ❌ DO NOT Use Emojis In:
- Headings and titles
- Section markers
- Bullet points
- Lists
- Tables
- Code blocks
- File names
- Directory names
- Technical descriptions
- Professional documentation

### ✅ Acceptable Alternatives:

Instead of emojis, use:
- **Bold text** for emphasis
- **Markdown formatting** (headers, lists, code blocks)
- **Symbols and characters**: `✓`, `✗`, `*`, `-`, `>`, `#`
- **Text labels**: `[NEW]`, `[UPDATED]`, `[DEPRECATED]`
- **Section numbers**: `1.`, `2.`, `3.`
- **Standard punctuation**

## Examples

### ❌ Incorrect (With Emojis):
```markdown
# 🚀 Project Setup

## 📋 Requirements
- ✅ Java 21
- ✅ Spring Boot 3.x
- 📦 Gradle

## 🎯 Features
- 🔐 Authentication
- 👤 User Management
```

### ✅ Correct (Without Emojis):
```markdown
# Project Setup

## Requirements
- Java 21
- Spring Boot 3.x
- Gradle

## Features
- Authentication
- User Management
```

### Alternative Professional Formatting:
```markdown
# Project Setup

## Requirements
* Java 21
* Spring Boot 3.x
* Gradle

## Key Features
1. Authentication
2. User Management
3. Role-based Access Control
```

## Implementation Guidelines

### When Creating New Files:
1. Write all content without emojis
2. Use standard markdown formatting
3. Use text-based emphasis (bold, italic, headers)
4. Use symbols sparingly and only when necessary

### When Editing Existing Files:
1. Remove all emojis from the content
2. Replace emoji-based markers with text or symbols
3. Maintain the same information hierarchy
4. Preserve the document structure

### Acceptable Symbols:
- Checkmarks: `✓` or `[x]`
- Cross marks: `✗` or `[ ]`
- Arrows: `->`, `=>`, `<-`
- Bullets: `-`, `*`, `+`
- Numbers: `1.`, `2.`, `3.`

## Special Cases

### User-Facing Chat Messages:
Emojis MAY be acceptable in conversational responses to users, but should still be used sparingly and professionally.

### Code and Technical Content:
NEVER use emojis in:
- Source code
- Configuration files
- Build scripts
- Technical specifications
- API documentation

## Enforcement

When reviewing or creating documentation:
1. Scan for emoji characters
2. Replace with appropriate text or formatting
3. Ensure professional appearance
4. Maintain readability without visual decorations

## Rationale

Professional documentation should:
- Be universally readable across all systems
- Maintain consistency in version control
- Be accessible to all users
- Focus on content over decoration
- Render correctly in all environments
- Be suitable for formal/corporate settings

## Summary

**Key Principle**: Keep all documentation, skills, and markdown files emoji-free. Use standard markdown formatting, text emphasis, and professional writing conventions instead.
