package app.auviora.android

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FoundationTest {
    @get:Rule val ui = createAndroidComposeRule<MainActivity>()

    @Test fun drawerDestinationsAndBackKeepChatAsHome() {
        ui.onNodeWithText(ui.activity.getString(R.string.welcome)).assertIsDisplayed()
        ui.onNodeWithTag("send").assertIsNotEnabled()
        ui.onNodeWithTag("menu").performClick()
        ui.onNodeWithTag("nav-MODELS").performClick()
        ui.onNodeWithTag("load-model").assertIsDisplayed()
        ui.activityRule.scenario.onActivity { it.onBackPressedDispatcher.onBackPressed() }
        ui.onNodeWithTag("chat-input").assertIsDisplayed()
        ui.onNodeWithTag("menu").performClick()
        ui.onNodeWithTag("nav-AGENTS").performClick()
        ui.onNodeWithText(ui.activity.getString(R.string.not_available)).assertIsNotEnabled()
    }

    @Test fun settingsSwitchThemeAndLocaleThroughSupportedApi() {
        try {
            ui.onNodeWithTag("menu").performClick()
            ui.onNodeWithTag("nav-SETTINGS").performClick()
            ui.onNodeWithTag("theme-DARK").performScrollTo().performClick()
            ui.onNodeWithTag("theme-DARK").assertIsSelected()
            ui.onNodeWithTag("language-pt-BR").performScrollTo().performClick()
            ui.waitUntil(10_000) { AppCompatDelegate.getApplicationLocales().toLanguageTags() == "pt-BR" }
            ui.onNodeWithText("Idioma do aplicativo").assertExists()
            ui.onNodeWithTag("language-en").performScrollTo().performClick()
            ui.waitUntil(10_000) { AppCompatDelegate.getApplicationLocales().toLanguageTags() == "en" }
            ui.onNodeWithText("Application language").assertExists()
        } finally {
            ui.activityRule.scenario.onActivity { AppCompatDelegate.setApplicationLocales(LocaleListCompat.getEmptyLocaleList()) }
        }
    }
}
