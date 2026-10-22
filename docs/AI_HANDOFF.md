# AI handoff

Phase 2.0 IN PROGRESS. Independent app.auviora.android / Auviora / 0.1.0-alpha.1 (1).
Commits: d4297ef repository/identity; 918ef6b native/core foundation. Original reference remains unchanged at d416a13 (expected existing native patch).
Implemented source: three modules, Compose Material3/design/drawer/chat/models/disabled Agents/settings; EN/pt-BR; SHA-256 provisioning; process-owned serial inference; basic atomic actual-message history.
Verified: native/core build, 5 JVM tests, patch fixture; app and instrumentation APK compile. UI/storage tests4/4 PASS; all 10 packaged ARM64 libraries pass 16KB ELF alignment and required OpenMP checks. Hardware 60a36dc3 reconnected; first install blocked by device policy, user has now enabled USB installation.
APKs installed successfully after USB setting was enabled. Next: finish provisioning via the app downloader, run real offline UI generation/cancel/recovery/unload/reload, inspect captures and crash logs. Do not claim new-app inference until observed.
Sources/decisions: docs/ARCHITECTURE.md, docs/DECISIONS.md, DESIGN.md, docs/TESTING.md, docs/legal/PROVENANCE.md.
No cloud inference, fake answers/history, publishing, pushes, production packages or Phase 3 authorized. Weights/APKs/logs stay ignored in artifacts or app sandbox. Parent Git will retain an expected patch-only modified vendor submodule.
