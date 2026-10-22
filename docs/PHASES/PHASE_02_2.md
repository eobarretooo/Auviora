# Phase 2.2 — release infrastructure and first Alpha candidate

Date: 2026-10-22. **Infrastructure and unsigned candidate validated. Public distribution blocked pending the owner's permanent signing identity and signed-candidate checks. Phase 3 not started.** Version remains 0.1.0-alpha.1 / code 1. No release or tag has been created, and no source/tag push was performed by this phase.

## Repository and access

Existing origin is https://github.com/eobarretooo/Auviora.git. The public repository's default branch is main; authenticated GitHub CLI 2.103.0 reports ADMIN access through the credential store. No installation/login was required. Initial remote main matched af794a2; local/remote tags and GitHub releases were empty. The workflow never changes visibility, credentials, permissions or unrelated repositories.

## Implementation

- Gradle release signing uses four environment variables, requires a complete configuration and has no debug fallback. Missing identity fails unless an explicitly unsigned candidate is requested. Application ID/ABI/version remain unchanged; minification/toolchain/native engine are not upgraded.
- Owner-interactive setup creates an external persistent PKCS12/RSA4096 key, restricts a new dedicated Windows directory, never overwrites identity and exports only public certificate metadata. The owner selected this path and intends to retain separate backups; creation/backup completion has not been reported or observed. No temporary public signing identity was generated.
- Local preparation resolves notices, tests the patch, builds/tests/lints, verifies/stages the APK, and clears signing credentials. A generated release asset binds the APK to the exact Git commit through the AGP Variant API. Signed candidates require committed parent source and a matching public certificate pin.
- Artifact verification checks compiled identity/version/SDK/debuggable/ABI, all 10 ARM64 ELF libraries, 16 KB LOAD/ZIP alignment, no GGUF names/magic, exact notice bytes, signature/certificate and staged checksum. Existing candidates are never overwritten. Unsigned names explicitly end in -UNSIGNED.apk.
- Controlled draft/publish tooling derives origin, protects existing releases/tags, checks monotonic codes and the scoped legal inventory, creates an annotated exact-source tag only after gates, uses a draft pre-release, re-downloads all four assets and checks their hashes before publication. Public mutation paths remain unexecuted because signing is unavailable.
- Runtime notice bundle includes original source/native/Maven licenses and a conservative complete NDK notice superset. The current graph has 106 module records / 74 cached artifact records. No model weights are included or approved for redistribution.
- English/pt-BR README download guidance links to the actual Releases page, without a fake direct APK URL. Alpha notes, release process, contribution instructions, changelog, testing, roadmap and handoff describe the actual blocked state.

## Verification

| Check | Result |
|---|---|
| Actual release configuration | PASS; explicit unsigned mode |
| Core shared JVM tests | 5/5 PASS through testDebugUnitTest |
| Release lint app/core/inference | 0 errors; 13/1/0 warnings |
| Native patch fixture |Clean/idempotent/reverse/conflict-rejection PASS |
| Exact native diff/pin |Reviewed patch and public 96bbf617 pin preserved |
| APK metadata/native/ZIP/weights/notices |PASS |
| Release-verifier rejection suite |Six cases: candidate, unsigned distribution, debug, hidden GGUF, changed notices, stale source commit |
| Fresh hardware debug navigation/history |4/4 PASS |
| Fresh hardware debug offline inference/cancel/reload |1/1 PASS |
| Fresh hardware debug offline visible streaming |1/1 PASS |
| Owner permanent signature / signed APK |BLOCKED; key/public pin absent |
| Signed release installed on hardware |NOT RUN; no destructive debug replacement |
| GitHub draft/tag/publication/asset URLs |NOT CREATED; signing gate stops them |

The local candidate and SHA256SUMS/notice/verification files are staged under ignored artifacts/releases. A preliminary candidate was built while source changes were uncommitted and is explicitly marked dirty. The final candidate is rebuilt/staged after all intended source commits, with embedded commit and verification.json agreeing; its actual size/SHA/source are in that local report and user delivery. No circular self-referential artifact hash is invented in this source document.

[Sanitized check summary](../testing/phase2-2-checks.json). The fresh streaming observer saw 27 distinct visible updates.

The debug regression APK is separate from the unsigned release. Navigation/history tests passed in 4.493s, offline inference in 22.054s and streaming in 10.041s; these are test execution durations, not model performance benchmarks. No model re-download was needed. The debug update used install-r with the existing certificate, preserving data. Offline runner restored saved radio settings. Original versioned Phase 2.0/2.1 evidence was preserved.

The Android Debug certificate SHA-256 is cb8cb08b661ad5772a6effe044c182ccee79b96cb60c0f2dac2dda7654107ebb. A newly created release key will differ. Android cannot normally update it in place with a separately signed APK. Uninstall deletes private history/GGUF files; no uninstall or private-data export was performed.

## Diagnosed preparation failures

No dependency upgrade or engine modification was used to hide failures. Core has no testReleaseUnitTest task, so shared tests use its actual debug test task. A dotted Gradle property needed quoting in PowerShell. A legacy artifact query hit Android project-variant ambiguity, so the inventory now reads resolved component IDs/POMs. Guava's missing direct license was inherited from its parent POM and verified. A single-element PowerShell branch result was being splatted as characters; it is now explicitly an array. AGP9 rejects Provider instances in the legacy source-set API; generated provenance uses the supported Variant API. Missing-signing failures are expected safety gates. Patch fixture conflict output is the expected negative test, not an unresolved checkout conflict.

## Exact continuation

1. The owner completes scripts/setup-release-key.ps1 locally and retains encrypted keystore backups/password separately. Never send a password through chat.
2. Record/commit only the approved public certificate fingerprint as described in [release process](../RELEASE_PROCESS.md).
3. Run the signed preparation script in the owner's local terminal. Inspect signature, source commit, checksum and all reports. Preserve the debug app or use separate test hardware/profile; do not automatically uninstall it.
4. If every gate passes, use the explicit draft workflow, verify downloaded assets, then publish the pre-release. Verify real public URLs/bytes and update publication documentation. Never substitute the current unsigned candidate.

Local Windows automation is the validated path; hosted Actions signing/publication has not been exercised and no automatically publishing workflow is installed. Model conversion terms remain separate; weights are not bundled. See [binary audit](../legal/RELEASE_AUDIT.md), [Alpha notes](../releases/v0.1.0-alpha.1.md) and [release process](../RELEASE_PROCESS.md).

## Commit checkpoints

Phase 2.1 baseline: af794a2. Checkpoint54c17ef records signing/provenance and runtime notices; subsequent tooling/documentation hashes are in the handoff/log. Retrieve the closure source with `git log -1 --format=%h -- docs/AI_HANDOFF.md`; it is separate from any future release tag.
