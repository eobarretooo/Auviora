# Decisions

2026-10-22: Phase 2.0 explicitly authorizes an independent application. The reference remains intact.

- Application ID and Kotlin namespace: `app.auviora.android`. This is an independent development identity, not an asserted domain ownership or published store identity. Documented before configuration/use. Display name: Auviora.
- Initial version: `0.1.0-alpha.1`, versionCode 1. SemVer prerelease; no release/tag per commit. Increment versionCode for future distributed builds.
- Three modules: core contracts, inference implementation, app presentation/data. Feature and design-system packages stay logical until separate modules have concrete value.
- Preserve AGP 9.1.1, Gradle 9.3.1, SDK 36.1, NDK 27.0.12077973, CMake 4.1.2 and ARM64. Compose compiler 2.2.10 matches AGP built-in Kotlin. Pin Compose BOM 2025.08.00, Activity 1.10.1 and Lifecycle 2.9.2 for this verified SDK instead of automatically adopting the latest toolchain.
- AppCompat supplies supported per-app locale handling down to API 24; Compose renders inside AppCompatActivity. No Hilt, Room or Navigation Compose: explicit composition, small atomic JSON conversation store and four enum destinations suffice.
- Native engine is process-global; one serial executor owns every JNI operation, with cooperative cancellation checked between tokens. No Activity-owned singleton destruction.
- Pinned HTTPS download/import uses staging, full SHA-256 and size checks before promotion/load. No reuse across Android sandboxes. No bundled weights.
- Source notices and model terms are separate; no weight/package redistribution in this phase.
