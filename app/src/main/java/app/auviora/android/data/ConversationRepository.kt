package app.auviora.android.data

import android.content.Context
import android.util.AtomicFile
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

data class Message(val role: String, val text: String)
data class Conversation(val id: String, val title: String, val messages: List<Message>)
class ConversationRepository(context: Context) {
    private val file = AtomicFile(File(context.filesDir, "conversations.json"))
    private val lock = Mutex()
    private var writable = true
    private val mutableConversations = MutableStateFlow<List<Conversation>>(emptyList())
    val conversations = mutableConversations.asStateFlow()
    suspend fun read() = withContext(Dispatchers.IO) {
        lock.withLock {
            if (!file.baseFile.exists()) return@withLock
            // Preserve malformed data for inspection; do not overwrite it on a failed read.
            try {
            val array = JSONArray(file.openRead().bufferedReader().use { it.readText() })
            mutableConversations.value = (0 until array.length()).map { index ->
                val item = array.getJSONObject(index)
                val messages = item.getJSONArray("messages")
                Conversation(item.getString("id"), item.getString("title"), (0 until messages.length()).map {
                    val message = messages.getJSONObject(it)
                    Message(message.getString("role"), message.getString("text"))
                })
            }
            } catch (error: Exception) { writable = false; throw error }
        }
    }
    suspend fun save(conversation: Conversation) = withContext(Dispatchers.IO) {
        lock.withLock {
            check(writable) { "Preserve unreadable conversation data before recovery" }
            val updated = listOf(conversation) + mutableConversations.value.filterNot { it.id == conversation.id }
            val array = JSONArray()
            updated.forEach { item ->
                val messages = JSONArray()
                item.messages.forEach { messages.put(JSONObject().put("role", it.role).put("text", it.text)) }
                array.put(JSONObject().put("id", item.id).put("title", item.title).put("messages", messages))
            }
            val stream = file.startWrite()
            try {
                stream.write(array.toString().toByteArray(Charsets.UTF_8))
                file.finishWrite(stream)
                mutableConversations.value = updated
            } catch (error: Throwable) { file.failWrite(stream); throw error }
        }
    }
}
