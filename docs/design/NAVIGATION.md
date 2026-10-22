# Navigation

Four enum destinations: Chat (home), Models, Agents, Settings. No bottom navigation component.
Phone: ModalNavigationDrawer closed on initial launch, upper-left hamburger, standard dismiss/back/swipe, selection closes drawer. Back from a secondary destination returns to Chat; back on Chat follows Android activity behavior.
Wide screens (>=840dp): 288dp persistent sidebar, collapsible via hamburger. Chat uses remaining space and bounded message/composer widths.
Drawer includes New chat, real persisted recent conversations, title search when history exists, current model and exact engine status. Opening saved history is read-only; it does not pretend to restore native KV state. New chat cancels/settles any generation then resets the native context.
