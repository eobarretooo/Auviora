package app.auviora.android.data

import android.content.Context
import android.net.Uri
import app.auviora.core.*
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

data class ProvisionState(val busy: Boolean = false, val progress: Int? = null, val filesPresent: Boolean = false)
class ModelRepository(private val context: Context) {
    val model = Models.miniCpm
    val directory = File(context.filesDir, "models/${model.id}")
    private val lock = Mutex()
    private val mutableState = MutableStateFlow(ProvisionState())
    val state = mutableState.asStateFlow()
    suspend fun refresh() = withContext(Dispatchers.IO) {
        mutableState.value = ProvisionState(filesPresent = model.artifacts.all { File(directory, it.name).length() == it.bytes })
    }
    suspend fun download() = withContext(Dispatchers.IO) {
        lock.withLock {
            directory.mkdirs()
            mutableState.value = ProvisionState(busy = true, progress = 0)
            try {
                var completed = 0L
                val total = model.artifacts.sumOf { it.bytes }
                for (artifact in model.artifacts) {
                    val destination = File(directory, artifact.name)
                    val alreadyVerified = try { ArtifactVerifier.verify(destination, artifact); true } catch (_: ArtifactException) { false }
                    if (alreadyVerified) { completed += artifact.bytes; continue }
                    val connection = URL(artifact.url).openConnection() as HttpURLConnection
                    connection.connectTimeout = 30_000
                    connection.readTimeout = 30_000
                    try {
                        check(connection.responseCode in 200..299) { "HTTP download failed" }
                        val staging = File(directory, "${artifact.name}.part")
                        connection.inputStream.use { input -> staging.outputStream().buffered().use { output ->
                            val buffer = ByteArray(256 * 1024)
                            var received = 0L
                            while (true) {
                                ensureActive()
                                val count = input.read(buffer)
                                if (count < 0) break
                                received += count
                                if (received > artifact.bytes) throw ArtifactException(EngineError.INVALID_ARTIFACT)
                                output.write(buffer, 0, count)
                                mutableState.value = ProvisionState(true, ((completed + received) * 100 / total).toInt())
                            }
                        } }
                        promote(staging, artifact)
                        completed += artifact.bytes
                    } finally { connection.disconnect() }
                }
            } finally { withContext(NonCancellable) { refresh() } }
        }
    }
    suspend fun import(uri: Uri, index: Int) = withContext(Dispatchers.IO) {
        lock.withLock {
            val artifact = model.artifacts[index]
            directory.mkdirs()
            mutableState.value = ProvisionState(busy = true)
            try {
                val staging = File(directory, "${artifact.name}.part")
                requireNotNull(context.contentResolver.openInputStream(uri)).use { input ->
                    staging.outputStream().buffered().use { output ->
                        val buffer = ByteArray(256 * 1024)
                        var received = 0L
                        while (true) {
                            ensureActive()
                            val count = input.read(buffer)
                            if (count < 0) break
                            received += count
                            if (received > artifact.bytes) throw ArtifactException(EngineError.INVALID_ARTIFACT)
                            output.write(buffer, 0, count)
                        }
                    }
                }
                promote(staging, artifact)
            } finally { withContext(NonCancellable) { refresh() } }
        }
    }
    private fun promote(staging: File, artifact: ModelArtifact) {
        ArtifactVerifier.verify(staging, artifact)
        // Same private filesystem: verified rename replaces only this exact catalog filename.
        check(staging.renameTo(File(directory, artifact.name))) { "Could not promote verified artifact" }
    }
}
