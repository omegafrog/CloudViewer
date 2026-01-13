# Code Style and Conventions
- Java packages follow spec: api.plugin, api.repository, api.common, core.repository, core.file, core.indexing, plugin.runtime.
- DTOs in api.common use Java records.
- Read-only v1: avoid write/delete/network/infra concerns in core and plugins.
- Architecture rules: core must not depend on plugin implementations; indexing must not depend on file/repository.
- Tests: JUnit 5, ArchUnit for architecture rules.