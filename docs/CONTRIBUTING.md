# Contributing

Read [AGENTS](../AGENTS.md), [handoff](AI_HANDOFF.md), [architecture](ARCHITECTURE.md), [design contract](design/DESIGN.md) and [testing](TESTING.md) before changing the project. Canonical source, scripts, comments, reports and Conventional Commits are English; the optional [pt-BR guide](i18n/pt-BR/README.md) is localized documentation.

Keep each contribution focused with related documentation and appropriate checks. Do not treat a build as runtime proof. Preserve the exact public engine pin and parent-owned patch; the patch-only modified vendor working tree is expected. Investigate failures before upgrading dependencies, and preserve the reference checkout, licenses and credits.

Inference must remain local. UI code consumes core contracts; JNI stays in inference. Model operations are serialized and cancellation settles before unload. Do not add fabricated answers, loaded states or conversation evidence. Document model terms separately from source-code licenses.

For source/build changes, run the build, relevant JVM/lint checks and opt-in hardware tests described in Testing. For documentation/media changes, run the [media and link workflow](media/README.md), inspect visuals and preserve original evidence. Never stage weights, APKs, raw recordings/logs, local SDK paths, credentials or device identifiers.

Use small Conventional Commits, update the changelog and handoff, and report untested paths explicitly. Phase 3 and automation require explicit new scope. Phase 2.2 permits only the controlled release operations in [Release process](RELEASE_PROCESS.md) after all gates; never publish unsigned/debug APKs or private signing material. No released package or issue-tracker URL is assumed.
