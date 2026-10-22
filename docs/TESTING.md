# Testing

## Build requirements
Git/submodule, Android Studio JBR launcher and daemon JDK21 (Gradle criteria/Foojay), SDK36.1/Build Tools36, NDK27.0.12077973, CMake4.1.2/Ninja, Gradle9.3.1 and ARM64 device. Initial dependency/KleidiAI downloads need network; runtime inference does not. SDK paths are environment/local.properties only, never versioned.

```powershell
$env:JAVA_HOME = Join-Path $env:ProgramFiles 'Android/Android Studio/jbr'
$env:ANDROID_HOME = Join-Path $env:LOCALAPPDATA 'Android/Sdk'
git submodule update --init --recursive --depth 1
& "$env:ANDROID_HOME/cmake/4.1.2/bin/cmake.exe" -DLLAMA_SRC="$PWD/vendor/llama.cpp-omni" -P scripts/apply-native-patches.cmake
./scripts/test-native-patches.ps1 -CMake "$env:ANDROID_HOME/cmake/4.1.2/bin/cmake.exe"
./gradlew.bat :app:assembleDebug :app:assembleDebugAndroidTest :core:testDebugUnitTest :app:lintDebug
```

CMake applies the patch automatically. The gitlink stays public/pinned; one patched vendor source is intentionally dirty. Initial clone may need `git -C vendor/llama.cpp-omni fetch --depth 1 origin 96bbf61710d44421ea54e63f5194f9b17c20d877` if the host's default branch differs. No local-only engine commit.

## Layers
- Core JVM verifies full hash, same-size corruption, truncation/missing file, GGUF signature and exact profile. Tiny test files validate integrity logic, not AI behavior.
- Patch fixture verifies clean application, idempotence, reversal and conflict rejection without destroying changes.
- FoundationTest exercises actual Compose drawer, disabled Send/Agents, back, themes and supported locale API. It is not inference proof.
- RealInferenceTest is skipped unless explicitly enabled. Provisioning uses the app's HTTPS downloader into its own sandbox. Inference drives actual load/input/send/stop/unload/reload UI and records literal text. No fake backend.

```powershell
adb devices -l
adb -s SERIAL install -r app/build/outputs/apk/debug/app-debug.apk
adb -s SERIAL install -r app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk
adb -s SERIAL shell am instrument -w -e class app.auviora.android.FoundationTest app.auviora.android.test/androidx.test.runner.AndroidJUnitRunner
adb -s SERIAL shell am instrument -w -e class app.auviora.android.RealInferenceTest#provisionExactArtifactsThroughApplicationDownload -e provision true app.auviora.android.test/androidx.test.runner.AndroidJUnitRunner
adb -s SERIAL shell am instrument -w -e class app.auviora.android.RealInferenceTest#displayRealResponseCancelRecoverAndReload -e realInference true app.auviora.android.test/androidx.test.runner.AndroidJUnitRunner
```

Verify both exact artifacts before loading. The UI also supports SAF imports. Never access another app's private model directory/root. Download is foreground-only and does not resume after process death. Cancellation is cooperative; native prefill may finish before Stop takes effect.

## Current evidence

[Full report](testing/PHASE_02_VALIDATION.md) and [machine-readable checks](testing/phase2-checks.json). Final app/test builds PASS; core JVM 5/5; app lint 0 errors/13 warnings, core 0 errors/1 warning, inference no issues. Native patch fixture PASS. APK structural check PASS: 10 ARM64 libraries, all ELF LOAD segments >=16KB, required OpenMP/JNI present, no desktop tools.

Hardware FoundationTest 2/2 and ConversationRepositoryTest 2/2 PASS. Exact in-app provisioning 1/1; final offline UI inference/cancel/recovery/unload/reload 1/1 and visible streaming 1/1 (33 updates). No mocks or canned runtime output. Test-only streaming observation uses the pinned Compose Android view semantics owner, respects enabled actions and advances the test frame clock while real IO runs. Framework upgrades must revalidate this interop. Failed intermediate observers are documented, not counted as positive engine evidence.

```powershell
python scripts/verify-project.py --apk app/build/outputs/apk/debug/app-debug.apk
./scripts/run-device-validation.ps1 -Serial SERIAL
./scripts/run-device-validation.ps1 -Serial SERIAL -Suite Streaming
```

The runner requires provisioned exact weights, verifies no active default network after asynchronous teardown, saves/restores network settings and copies raw UTF-8 JSON bytes. StreamingVisualTest is skipped without realInference=true. No runtime test endpoint or AccessibilityService is shipped. Screenshots/literal outputs are sanitized in docs/testing; raw logs, local reproduction clone, APKs and weights remain ignored or in the app sandbox. Original settings were verified restored to airplane0/Wi-Fi1/mobile-data0 after tests.

An independent checkout/submodule initialization/build passed with automatic patch application. Subsequent production source checkpoint12b5a76 also built in that checkout. This is buildability evidence, not deterministic binary hashing.

Inherited model quality limitations remain: language mixing, false identity, factual errors. The rain explanation is simplified model output, not validated scientific advice. Current measurements are samples rather than peak/thermal/TTFT benchmarks. SAF picker/import, broad tablet/dynamic-color/light visual matrices, TalkBack/font scaling, long-context and extended stress remain unverified. No production distribution/weight-license approval.

## Phase 2.1 presentation verification

No Android source/test/build changes; no rebuild or full regression rerun. The installed APK hash matched the previously validated APK. A fresh normal-UI offline generation, streaming capture and model load/unload were observed on Android 16; radios/locale/theme were restored. The literal eight-item response to a ten-sentence request is retained in [media provenance](media/README.md).

```powershell
python scripts/check-docs.py
python scripts/media.py check docs/media/gifs/offline-inference.gif
```

Run the media checker on `docs/media/screenshots/*.png` paths too (expand them in PowerShell with Get-ChildItem, or list individual files). It verifies dimensions/size and GIF frame count/duration; human privacy/authenticity review remains necessary. PNG/GIF decoding, all local links, fenced blocks, GFM parsing and desktop/mobile local preview were checked; see [Phase 2.1](PHASES/PHASE_02_1.md). These checks add presentation evidence; they do not replace the historical app/JVM/lint/device suites above. Exact current checks and asset hashes are in the report and manifest.

## Phase 2.2 release gates

Release configuration now supports an explicit unsigned candidate and an owner-managed signed build. `./scripts/prepare-release.ps1 -UnsignedCandidate` runs the actual local build, resolved notice collection, patch fixture, five shared core JVM tests, release lint and APK staging checks. Omit the switch only after committing the permanent public certificate pin; the script prompts locally for the keystore password. Release/lintRelease without signing fails closed unless the unsigned property is explicitly selected.

`python scripts/test-release.py -v` requires the built unsigned APK and ANDROID_HOME. It checks a real candidate and rejection of unsigned distribution, debug APKs, hidden GGUF fixtures, changed notices and stale embedded source commits. ZIP fixtures are ignored and are not inference evidence. The verifier also checks all ten native ELF alignments, ZIP alignment, compiled identity/version/ABI/SDK and exact source/notice assets.

Fresh hardware debug source-regression tests: navigation/history 4/4, actual offline inference/cancel/reload 1/1 and visible streaming 1/1 PASS. Debug update preserved private data/weights. Those results do not assert a permanent-key release APK was installed. The debug certificate differs from the intended release identity; no automatic uninstall or data transfer occurs. Detailed status, logs and limits: [Phase 2.2](PHASES/PHASE_02_2.md), [release process](RELEASE_PROCESS.md).
