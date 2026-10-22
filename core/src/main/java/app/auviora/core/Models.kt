package app.auviora.core

object Models {
    private const val REVISION = "8716e5209f308fd53f197ccd7d50c6fdcc2dc37d"
    private const val BASE = "https://huggingface.co/tc-mb/MiniCPM-V-Apps-gguf/resolve/$REVISION/MiniCPM-V-4.7-1B"
    val miniCpm = ModelDescriptor(
        "minicpm-v-4_7-1b", "MiniCPM-V 4.7 1B · Q4_K_M", REVISION,
        listOf(
            ModelArtifact("MiniCPM-V-4_7-1B-Q4_K_M.gguf", 672003584L,
                "bdc2c0da2320129a4fd91b0d464c63f8047b511a7ef7f7e1d143b433c7165cd5", "$BASE/MiniCPM-V-4_7-1B-Q4_K_M.gguf"),
            ModelArtifact("mmproj-model-f16.gguf", 1108746816L,
                "febb329c29ec9f1643c9d690ca74d35f486b83fa48c2f54c108b135729f635fd", "$BASE/mmproj-model-f16.gguf")
        ), 47
    )
}
