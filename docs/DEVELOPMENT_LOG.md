# Development log

## 2026-10-22 — Phase 2.0 start

Read reference instructions, architecture/plan/structure/decisions/handoff/testing/history and Phase 1.5/1.6 evidence. Reference baseline d416a13; engine 96bbf617 with documented patch remains modified. No reference edits authorized or made.
Created independent repository and documented identity before Android configuration. Toolchain retained; only new Compose/presentation dependencies are introduced. Initial ADB check found no devices; user offered to reconnect.

## Native integration checkpoint

Initial commit d4297ef records repository/identity. Independent public submodule fetched at exact 96bbf617 after a shallow branch checkout failed; the default branch was different, exact SHA fetch succeeded. No reference mutation. Native module build and 5 JVM tests passed in 3m51s; patch fixture passed clean/idempotent/reverse/conflict checks. First app build passed in 1m19s, lint0 errors/23 warnings. Lint exposed bridge ELF alignment; parent CMake now sets 16KB alignment. Desktop tool libraries are excluded without vendor edits. Direct mtmd inclusion initially lacked parent-scope LLAMA_INSTALL_VERSION; diagnosed and fixed before retry. No dependency upgrade was used as a workaround.

Native foundation checkpoint: full build including Compose/test APK passed in 2m28s after scope/alignment correction. JNI packaging review detected imported OpenMP missing when restricting native targets; explicitly package pinned NDK libomp with both CPU variants. Application/UI source is the next logical commit. User device rejected the first install with INSTALL_FAILED_USER_RESTRICTED; awaiting device installation setting/confirmation, not bypassing it.

Commit 918ef6b integrates core/native sources, licenses and patch verification. The APK structural check found 4KB ELF segments in transitive ggml libraries despite lint only naming the bridge; 16KB linker alignment is now inherited by all native targets. Pinned NDK OpenMP already uses 16KB alignment. These checks prevent assuming lint alone covers every packaged dependency.

ARM64 packaging verification now passes: 10 native libraries, all ELF LOAD segments >=16KB, pinned OpenMP included, no unrelated desktop/omni libraries. Build/test APK and lint passed. Auviora/test APK installation succeeded after user enabled USB installation. Foundation navigation/theme/locale tests 2/2 and isolated actual-message persistence/corruption tests 2/2 passed on hardware. Provisioning exact artifacts through the application is now running.

## UI/state verification corrections

Commits 7e0cf02 (all-native ELF alignment) and 7e89e91 (independent localized Compose foundation) are complete. Device UI/storage tests4/4 passed; in-app exact artifact download passed1/1 in37s. Device SHA-256 matched both pinned artifacts. First offline generation/cancel/reload test passed1/1 with actual responses. An independent clean checkout built successfully in 2m 30s and applied the patch automatically.

Visual review kept current model status visible above existing messages and centered the empty-state copy. New session identity now resets before loading, preserving a draft typed during load instead of clearing it when READY arrives. Model provisioning copy describes integrity checks without claiming success from presence or referring to internal project phases.

A synchronized streaming observer sometimes saw only one update. Direct UI semantics observation initially saw no rendered messages because the Compose test frame clock was not advanced during real native IO; it also needed to await load-operation completion and respect enabled controls. Corrected test-only observation advances frames and reads actual placed text on main. Offline streaming then passed with24 distinct visible updates. No AccessibilityService/agent feature was added. Intermediate failed observers and asynchronous network-teardown detection are retained in ignored logs, not classified as engine crashes. Runner now waits for no active network and restores saved settings in finally.

## Final hardware verification

Production source commit12b5a76 preserves drafts and clarifies states. Final app/test build PASS in6s; core JVM 5/5; lint app 0 errors/13 warnings, core0/1, inference no issues. Final hardware foundation/storage4/4, offline inference1/1 and offline streaming 1/1 all PASS. Provisioning had already passed1/1. Latest streaming observer recorded33 distinct placed text updates. Math 105, post-cancel4 and post-reload 105 were displayed through real UI; exact outputs remain in JSON.

Tested APK SHA-256 01a6ef514073b1eb3bb44e4168de7ea93b057e6a79f0467ecf4fdbc23809ae7e. Verified load 4376ms; PSS 2301220KiB before unload and276668KiB after. Network settings restored to0/1/0. Screenshot review completed on actual app captures. Independent checkout updated to12b5a76 built successfully again. No crash/ANR observed; exit-info reports instrumentation force-stops. Expanded accessibility/tablet/stress/model-quality/SAF and fresh vision tests remain outside measured evidence.

Next commit records the final test observer, runner, sanitized screenshots, outputs and validation report. Its hash is listed in the final handoff. Raw failed observer/build/network-transition attempts remain ignored under artifacts/; they are explicitly described in the report.

## Closure

Commit 8fa0c99 records final hardware evidence/tests. All phase gates passed; the reference remains intact. Static dark/light normal-text pairs have minimum contrast 5.38:1, without claiming full accessibility certification. Documentation now records completed scope, raw-output limits, privacy/model-license gaps and exact next action. Version remains 0.1.0-alpha.1 (1), changelog has Unreleased; no tag/push/release/Phase 3.

The closing commit is `docs: finalize independent Android foundation handoff`; obtain its hash with `git log -1 --format=%h -- docs/AI_HANDOFF.md`. Native submodule remains intentionally modified only by the reproducible patch. No required Phase 2 implementation work remains for the stated foundation; untested/out-of-scope paths stay explicitly listed.

## Phase 2.1 — authentic media checkpoint (2026-10-22)

Read the current handoff, architecture, design, testing, legal and phase evidence before edits; inspected all four previous screenshots and preserved their bytes. Confirmed installed APK hash and version; ADB and FFmpeg were already available. Captured seven real English UI screenshots, including dark/light Settings, without changing app code. Recorded actual offline native streaming under no active default network and restored radio/locale/theme/model state afterward. Rejected a draft-losing first take; retained the accepted response exactly, including eight items instead of ten.

Added Python standard-library media and local documentation validation tools, manifest and reproduction guide. GIF is 8.5s/102 frames/469,746 bytes; all curated media totals 1,239,778 bytes. Real capture/record/convert/check commands passed. Full GIF decode found39 distinct RGB frames; invalid crop and invalid-link/fence fixtures were rejected. Temporary recordings/logs/previews remain ignored. Presentation integration and final commit checkpoints follow below.

## Phase 2.1 — presentation closure

Commit `6afbeec` records reproducible media/link tooling; `fbc407e` integrates the English landing page, seven real screenshots/GIF/brand manifest, expanded pt-BR guide, contribution guide, unchanged design-contract relocation and Android quality backlog. All23 Markdown files parsed as GFM and111 local links/media/anchors passed. All9 manifest assets matched SHA-256/size; all8 raster assets passed FFprobe checks. Pillow decoded102 GIF frames/39 distinct RGB frames, 8500ms timing and infinite loop metadata. Desktop/mobile preview loaded all12 images without page overflow at the measured sizes; this is a local approximation, not live GitHub publication.

Application source/build/license files and original testing evidence match baseline `3ed2d8e`. The public engine pin and exact expected native patch remain intact. No app build/suite rerun was required; the fresh manual offline run is distinct from historical Phase 2.0 tests. Version remains 0.1.0-alpha.1 (1), no remote/push/tag/release/Phase 3. Review presentation and choose the next explicit scope. This closure commit is discoverable through `git log -1 --format=%h -- docs/AI_HANDOFF.md`; its hash is supplied in the user delivery.

## Phase 2.2 — controlled release preparation (2026-10-22)

Read all release context, source/build/version/signing configuration and previous evidence. Existing public origin is eobarretooo/Auviora; remote main matched af794a2. CLI 2.103.0 was already installed/authenticated with repository ADMIN access; tags/releases were empty. No credential disclosure, duplicate repository, permission change or CLI install. Owner requested interactive creation of a permanent identity; no completion/key/public pin has been observed, so public distribution remains blocked.

Implemented environment-only release signing with no debug fallback, explicit non-distributable candidate mode, owner-interactive external PKCS12/RSA4096 setup, exact-source provenance through the AGP Variant API, local preparation/verification and explicit draft/publish tooling. Collected106 runtime module records / 74 artifact records and original Maven/native/NDK notices into a scoped APK-code audit/bundle. Model weights are absent and redistribution remains unapproved. No UI/engine/profile/toolchain upgrade or version change.

Release candidate build passed (initial 56s, then cached incremental builds); core JVM 5/5, release lint app 0/13, core 0/1, inference 0/0, patch fixture and exact native diff passed. New verifier tests cover a real unsigned APK and rejection of unsafe distribution/debug/hidden-weight/notice/source fixtures. Fresh debug installation preserved data and passed hardware navigation/history 4/4, offline inference/cancel/reload 1/1 and visible streaming 1/1. Release signature/device installation, tag and GitHub mutation remain unexecuted pending the permanent identity.

Investigated setup failures before changes: nonexistent release unit-test task, PowerShell dotted argument splitting, Gradle legacy artifact ambiguity, inherited Guava license metadata, one-element PowerShell splat and AGP9 generated-source API restrictions. Fixed workflow code without changing dependencies. Missing-signing and fixture conflict errors are intentional negative gates. Candidate/checkbox details and exact continuation are recorded in the Phase 2.2 report/release process. Atomic commit checkpoints and final local verification follow below.

Release implementation checkpoint: six verifier cases and the unsigned publisher rejection passed. All 27 root/docs Markdown files and 143 local references passed; public mutation paths were intentionally not executed. New signed-release provenance uses the supported generated-assets Variant API. Intended changes are being committed in signing/notices, release tooling and documentation groups; a final committed-source unsigned candidate will be built afterward.

Commit 54c17ef records environment-only signing, owner setup, release provenance/inventory and the scoped runtime notice bundle. The next checkpoint records candidate/publication tooling and actual release-regression evidence.

## Phase 2.2 — documented candidate closure

Commit 99330c6 records candidate verification, explicit draft/publication tooling, six rejection/positive cases and fresh sanitized test evidence. All 27 Markdown files/143 local references passed; GFM parsing and script syntax checks passed. The current owner key/public pin is absent, so the signed Gradle/apksigner path, public mutation paths, actual release URLs and signed-release device installation remain unverified. No temporary key was used to make those gates appear complete.

The final documentation checkpoint updates AGENTS, README/pt-BR downloads, candidate changelog, roadmap, handoff and continuation instructions. It is separate from any release tag. After committing, run the unsigned preparation once more so the APK's embedded source commit and verification.json match the final committed tree; the actual artifact size/hash belongs in that ignored report/SHA256SUMS and user delivery. Preserve the expected native-only dirty state. No Phase 3, push, tag, draft, release, uninstall or model redistribution. Exact next action: owner completes key/backups/public pin, then runs signed preparation locally before controlled draft/publication.

## Permanent signing certificate checkpoint

Owner created the permanent external identity locally; public DER SHA-256 matches the supplied e19c9d002254a776d6b19c0ad2c1dcee01971a46971d5d1fb915867bd6530a2a. Only the public pin is added. Password/private key are not read. Backup completion and signed preparation remain pending. Connected the owner-provided emulator at127.0.0.1:5555: Android9/API28, x86_64 with advertised ARM64 through libnb native translation. No Auviora package is installed; installation/runtime results must be recorded separately from physical ARM64 evidence.

## Windows PowerShell 5.1 negative-fixture fix

Public pin checkpoint: 489a1d0. The owner's signed preparation failed before APK compilation because Windows PowerShell 5.1 promoted the expected CMake conflict stderr into a terminating NativeCommandError under Stop. Reproduced the exact failure before editing. The fixture runner now scopes Continue only around expected-failure capture, restores the preference, and requires exit code 1 plus the conflict diagnostic and unchanged fixture source. Engine, native patch, dependencies and signing identity remain unchanged.

Fixture validation passed on Windows PowerShell 5.1.19041.3803 and PowerShell 7.6.5 with native error-action preference enabled. Complete unsigned preparation on 5.1 passed: 9s incremental release assembly, five cached core JVM test results, release lint 0 errors/13+1+0 warnings and metadata/native/alignment/notice/weight checks. All six release-verifier cases passed again (17.733s); 27 Markdown files and 143 local references passed. The ignored powershell51-patch-fix-check candidate is explicitly unsigned and records dirty parent source; never distribute it. Owner-local signed retry and backup confirmation are still required. No tag, push, release, installation or Phase 3.


## Phase 2.2 — signed Alpha publication closure

Commit 4997566 fixes the reproduced PowerShell 5.1 fixture interruption and is the exact signed source. The owner-local retry produced Auviora-v0.1.0-alpha.1-arm64-v8a.apk, 42,736,118 bytes, SHA-256 19a4587de5d47a46f266a20d6c11a8ffc64459950b8feffcfb5dda13efd869ba. Independently verified its permanent RSA4096 v2 signature against the public pin, clean parent source, compiled metadata, ten native libraries, 16KB ELF/ZIP alignment, notices and absence of bundled weights. Core JVM 5/5 and release lint 0 errors/13+1+0 warnings remain recorded.

At the owner's request, copied the existing keystore/public DER/password-free identity into two new owner-only directories outside Git on distinct C: and D: physical disks. Every copied file hash matched and ACLs were checked. The owner then confirmed separate password storage. Password/private key contents were never printed or versioned; both disks are in the same computer, so an additional off-device backup remains recommended.

Installed and launched the exact signed APK on the owner's Android 9/API 28 x86_64 emulator with ARM64 native translation. Actual app provisioning/integrity/load/projector/READY and visible real generation passed; literal multiplication answer ended in 105. A forest-story request was stopped after partial text, returning to READY. Unload/reload passed, and a fresh chat answered 4 to 2 plus 2. Final model unloaded and verified GGUFs retained. No production test hook or debug replacement. Emulator offline isolation was not established: eth0 remained default and the protected airplane broadcast was denied; restored radio settings 0/0/1 were verified. Physical ARM64 Android 16 debug offline evidence remains separate.

Ran controlled draft and publish workflows with the existing public repository. Normal source/tag pushes preserved remote work. Annotated tag object a51b6071902cc205c14bf10861bad2a93cb1b418 peels to 4997566a22e4425f6d8f4d4f1800ff99ab7be96f. Draft downloads of all four assets matched local bytes. Published pre-release at 2026-10-22T19:27:15Z: https://github.com/eobarretooo/Auviora/releases/tag/v0.1.0-alpha.1. Anonymous public downloads of APK/checksums/notices/verification all returned HTTP 200 and identical SHA-256; the downloaded APK passed the full verifier again before publication documentation changed HEAD.

This final documentation checkpoint updates download links, changelog, English release notes, roadmap, release process, testing summary and handoff. It follows the immutable tested tag without replacing assets or rebuilding. Local Windows automation is validated; hosted signing Actions, signed physical-device offline checks and a broad device/stress matrix remain unverified. Phase 2.2 complete; Phase 3 not started. For future releases choose a new version/code, retain the permanent identity and repeat all gates. No unrelated app/engine/dependency changes.
