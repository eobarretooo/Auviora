# Changelog

All notable changes are documented here using Keep a Changelog conventions and Semantic Versioning 2.0.0.

## [Unreleased]

No unreleased application changes.

## [0.1.0-alpha.1] - 2026-10-22

First public pre-release: [GitHub Release](https://github.com/eobarretooo/Auviora/releases/tag/v0.1.0-alpha.1). Annotated tag identifies tested source 4997566a22e4425f6d8f4d4f1800ff99ab7be96f; later publication documentation does not retag the APK.

### Added
- Controlled Windows release-signing setup, candidate build/verification and explicit GitHub draft/publication scripts.
- Bundled third-party notices, resolved dependency inventory and scoped binary redistribution audit.
- Release source-commit provenance, signature/certificate/metadata/alignment/weight/checksum gates and verifier regression checks.
- Version-specific Alpha notes and release/update/recovery documentation.
- Permanent-key signed ARM64 APK, checksum, notices and verification report distributed through GitHub Releases; public bytes verified after draft validation/publication.
- Phase 2.1 authentic Android screenshot gallery and offline streaming GIF with provenance, hashes and reproducible ADB/FFmpeg media/link scripts.
- Professional English README, expanded pt-BR guide, contribution instructions and evidence-based Android quality backlog.
- Independent Auviora Android identity and 0.1.0-alpha.1 version (code 1).
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
- Expected native-patch conflict handling on Windows PowerShell 5.1 while retaining failure checks and source preservation.

The signed APK was validated on an Android 9 x86_64 emulator through ARM64 translation. Physical Android 16 offline evidence uses the separate debug build; emulator network isolation was not established. See the [phase report](PHASES/PHASE_02_2.md).
