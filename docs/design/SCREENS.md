# Screens

- Chat: original branding, model destination shortcut, exact engine status, empty welcome, user/assistant messages, selection/copy, IME-aware text composer, streaming and Stop. Send requires ready state; no canned response. No image-attachment controls yet.
- Models: exact MiniCPM profile, files-present versus verified/load states, full SHA-256 on provisioning/load, HTTPS download with progress/cancel and SAF imports for each artifact. No advanced queue/resume/background service; keep app open. Quality warning remains explicit.
- Agents: disabled, honest explanation. No tool registry/executor, sensitive permission or automation service.
- Settings: persisted appearance/dynamic colors, per-app locale selector, actual BuildConfig version, privacy information and current fixed native configuration.

No fake history. Basic local history stores actual messages in AtomicFile-backed JSON; advanced search, editing, export/deletion and native-context restoration are future work. UI strings use values/ and values-pt-rBR/.
