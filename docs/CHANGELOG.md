# Changelog

All notable changes are documented here using Keep a Changelog conventions and Semantic Versioning 2.0.0.

## [Unreleased]

### Added
- Phase 2.1 authentic Android screenshot gallery and offline streaming GIF with provenance, hashes and reproducible ADB/FFmpeg media/link scripts.
- Professional English README, expanded pt-BR guide, contribution instructions and evidence-based Android quality backlog.
- Independent Auviora Android identity and 0.1.0-alpha.1 development version (code 1); no published release/tag.
- Compose Material3 dark/light/system/dynamic themes and original vector icon.
- Left adaptive drawer, chat, verified model provisioning, disabled Agents and Settings.
- English/pt-BR strings and supported per-app language selection.
- Core inference contracts, pinned native backend/patch and local streamed chat.
- Basic atomic local conversation history with read-only saved views.
- Integrity/catalog, navigation/locale and opt-in hardware tests.

### Changed
- Canonical design contract relocated to docs/design/DESIGN.md with references updated. Documentation/media changes retain app version 0.1.0-alpha.1 (1).

### Security
- SHA-256/size checks before model promotion/loading, HTTPS-only provisioning, no cloud inference fallback.
- Android backup/transfer exclusions; no sensitive runtime permissions or prompt/token bridge logging.

### Fixed
- Complete ARM64/OpenMP packaging and 16KB native alignment without a vendor upgrade.
- Draft preservation during model load and accurate visible engine states.
- Test-frame-clock observation and asynchronous network teardown verification.

There is no released version section yet. Do not create tags or publish packages without explicit authorization.
