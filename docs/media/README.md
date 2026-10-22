# Application media and reproducible capture

Phase 2.1, captured 2026-10-22. These are actual Android APK pixels, not mockups. The SVG in `brand/` is a documentation adaptation of the application's original A/spark vector, with a rounded container; it is a brand asset, not runtime evidence.

## Gallery and provenance

| File | Actual state |
|---|---|
| [welcome-dark.png](screenshots/welcome-dark.png) | Empty Chat, model unloaded, fixed dark palette |
| [welcome-light.png](screenshots/welcome-light.png) | Empty Chat, model unloaded, fixed light palette |
| [navigation-dark.png](screenshots/navigation-dark.png) | Open drawer, actual controlled test history; Agents is a disabled destination |
| [models-ready-dark.png](screenshots/models-ready-dark.png) | Both model artifacts loaded, Ready, unload available |
| [offline-chat-dark.png](screenshots/offline-chat-dark.png) | Literal answer from the recorded offline generation |
| [settings-dark.png](screenshots/settings-dark.png) | Dark appearance, dynamic colors off, language controls |
| [settings-light.png](screenshots/settings-light.png) | Light appearance, dynamic colors off, language controls |
| [offline-inference.gif](gifs/offline-inference.gif) | Prepared draft → actual streaming → completed answer |

The device is ARM64 Android 16/API 36. No serial, account or private conversation is included. Application ID `app.auviora.android`, version `0.1.0-alpha.1` (1). The installed base APK SHA-256 was checked against the host APK: `01a6ef514073b1eb3bb44e4168de7ea93b057e6a79f0467ecf4fdbc23809ae7e`. No app/test/build configuration changed for capture. The current profile is the exact [verified artifact pair](../testing/model-artifacts.json), already provisioned in app-private storage.

All PNGs use ADB `exec-out screencap -p`; the MP4 uses Android `screenrecord`. Source resolution was 1280×2772. Window insets identified the status bar at y=0–152 and navigation bar at y=2720–2772. The explicit crop `1216:2540:32:160` also excludes the narrow system edge handle, leaving application content. Screenshots are scaled to 608×1270 with Lanczos and losslessly PNG-compressed; GIF frames are 480×1003, 12 fps with an optimized palette. Settings is scrollable and these captures show its initial viewport, not the complete page.

The original [Phase 2.0 evidence](../testing/PHASE_02_VALIDATION.md), including all four `docs/testing/phase2-*.png` files, is unchanged. Raw recordings, network snapshots, APKs and temporary previews remain ignored under `artifacts/phase-02-1/`. They are local working material, not required by a clean clone; do not publish their system bars or raw device logs.

## Offline demonstration

Before loading/generation: airplane mode was enabled, Wi-Fi and mobile data disabled, and `dumpsys connectivity` reported `Active default network: none`. The same absence was checked after generation. USB ADB remained connected. This demonstrates local inference under no active default network; it is not a packet/socket audit or a broad privacy certification.

The selected model and loaded Ready state were inspected before recording. The accepted second take starts on Chat with a prepared draft and the model selected in the header. Send is tapped about 1.3 seconds into the recording. The visible response grows, Stop appears during generation, and Ready returns when complete. The GIF uses the first 8.5 seconds of this single take, with no speed change, compositing, response replacement or fabricated status. The later navigation to Models is outside the chosen interval. The loop returns to the original prepared draft.

Prompt: `Explain how rain forms in ten short sentences.`

Literal response (including its failure to supply ten items):

```text
1. The sky begins to gather moisture.
2. Water vapor rises and cools.
3. Condensation occurs in the atmosphere.
4. Droplets form and settle.
5. Rain falls from the sky.
6. The cloud dissipates.
7. The weather clears up.
8. The rain stops.
```

This is not an answer-quality endorsement or a performance benchmark. The first take was rejected: navigating away from Chat discarded the prepared draft, so no inference was submitted. Android screenrecord also omits long unchanged tails; its encoded duration may be shorter than the requested wall time. The accepted MP4 has 325 video frames and duration 11.272933 seconds; the GIF has 102 frames, 8.50 seconds and 469,746 bytes, well below 8 MiB. No MP4 is committed because it would duplicate the demonstration.

## Dependencies and commands

Use Python 3.10+, ADB on PATH, and FFmpeg/FFprobe on PATH. Capture used Python 3.12 and FFmpeg 9.0.2; the scripts use only the Python standard library. No system package installation is performed. Replace `DEVICE_SERIAL` with your own device from `adb devices`. Launch the actual APK, authorize USB debugging, select a supported locale/theme, and manually inspect the desired non-sensitive UI state before capture.

```powershell
python scripts/media.py capture --serial DEVICE_SERIAL --output artifacts/welcome.png --crop 1216:2540:32:160
python scripts/media.py record --serial DEVICE_SERIAL --seconds 15 --output artifacts/demo.mp4
python scripts/media.py convert artifacts/demo.mp4 artifacts/demo.gif --crop 1216:2540:32:160 --seconds 8.5
python scripts/media.py check artifacts/demo.gif
python scripts/check-docs.py
```

Run recording in one terminal and operate the app in another or on the phone. The record command waits until Android finishes; it pulls only its own generated temporary MP4 and removes that remote file after successful transfer. Scripts refuse output overwrite. Use new output names; inspect candidates before deliberately replacing curated media. For the committed GIF, the source was `artifacts/phase-02-1/offline-demo-take2.mp4` and the destination `docs/media/gifs/offline-inference.gif`. `--start` can trim a source interval; it never changes playback speed. `--width` defaults to 608 for PNG and 480 for GIF, with `--fps 12` for GIF.

**Do not reuse this device's crop blindly.** Inspect a fresh screenshot and `adb shell dumpsys window` for your device's resolution/insets. Crop only system bars/edges; never hide an application failure, rewrite answers or imply a state the app did not reach. Check the complete candidate animation, including its loop, for keyboard suggestions, notifications and private history before committing. The checker validates existence, dimensions, size (<8 MiB), multiple GIF frames and 8–15.2-second duration, not truthfulness or privacy.

For repeatable offline evidence, save the current `airplane_mode_on`, `wifi_on` and `mobile_data` values with `adb shell settings get global NAME`. Enable airplane mode with `adb shell cmd connectivity airplane-mode enable`, then `adb shell svc wifi disable` and `adb shell svc data disable`. Wait for the no-active-default-network condition before generating and check again afterward. Restore the saved radio values in a `finally` block or immediately after the session. Do not infer offline conditions from a cropped screenshot alone. The existing opt-in [device runner](../../scripts/run-device-validation.ps1) automates this reversible network procedure for instrumentation validation; it is separate from manual media capture.

The capture session restored the original radio values (airplane 0, Wi-Fi 1, mobile data 0), app locale list (empty/system), fixed dark theme and unloaded model. It added only the controlled demonstration conversation; existing histories and weights were not cleared.

## Review and integrity

[manifest.json](manifest.json) records dimensions, sizes and SHA-256 of curated files. Reviewers inspected each PNG and GIF contact-sheet frames; full GIF decoding and browser playback are also recorded in the [Phase 2.1 report](../PHASES/PHASE_02_1.md). A local GitHub-compatible preview is an approximation until an authorized GitHub publication; no remote was invented or pushed.
