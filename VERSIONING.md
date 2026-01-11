# 📌 **VERSIONING.md — Photo Organizer Versioning Policy**

Photo Organizer follows a clear and intentional versioning strategy designed to keep releases meaningful and predictable. Version numbers reflect **code changes only**, not documentation or repository housekeeping.

The project uses **Semantic Versioning (SemVer)** in a practical, lightweight form:

```
MAJOR.MINOR.PATCH
```

---

## 🧱 **1. Patch Releases (v1.0.1, v1.0.2, …)**
Patch releases are created **only when code changes occur** that do *not* introduce new features.

Patch releases include:

- bug fixes
- performance improvements
- behavior‑preserving refactoring
- dependency updates that affect runtime
- installer/runtime adjustments

Patch releases **do NOT include**:

- README updates
- documentation changes
- roadmap edits
- CONTRIBUTING.md updates
- social preview image changes
- repository cleanup

Documentation‑only changes **never** trigger a release.

---

## 🚀 **2. Minor Releases (v1.1.0, v1.2.0, …)**
Minor releases introduce **new features** that users can see or interact with.

Examples include:

- duplicate detection
- metadata repair tools
- thumbnail grid
- backup/rollback system
- map view
- new UI components
- new workflows

Minor releases must include **at least one meaningful new capability**.

---

## 🔧 **3. Major Releases (v2.0.0, v3.0.0, …)**
Major releases are reserved for **breaking changes** or **significant architectural shifts**.

Examples include:

- redesigned workflows
- incompatible folder structures
- major UI overhaul
- new runtime or installer architecture
- removal or replacement of existing features

Major releases should be rare and intentional.

---

## 🧭 **4. When NOT to Release**
The following changes **never** justify a version bump on their own:

- documentation edits
- README improvements
- roadmap updates
- CONTRIBUTING.md changes
- social preview image updates
- GitHub workflow or metadata changes
- issue templates
- project board updates

These changes improve the project but do not affect the software itself.

---

## 🎯 **5. Release Philosophy**
Photo Organizer values:

- stability
- clarity
- meaningful version history
- user trust

Releases are created **only when the software itself changes**, not when the repository changes.

This ensures that every version number corresponds to real, user‑visible or code‑level improvements.
