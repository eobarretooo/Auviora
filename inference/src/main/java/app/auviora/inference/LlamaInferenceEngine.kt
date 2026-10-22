package app.auviora.inference

import android.content.Context
import app.auviora.core.*
import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/** One instance owned by Application; JNI globals are never accessed by a second owner. */
class LlamaInferenceEngine(context: Context) : LocalInferenceEngine {
    private val nativeDirectory = context.applicationInfo.nativeLibraryDir
    private val dispatcher = Executors.newSingleThreadExecutor { Thread(it, "AuvioraInference") }.asCoroutineDispatcher()
    private val lock = Mutex()
    private val cancelled = AtomicBoolean(false)
    private val mutableState = MutableStateFlow(EngineState())
    override val state = mutableState.asStateFlow()
    private val bridge = NativeBridge()
    private var initialized = false
    private var loaded = false

    private fun fail(error: Throwable) {
        mutableState.value = EngineState(EnginePhase.ERROR,
            (error as? ArtifactException)?.reason ?: EngineError.NATIVE_FAILURE)
    }
    override suspend fun load(model: ModelDescriptor, directory: File) = withContext(dispatcher) {
        lock.withLock {
            check(!loaded) { "Unload the active model first" }
            try {
                mutableState.value = EngineState(EnginePhase.VERIFYING)
                model.artifacts.forEach { ArtifactVerifier.verify(File(directory, it.name), it) }
                mutableState.value = EngineState(EnginePhase.LOADING)
                if (!initialized) {
                    CpuFeatures.bestGgmlCpuVariant()?.let { variant ->
                        try { System.loadLibrary("ggml-cpu-$variant") } catch (_: UnsatisfiedLinkError) { /* Baseline remains available. */ }
                    }
                    System.loadLibrary("auviora_inference")
                    bridge.init(nativeDirectory)
                    initialized = true
                }
                check(bridge.load(File(directory, model.artifacts[0].name).path) == 0)
                check(bridge.loadMmproj(File(directory, model.artifacts[1].name).path, 9) == 0)
                bridge.setMinicpmvVersionNative(model.nativeVersion)
                check(bridge.prepare() == 0)
                bridge.setEnableThinkingNative(false)
                loaded = true
                mutableState.value = EngineState(EnginePhase.READY)
            } catch (error: Throwable) {
                if (initialized) bridge.unload()
                loaded = false
                fail(error)
                throw error
            }
        }
    }
    override fun generate(request: GenerationRequest): Flow<String> = flow {
        lock.withLock {
            check(loaded && state.value.phase == EnginePhase.READY)
            require(request.text.isNotBlank() && request.text.length <= 8000)
            cancelled.set(false)
            mutableState.value = EngineState(EnginePhase.GENERATING)
            try {
                request.image?.let { check(bridge.prefillImage(it, it.size) == 0) }
                if (!cancelled.get()) {
                    check(bridge.processUserPrompt(request.text, request.maxTokens.coerceIn(1,1024)) == 0)
                    while (!cancelled.get()) {
                        currentCoroutineContext().ensureActive()
                        val piece = bridge.generateNextToken() ?: break
                        if (piece.isNotEmpty()) emit(piece)
                    }
                }
            } catch (error: CancellationException) {
                cancelled.set(true)
                throw error
            } catch (error: Throwable) {
                fail(error)
                throw error
            } finally {
                if (cancelled.get()) bridge.nativeCancelGeneration()
                if (state.value.phase != EnginePhase.ERROR) mutableState.value = EngineState(EnginePhase.READY)
            }
        }
    }.flowOn(dispatcher)
    override fun cancel() { cancelled.set(true) }
    override suspend fun reset() = withContext(dispatcher) {
        lock.withLock {
            if (loaded) {
                try { bridge.fullReset(); mutableState.value = EngineState(EnginePhase.READY) }
                catch (error: Throwable) { fail(error); throw error }
            }
        }
    }
    override suspend fun unload() {
        cancel()
        withContext(dispatcher) {
            lock.withLock {
                mutableState.value = EngineState(EnginePhase.UNLOADING)
                if (initialized) bridge.unload()
                loaded = false
                mutableState.value = EngineState()
            }
        }
    }
}
