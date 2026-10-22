# Auviora — AI handoff

Updated 2026-10-22. **Phase 2.2 COMPLETE: v0.1.0-alpha.1 published as a verified signed pre-release. Phase 3 NOT STARTED.** Version remains 0.1.0-alpha.1 (1). Release source/tag stays at 4997566a22e4425f6d8f4d4f1800ff99ab7be96f; publication documentation follows separately.

## Published release checkpoint (2026-10-22)

[Release](https://github.com/eobarretooo/Auviora/releases/tag/v0.1.0-alpha.1) and [signed APK](https://github.com/eobarretooo/Auviora/releases/download/v0.1.0-alpha.1/Auviora-v0.1.0-alpha.1-arm64-v8a.apk) are public. APK: 42,736,118 bytes, SHA-256 19a4587de5d47a46f266a20d6c11a8ffc64459950b8feffcfb5dda13efd869ba. Its embedded source is 4997566, parent source was committed, permanent signature verifies, all ten ARM64 libraries/16KB ELF+ZIP alignment/notices/no-weight checks pass. Four assets: APK, SHA256SUMS.txt, THIRD_PARTY_NOTICES.txt, verification.json. Draft downloads matched; after publication all four anonymous public HTTP downloads returned 200 and identical hashes. Annotated remote tag peels to the exact tested source. Never replace these assets/tag.

The owner created the permanent RSA4096 identity locally. Public DER/password-free metadata match the committed e19c9d002254a776d6b19c0ad2c1dcee01971a46971d5d1fb915867bd6530a2a pin; CN=Auviora Android Release, valid through 2054. Only public data is tracked. At the owner's request, identical keystore/public DER/identity backups were created outside Git on distinct C: and D: physical disks, with owner-only ACLs verified; the owner confirmed separate password storage. No password was read or copied. Both disks remain in one computer; an additional off-device copy is recommended.

Exact signed APK installed/launched on the owner's Android 9/API 28 x86_64 emulator via libnb/Houdini ARM64 translation. Actual in-app 1.78GB download/integrity/load/projector/READY, visible response growth ending in 105, cooperative Stop, unload/reload and post-reload answer 4 passed through normal UI. No debug instrumentation or production test hook was added. Emulator eth0 kept an active default network; AIRPLANE_MODE broadcast was denied. Do not label this run offline. Saved radio settings restored (0/0/1); final model unloaded, verified files retained. Physical-device debug data untouched. Broad Android/ARM64 hardware, signed-hardware offline, stress and memory benchmarks remain unverified.

## Phase 2.2 implementation and checks

Existing origin https://github.com/eobarretooo/Auviora.git is PUBLIC; authenticated GitHub CLI 2.103.0 uses the credential store. Initial remote main matched af794a2 and tags/releases were empty. Controlled normal source/tag pushes and draft/publication now completed. No duplicate repository, login, installation, force push or unrelated permission change was needed.

Read [release process](RELEASE_PROCESS.md), [Phase 2.2](PHASES/PHASE_02_2.md), [Alpha notes](releases/v0.1.0-alpha.1.md) and [binary audit](legal/RELEASE_AUDIT.md). New scripts: setup-release-key.ps1 (owner local interactive), prepare-release.ps1 (signed or explicitly unsigned build), release.py (APK/signature/source/metadata/native/weight/notice/checksum checks), collect-release-notices.py, test-release.py and publish-release.py (explicit controlled draft/publish). Credentials/private keys never enter Git; public certificate pin is required for signed staging. No hosted Actions job is assumed tested.

Unsigned local workflow passed release build, five shared core JVM tests, release lint with 0 errors / 13+1+0 warnings, native patch fixtures and artifact checks. Six verifier cases include hostile local ZIP fixtures; publisher rejects unsigned candidates before remote mutation. Runtime inventory 106 modules/74 cached artifacts, original notice bundle packaged in APK. Fresh debug hardware navigation/history 4/4, offline inference/cancel/reload 1/1 and streaming 1/1 passed; these are not signed-release device results. Debug certificate differs from a new release key; no automatic uninstall/data export was performed.

The owner's first signed preparation exposed a Windows PowerShell 5.1 negative-fixture stderr interruption. Reproduced and corrected in 4997566 without engine/dependency/key changes. The fixture passes under 5.1 and 7.6.5; six release-verifier cases and full unsigned 5.1 preparation passed. Owner-local signed retry then passed and staged the published candidate. Password entry stays local. Exact local source/checksum/reports and raw emulator evidence remain in ignored artifacts/releases; sanitized results are in docs/testing/phase2-2-checks.json.

Phase 2.2 checkpoints: 54c17ef (signing/notices), 99330c6 (verification/publication), de17394 (preparation docs), 489a1d0 (public certificate pin), 4997566 (PowerShell fixture fix and exact published source). The annotated v0.1.0-alpha.1 tag points at 4997566. Later documentation commits do not rebuild or retag this release. Reproduce the released APK from the tag, not a newer documentation HEAD.

## Phase 2.1 continuation checkpoint

Presentation only: premium truthful README, seven real screenshots, 8.5-second offline streaming GIF (469,746 bytes), original vector documentation logo, media manifest/workflow and Python standard-library scripts. Canonical design is now [docs/design/DESIGN.md](design/DESIGN.md); root DESIGN.md was moved without content loss. See [Phase 2.1 report](PHASES/PHASE_02_1.md), [media provenance](media/README.md), [contributing](CONTRIBUTING.md) and [future Android quality findings](design/ANDROID_QUALITY_BACKLOG.md).

Fresh manual device capture used the exact installed Phase 2.0 APK hash. All eight raster assets decoded/checked, all local Markdown links/fences checked, GFM parsed and local desktop/mobile preview inspected. App source/build/native/license/original testing evidence unchanged; no app suite rerun. Offline radios and system locale restored, fixed dark theme/model unloaded. The response's eight items instead of ten are intentionally preserved. Navigation draft loss is a documented future issue, not repaired. No Phase 3, push, remote, release or tag. Phase 2.1 checkpoints: `6afbeec` adds capture/link tools and the media work log; `fbc407e` adds the README, authentic assets, design move and related documentation. The final closure commit records those hashes; retrieve it with `git log -1 --format=%h -- docs/AI_HANDOFF.md` (no self-referential hash).

## Current implementation

- Independent repository/application: app.auviora.android, display Auviora, version 0.1.0-alpha.1 / code 1, published pre-release as above. Reference OpenBMB checkout remains at d416a13, unchanged except its pre-existing documented native patch.
- Three modules: core (contracts/profile/integrity), inference (serial native owner/JNI/CMake) and app (composition/ViewModel/Compose/data). No permanent sibling-checkout dependency.
- Public engine gitlink 96bbf61710d44421ea54e63f5194f9b17c20d877. Parent-owned F_NOCACHE patch applies automatically and passes clean/idempotent/reverse/conflict tests. Expected dirty vendor submodule contains only src/llama-moe-slot.cpp. Never discard it or commit a local-only engine pointer.
- Original Material 3 dark/light/system/dynamic theme, vector brand and left drawer (closed on phones; collapsible sidebar>=840dp). No bottom navigation. Chat/Models/disabled Agents/Settings are implemented. English/pt-BR selector works via AppCompat; 71 paired string resources.
- Actual HTTPS downloader and SAF import implementation validate exact artifact size/SHA-256/magic before promotion; full verification repeats at load. Model files are in Auviora's own filesDir/models/minicpm-v-4_7-1b, never the demo sandbox or Git. Project revision/hashes: docs/testing/model-artifacts.json and core/Models.kt. Similar OpenBMB files are not interchangeable.
- Native READY requires language model/projector/context setup. One Application-owned adapter serializes all calls through a Mutex/dedicated executor; cancel is atomic/cooperative, sampler reset stays on owner thread, unload waits and releases model resources. Process backend/executor remains available for reload. Partial failures clean up and map errors.
- Chat uses real Flow output and Stop; current phase stays visible. New chat identity is set before loading so drafts typed during load survive. Basic AtomicFile actual-message history persists; malformed history cannot be overwritten silently. Saved conversations are read-only until context restoration is implemented.

## Phase 2.0 verified evidence

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

Do not start Phase 3 automatically. Phase 2.2 is complete; wait for explicit new scope. Future releases require a new version/code, the same permanent identity and all gates in RELEASE_PROCESS.md. Candidates for product work: image attachments with new-app vision validation, saved-context/history management or additional lifecycle/device tests. Preserve the tested profile, model-quality failures, legal attribution and published source/tag/assets.

Not implemented: agents/tools, advanced memory, background automation, unrestricted control, TTS/video/image-attachment UI, advanced resumable/background downloads, history export/delete/migration/context restoration. SAF import exists but interactive picker/provider behavior was not separately tested. Tablet/light/dynamic visual matrices, TalkBack/font scaling, long contexts and extended stress are unverified. Compact-model Portuguese mixing, false identity and factual errors remain. Model conversion redistribution remains unapproved; reviewed binary notice assembly is now packaged separately from model terms. APK pre-release publication is authorized only after Phase 2.2 gates; model weights are never attached.

## Commands

Set standard JAVA_HOME/ANDROID_HOME, run `git submodule update --init --recursive --depth 1`, then `./gradlew.bat :app:assembleDebug :app:assembleDebugAndroidTest :core:testDebugUnitTest :app:lintDebug :core:lintDebug :inference:lintDebug`. See [TESTING](TESTING.md), [design](design/DESIGN.md), [architecture](ARCHITECTURE.md), [legal provenance](legal/PROVENANCE.md) and [development log](DEVELOPMENT_LOG.md).

Opt-in device runner requires provisioned verified weights: `./scripts/run-device-validation.ps1 -Serial SERIAL`, then `-Suite Streaming` for the visible stream/captures. Artifacts/raw logs/reproduction checkout remain ignored within this repository. Do not delete unrelated checkouts or alter the reference project. Canonical code/docs/commits are English; user communication is Brazilian Portuguese.
