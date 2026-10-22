# Design

## Source of truth
Active, 2026-10-22. Android Compose foundation. Evidence: Phase 2.0 brief and reference Phase 1.6 engine evidence. No existing Auviora UI/assets/screenshots existed before implementation. This contract governs the new UI.
## Brand
Auviora. Private AI. Real Capabilities. Calm, professional, private, lightweight. Trust comes from accurate states. Original vector A/spark mark. Avoid demo identity, glass effects, fake status and ornamental animations.
## Product goals
Local chat foundation, verified provisioning, errors and left navigation. No agents, advanced memory or background automation. Success includes a real response in the new UI.
## Personas and jobs
Android users wanting private local assistance and developers verifying compatible models. Offline use after explicit provisioning; compact screens and memory limits matter.
## Information architecture
Chat is the initial destination. Left modal drawer on compact screens; collapsible sidebar from 840dp. Chat, Models, Agents, Settings, New chat, real recent conversations and local search. No bottom navigation.
## Design principles
Show actual engine/file states. Keep the main task visible. Separate download networking from offline inference. Preserve accessible controls and restrained hierarchy.
## Visual language
Dark reference: background #0B1020, surface #171E31, primary #8B7CFF, accent #44D7B6, text #F3F5FF. Semantic schemes centralized in app/design/Theme.kt. Light theme uses dark purple on pale neutral surfaces. System theme default, Dark/Light options and optional API31 dynamic colors. Android sans typography, 4/8dp spacing rhythm, 16/24dp radii, minimum 48dp controls. No remote fonts.
## Components
AuvioraTheme owns palette/type/shapes. BrandMark, StatusBadge, SectionCard, messages and composer reuse theme tokens. Loading, ready, error and missing files are distinct.
## Accessibility
WCAG AA text contrast target: 4.5:1 normal and 3:1 large. Semantic descriptions, 48dp targets and text scaling. Status uses text and color. Back closes drawer before returning to Chat. No decorative motion loops.
## Responsive behavior
Below 840dp, closed-by-default modal drawer/hamburger. At 840dp, 288dp sidebar with collapse control and readable bounded chat column. Scrollable model/settings screens and IME-aware composer. Device/tablet validation remains separate from compilation.
## Interaction states
No automatic model load. Missing/present/verifying/loading/ready/generating/unloading/error. Send requires ready and nonempty text. Stop requests cooperative cancellation. Agents explains disabled scope. Saved history is read-only; new chat resets native context.
## Content voice
English default and complete pt-BR resources. Concrete status. File presence never means model loaded. Keep model quality limitations visible.
## Implementation constraints
Kotlin/Compose/Material3. No bottom bar, copied demo UI, heavy animation, fake responses or cloud AI. Device screenshots and navigation/locale tests when hardware is available.
## Open questions
- [ ] Publication/trademark review: product owner before release.
- [ ] Broader tablet/font/thermal/device matrix: engineering in future validation.
