package app.auviora.android

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.auviora.android.data.*
import app.auviora.android.design.*
import app.auviora.core.*
import kotlinx.coroutines.launch

private fun Destination.label() = when (this) {
    Destination.CHAT -> R.string.chat; Destination.MODELS -> R.string.models
    Destination.AGENTS -> R.string.agents; Destination.SETTINGS -> R.string.settings
}
private fun EnginePhase.label() = when (this) {
    EnginePhase.UNLOADED -> R.string.status_unloaded; EnginePhase.VERIFYING -> R.string.status_verifying
    EnginePhase.LOADING -> R.string.status_loading; EnginePhase.READY -> R.string.status_ready
    EnginePhase.GENERATING -> R.string.status_generating; EnginePhase.UNLOADING -> R.string.status_unloading
    EnginePhase.ERROR -> R.string.status_error
}
private fun EngineError.label() = when (this) {
    EngineError.MISSING_ARTIFACT -> R.string.error_missing; EngineError.INVALID_ARTIFACT -> R.string.error_invalid
    EngineError.NATIVE_FAILURE -> R.string.error_native; EngineError.STORAGE_FAILURE -> R.string.error_storage
}
@Composable private fun BrandMark(size: Int = 48) {
    Image(painterResource(R.drawable.ic_auviora), contentDescription = null, modifier = Modifier.size(size.dp))
}
@Composable private fun StatusBadge(phase: EnginePhase) {
    val active = phase == EnginePhase.READY || phase == EnginePhase.GENERATING
    Surface(color = if (active) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh,
        shape = RoundedCornerShape(12.dp)) {
        Row(Modifier.padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Space.small)) {
            Box(Modifier.size(6.dp).background(if (active) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outline, RoundedCornerShape(3.dp)))
            Text(stringResource(phase.label()), style = MaterialTheme.typography.labelMedium,
                color = if (active) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.testTag("engine-status"))
        }
    }
}
@Composable fun AuvioraApp(vm: AppViewModel) {
    val theme by vm.theme.collectAsStateWithLifecycle()
    val dynamic by vm.dynamic.collectAsStateWithLifecycle()
    val destination by vm.destination.collectAsStateWithLifecycle()
    val engine by vm.engine.collectAsStateWithLifecycle()
    val provision by vm.provision.collectAsStateWithLifecycle()
    val working by vm.working.collectAsStateWithLifecycle()
    val chat by vm.chat.collectAsStateWithLifecycle()
    val conversations by vm.conversations.collectAsStateWithLifecycle()
    val error by vm.error.collectAsStateWithLifecycle()
    AuvioraTheme(theme, dynamic) {
        Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            BoxWithConstraints {
                val wide = maxWidth >= 840.dp
                var sidebar by rememberSaveable { mutableStateOf(true) }
                val drawer = rememberDrawerState(DrawerValue.Closed)
                val scope = rememberCoroutineScope()
                val select: (Destination) -> Unit = {
                    vm.destination.value = it
                    scope.launch { drawer.close() }
                }
                val close: () -> Unit = { scope.launch { drawer.close() } }
                BackHandler(enabled = drawer.isOpen || destination != Destination.CHAT) {
                    if (drawer.isOpen) close() else select(Destination.CHAT)
                }
                val nav: @Composable () -> Unit = {
                    DrawerContent(destination, conversations, engine.phase,
                        onSelect = select,
                        onNew = { vm.newChat(); close() },
                        onConversation = { vm.openConversation(it); close() })
                }
                val content: @Composable () -> Unit = {
                    Column(Modifier.fillMaxSize().statusBarsPadding()) {
                        AppHeader(destination, onMenu = { if (wide) sidebar = !sidebar else scope.launch { drawer.open() } }, onModel = { select(Destination.MODELS) })
                        error?.let {
                            Surface(Modifier.fillMaxWidth().padding(horizontal = Space.page), color = MaterialTheme.colorScheme.errorContainer, shape = MaterialTheme.shapes.medium) {
                                Column(Modifier.padding(Space.medium)) {
                                    Text(stringResource(it.label()), color = MaterialTheme.colorScheme.onErrorContainer)
                                    TextButton(onClick = { vm.error.value = null }) { Text(stringResource(R.string.dismiss)) }
                                }
                            }
                        }
                        when (destination) {
                            Destination.CHAT -> ChatScreen(chat, engine.phase, working, vm::send, vm::stop) { select(Destination.MODELS) }
                            Destination.MODELS -> ModelsScreen(engine.phase, provision, working, vm)
                            Destination.AGENTS -> AgentsScreen()
                            Destination.SETTINGS -> SettingsScreen(theme, dynamic, vm)
                        }
                    }
                }
                if (wide) Row(Modifier.fillMaxSize()) {
                    if (sidebar) Surface(Modifier.width(288.dp).fillMaxHeight(), color = MaterialTheme.colorScheme.surfaceContainerLow) { nav() }
                    Box(Modifier.weight(1f)) { content() }
                } else ModalNavigationDrawer(
                    drawerState = drawer,
                    drawerContent = { ModalDrawerSheet(Modifier.widthIn(max = 320.dp), drawerContainerColor = MaterialTheme.colorScheme.surfaceContainerLow) { nav() } }
                ) { content() }
            }
        }
    }
}
@Composable private fun DrawerContent(selected: Destination, conversations: List<Conversation>, phase: EnginePhase,
    onSelect: (Destination) -> Unit, onNew: () -> Unit, onConversation: (Conversation) -> Unit) {
    var query by rememberSaveable { mutableStateOf("") }
    Column(Modifier.fillMaxHeight().systemBarsPadding().padding(horizontal = Space.medium)) {
        Row(Modifier.padding(vertical = Space.large), verticalAlignment = Alignment.CenterVertically) {
            BrandMark(); Spacer(Modifier.width(8.dp))
            Text(stringResource(R.string.app_name), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
        }
        FilledTonalButton(onClick = onNew, enabled = phase !in listOf(EnginePhase.LOADING, EnginePhase.VERIFYING, EnginePhase.UNLOADING), modifier = Modifier.fillMaxWidth().heightIn(min = Space.touch).testTag("new-chat")) {
            Icon(Icons.Default.Add, null); Spacer(Modifier.width(8.dp)); Text(stringResource(R.string.new_chat))
        }
        Spacer(Modifier.height(Space.large))
        Destination.entries.forEach { item ->
            NavigationDrawerItem(label = { Text(stringResource(item.label())) }, selected = item == selected,
                onClick = { onSelect(item) }, modifier = Modifier.testTag("nav-${item.name}"),
                icon = { Icon(when (item) { Destination.CHAT -> Icons.Default.Home; Destination.MODELS -> Icons.AutoMirrored.Filled.List; Destination.AGENTS -> Icons.Default.Star; Destination.SETTINGS -> Icons.Default.Settings }, null) })
        }
        HorizontalDivider(Modifier.padding(vertical = Space.large))
        Text(stringResource(R.string.recent), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        if (conversations.isNotEmpty()) {
            OutlinedTextField(query, { query = it }, label = { Text(stringResource(R.string.search)) }, singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp), leadingIcon = { Icon(Icons.Default.Search, null) })
        }
        val filtered = conversations.filter { it.title.contains(query, ignoreCase = true) }
        LazyColumn(Modifier.weight(1f).padding(top = 12.dp)) {
            if (filtered.isEmpty()) item { Text(stringResource(if (conversations.isEmpty()) R.string.no_history else R.string.no_results), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
            items(filtered, key = { it.id }) { item ->
                TextButton(onClick = { onConversation(item) }, modifier = Modifier.fillMaxWidth()) {
                    Text(item.title, maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = Modifier.fillMaxWidth())
                }
            }
        }
        Column(Modifier.padding(vertical = Space.medium)) {
            Text(stringResource(R.string.model_name), style = MaterialTheme.typography.labelMedium)
            Spacer(Modifier.height(8.dp)); StatusBadge(phase)
        }
    }
}
@Composable private fun AppHeader(destination: Destination, onMenu: () -> Unit, onModel: () -> Unit) {
    Column(Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
        Row(Modifier.fillMaxWidth().heightIn(min = 64.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onMenu, modifier = Modifier.testTag("menu")) { Icon(Icons.Default.Menu, stringResource(R.string.menu)) }
            Text(stringResource(if (destination == Destination.CHAT) R.string.app_name else destination.label()), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
            if (destination == Destination.CHAT) TextButton(onClick = onModel, modifier = Modifier.testTag("model-selector")) {
                Text("MiniCPM-V", style = MaterialTheme.typography.labelLarge)
                Icon(Icons.Default.ArrowDropDown, stringResource(R.string.model_selector))
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = .5f))
    }
}
@Composable private fun ColumnScope.ChatScreen(chat: ChatState, phase: EnginePhase, working: Boolean,
    send: (String) -> Unit, stop: () -> Unit, models: () -> Unit) {
    var draft by rememberSaveable(chat.id) { mutableStateOf("") }
    val generating = phase == EnginePhase.GENERATING
    val list = rememberLazyListState()
    LaunchedEffect(chat.messages.lastOrNull()?.text, chat.id) { if (chat.messages.isNotEmpty()) list.scrollToItem(chat.messages.lastIndex) }
    Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
        if (chat.messages.isEmpty()) Column(Modifier.widthIn(max = 560.dp).padding(Space.large), horizontalAlignment = Alignment.CenterHorizontally) {
            BrandMark(80)
            Spacer(Modifier.height(24.dp))
            Text(stringResource(R.string.welcome), style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(12.dp))
            Text(stringResource(R.string.welcome_support), style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(32.dp)); StatusBadge(phase)
            if (phase == EnginePhase.UNLOADED || phase == EnginePhase.ERROR) TextButton(onClick = models) { Text(stringResource(R.string.chat_hint)) }
        } else LazyColumn(Modifier.fillMaxSize().testTag("messages"), state = list, contentPadding = PaddingValues(Space.page), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            items(chat.messages.size) { index ->
                val message = chat.messages[index]
                val user = message.role == "user"
                Row(Modifier.fillMaxWidth(), horizontalArrangement = if (user) Arrangement.End else Arrangement.Start) {
                    Surface(Modifier.widthIn(max = 680.dp).fillMaxWidth(if (user) .88f else 1f),
                        shape = MaterialTheme.shapes.large, color = if (user) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerLow) {
                        Column(Modifier.padding(Space.medium)) {
                            Text(stringResource(if (user) R.string.you else R.string.assistant), style = MaterialTheme.typography.labelMedium,
                                color = if (user) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.secondary)
                            Spacer(Modifier.height(8.dp))
                            SelectionContainer { Text(message.text, style = MaterialTheme.typography.bodyLarge,
                                color = if (user) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.testTag(if (user) "user-$index" else "assistant-$index")) }
                            if (!user && index == chat.messages.lastIndex && generating) LinearProgressIndicator(Modifier.padding(top = 12.dp).width(64.dp))
                        }
                    }
                }
            }
        }
    }
    Column(Modifier.fillMaxWidth().navigationBarsPadding().imePadding().padding(horizontal = Space.page, vertical = 12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        if (chat.readOnly) Text(stringResource(R.string.history_readonly), style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(bottom = 12.dp))
        if (phase in listOf(EnginePhase.VERIFYING, EnginePhase.LOADING, EnginePhase.UNLOADING, EnginePhase.GENERATING)) {
            Text(stringResource(phase.label()), style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(bottom = 8.dp))
        }
        Surface(Modifier.widthIn(max = 800.dp).fillMaxWidth(), shape = MaterialTheme.shapes.large, color = MaterialTheme.colorScheme.surfaceContainerHigh) {
            Row(Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                TextField(draft, { if (it.length <= 8000) draft = it }, placeholder = { Text(stringResource(R.string.input_hint)) },
                    enabled = !generating && !chat.readOnly, maxLines = 5, modifier = Modifier.weight(1f).testTag("chat-input"),
                    colors = TextFieldDefaults.colors(focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent, unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent))
                FilledIconButton(onClick = { if (generating) stop() else { send(draft); draft = "" } },
                    enabled = generating || (phase == EnginePhase.READY && draft.isNotBlank() && !chat.readOnly && !working),
                    modifier = Modifier.size(48.dp).testTag(if (generating) "stop" else "send")) {
                    Icon(if (generating) Icons.Default.Close else Icons.AutoMirrored.Filled.Send, stringResource(if (generating) R.string.stop else R.string.send))
                }
            }
        }
        Text(stringResource(R.string.privacy_badge), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 12.dp))
    }
}
@Composable private fun SectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Surface(Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.surfaceContainerLow, shape = MaterialTheme.shapes.large) {
        Column(Modifier.padding(Space.large), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold); content()
        }
    }
}
@Composable private fun Page(content: @Composable ColumnScope.() -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Column(Modifier.widthIn(max = 840.dp).fillMaxSize().verticalScroll(rememberScrollState()).navigationBarsPadding().padding(Space.page),
            verticalArrangement = Arrangement.spacedBy(16.dp), content = content)
    }
}
@Composable private fun ModelsScreen(phase: EnginePhase, provision: ProvisionState, working: Boolean, vm: AppViewModel) {
    val importModel = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { it?.let { uri -> vm.import(uri, 0) } }
    val importProjector = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { it?.let { uri -> vm.import(uri, 1) } }
    val loaded = phase == EnginePhase.READY || phase == EnginePhase.GENERATING
    Page {
        SectionCard(stringResource(R.string.model_name)) {
            Text(stringResource(R.string.model_quant), color = MaterialTheme.colorScheme.onSurfaceVariant)
            StatusBadge(phase)
            Text(stringResource(if (provision.filesPresent) R.string.verify_files else R.string.files_missing), style = MaterialTheme.typography.bodyMedium)
            if (provision.busy) {
                LinearProgressIndicator(Modifier.fillMaxWidth())
                Text(if (provision.progress != null) stringResource(R.string.download_progress, provision.progress) else stringResource(R.string.provisioning))
                TextButton(onClick = vm::cancelDownload) { Text(stringResource(R.string.cancel_download)) }
            } else {
                Button(onClick = { if (loaded) vm.unload() else vm.load() }, enabled = !working && phase != EnginePhase.GENERATING,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).testTag(if (loaded) "unload-model" else "load-model")) {
                    Text(stringResource(if (loaded) R.string.unload else R.string.load))
                }
                OutlinedButton(onClick = vm::download, enabled = !working && !loaded, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).testTag("download-model")) { Text(stringResource(R.string.download)) }
            }
        }
        SectionCard(stringResource(R.string.model_verified)) {
            Text(stringResource(R.string.download_size))
            Text(stringResource(R.string.download_notice), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            HorizontalDivider()
            Text(stringResource(R.string.import_notice), style = MaterialTheme.typography.bodySmall)
            OutlinedButton(onClick = { importModel.launch(arrayOf("*/*")) }, enabled = !working && !loaded, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.import_model)) }
            OutlinedButton(onClick = { importProjector.launch(arrayOf("*/*")) }, enabled = !working && !loaded, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.import_projector)) }
        }
        SectionCard(stringResource(R.string.model_settings)) {
            Text(stringResource(R.string.model_config)); Text(stringResource(R.string.engine_note), style = MaterialTheme.typography.bodySmall)
            Text(stringResource(R.string.model_warning), color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
        }
    }
}
@Composable private fun AgentsScreen() = Page {
    SectionCard(stringResource(R.string.agents_title)) {
        Icon(Icons.Default.Star, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(40.dp))
        Text(stringResource(R.string.agents_body))
        OutlinedButton(onClick = {}, enabled = false) { Text(stringResource(R.string.not_available)) }
    }
}
@Composable private fun SettingsScreen(theme: ThemeMode, dynamic: Boolean, vm: AppViewModel) = Page {
    SectionCard(stringResource(R.string.theme)) {
        ThemeMode.entries.forEach { mode ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = mode == theme, onClick = { vm.setTheme(mode) }, modifier = Modifier.testTag("theme-${mode.name}"))
                TextButton(onClick = { vm.setTheme(mode) }) { Text(stringResource(when (mode) { ThemeMode.SYSTEM -> R.string.theme_system; ThemeMode.DARK -> R.string.theme_dark; ThemeMode.LIGHT -> R.string.theme_light })) }
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(R.string.dynamic_color), modifier = Modifier.weight(1f))
            Switch(checked = dynamic, onCheckedChange = vm::setDynamic, enabled = Build.VERSION.SDK_INT >= 31, modifier = Modifier.testTag("dynamic-colors"))
        }
    }
    SectionCard(stringResource(R.string.language)) {
        listOf("" to R.string.language_system, "en" to R.string.language_en, "pt-BR" to R.string.language_pt).forEach { (tag, label) ->
            OutlinedButton(onClick = { AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(tag)) }, modifier = Modifier.fillMaxWidth().testTag("language-$tag")) { Text(stringResource(label)) }
        }
    }
    SectionCard(stringResource(R.string.about)) {
        Text(stringResource(R.string.tagline)); Text(stringResource(R.string.version, BuildConfig.VERSION_NAME))
        Text(stringResource(R.string.model_config), style = MaterialTheme.typography.bodySmall)
    }
    SectionCard(stringResource(R.string.privacy)) { Text(stringResource(R.string.privacy_detail), style = MaterialTheme.typography.bodyMedium) }
}
