package app.auviora.core

import java.io.File
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

data class ModelArtifact(val name: String, val bytes: Long, val sha256: String, val url: String)
data class ModelDescriptor(
    val id: String, val name: String, val revision: String,
    val artifacts: List<ModelArtifact>, val nativeVersion: Int,
    val supportsImages: Boolean = true
)
enum class EnginePhase { UNLOADED, VERIFYING, LOADING, READY, GENERATING, UNLOADING, ERROR }
enum class EngineError { MISSING_ARTIFACT, INVALID_ARTIFACT, NATIVE_FAILURE, STORAGE_FAILURE }
data class EngineState(val phase: EnginePhase = EnginePhase.UNLOADED, val error: EngineError? = null)
data class GenerationRequest(val text: String, val image: ByteArray? = null, val maxTokens: Int = 1024)
interface LocalInferenceEngine {
    val state: StateFlow<EngineState>
    suspend fun load(model: ModelDescriptor, directory: File)
    fun generate(request: GenerationRequest): Flow<String>
    fun cancel()
    suspend fun reset()
    suspend fun unload()
}
