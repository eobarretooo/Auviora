package app.auviora.android

import android.content.ContextWrapper
import androidx.test.platform.app.InstrumentationRegistry
import app.auviora.android.data.*
import java.io.File
import java.util.UUID
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class ConversationRepositoryTest {
    private fun isolated(test: suspend (ConversationRepository, File, android.content.Context) -> Unit) = runBlocking {
        val base = InstrumentationRegistry.getInstrumentation().targetContext
        val directory = File(base.cacheDir, "history-test-${UUID.randomUUID()}").apply { mkdirs() }
        val context = object : ContextWrapper(base) { override fun getFilesDir() = directory }
        try { test(ConversationRepository(context), directory, context) }
        finally { directory.deleteRecursively() }
    }
    @Test fun roundTripsActualUnicodeMessagesAndReplacesConversation() = isolated { repository, _, context ->
        val first = Conversation("one", "A real title", listOf(Message("user", "Olá 👋"), Message("assistant", "105")))
        repository.save(first)
        repository.save(first.copy(title = "Updated"))
        val reloaded = ConversationRepository(context)
        reloaded.read()
        assertEquals(listOf(first.copy(title = "Updated")), reloaded.conversations.value)
    }
    @Test fun malformedHistoryCannotBeSilentlyOverwritten() = isolated { repository, directory, _ ->
        val file = File(directory, "conversations.json").apply { writeText("[{\"id\":\"old\"}]") }
        val original = file.readBytes()
        assertTrue(runCatching { repository.read() }.isFailure)
        assertTrue(runCatching { repository.save(Conversation("new", "title", emptyList())) }.isFailure)
        assertArrayEquals(original, file.readBytes())
    }
}
