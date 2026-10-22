package app.auviora.core

import java.nio.file.Files
import java.security.MessageDigest
import org.junit.Assert.*
import org.junit.Test

class ArtifactVerifierTest {
    private fun fixture(bytes: ByteArray, test: (java.io.File, ModelArtifact) -> Unit) {
        val file = Files.createTempFile("auviora-artifact", ".gguf").toFile()
        try {
            file.writeBytes(bytes)
            val hash = MessageDigest.getInstance("SHA-256").digest(bytes).joinToString("") { "%02x".format(it) }
            test(file, ModelArtifact(file.name, bytes.size.toLong(), hash, "https://example.invalid/model"))
        } finally { file.delete() }
    }
    @Test fun acceptsExactContent() = fixture("GGUFverified".toByteArray()) { file, artifact ->
        ArtifactVerifier.verify(file, artifact)
    }
    @Test fun rejectsSameSizeCorruption() = fixture("GGUFverified".toByteArray()) { file, artifact ->
        file.writeText("GGUFcorrupt!")
        assertEquals(artifact.bytes, file.length())
        assertEquals(EngineError.INVALID_ARTIFACT, assertThrows(ArtifactException::class.java) { ArtifactVerifier.verify(file, artifact) }.reason)
    }
    @Test fun rejectsMissingAndTruncatedFiles() = fixture("GGUFverified".toByteArray()) { file, artifact ->
        file.writeText("GGUF")
        assertThrows(ArtifactException::class.java) { ArtifactVerifier.verify(file, artifact) }
        file.delete()
        assertEquals(EngineError.MISSING_ARTIFACT, assertThrows(ArtifactException::class.java) { ArtifactVerifier.verify(file, artifact) }.reason)
    }
    @Test fun rejectsNonGgufEvenWithMatchingHash() = fixture("HTMLverified".toByteArray()) { file, artifact ->
        assertThrows(ArtifactException::class.java) { ArtifactVerifier.verify(file, artifact) }
    }
    @Test fun pinsExactValidatedArtifacts() {
        val model = Models.miniCpm
        assertEquals(47, model.nativeVersion)
        assertEquals(1780750400L, model.artifacts.sumOf { it.bytes })
        assertTrue(model.artifacts.all { it.url.contains(model.revision) && it.sha256.length == 64 })
    }
}
