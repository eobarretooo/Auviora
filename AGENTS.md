# Auviora development instructions

Read docs/AI_HANDOFF.md, docs/ARCHITECTURE.md and docs/design/DESIGN.md before edits.
Canonical code, documentation, tests and Conventional Commits are English. User communication is Brazilian Portuguese.
Phase 2.1 presentation and documentation is authorized; Phase 3 is not. No cloud inference, agent automation, publishing, pushes or production releases.
Keep the reference project intact. Never depend on a sibling checkout at build/runtime.
Pin vendor/llama.cpp-omni to 96bbf61710d44421ea54e63f5194f9b17c20d877. Preserve MIT notices and parent-owned portability patch. Never discard local edits or commit a local-only vendor gitlink.
UI uses core contracts; only inference owns JNI. Serialize native calls. Cancellation must settle before unload. No fake runtime answers, history or loaded states.
Stage intentionally and commit small completed changes with related documentation and relevant checks. Weights, APKs, SDK paths, credentials and raw logs are ignored.
Report device tests separately from host tests and builds. A successful build is not proof of inference.
