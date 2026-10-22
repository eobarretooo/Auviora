# Development log

## 2026-10-22 — Phase 2.0 start

Read reference instructions, architecture/plan/structure/decisions/handoff/testing/history and Phase 1.5/1.6 evidence. Reference baseline d416a13; engine 96bbf617 with documented patch remains modified. No reference edits authorized or made.
Created independent repository and documented identity before Android configuration. Toolchain retained; only new Compose/presentation dependencies are introduced. Initial ADB check found no devices; user offered to reconnect.

## Native integration checkpoint

Initial commit d4297ef records repository/identity. Independent public submodule fetched at exact 96bbf617 after a shallow branch checkout failed; the default branch was different, exact SHA fetch succeeded. No reference mutation. Native module build and 5 JVM tests passed in 3m51s; patch fixture passed clean/idempotent/reverse/conflict checks. First app build passed in 1m19s, lint0 errors/23 warnings. Lint exposed bridge ELF alignment; parent CMake now sets 16KB alignment. Desktop tool libraries are excluded without vendor edits. Direct mtmd inclusion initially lacked parent-scope LLAMA_INSTALL_VERSION; diagnosed and fixed before retry. No dependency upgrade was used as a workaround.

Native foundation checkpoint: full build including Compose/test APK passed in 2m28s after scope/alignment correction. JNI packaging review detected imported OpenMP missing when restricting native targets; explicitly package pinned NDK libomp with both CPU variants. Application/UI source is the next logical commit. User device rejected the first install with INSTALL_FAILED_USER_RESTRICTED; awaiting device installation setting/confirmation, not bypassing it.
