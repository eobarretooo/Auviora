# Auviora — AI handoff

Updated 2026-10-22. **Phase 2.0 independent foundation COMPLETE for tested workloads. Phase 3 NOT STARTED.** Development build only; model quality/publication are not approved.

## Current implementation

- Independent repository/application: app.auviora.android, display Auviora, version 0.1.0-alpha.1 / code 1. No remote/push/release/tag. Reference OpenBMB checkout remains at d416a13, unchanged except its pre-existing documented native patch.
- Three modules: core (contracts/profile/integrity), inference (serial native owner/JNI/CMake) and app (composition/ViewModel/Compose/data). No permanent sibling-checkout dependency.
- Public engine gitlink 96bbf61710d44421ea54e63f5194f9b17c20d877. Parent-owned F_NOCACHE patch applies automatically and passes clean/idempotent/reverse/conflict tests. Expected dirty vendor submodule contains only src/llama-moe-slot.cpp. Never discard it or commit a local-only engine pointer.
- Original Material 3 dark/light/system/dynamic theme, vector brand and left drawer (closed on phones; collapsible sidebar>=840dp). No bottom navigation. Chat/Models/disabled Agents/Settings are implemented. English/pt-BR selector works via AppCompat; 71 paired string resources.
- Actual HTTPS downloader and SAF import implementation validate exact artifact size/SHA-256/magic before promotion; full verification repeats at load. Model files are in Auviora's own filesDir/models/minicpm-v-4_7-1b, never the demo sandbox or Git. Project revision/hashes: docs/testing/model-artifacts.json and core/Models.kt. Similar OpenBMB files are not interchangeable.
- Native READY requires language model/projector/context setup. One Application-owned adapter serializes all calls through a Mutex/dedicated executor; cancel is atomic/cooperative, sampler reset stays on owner thread, unload waits and releases model resources. Process backend/executor remains available for reload. Partial failures clean up and map errors.
- Chat uses real Flow output and Stop; current phase stays visible. New chat identity is set before loading so drafts typed during load survive. Basic AtomicFile actual-message history persists; malformed history cannot be overwritten silently. Saved conversations are read-only until context restoration is implemented.

## Latest verified evidence

- Final app/test APK build PASS (6s); core JVM 5/5; lint app 0 errors/13 warnings, core 0 errors/1 warning, inference no issues. No suppression/baseline hides errors.
- APK structural check: 10 ARM64 libraries, all ELF LOAD alignment>=16KB, JNI/OpenMP present, no desktop tool libraries. SHA-256 01a6ef514073b1eb3bb44e4168de7ea93b057e6a79f0467ecf4fdbc23809ae7e.
- Hardware Android 16/API 36 model 25053PC47G: foundation navigation/theme/locale tests 2/2, actual history/corruption tests 2/2, real provisioning 1/1, final offline text/cancel/recovery/unload/reload 1/1 and offline visible streaming 1/1. No mocked inference. Final longer response had 33 distinct visible updates.
- Exact literal outputs, checked metadata and inspected screenshots: docs/testing/. [Full report](testing/PHASE_02_VALIDATION.md). Math 105, recovery 4 and post-reload 105 were generated/displayed. Verified load 4376ms; PSS 2301220KiB before unload / 276668KiB after. These are samples, not native TTFT/peak/thermal benchmarks.
- Offline runner confirms no active default network and restores settings in finally. Final restoration checked: airplane 0, Wi-Fi 1, mobile-data 0. No syscall/socket/packet audit; do not claim every failed request attempt is absent.
- Independent checkout inside ignored artifacts/reproduction initialized the public submodule and built successfully in 2m 30s, applying patch automatically. Updated production-source checkpoint 12b5a76 built again. This is independent buildability, not binary identity.
- Fixed-palette normal-text pairs pass static contrast >=5.38:1. This is not a full TalkBack/font/dynamic-color accessibility audit.
- Initial synchronized/clockless streaming observers failed to establish updates. Final test-only semantics reflection respects enabled actions and advances Compose 1.9 test frames while native IO runs. No AccessibilityService/agent executor or production test endpoint. Revalidate this pinned interop when upgrading Compose.

## Commits and Git state

| Commit | Purpose |
|---|---|
| d4297ef | Independent repository, identity decision and wrapper |
| 918ef6b | Pinned native/core integration, checksums, licenses and patch verification |
| 7e0cf02 | All-native 16KB ELF alignment |
| 7e89e91 | Localized Compose foundation, drawer, data and real local chat |
| 12b5a76 | Accurate states/copy and draft preservation during loading |
| 8fa0c99 | Offline hardware tests, reliable streaming observer, runner and sanitized evidence |

Closing documentation commit: retrieve with `git log -1 -- docs/AI_HANDOFF.md` (avoids a self-referential hash); its hash is in the user delivery. Intended parent files are committed. `git status --short` still reports modified vendor/llama.cpp-omni because of the exact patch. Do not call the entire working tree clean. No weights/APKs/credentials/local SDK paths/caches/raw logs are tracked.

## Boundaries and exact next action

Do not start Phase 3 automatically. Next action is user review of the foundation/report and an explicit instruction selecting the next scope. Candidates: image attachments with new-app vision validation, saved-context/history management or additional lifecycle/device tests. Preserve the tested profile, model-quality failures, legal attribution and source baseline.

Not implemented: agents/tools, advanced memory, background automation, unrestricted control, TTS/video/image-attachment UI, advanced resumable/background downloads, history export/delete/migration/context restoration. SAF import exists but interactive picker/provider behavior was not separately tested. Tablet/light/dynamic visual matrices, TalkBack/font scaling, long contexts and extended stress are unverified. Compact-model Portuguese mixing, false identity and factual errors remain. Model conversion redistribution and binary notice assembly need publication review; no package/weight publication is authorized.

## Commands

Set standard JAVA_HOME/ANDROID_HOME, run `git submodule update --init --recursive --depth 1`, then `./gradlew.bat :app:assembleDebug :app:assembleDebugAndroidTest :core:testDebugUnitTest :app:lintDebug :core:lintDebug :inference:lintDebug`. See [TESTING](TESTING.md), DESIGN.md, [architecture](ARCHITECTURE.md), [legal provenance](legal/PROVENANCE.md) and [development log](DEVELOPMENT_LOG.md).

Opt-in device runner requires provisioned verified weights: `./scripts/run-device-validation.ps1 -Serial SERIAL`, then `-Suite Streaming` for the visible stream/captures. Artifacts/raw logs/reproduction checkout remain ignored within this repository. Do not delete unrelated checkouts or alter the reference project. Canonical code/docs/commits are English; user communication is Brazilian Portuguese.
