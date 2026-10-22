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
Native/core build and 5 JVM tests PASS; patch fixture PASS. App and test APK build PASS. Hardware FoundationTest 2/2 and isolated ConversationRepositoryTest 2/2 PASS. APK structural check PASS: 10 ARM64 libraries, all ELF LOAD segments >=16KB, required OpenMP/JNI present, no desktop tools. Exact model provisioning and new-app inference are in progress; not yet claimed. Raw logs remain ignored in artifacts/. Device: 60a36dc3.

Retain model quality failures inherited from Phase1.6: language mixing, false identity, factual errors and ~24s vision samples. Do not transfer those old measurements into new-app benchmark claims. No production distribution/weight license approval.
