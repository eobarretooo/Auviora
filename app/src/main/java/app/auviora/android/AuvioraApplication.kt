package app.auviora.android

import android.app.Application
import app.auviora.android.data.ConversationRepository
import app.auviora.android.data.ModelRepository
import app.auviora.core.LocalInferenceEngine
import app.auviora.inference.LlamaInferenceEngine

class AuvioraApplication : Application() {
    val engine: LocalInferenceEngine by lazy { LlamaInferenceEngine(this) }
    val models by lazy { ModelRepository(this) }
    val conversations by lazy { ConversationRepository(this) }
}
