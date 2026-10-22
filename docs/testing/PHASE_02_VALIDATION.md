# Phase 2.0 validation

Date: 2026-10-22. **Independent foundation COMPLETE for the tested workload. Phase 3 NOT STARTED.** This is a development build, not production/model-quality approval.

## Identity and independent build

Repository: Auviora. Application ID/namespace `app.auviora.android`, display name Auviora, version `0.1.0-alpha.1` / code 1. Identity was documented before use. `aapt dump badging` confirms the compiled identity/version/label. No release tag, push or published APK/weights.

The original reference remains at d416a13 with its existing documented native patch and no parent changes. A separate checkout inside ignored artifacts/reproduction cloned the public native submodule, applied the parent patch automatically and built successfully in 2m 30s; after updating to production-source checkpoint 12b5a76, its incremental app/core build passed again. No sibling checkout, shared Git object alternates or undocumented vendor edits are required. This validates independent buildability, not byte-for-byte reproducible binaries.

Toolchain: Gradle 9.3.1, AGP 9.1.1/built-in Kotlin 2.2.10 and matching Compose compiler, Compose BOM 2025.08.00 (UI 1.9.0), Activity 1.10.1, Lifecycle 2.9.2, SDK 36.1/target 36/min 24, NDK 27.0.12077973, CMake 4.1.2/Ninja, daemon JDK 21. Versions are pinned and not automatically upgraded to current documentation examples.

Tested APK SHA-256: `01a6ef514073b1eb3bb44e4168de7ea93b057e6a79f0467ecf4fdbc23809ae7e`.

## Model provisioning and compatibility

Actual app HTTPS downloader provisioned the exact Phase 1.6 pair into `/data/user/0/app.auviora.android/files/models/minicpm-v-4_7-1b/`. No access to the demo application's sandbox, root, shared private directory or bundled weights. Provisioning test passed 1/1 in approximately 37s, including download/verification. This is a single host/network sample, not a download-speed guarantee.

[Artifact manifest](model-artifacts.json): project repository tc-mb/MiniCPM-V-Apps-gguf revision 8716e5209f308fd53f197ccd7d50c6fdcc2dc37d:

| Artifact | Verified bytes | Verified SHA-256 |
|---|---:|---|
| MiniCPM-V-4_7-1B-Q4_K_M.gguf | 672,003,584 | bdc2c0da2320129a4fd91b0d464c63f8047b511a7ef7f7e1d143b433c7165cd5 |
| mmproj-model-f16.gguf | 1,108,746,816 | febb329c29ec9f1643c9d690ca74d35f486b83fa48c2f54c108b135729f635fd |

Downloader/import stages, checks size/full SHA-256/GGUF magic and promotes only verified bytes. Every native load verifies again. On-device sha256sum independently matched both files. No substitution with similarly named OpenBMB artifacts. The pinned backend/version47 prefix, CPU 4 threads, context 4096, slices 9, temperature 0.7 and thinking-off baseline remain.

The SAF import implementation is present and compiled; interactive document-provider selection/import was not separately exercised. New-app image attachments are not implemented: the compatible projector loads and the image request/JNI contract is retained, but this phase does not claim fresh Auviora vision inference. Phase 1.6 vision evidence belongs to the reference app.

## Actual device and offline results

Hardware: ARM64 model 25053PC47G, Android 16/API 36. Initial install was rejected by device USB policy; after the user enabled installation, both APKs installed successfully. Approximately 162GiB free /data before the new download.

Offline runner saves airplane/Wi-Fi/mobile-data settings, disables connectivity, waits for Android `Active default network: none`, runs actual UI tests and restores settings in finally. Original/final values: airplane 0, Wi-Fi 1, mobile-data 0. No cloud inference path exists. No packet/syscall tracing or failed-request-attempt audit was performed; this is not a global privacy certification.

[Literal inference evidence](phase2-inference.json):

- Main UI prompt: `What is 15 multiplied by 7?` Displayed a real explanation with `15 * 7 = 105` and correct final answer 105.
- Actual Stop action interrupted a story request; the next prompt displayed `The sum of 2 plus 2 is 4.`
- Unload/reload succeeded; the following math response again returned 105.
- Verified load including full hashing/native setup: 4,376ms. Synchronized UI observer first saw text at 1,964ms and completed at 2,122ms. These include test synchronization and are **not native TTFT/decode benchmarks**.
- PSS before unload 2,301,220KiB (~2,247MiB), after 276,668KiB (~270MiB). Samples, not peaks; no leak-free claim.

[Literal streaming evidence](phase2-streaming.json): the ten-sentence rain request produced 33 distinct visible text updates while offline. Content remains a compact model's oversimplified explanation, not validated scientific guidance. Screenshot shows actual generated text, not a mock.

## Observer investigation and corrections

The initial online observer saw 5 visible updates. Some synchronized repeats saw only one; an early unsynchronized observer saw no placed message updates because the Compose test clock did not advance during real native IO. An accessibility-node approach did not find the action and was discarded. A nullable test action fallback also failed compilation and was corrected before device execution. These were test-tool failures, not observed app/native crashes.

Final test invokes the actual enabled Compose Send semantics action on main, advances the test frame clock while native IO runs, and reads actual placed text nodes without draining generation through Espresso idleness. It uses test-only reflection into the pinned Compose 1.9 view; upgrades must revalidate this interop. No AccessibilityService, agent executor, fake response or production test endpoint was added.

The runner also initially checked network teardown too soon; bounded polling now confirms actual disconnection before proceeding. UI session identity resets before load starts, preventing drafts typed during loading from being cleared at readiness. Current engine status remains visible during existing conversations. No backend sampling change was needed.

## Checks and visual evidence

| Check | Final result |
|---|---|
| Debug application + instrumentation APK | PASS |
| Core JVM integrity/catalog tests | 5/5 PASS |
| App lint | 0 errors / 13 warnings |
| Core lint | 0 errors / 1 warning |
| Inference lint | No issues |
| Hardware navigation/theme/locale tests | 2/2 PASS |
| Hardware actual history/corruption preservation tests | 2/2 PASS |
| Actual app provisioning | 1/1 PASS |
| Final offline inference/cancel/recovery/unload/reload | 1/1 PASS |
| Final offline visible streaming | 1/1 PASS, 33 updates |
| Native patch fixture | Clean/idempotent/reverse/conflict checks PASS |
| APK structural verification | 10 ARM64 libraries, required JNI/OpenMP, all ELF LOAD alignments>=16KB, no desktop tool libraries |
| Independent checkout build | PASS, including automatic patch application |

Remaining lint warnings concern pinned dependency updates and platform attribute/target advice; no lint baseline or broad suppression hides errors. Native vendor/compiler and Gradle deprecation warnings remain visible in logs. Lint alone initially missed some transitive 4KB libraries; all targets now inherit 16KB linker alignment, and APK ELF inspection covers each packaged library. The imported NDK OpenMP runtime is explicitly packaged.

Inspected real screenshots: [welcome](phase2-welcome.png), [left drawer/history](phase2-drawer.png), [loaded model](phase2-models.png), [streamed chat](phase2-chat.png). They show the pt-BR interface on this phone. English/Portuguese switching was separately exercised. No bottom navigation exists. Static [palette text contrast](palette-contrast.json) passed 16 explicit dark/light pairs with minimum 5.38:1. Screenshot/contrast review is not an exhaustive accessibility audit.

## Limits and readiness

Only this Android 16 ARM64 device was used. Tablet/sidebar resizing, large fonts/TalkBack, visual light/dynamic-color matrices, extended thermal/battery/memory stress and long-context failure paths remain unverified. Basic JSON history has no export/delete/migration UI or native-context restoration; opening saved conversations is honestly read-only. Downloader is foreground-only, without resume after process death; partial staging files may remain for the next attempt. Native prefill cancellation is cooperative and can wait for the current call.

Known model risks carry forward: Portuguese language mixing, false provider identity and factual errors. The new UI does not fix them with canned text. Saved recent conversations contain actual test requests/responses. No agents, advanced memory, background automation, TTS or image-attachment UI are claimed complete. Code/model terms and publication gaps are documented in [legal provenance](../legal/PROVENANCE.md).

The independent foundation is ready for user review and separately authorized next-phase work. Phase 3 remains unstarted.
