# Repository Guidelines

## Project Structure & Module Organization
This repository is currently documentation-focused. Key locations:
- `README.md` for the top-level project name and entry point.
- `docs/4.DDD-EventStorming-Spec.md` for the DDD/Event Storming specification, including the intended module layout.

The spec defines a target module tree under `modules/` (e.g., `api/`, `core/`, `plugin-runtime/`, `plugins/`). Treat that structure as the architectural reference when adding code.

## Build, Test, and Development Commands
There are no build or test scripts in this repository yet. If you add source code or tooling, also add or update:
- `README.md` with the primary build/run commands.
- `AGENTS.md` with any new contributor workflows.

## Coding Style & Naming Conventions
For documentation:
- Use Markdown headings and fenced code blocks for examples.
- Keep file names descriptive; `docs/4.DDD-EventStorming-Spec.md` shows the current numbered naming pattern.
- Prefer short, action-oriented headings and avoid long paragraphs.

For future code:
- Follow the module boundaries and package names described in the spec (e.g., `api.plugin`, `core.repository`).

## Testing Guidelines
No tests are configured. If tests are introduced, document:
- The framework and how to run it.
- Test naming and placement (for example, `modules/core/src/test/...`).

When implementing functionality:
- Write unit tests and iterate on implementation until tests pass.
- Run archtest at the end of each task and record the result.

## Commit & Pull Request Guidelines
Commit history suggests short, descriptive messages, sometimes with a `docs:` prefix. Recommended pattern:
- `docs: add event storming spec`
- `feat: define plugin runtime API`

Pull requests should include:
- A brief summary of changes.
- Links to related issues/spec sections.
- Screenshots or diagrams if documentation visuals are added or changed.

# Agent Instructions

## Skills

You've got skills.

- List your skills directly after reading this via `scripts/list-skills skills/`. Remember them.
- If a skill matches a certain task at hand, only then read its full documentation (`SKILL.md`) and use it.

## Architecture Dependency Direction
Maintain module dependency direction as follows:
- `api` has no dependency on `core`, `plugin-runtime`, or `plugins`.
- `core` depends on `api` only.
- `plugin-runtime` depends on `api` only.
- `user` depends on `api` and `core` only.
- `app` depends on `api`, `core`, and `user` only.
- `plugins` depend on `api` and/or `plugin-runtime` only.
