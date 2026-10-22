# Auviora

**Private AI. Real Capabilities.**

Independent Kotlin/Jetpack Compose Android application with a pinned local inference engine. English canonical documentation | [Português brasileiro](docs/i18n/pt-BR/README.md).

**Phase 2.0 foundation complete for tested workloads. Phase 3 has not started.** Development version `0.1.0-alpha.1` (code 1), application ID `app.auviora.android`. No published release or bundled model weights.

Implemented: original Material 3 identity, dark/light/system/dynamic themes, left adaptive drawer, streamed chat with Stop, verified model download/SAF import, basic real conversation history, disabled Agents and Settings with English/pt-BR selection. There is no bottom navigation or cloud inference fallback.

## Build

Install Android Studio/JDK 21, SDK 36.1, NDK 27.0.12077973 and CMake 4.1.2. Gradle wrapper 9.3.1 and AGP 9.1.1 are pinned. Set JAVA_HOME/ANDROID_HOME for your machine; never commit SDK paths.

```powershell
git submodule update --init --recursive --depth 1
./gradlew.bat :app:assembleDebug :app:assembleDebugAndroidTest :core:testDebugUnitTest :app:lintDebug :core:lintDebug :inference:lintDebug
```

CMake automatically applies the versioned portability patch to the public pinned submodule. See [testing commands](docs/TESTING.md) and [legal provenance](docs/legal/PROVENANCE.md). The resulting patch-only modified vendor working tree is expected.

## Use

Install the debug APK locally on an ARM64 device. Open Models and explicitly download the 1.78 GB compatible pair or import each exact file through Android's document picker. Files are fully checked before promotion/loading. Load the model, return to Chat and send a message. After provisioning, generation works offline. Unload releases model/session memory. Keep the app open during downloads; advanced/background download management is not implemented.

The compact model can mix languages, claim incorrect identity and make factual errors. Saved history is read-only until native-context restoration is implemented. Agents, advanced memory, image attachments and background automation are future work.

## Evidence and continuation

[Full validation report](docs/testing/PHASE_02_VALIDATION.md) · [real chat capture](docs/testing/phase2-chat.png) · [left drawer capture](docs/testing/phase2-drawer.png).

[Architecture](docs/ARCHITECTURE.md) · [structure](docs/PROJECT_STRUCTURE.md) · [design contract](DESIGN.md) · [handoff](docs/AI_HANDOFF.md) · [roadmap](docs/ROADMAP.md) · [changelog](docs/CHANGELOG.md).

MIT source with original third-party notices preserved. Model terms are separate and conversion redistribution remains unapproved. Nothing has been pushed or published.
