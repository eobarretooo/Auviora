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
