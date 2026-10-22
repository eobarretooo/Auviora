package app.auviora.android

import android.os.Debug
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import app.auviora.core.EnginePhase
import java.io.File
import org.json.JSONObject
import org.junit.Assume.assumeTrue
import org.junit.Rule
import org.junit.Test

/** Opt-in hardware tests. No canned output, fake engine or access to another app's files. */
class RealInferenceTest {
    @get:Rule val ui = createAndroidComposeRule<MainActivity>()
    private val app get() = ui.activity.application as AuvioraApplication

    @Test fun provisionExactArtifactsThroughApplicationDownload() {
        assumeTrue(InstrumentationRegistry.getArguments().getString("provision") == "true")
        ui.onNodeWithTag("menu").performClick()
        ui.onNodeWithTag("nav-MODELS").performClick()
        ui.onNodeWithTag("download-model").performClick()
        ui.waitUntil(1_200_000) { app.models.state.value.filesPresent }
        check(!app.models.state.value.busy)
    }

    @Test fun displayRealResponseCancelRecoverAndReload() {
        assumeTrue(InstrumentationRegistry.getArguments().getString("realInference") == "true")
        check(app.models.model.artifacts.all { File(app.models.directory, it.name).length() == it.bytes }) { "Provision exact model files first" }
        val report = JSONObject().put("application_id", ui.activity.packageName).put("version", BuildConfig.VERSION_NAME)
        ui.onNodeWithTag("menu").performClick()
        ui.onNodeWithTag("nav-MODELS").performClick()
        val loadStart = System.currentTimeMillis()
        ui.onNodeWithTag("load-model").performClick()
        ui.waitUntil(180_000) { app.engine.state.value.phase in listOf(EnginePhase.READY, EnginePhase.ERROR) }
        check(app.engine.state.value.phase == EnginePhase.READY)
        report.put("verified_load_ms", System.currentTimeMillis() - loadStart)
        ui.activityRule.scenario.onActivity { it.onBackPressedDispatcher.onBackPressed() }
        ui.onNodeWithTag("chat-input").performTextInput("What is 15 multiplied by 7?")
        ui.onNodeWithTag("send").performClick()
        var firstText: Long? = null
        val snapshots = linkedSetOf<String>()
        val start = System.currentTimeMillis()
        ui.waitUntil(120_000) {
            val nodes = ui.onAllNodesWithTag("assistant-1", useUnmergedTree = true).fetchSemanticsNodes()
            nodes.firstOrNull()?.config?.getOrElse(androidx.compose.ui.semantics.SemanticsProperties.Text) { emptyList() }?.joinToString("") { it.text }?.takeIf { it.isNotBlank() }?.let {
                if (firstText == null) firstText = System.currentTimeMillis()
                snapshots.add(it)
            }
            snapshots.isNotEmpty() && app.engine.state.value.phase != EnginePhase.GENERATING
        }
        val actual = snapshots.last()
        ui.onNodeWithTag("assistant-1", useUnmergedTree = true).assertIsDisplayed()
        report.put("prompt", "What is 15 multiplied by 7?").put("response", actual)
            .put("contains_105", actual.contains("105")).put("visible_snapshots", snapshots.size)
            .put("first_observed_text_ms", firstText?.minus(start)).put("response_ms", System.currentTimeMillis() - start)
        check(actual.isNotBlank())
        ui.onNodeWithTag("chat-input").performTextInput("Write a long story about a forest.")
        ui.onNodeWithTag("send").performClick()
        ui.waitUntil(60_000) { app.engine.state.value.phase == EnginePhase.GENERATING }
        ui.onNodeWithTag("stop").performClick()
        ui.waitUntil(60_000) { app.engine.state.value.phase == EnginePhase.READY }
        ui.onNodeWithTag("chat-input").performTextInput("What is 2 plus 2?")
        ui.onNodeWithTag("send").performClick()
        ui.waitUntil(120_000) {
            ui.onAllNodesWithTag("assistant-5", useUnmergedTree = true).fetchSemanticsNodes().isNotEmpty() &&
                app.engine.state.value.phase == EnginePhase.READY &&
                ui.onNodeWithTag("assistant-5", useUnmergedTree = true).fetchSemanticsNode().config[androidx.compose.ui.semantics.SemanticsProperties.Text].any { it.text.isNotBlank() }
        }
        report.put("post_cancel_response", ui.onNodeWithTag("assistant-5", useUnmergedTree = true).fetchSemanticsNode().config[androidx.compose.ui.semantics.SemanticsProperties.Text].joinToString("") { it.text })
        ui.onNodeWithTag("menu").performClick()
        ui.onNodeWithTag("nav-MODELS").performClick()
        report.put("pss_before_unload_kib", Debug.getPss())
        ui.onNodeWithTag("unload-model").performClick()
        ui.waitUntil(60_000) { app.engine.state.value.phase == EnginePhase.UNLOADED }
        report.put("pss_after_unload_kib", Debug.getPss())
        ui.onNodeWithTag("load-model").performClick()
        ui.waitUntil(180_000) { app.engine.state.value.phase in listOf(EnginePhase.READY, EnginePhase.ERROR) }
        check(app.engine.state.value.phase == EnginePhase.READY)
        report.put("reload_ready", true)
        ui.activityRule.scenario.onActivity { it.onBackPressedDispatcher.onBackPressed() }
        ui.onNodeWithTag("chat-input").performTextInput("What is 15 multiplied by 7?")
        ui.onNodeWithTag("send").performClick()
        ui.waitUntil(120_000) {
            ui.onAllNodesWithTag("assistant-1", useUnmergedTree = true).fetchSemanticsNodes().isNotEmpty() &&
                app.engine.state.value.phase == EnginePhase.READY &&
                ui.onNodeWithTag("assistant-1", useUnmergedTree = true).fetchSemanticsNode().config[androidx.compose.ui.semantics.SemanticsProperties.Text].any { it.text.isNotBlank() }
        }
        report.put("post_reload_response", ui.onNodeWithTag("assistant-1", useUnmergedTree = true).fetchSemanticsNode().config[androidx.compose.ui.semantics.SemanticsProperties.Text].joinToString("") { it.text })
        File(app.filesDir, "phase2-inference.json").writeText(report.toString(2))
    }
}
