# Auviora — AI handoff

Updated 2026-10-22. **Phase 2.2 release infrastructure/candidate validated; public Alpha blocked pending owner signing identity and signed-candidate gates. Phase 3 NOT STARTED.** Version remains 0.1.0-alpha.1 (1); no tag/release/public artifact created.

## Phase 2.2 continuation checkpoint

Existing origin https://github.com/eobarretooo/Auviora.git is PUBLIC; GitHub CLI 2.103.0 is already installed/authenticated via credential store with ADMIN repository access. Initial remote main matched af794a2; no tags/releases existed. No duplicate repository, login, installation or unrelated permission change was needed.

Read [release process](RELEASE_PROCESS.md), [Phase 2.2](PHASES/PHASE_02_2.md), [Alpha notes](releases/v0.1.0-alpha.1.md) and [binary audit](legal/RELEASE_AUDIT.md). New scripts: setup-release-key.ps1 (owner local interactive), prepare-release.ps1 (signed or explicitly unsigned build), release.py (APK/signature/source/metadata/native/weight/notice/checksum checks), collect-release-notices.py, test-release.py and publish-release.py (explicit controlled draft/publish). Credentials/private keys never enter Git; public certificate pin is required for signed staging. No hosted Actions job is assumed tested.

Unsigned local workflow passed release build, five shared core JVM tests, release lint with 0 errors / 13+1+0 warnings, native patch fixtures and artifact checks. Six verifier cases include hostile local ZIP fixtures; publisher rejects unsigned candidates before remote mutation. Runtime inventory 106 modules/74 cached artifacts, original notice bundle packaged in APK. Fresh debug hardware navigation/history 4/4, offline inference/cancel/reload 1/1 and streaming 1/1 passed; these are not signed-release device results. Debug certificate differs from a new release key; no automatic uninstall/data export was performed.

Owner selected creation of a new permanent key and intends backups, but completion/key presence has not been observed. Do not interpret that intent as a created/approved certificate or verified backups. The default external identity path is the owner's home/.auviora/signing/identity.json. No public certificate pin exists yet. Next: owner finishes interactive setup and backups, commit only the public pin, run signed preparation locally, inspect exact reports, then use draft and publish only if every gate passes. Until then keep an explicitly UNSIGNED local candidate. No tag, source push, draft, release or direct APK URL was invented. Candidate/checksum/source evidence lives in ignored artifacts/releases; final staging must follow the final committed source.

Phase 2.2 checkpoints: 54c17ef (owner signing/provenance/runtime notices) and 99330c6 (candidate verification, explicit publication tooling and current test evidence). The closure documentation commit is retrieved with `git log -1 --format=%h -- docs/AI_HANDOFF.md`; final candidate verification.json records its full commit after rebuilding. No release tag is associated with these development commits.

## Phase 2.1 continuation checkpoint

Presentation only: premium truthful README, seven real screenshots, 8.5-second offline streaming GIF (469,746 bytes), original vector documentation logo, media manifest/workflow and Python standard-library scripts. Canonical design is now [docs/design/DESIGN.md](design/DESIGN.md); root DESIGN.md was moved without content loss. See [Phase 2.1 report](PHASES/PHASE_02_1.md), [media provenance](media/README.md), [contributing](CONTRIBUTING.md) and [future Android quality findings](design/ANDROID_QUALITY_BACKLOG.md).

Fresh manual device capture used the exact installed Phase 2.0 APK hash. All eight raster assets decoded/checked, all local Markdown links/fences checked, GFM parsed and local desktop/mobile preview inspected. App source/build/native/license/original testing evidence unchanged; no app suite rerun. Offline radios and system locale restored, fixed dark theme/model unloaded. The response's eight items instead of ten are intentionally preserved. Navigation draft loss is a documented future issue, not repaired. No Phase 3, push, remote, release or tag. Phase 2.1 checkpoints: `6afbeec` adds capture/link tools and the media work log; `fbc407e` adds the README, authentic assets, design move and related documentation. The final closure commit records those hashes; retrieve it with `git log -1 --format=%h -- docs/AI_HANDOFF.md` (no self-referential hash).

## Current implementation

- Independent repository/application: app.auviora.android, display Auviora, version 0.1.0-alpha.1 / code 1. Existing GitHub origin as above; no Phase 2.2 push/release/tag. Reference OpenBMB checkout remains at d416a13, unchanged except its pre-existing documented native patch.
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

Do not start Phase 3 automatically. Next action is completing the owner-managed signing/release gates above. Any product-feature development still requires explicit new scope. Candidates: image attachments with new-app vision validation, saved-context/history management or additional lifecycle/device tests. Preserve the tested profile, model-quality failures, legal attribution and source baseline.

Not implemented: agents/tools, advanced memory, background automation, unrestricted control, TTS/video/image-attachment UI, advanced resumable/background downloads, history export/delete/migration/context restoration. SAF import exists but interactive picker/provider behavior was not separately tested. Tablet/light/dynamic visual matrices, TalkBack/font scaling, long contexts and extended stress are unverified. Compact-model Portuguese mixing, false identity and factual errors remain. Model conversion redistribution remains unapproved; reviewed binary notice assembly is now packaged separately from model terms. APK pre-release publication is authorized only after Phase 2.2 gates; model weights are never attached.

## Commands

Set standard JAVA_HOME/ANDROID_HOME, run `git submodule update --init --recursive --depth 1`, then `./gradlew.bat :app:assembleDebug :app:assembleDebugAndroidTest :core:testDebugUnitTest :app:lintDebug :core:lintDebug :inference:lintDebug`. See [TESTING](TESTING.md), [design](design/DESIGN.md), [architecture](ARCHITECTURE.md), [legal provenance](legal/PROVENANCE.md) and [development log](DEVELOPMENT_LOG.md).

Opt-in device runner requires provisioned verified weights: `./scripts/run-device-validation.ps1 -Serial SERIAL`, then `-Suite Streaming` for the visible stream/captures. Artifacts/raw logs/reproduction checkout remain ignored within this repository. Do not delete unrelated checkouts or alter the reference project. Canonical code/docs/commits are English; user communication is Brazilian Portuguese.
