package app.auviora.android

import android.graphics.Bitmap
import android.os.SystemClock
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.semantics.SemanticsOwner
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.lifecycle.ViewModelProvider
import app.auviora.core.EnginePhase
import java.io.File
import org.json.JSONObject
import org.junit.Assume.assumeTrue
import org.junit.Rule
import org.junit.Test

class StreamingVisualTest {
    @get:Rule val ui = createAndroidComposeRule<MainActivity>()
    private val app get() = ui.activity.application as AuvioraApplication
    // Test-only interop with the pinned Compose 1.9 Android view. Avoid Espresso
    // idleness draining the generation before inspecting the actual rendered tree.
    private fun taggedNode(tag: String): SemanticsNode? {
        fun owner(view: View): SemanticsOwner? {
            if (view.javaClass.name == "androidx.compose.ui.platform.AndroidComposeView") {
                return view.javaClass.getMethod("getSemanticsOwner").invoke(view) as SemanticsOwner
            }
            if (view is ViewGroup) for (index in 0 until view.childCount) owner(view.getChildAt(index))?.let { return it }
            return null
        }
        fun find(node: SemanticsNode): SemanticsNode? {
            if (node.config.getOrElse(SemanticsProperties.TestTag) { "" } == tag && !node.boundsInRoot.isEmpty) return node
            node.children.forEach { find(it)?.let { found -> return found } }
            return null
        }
        val root = owner(ui.activity.window.decorView)?.unmergedRootSemanticsNode ?: return null
        return find(root)
    }
    private fun capture(name: String) {
        File(app.filesDir, name).outputStream().use {
            ui.onRoot().captureToImage().asAndroidBitmap().compress(Bitmap.CompressFormat.PNG, 100, it)
        }
    }
    @Test fun observeLongerVisibleStreamingAndCaptureRealScreens() {
        assumeTrue(InstrumentationRegistry.getArguments().getString("realInference") == "true")
        capture("phase2-welcome.png")
        ui.onNodeWithTag("menu").performClick()
        capture("phase2-drawer.png")
        ui.onNodeWithTag("nav-MODELS").performClick()
        lateinit var vm: AppViewModel
        ui.activityRule.scenario.onActivity { vm = ViewModelProvider(it)[AppViewModel::class.java] }
        ui.onNodeWithTag("load-model").performClick()
        ui.waitUntil(180_000) { !vm.working.value && app.engine.state.value.phase in listOf(EnginePhase.READY, EnginePhase.ERROR) }
        check(app.engine.state.value.phase == EnginePhase.READY)
        capture("phase2-models.png")
        ui.activityRule.scenario.onActivity { it.onBackPressedDispatcher.onBackPressed() }
        val prompt = "Explain how rain forms in ten short sentences."
        ui.onNodeWithTag("chat-input").performTextInput(prompt)
        ui.onNodeWithTag("send").assertIsEnabled()
        // Invoke the real button's semantics action on main without waiting for idle.
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            val button = requireNotNull(taggedNode("send"))
            check(!button.config.contains(SemanticsProperties.Disabled))
            check(button.config[SemanticsActions.OnClick].action?.invoke() == true)
        }
        val snapshots = linkedSetOf<String>()
        val deadline = SystemClock.elapsedRealtime() + 120_000
        val observationStart = SystemClock.elapsedRealtime()
        while (SystemClock.elapsedRealtime() < deadline) {
            // The test frame clock must advance while real native IO is running.
            ui.mainClock.advanceTimeByFrame()
            InstrumentationRegistry.getInstrumentation().runOnMainSync {
                taggedNode("assistant-1")?.config?.getOrElse(SemanticsProperties.Text) { emptyList() }
                    ?.joinToString("") { it.text }?.takeIf { it.isNotBlank() }?.let { snapshots.add(it) }
            }
            if (snapshots.isNotEmpty() && app.engine.state.value.phase == EnginePhase.READY) break
            if (SystemClock.elapsedRealtime() - observationStart > 10_000 && app.engine.state.value.phase == EnginePhase.READY) break
            Thread.sleep(50)
        }
        ui.waitForIdle()
        val response = ui.onNodeWithTag("assistant-1", useUnmergedTree = true).fetchSemanticsNode().config[SemanticsProperties.Text].joinToString("") { it.text }
        capture("phase2-chat.png")
        File(app.filesDir, "phase2-streaming.json").writeText(JSONObject()
            .put("prompt", prompt).put("response", response).put("visible_snapshots", snapshots.size)
            .put("multiple_visible_updates", snapshots.size > 1).toString(2))
        check(snapshots.size > 1) { "No multiple visible text updates observed" }
    }
}
