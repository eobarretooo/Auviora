# Android quality backlog

Reviewed 2026-10-22 for Phase 2.1. This is future work, not an implementation or compliance claim. No app code, build configuration, engine pin or model profile changed. Priorities below are engineering recommendations; acceptance requires a separately authorized phase.

## Evidence-backed findings

| Priority | Finding and evidence | Proposed bounded work and acceptance |
|---|---|---|
| P1 | An unsent draft disappears after Chat → Models → Chat. Observed during the rejected first recording; `ChatScreen` owns `rememberSaveable(chat.id)` inside a conditional destination, without a destination state holder. | Decide draft lifetime, retain it in ViewModel or a state holder if desired, then test navigation and process/lifecycle behavior. The existing preservation fix during model loading addresses a different path. |
| P1 | The actual recorded model answered eight items to a ten-sentence request and provided a simplistic explanation. Earlier evidence also records language/identity/factual errors. | Define repeatable factual/instruction/language evaluation prompts before changing sampling or models. Preserve literal outputs; require documented improvement and memory/latency tradeoffs. |
| P1 | TalkBack traversal, large fonts, switch access, contrast under dynamic colors and broader devices remain untested. Static fixed-palette contrast and a few themed screenshots do not establish accessibility. | Audit Chat streaming announcements, model/error status semantics, drawer traversal and input/Stop focus on real hardware, including large text/display sizes. Record results per theme/device. |
| P2 | No release UI-performance benchmark or thermal/long-session baseline exists. Debug capture logged a skipped-frame warning during navigation; that single log is a signal, not a diagnosed performance defect. | Measure startup/navigation/streaming with an optimized release configuration and profiling tools. Establish frame-time, memory and sustained inference baselines before choosing changes. |
| P2 | `:app` currently combines ViewModel orchestration, UI and local repositories; core contracts and a separate native owner already provide useful boundaries. | Review UDF/state ownership, lifecycle collection and repository interfaces against actual growth needs. Avoid a module/domain-layer rewrite without a concrete use case and regression coverage. |
| P2 | History is app-private atomic JSON; saved context, delete/export, process-death recovery and broad SAF provider behavior are unfinished or unverified. | Define retention/control/error policy and verify data handling/permission denial before adding attachments or tools. Retain minimal permissions and explicit user actions. |
| P2 | A fixed navy square remains behind the brand mark in the light welcome screenshot; this is the actual existing drawable. | Review light-theme brand treatment in a future visual quality task. Do not retouch the screenshot or imply that a UI fix was delivered here. |

## Official guidance consulted

- [Compose accessibility](https://developer.android.com/develop/ui/compose/accessibility): semantics, traversal, scalable content and testing are explicit review areas. Apply these to the actual chat and navigation, then test; default Material components alone do not certify the whole experience.
- [Compose performance](https://developer.android.com/develop/ui/compose/performance): evaluate release behavior, R8 and app-specific Baseline Profiles, with tooling and measurement. No optimization/profile or measured speedup was delivered in Phase 2.1.
- [Architecture recommendations](https://developer.android.com/topic/architecture/recommendations): layered separation, unidirectional data flow, state holders and lifecycle-aware state consumption guide a future review. Recommendations have different strengths and should fit the app's concrete needs.
- [Privacy checklist](https://developer.android.com/privacy-and-security/about): minimum permissions, transparent handling and graceful denial/revocation behavior guide future media/tool flows. The brief's `https://developer.android.com/privacy-and-security` returned HTTP 404; this is the current checklist page found and read instead.

Official guidance informs the backlog; it does not prove this APK is optimized, accessible, private under every threat model, or production-ready. Device inference evidence and licensing/publication gaps remain in the [handoff](../AI_HANDOFF.md).
