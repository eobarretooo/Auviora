package app.auviora.inference

internal class NativeBridge {
    external fun init(nativeLibDir: String)
    external fun load(modelPath: String): Int
    external fun loadMmproj(path: String, slices: Int): Int
    external fun setMinicpmvVersionNative(version: Int)
    external fun setEnableThinkingNative(enable: Boolean)
    external fun prepare(): Int
    external fun processUserPrompt(prompt: String, tokens: Int): Int
    external fun generateNextToken(): String?
    external fun prefillImage(bytes: ByteArray, size: Int): Int
    external fun nativeCancelGeneration()
    external fun fullReset()
    external fun unload()
    external fun shutdown()
}
