package app.auviora.core

import java.io.File
import java.security.MessageDigest

class ArtifactException(val reason: EngineError) : Exception(reason.name)
object ArtifactVerifier {
    fun verify(file: File, artifact: ModelArtifact) {
        if (!file.isFile) throw ArtifactException(EngineError.MISSING_ARTIFACT)
        if (file.length() != artifact.bytes) throw ArtifactException(EngineError.INVALID_ARTIFACT)
        val digest = MessageDigest.getInstance("SHA-256")
        file.inputStream().buffered().use { input ->
            val buffer = ByteArray(1024 * 1024)
            while (true) {
                val count = input.read(buffer)
                if (count < 0) break
                digest.update(buffer, 0, count)
            }
        }
        val actual = digest.digest().joinToString("") { "%02x".format(it) }
        if (actual != artifact.sha256) throw ArtifactException(EngineError.INVALID_ARTIFACT)
        file.inputStream().use {
            if (!it.readNBytesCompat().contentEquals(byteArrayOf(71,71,85,70)))
                throw ArtifactException(EngineError.INVALID_ARTIFACT)
        }
    }
    private fun java.io.InputStream.readNBytesCompat(): ByteArray = ByteArray(4).also { read(it) }
}
