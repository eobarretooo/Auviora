# Design system

The active [design contract](DESIGN.md) governs tokens and states. app/design/Theme.kt owns semantic dark/light schemes, Android dynamic color, typography, shapes and Space tokens. Features use MaterialTheme colors, not separate palettes. Dark reference matches the five requested colors. Contrast target is AA; status includes text and color, controls at least 48dp. Default uses system theme, dynamic off. Persistent settings allow explicit dark/light/system. Font is Android sans.

Reusable UI: BrandMark, StatusBadge, SectionCard, message surfaces and composer. No decorative animations; only standard drawer/progress transitions. Contrast calculations and device screenshots belong in testing evidence; dynamic colors depend on OS/wallpaper and are not a fixed palette claim.
