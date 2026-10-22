# Alpha binary redistribution audit

Reviewed 2026-10-22 for version 0.1.0-alpha.1. Scope: application code and libraries packaged in the APK, with the pinned native backend and current Maven runtime graph. This is a source/notice inventory, not a blanket legal certification of model weights or every future dependency.

## Included code and notice treatment

- Auviora and reused reference integration: original MIT texts retained, including tc-mb credits.
- llama.cpp-omni/ggml at 96bbf61710d44421ea54e63f5194f9b17c20d877: original MIT notice, vendor license directory, and license blocks in common/ggml/src/tools/mtmd retained. curl/httplib/linenoise notices are included conservatively even where a code path is not linked.
- nlohmann/json: full upstream MIT license retained. stb_image and miniaudio: original embedded alternative-license blocks retained, including their authors' terms.
- KleidiAI v1.24.0: Apache-2.0/BSD-3-Clause texts and source SPDX copyright inventory retained. Generic BSD templates alone were not treated as author attribution.
- NDK r27 OpenMP and statically linked C++ support: complete original NOTICE and NOTICE.toolchain included as a conservative superset. The bundle includes notices for tools not shipped in the APK; their presence does not mean those tools are linked or that Auviora adopts all their licenses.
- 106 resolved Maven module records (including metadata/BOM modules), with 74 cached artifact records: POM license declarations are Apache-2.0 variants; archive LICENSE/NOTICE/COPYING resources, including nested classes.jar resources, are retained. Guava listenablefuture 1.0 inherits its Apache declaration from guava-parent 26.0-android; the parent was inspected rather than inventing a missing declaration. The runtime inventory includes module versions, POM/artifact hashes and license URLs, without local paths.

The deterministic [collector](../../scripts/collect-release-notices.py) fails on missing or non-Apache Maven declarations pending manual review. Its output is [third-party-notices.txt](apk-assets/third-party-notices.txt), packaged verbatim at `assets/third-party-notices.txt` and staged as a separate release attachment. [runtime-dependencies.json](runtime-dependencies.json) and [release-audit.json](release-audit.json) bind this review to the current inventory and notice hash. Re-review any dependency/native change; do not carry this status to an arbitrary new stack.

## Separate model terms

APK verification checks every ZIP entry for GGUF names/magic and rejects bundled weights. The APK is approximately 43 MB, not the 1.78 GB model pair. The application offers an explicit download from the original model publisher or imports exact user-provided files. It does not redistribute model files as GitHub assets.

The converted pair's pinned publisher card had no license declaration in the prior publisher check; no new redistribution permission is inferred. Base-model licensing and code licensing do not settle the conversion's terms. Weight redistribution remains unapproved. Users must review publisher terms before obtaining/using weights. Publishing an APK must never attach the GGUF pair or imply ownership of the model. See [provenance](PROVENANCE.md).

No unresolved binary-code notice blocker was found within this reviewed scope after collecting the original notices. This does not resolve weight redistribution, trademark registration, all jurisdictions, or future code changes. Permanent signing, signature/artifact checks and functional release gates remain independently required.
