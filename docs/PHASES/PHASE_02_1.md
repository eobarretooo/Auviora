# Phase 2.1 — GitHub presentation and authentic media

Date: 2026-10-22. **Presentation scope complete. Phase 3 not started.** App version remains `0.1.0-alpha.1` (1). No app, test, Gradle, JNI, CMake, engine or model-profile code changed. No remote, push, release or tag was created.

## Delivered

- Root README: centered original brand, exact tagline, four accurate technology badges, prominent pt-BR guide, current features/limitations, real media, compatibility, build/install/model instructions, module architecture and documentation/contribution/legal links.
- Seven actual PNG screenshots and one real offline inference GIF under [docs/media](../media/README.md), with [SHA-256 manifest](../media/manifest.json). No raw MP4, APK or weights are tracked.
- Standard-library Python [media capture/record/convert/check script](../../scripts/media.py) and [local documentation checker](../../scripts/check-docs.py). Dependencies: Python 3.10+, ADB, FFmpeg/FFprobe; none installed system-wide for this phase.
- Canonical design contract moved intact from root `DESIGN.md` to [docs/design/DESIGN.md](../design/DESIGN.md), with AGENTS, README, structure, handoff and design links updated.
- [Android quality backlog](../design/ANDROID_QUALITY_BACKLOG.md), contribution guide, expanded localized guide, roadmap/changelog/testing/handoff/log updates.

## Real-device evidence

Installed and host APK hashes match the Phase 2.0 final APK: `01a6ef514073b1eb3bb44e4168de7ea93b057e6a79f0467ecf4fdbc23809ae7e`. Hardware: ARM64 Android 16/API 36. No installation or rebuild was needed. Actual navigation, theme switching, verified model load/unload and normal Chat streaming were exercised manually through ADB/UI; no mock backend or instrumentation response was substituted.

Airplane mode enabled, Wi-Fi/mobile data disabled, no active default network before/after generation. USB remained attached. Model output was preserved verbatim: the ten-sentence request yielded eight numbered items. The first take was rejected after navigation discarded its draft; it is not counted as successful inference. A missed initial automation tap/load-state wait was diagnosed from unchanged UI/no native load, then an actual tap reached Ready; no dependency or app change was made.

The accepted recording uses Android screenrecord; source 1280×2772, 325 video frames, 11.272933 seconds. GIF: first 8.5 seconds at original timing, cropped to remove system bars/edge handle, 480×1003, 12 fps, 102 frames, **469,746 bytes**. No response edits, speed manipulation or synthetic UI. PNGs: 608×1270, 78,897–147,387 bytes each. All curated media including the brand SVG: **1,239,778 bytes**. Raw working files stay ignored in `artifacts/phase-02-1/`.

Original settings restored: airplane 0, Wi-Fi 1, mobile data 0; app locale empty/system, fixed dark theme, model unloaded. Existing history/weights were preserved; one controlled conversation was added. No notification text, account, serial or private chat appears in committed pixels/metadata. Original `docs/testing/phase2-*.png` files remain byte-identical to the Phase 2.0 baseline.

## Verification

- All seven PNGs inspected; GIF contact-sheet frames inspected and full animation decoded: 102 frames, 39 distinct full RGB frames, valid timing/loop and size below 8 MiB.
- `python scripts/media.py check` passed on all eight raster assets, with dimensions, frames, duration and sizes reported.
- Capture, recording and conversion commands ran on actual device media. Invalid-crop input was rejected before ADB. Link checker fixtures accepted a valid link and rejected missing target, missing anchor and unclosed fence.
- `python scripts/check-docs.py` passed for 23 repository root/docs Markdown files, 111 local links/media/anchors and closed fences. Marked's GFM parser successfully rendered the README. All canonical Markdown was also parsed as GFM.
- Local GitHub-style preview inspected at desktop 1280×900 and mobile 390×844. All 12 README images loaded (logo, four badges, GIF, six gallery shots). Document scrollWidth equaled clientWidth in both cases (1265 desktop, 375 mobile); gallery wraps to single-column 250px images on mobile. This approximates GitHub styling, not a live github.com publication or every device/zoom setting.
- Native patch reverse check passed; vendor has the expected single patched file and original public gitlink. Application source/build/license files are unchanged. Original testing evidence is unchanged. No APK, weight, secret, raw recording or local configuration was staged.
- No application build/JVM/lint/instrumentation suite rerun: only documentation, scripts and media changed. Prior Phase 2.0 results remain separately documented in [validation](../testing/PHASE_02_VALIDATION.md); this phase adds a fresh manual offline generation/capture, not a new full regression run.

## Constraints and next action

Only one Android 16 device has runtime evidence. Wider SDK/device/tablet, dynamic-color/font/TalkBack matrices, SAF picker/provider coverage, sustained performance, model quality and distribution-license review remain open. The new backlog includes navigation draft loss, light-theme icon treatment and measurable quality gates; no repair is implied by documenting them. Agents, automation, advanced memory, image attachments and context restoration remain unavailable.

Review the README, media and backlog, then explicitly choose any next development scope. Do not start Phase 3 or publish automatically. The local preview uses a temporary HTTP server and approximate GitHub CSS; a live GitHub rendering can only be verified after an authorized publication.

## Commit checkpoints

Phase 2.0 baseline: `3ed2d8e`. Phase 2.1: `6afbeec` adds reproducible media/documentation scripts and work log; `fbc407e` integrates authentic media, README and canonical documentation. The closure commit records the final checks and these checkpoints. For the self-containing closure commit use `git log -1 --format=%h -- docs/PHASES/PHASE_02_1.md`.
