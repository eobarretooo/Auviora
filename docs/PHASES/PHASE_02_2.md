# Phase 2.2 — release infrastructure and first published Alpha

Date: 2026-10-22. **COMPLETE: permanent-key signed v0.1.0-alpha.1 published as a pre-release after all release gates. Phase 3 not started.** Version remains 0.1.0-alpha.1 / code 1. Normal source/tag pushes and controlled draft/publication completed; existing work was preserved.

## Published artifact

- [Release](https://github.com/eobarretooo/Auviora/releases/tag/v0.1.0-alpha.1), published 2026-10-22T19:27:15Z.
- [Auviora-v0.1.0-alpha.1-arm64-v8a.apk](https://github.com/eobarretooo/Auviora/releases/download/v0.1.0-alpha.1/Auviora-v0.1.0-alpha.1-arm64-v8a.apk): 42,736,118 bytes (40.76 MiB).
- APK SHA-256: 19a4587de5d47a46f266a20d6c11a8ffc64459950b8feffcfb5dda13efd869ba.
- Certificate SHA-256: e19c9d002254a776d6b19c0ad2c1dcee01971a46971d5d1fb915867bd6530a2a; RSA4096, CN=Auviora Android Release, APK Signature Scheme v2 verified.
- Annotated tag v0.1.0-alpha.1 peels to 4997566a22e4425f6d8f4d4f1800ff99ab7be96f, matching the APK's embedded source and public verification.json. Later documentation commits do not retag/rebuild this artifact.
- Four assets: APK, SHA256SUMS.txt, THIRD_PARTY_NOTICES.txt and verification.json. Draft downloads matched; all four anonymous public downloads returned HTTP 200 with identical hashes. Public downloaded APK passed full verification again.

## Repository and access

Existing origin is https://github.com/eobarretooo/Auviora.git. The public repository's default branch is main; authenticated GitHub CLI 2.103.0 reports ADMIN access through the credential store. No installation/login was required. Initial remote main matched af794a2; local/remote tags and GitHub releases were empty. The workflow never changes visibility, credentials, permissions or unrelated repositories.

## Implementation

- Gradle release signing uses four environment variables, requires a complete configuration and has no debug fallback. Missing identity fails unless an explicitly unsigned candidate is requested. Application ID/ABI/version remain unchanged; minification/toolchain/native engine are not upgraded.
- Owner-interactive setup creates an external persistent PKCS12/RSA4096 key, restricts a new dedicated Windows directory, never overwrites identity and exports only public certificate metadata. The owner created the identity; its public DER and password-free metadata independently match the committed pin. At the owner's request, exact backups were copied outside Git to distinct C: and D: physical disks with owner-only ACLs verified. The owner confirmed separate password storage before publication. An additional off-device copy remains recommended; passwords/private key contents were never printed or versioned.
- Local preparation resolves notices, tests the patch, builds/tests/lints, verifies/stages the APK, and clears signing credentials. A generated release asset binds the APK to the exact Git commit through the AGP Variant API. Signed candidates require committed parent source and a matching public certificate pin.
- Artifact verification checks compiled identity/version/SDK/debuggable/ABI, all 10 ARM64 ELF libraries, 16 KB LOAD/ZIP alignment, no GGUF names/magic, exact notice bytes, signature/certificate and staged checksum. Existing candidates are never overwritten. Unsigned names explicitly end in -UNSIGNED.apk.
- Controlled draft/publish tooling derives origin, protects existing releases/tags, checks monotonic codes and the scoped legal inventory, creates an annotated exact-source tag only after gates, uses a draft pre-release, re-downloads all four assets and checks their hashes before publication. These paths completed for the first Alpha, followed by anonymous public verification.
- Runtime notice bundle includes original source/native/Maven licenses and a conservative complete NDK notice superset. The current graph has 106 module records / 74 cached artifact records. No model weights are included or approved for redistribution.
- English/pt-BR README download guidance now links to the verified public release, exact APK and checksums. Alpha notes, release process, changelog, testing, roadmap and handoff record publication and limits.

## Verification

| Check | Result |
|---|---|
| Actual release configuration | PASS; explicit unsigned preparation and permanent-key signed build |
| Core shared JVM tests | 5/5 PASS through testDebugUnitTest |
| Release lint app/core/inference | 0 errors; 13/1/0 warnings |
| Native patch fixture |Clean/idempotent/reverse/conflict-rejection PASS |
| Exact native diff/pin |Reviewed patch and public 96bbf617 pin preserved |
| APK metadata/native/ZIP/weights/notices |PASS |
| Release-verifier rejection suite |Six cases: candidate, unsigned distribution, debug, hidden GGUF, changed notices, stale source commit |
| Fresh hardware debug navigation/history |4/4 PASS |
| Fresh hardware debug offline inference/cancel/reload |1/1 PASS |
| Fresh hardware debug offline visible streaming |1/1 PASS |
| Owner permanent signature / signed APK |PASS; committed public pin, permanent v2 signature, exact source/checksum |
| Signed release installed on hardware |NOT RUN; no destructive debug replacement |
| Signed release installed on emulator |PASS; Android 9/API 28 x86_64 with ARM64 translation; actual provisioning/inference/streaming/Stop/unload/reload |
| GitHub draft/tag/publication/asset URLs |PASS; annotated exact-source tag, pre-release, four draft and anonymous public asset checks |

The local signed candidate and SHA256SUMS/notice/verification files are staged under ignored artifacts/releases/v0.1.0-alpha.1-signed-candidate. Earlier explicitly unsigned/dirty candidates remain nondistributable historical checks. The published candidate was built from committed 4997566; current publication documentation is a separate later source checkpoint, so this report can record the exact published artifact hash without a circular provenance claim.

[Sanitized check summary](../testing/phase2-2-checks.json). The fresh streaming observer saw 27 distinct visible updates.

The debug regression APK is separate from the signed release. Navigation/history tests passed in 4.493s, offline inference in 22.054s and streaming in 10.041s; these are test execution durations, not model performance benchmarks. No model re-download was needed on hardware. The debug update used install-r with the existing certificate, preserving data. Offline runner restored saved radio settings. Original versioned Phase 2.0/2.1 evidence was preserved.

The Android Debug certificate SHA-256 is cb8cb08b661ad5772a6effe044c182ccee79b96cb60c0f2dac2dda7654107ebb, different from the permanent release identity. Android cannot normally update it in place with a separately signed APK. Uninstall deletes private history/GGUF files; no uninstall or private-data export was performed.

## Signed emulator validation

Installed the exact signed candidate on the owner's endpoint 127.0.0.1:5555, initially without an Auviora installation. Android 9/API 28 advertises x86_64 plus ARM64 via libnb/Houdini translation; this is not physical ARM64 hardware. App launch, Models navigation, actual 1.78GB in-app download/integrity checks, native model/projector/context initialization and READY passed. Normal UI generation visibly grew through multiple screenshots, settled to READY and displayed this literal response:

```text
To find the product of 15 and 7, we can multiply them directly:

15 * 7 = 105

So, 15 multiplied by 7 is 105.
```

A real forest-story request produced "In the heart of" before Stop, then returned to READY. Unload and verified reload passed; a new chat answered "4" to "What is 2 plus 2? Answer with one number." The final model was unloaded and downloaded files retained. No debug instrumentation, engine substitute or production test hook was used.

Offline isolation could not be established on this emulator: eth0 retained active default network 100 despite Wi-Fi/data commands, and the protected airplane broadcast returned Permission Denial. Original radio settings were restored and checked: airplane=0, Wi-Fi=0, mobile-data=1. This signed run has network available; physical Android 16 debug offline evidence stays separate. Process PSS samples were 1,521,520 KiB before unload and 1,369,537 KiB after; translation/process caches are not isolated, and this is neither a memory-leak determination nor a peak/thermal benchmark. Uiautomator could not obtain idle state during download/focused input; screenshots and later idle hierarchy captures supplied actual UI evidence. Raw captures/logs remain ignored; literal outputs and checks are sanitized in the JSON summary.

## Diagnosed preparation failures

No dependency upgrade or engine modification was used to hide failures. Core has no testReleaseUnitTest task, so shared tests use its actual debug test task. A dotted Gradle property needed quoting in PowerShell. A legacy artifact query hit Android project-variant ambiguity, so the inventory now reads resolved component IDs/POMs. Guava's missing direct license was inherited from its parent POM and verified. A single-element PowerShell branch result was being splatted as characters; it is now explicitly an array. AGP9 rejects Provider instances in the legacy source-set API; generated provenance uses the supported Variant API. Missing-signing failures are expected safety gates. Patch fixture conflict output is the expected negative test, not an unresolved checkout conflict.

## Exact continuation

1. Phase 2.2 is complete. Preserve the published tag/assets/checksums and permanent identity; keep an additional off-device key backup.
2. Do not rerun publication for v0.1.0-alpha.1 or replace its assets. Future application releases need a new version/code and every gate in the [release process](../RELEASE_PROCESS.md).
3. Leave the physical debug installation/data intact. Signed hardware offline and expanded device/stress testing remain future work.
4. Wait for explicit new scope before Phase 3 or product changes.

Local Windows automation is the validated path; hosted Actions signing/publication has not been exercised and no automatically publishing workflow is installed. Model conversion terms remain separate; weights are not bundled. See [binary audit](../legal/RELEASE_AUDIT.md), [Alpha notes](../releases/v0.1.0-alpha.1.md) and [release process](../RELEASE_PROCESS.md).

## Windows PowerShell 5.1 signed-preparation interruption

The first owner-signed attempt stopped in the deliberately conflicting native-patch fixture, before APK compilation. Reproduced the exact NativeCommandError on Windows PowerShell 5.1.19041.3803: native stderr redirected with 2>&1 becomes an error record, so ErrorActionPreference=Stop prevents checking the expected nonzero exit. The fixture now captures this expected failure with a narrowly scoped Continue preference, restores the previous preference, and checks exit code 1, the specific conflict diagnostic and unchanged fixture source. Unexpected failures remain failures. No engine, patch, signing identity or dependency was changed.

The fixture passed on Windows PowerShell 5.1 and PowerShell 7.6.5 with PSNativeCommandUseErrorActionPreference enabled. Full unsigned preparation passed under 5.1, including release assembly (9s incremental), five cached core JVM test results, release lint with 0 errors/13+1+0 warnings and artifact checks. This explicitly unsigned, dirty-source diagnostic candidate is not distributable. The owner's subsequent signed retry succeeded at 4997566 and produced the verified published APK described above.

## Commit checkpoints

Phase 2.1 baseline: af794a2. Checkpoint 54c17ef records signing/provenance/runtime notices; 99330c6 records verification/publication tooling; de17394 records preparation docs; 489a1d0 pins the certificate; 4997566 fixes PowerShell 5.1 and is the exact signed release source. Annotated tag object a51b6071902cc205c14bf10861bad2a93cb1b418 peels to 4997566. This publication documentation follows separately without retagging. Retrieve its checkpoint with `git log -1 --format=%h -- docs/AI_HANDOFF.md` and see the delivery for its hash.
