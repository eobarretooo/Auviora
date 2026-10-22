# Project structure

```text
app/                       Android application, composition and presentation
  src/main/java/app/auviora/android/
    MainActivity.kt        AppCompat locale host, Compose content
    AuvioraApplication.kt  Process ownership/composition
    AppViewModel.kt        Lifecycle state/actions and streamed chat
    AuvioraUi.kt           Four screens, left drawer, reusable components
    design/Theme.kt        Material3 schemes/type/shapes/spacing
    data/                  Model provisioning and AtomicFile history
  src/main/res/            English/pt-BR, icon, locale/backup/theme configuration
  src/androidTest/         Foundation navigation/locale and opt-in real inference
core/                      Engine/model/error contracts, profile and verifier
  src/test/                Integrity/catalog regression tests
inference/                 Serial native adapter, JNI and CMake/v86 build
vendor/llama.cpp-omni/      Public pinned submodule; expected patch-only modification
patches/                   Reproducible Android portability patch
scripts/                   Patch verification and validation helpers
artifacts/                 Ignored build logs, captures and local reproduction checkout
DESIGN.md                  Canonical UI contract
docs/                      Technical handoff, decisions, testing, design and legal
```

No persistent dependency on any sibling checkout. Gradle module dependencies: app → core and inference; inference → core. Native sources and weights are separate. Build outputs, models, SDK paths, caches and APKs are ignored.
