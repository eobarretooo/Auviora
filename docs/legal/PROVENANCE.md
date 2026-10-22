# Reuse and legal inventory

Reference: OpenBMB/MiniCPM-V-Apps commit d416a13. The original checkout is read-only reference, not a build/runtime dependency.

| Component | Provenance / terms | Treatment |
|---|---|---|
| llama.cpp-omni | tc-mb fork, 96bbf61710d44421ea54e63f5194f9b17c20d877; MIT, ggml authors and tc-mb | Public submodule; full license retained |
| JNI text/vision bridge, logging.h, CpuFeatures | Reference Android sources at d416a13; MIT, 2026 tc-mb | Copied only necessary integration, renamed JNI/package, removed prompt/token logs, made teardown idempotent, reset/decode errors observable |
| Portable expert-cache patch/application/test and v86 task | Reference validated downstream integration at d416a13; MIT | Parent-owned, adapted repository paths only |
| Gradle wrapper | Gradle distribution wrapper; Apache-2.0 | Wrapper notices retained in scripts; no Gradle binaries other than standard wrapper JAR |
| KleidiAI | Vendor CMake pins v1.24.0 archive/MD5; Apache-2.0 with BSD-3-Clause files | Build-time fetched source; license retained by upstream, see native dependency audit before distribution |
| AndroidX / Compose / Material 3 | Google/AndroidX; Apache-2.0 | Maven dependencies, pinned versions, no borrowed demo UI/assets |
| MiniCPM-V 4.7 1B artifacts | tc-mb HF revision 8716e5209f308fd53f197ccd7d50c6fdcc2dc37d | Sizes/SHA-256 pinned; no bundled weights |

OpenBMB identifies the base model as Apache-2.0, but the project-hosted converted GGUF pair has no license declaration in its API card. Source-code licensing does not settle conversion/weight redistribution rights. Redistribution review remains open; no APK/weights are published in this phase.

Auviora sources use MIT with original reused notices preserved. Brand/icon are newly authored vectors, not demo assets. No claim of trademark registration/domain ownership is made.

## Additional native notices inspected

- nlohmann/json: vendor/nlohmann/json.hpp, SPDX MIT, Niels Lohmann.
- stb_image: vendor/stb/stb_image.h, MIT (Sean Barrett) or public domain alternatives; original complete header retained.
- miniaudio: vendor/miniaudio/miniaudio.h, public domain or MIT-0; original complete header retained. Its code is compiled in MTMD; this does not add an audio feature to the UI.
- KleidiAI fetched archive includes LICENSES/Apache-2.0.txt and BSD-3-Clause.txt. The archive/version/hash are pinned by vendor CMake.
- NDK OpenMP: pinned r27 toolchain NOTICE.toolchain identifies LLVM Apache-2.0 with LLVM exceptions and additional third-party terms. Runtime copied from that NDK; publication requires assembling the applicable binary notices.

2026-10-22 live publisher API check: project artifact revision 8716e5209f308fd53f197ccd7d50c6fdcc2dc37d still has no card license. OpenBMB base-model revision 379f120c2ff3588e6e06559ec6b9ec15cc05114e declares apache-2.0. This is not a license certification for the converted pair. No weights or packages are redistributed.

Sources: [pinned project metadata](https://huggingface.co/api/models/tc-mb/MiniCPM-V-Apps-gguf/revision/8716e5209f308fd53f197ccd7d50c6fdcc2dc37d), [base model](https://huggingface.co/openbmb/MiniCPM-V-4.7-1B/tree/379f120c2ff3588e6e06559ec6b9ec15cc05114e).

## Phase 2.2 binary notice preparation

The current scoped APK-code redistribution inventory is in [RELEASE_AUDIT.md](RELEASE_AUDIT.md) and [release-audit.json](release-audit.json). Original Maven/native notices are now packaged in the APK and staged as an accompanying text asset. This closes the previous notice-assembly gap for the reviewed code inventory; it does not grant converted-model redistribution rights. No model weights are bundled. Permanent signing and publication status remain independent gates in the [release process](../RELEASE_PROCESS.md).
