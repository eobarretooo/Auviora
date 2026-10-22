# Architecture

Implemented Phase 2.0 foundation. app → core ← inference. app composes the implementation; feature UI never imports JNI/native classes.

- core: model descriptors, exact artifact profile, full size/SHA-256/GGUF verifier, engine state/error contract and streamed generation requests.
- inference: one process-scoped LlamaInferenceEngine, Mutex and dedicated single executor, NativeBridge, text/vision JNI and pinned backend. Ownership survives Activity recreation through Application; no destroyed singleton.
- app: Application composition, lifecycle ViewModel, local model provisioning/history/preferences, Compose design system and four destinations.

All native calls serialize, including across Flow suspension. cancel only sets an atomic flag; the native sampler is reset on its owner thread after the next token/prefill returns. unload waits for that operation. Partial loads release all acquired native resources. READY is emitted only after GGUF, matching projector, context and sampler succeed. Full hashes run on IO before native initialization. JNI decode/reset failures are propagated. Activity lifecycle does not block main with cleanup.

Only one validated MiniCPM profile exists. Future backends implement LocalInferenceEngine; new architectures need their own native compatibility/hardware validation. Additional native engines in the same process must not instantiate another owner of these global pointers.

The reference TTS/video/demo UI and downloader are not copied. A generic image request is retained in the contract/native path; attachment UI and multimodal migration tests are later work.

Local inference state and UI operation completion are distinct: the ViewModel prevents actions during ongoing provisioning/load work and starts a fresh chat identity before loading. Existing conversations show the active engine phase. AtomicFile history preserves actual messages; unreadable history blocks overwriting until an explicit recovery policy exists. Saved conversations are read-only, without pretending to restore native context.

Model unload frees projector/sampler/context/model allocations; the process-owned backend/executor remains available for reload until process exit. No foreground inference service, scheduler, agent registry or sensitive permission is introduced. Current verified behavior and gaps: [validation report](testing/PHASE_02_VALIDATION.md).
