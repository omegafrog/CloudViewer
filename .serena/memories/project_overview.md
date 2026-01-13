# Project Overview
- Purpose: CloudViewer backend architecture scaffold following the DDD/Event Storming spec, separating api/core/plugin-runtime/plugins.
- Tech stack: Java 21, Gradle multi-module build, Spring Boot (core module).
- Modules: modules/api (interfaces/DTOs), modules/plugin-runtime (plugin loader/registry), modules/core (Repository/File/Indexing services), modules/plugins (external plugin drop folder), plugins/ (separate plugin projects).
- Key docs: docs/4.DDD-EventStorming-Spec.md (architecture/spec), docs/*-task-*.md (task logs).