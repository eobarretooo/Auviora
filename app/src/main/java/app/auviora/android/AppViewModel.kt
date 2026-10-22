package app.auviora.android

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.core.content.edit
import app.auviora.android.data.*
import app.auviora.android.design.ThemeMode
import app.auviora.core.*
import java.util.UUID
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

enum class Destination { CHAT, MODELS, AGENTS, SETTINGS }
data class ChatState(val id: String = UUID.randomUUID().toString(), val messages: List<Message> = emptyList(), val readOnly: Boolean = false)
class AppViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as AuvioraApplication
    private val preferences = app.getSharedPreferences("settings", 0)
    val engine = app.engine.state
    val provision = app.models.state
    val conversations = app.conversations.conversations
    val destination = MutableStateFlow(Destination.CHAT)
    val chat = MutableStateFlow(ChatState())
    val error = MutableStateFlow<EngineError?>(null)
    val working = MutableStateFlow(false)
    val theme = MutableStateFlow(runCatching { ThemeMode.valueOf(preferences.getString("theme", "SYSTEM")!!) }.getOrDefault(ThemeMode.SYSTEM))
    val dynamic = MutableStateFlow(preferences.getBoolean("dynamic", false))
    private var generation: Job? = null
    private var provisioning: Job? = null
    init {
        viewModelScope.launch {
            try { app.models.refresh(); app.conversations.read() }
            catch (_: Exception) { error.value = EngineError.STORAGE_FAILURE }
        }
    }
    fun setTheme(mode: ThemeMode) { theme.value = mode; preferences.edit { putString("theme", mode.name) } }
    fun setDynamic(value: Boolean) { dynamic.value = value; preferences.edit { putBoolean("dynamic", value) } }
    private fun operation(action: suspend () -> Unit) = viewModelScope.launch {
        if (working.value) return@launch
        working.value = true
        error.value = null
        try { action() }
        catch (cancelled: CancellationException) { throw cancelled }
        catch (failure: Exception) { error.value = (failure as? ArtifactException)?.reason ?: engine.value.error ?: EngineError.STORAGE_FAILURE }
        finally { working.value = false }
    }
    fun load() = operation {
        app.engine.load(app.models.model, app.models.directory)
        chat.value = ChatState()
    }
    fun unload() = operation { app.engine.cancel(); generation?.join(); app.engine.unload() }
    fun download() { provisioning = operation { app.models.download() } }
    fun cancelDownload() { provisioning?.cancel() }
    fun import(uri: Uri, index: Int) { provisioning = operation { app.models.import(uri, index) } }
    fun newChat() = operation {
        app.engine.cancel()
        generation?.join()
        app.engine.reset()
        chat.value = ChatState()
        destination.value = Destination.CHAT
    }
    fun openConversation(item: Conversation) {
        if (working.value || engine.value.phase == EnginePhase.GENERATING) return
        chat.value = ChatState(item.id, item.messages, readOnly = true)
        destination.value = Destination.CHAT
    }
    fun send(text: String) {
        if (text.isBlank() || text.length > 8000 || chat.value.readOnly || engine.value.phase != EnginePhase.READY || generation?.isActive == true || working.value) return
        val initial = chat.value
        val messages = initial.messages + Message("user", text.trim()) + Message("assistant", "")
        chat.value = initial.copy(messages = messages)
        generation = viewModelScope.launch {
            error.value = null
            try {
                persist()
                app.engine.generate(GenerationRequest(text.trim())).collect { piece ->
                    val current = chat.value
                    chat.value = current.copy(messages = current.messages.dropLast(1) + current.messages.last().copy(text = current.messages.last().text + piece))
                }
            } catch (cancelled: CancellationException) { throw cancelled }
            catch (_: Exception) { error.value = engine.value.error ?: EngineError.NATIVE_FAILURE }
            finally {
                withContext(NonCancellable) {
                    try { persist() } catch (_: Exception) { error.value = EngineError.STORAGE_FAILURE }
                }
            }
        }
    }
    private suspend fun persist() {
        val current = chat.value
        val title = current.messages.firstOrNull { it.role == "user" }?.text?.take(60) ?: return
        app.conversations.save(Conversation(current.id, title, current.messages))
    }
    fun stop() { app.engine.cancel() }
    override fun onCleared() { app.engine.cancel(); super.onCleared() }
}
