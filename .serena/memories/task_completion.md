# Task Completion Checklist
- Update task plan docs (docs/2026-01-13-task-implementation-plan.md) with progress.
- Add a task log under docs/YYYY-MM-DD-task-<name>.md in Korean with purpose, changes, rules mapping, validation results, TODOs.
- Run unit tests for changed module(s).
- Run architecture rules: ./gradlew --no-daemon architectureRules (with GRADLE_USER_HOME/GRADLE_OPTS in this environment).