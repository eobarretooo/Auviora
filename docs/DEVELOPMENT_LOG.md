# Development log

## 2026-10-22 — Phase 2.0 start

Read reference instructions, architecture/plan/structure/decisions/handoff/testing/history and Phase 1.5/1.6 evidence. Reference baseline d416a13; engine 96bbf617 with documented patch remains modified. No reference edits authorized or made.
Created independent repository and documented identity before Android configuration. Toolchain retained; only new Compose/presentation dependencies are introduced. Initial ADB check found no devices; user offered to reconnect.

## Native integration checkpoint

Initial commit d4297ef records repository/identity. Independent public submodule fetched at exact 96bbf617 after a shallow branch checkout failed; the default branch was different, exact SHA fetch succeeded. No reference mutation. Native module build and 5 JVM tests passed in 3m51s; patch fixture passed clean/idempotent/reverse/conflict checks. First app build passed in 1m19s, lint0 errors/23 warnings. Lint exposed bridge ELF alignment; parent CMake now sets 16KB alignment. Desktop tool libraries are excluded without vendor edits. Direct mtmd inclusion initially lacked parent-scope LLAMA_INSTALL_VERSION; diagnosed and fixed before retry. No dependency upgrade was used as a workaround.

Native foundation checkpoint: full build including Compose/test APK passed in 2m28s after scope/alignment correction. JNI packaging review detected imported OpenMP missing when restricting native targets; explicitly package pinned NDK libomp with both CPU variants. Application/UI source is the next logical commit. User device rejected the first install with INSTALL_FAILED_USER_RESTRICTED; awaiting device installation setting/confirmation, not bypassing it.

Commit 918ef6b integrates core/native sources, licenses and patch verification. The APK structural check found 4KB ELF segments in transitive ggml libraries despite lint only naming the bridge; 16KB linker alignment is now inherited by all native targets. Pinned NDK OpenMP already uses 16KB alignment. These checks prevent assuming lint alone covers every packaged dependency.

ARM64 packaging verification now passes: 10 native libraries, all ELF LOAD segments >=16KB, pinned OpenMP included, no unrelated desktop/omni libraries. Build/test APK and lint passed. Auviora/test APK installation succeeded after user enabled USB installation. Foundation navigation/theme/locale tests2/2 and isolated actual-message persistence/corruption tests2/2 passed on hardware. Provisioning exact artifacts through the application is now running.

## UI/state verification corrections

Commits 7e0cf02 (all-native ELF alignment) and 7e89e91 (independent localized Compose foundation) are complete. Device UI/storage tests4/4 passed; in-app exact artifact download passed1/1 in37s. Device SHA-256 matched both pinned artifacts. First offline generation/cancel/reload test passed1/1 with actual responses. An independent clean checkout built successfully in2m30s and applied the patch automatically.

Visual review kept current model status visible above existing messages and centered the empty-state copy. New session identity now resets before loading, preserving a draft typed during load instead of clearing it when READY arrives. Model provisioning copy describes integrity checks without claiming success from presence or referring to internal project phases.

A synchronized streaming observer sometimes saw only one update. Direct UI semantics observation initially saw no rendered messages because the Compose test frame clock was not advanced during real native IO; it also needed to await load-operation completion and respect enabled controls. Corrected test-only observation advances frames and reads actual placed text on main. Offline streaming then passed with24 distinct visible updates. No AccessibilityService/agent feature was added. Intermediate failed observers and asynchronous network-teardown detection are retained in ignored logs, not classified as engine crashes. Runner now waits for no active network and restores saved settings in finally.
