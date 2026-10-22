package app.auviora.android.design

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

enum class ThemeMode { SYSTEM, DARK, LIGHT }
object Space {
    val small = 8.dp
    val medium = 16.dp
    val large = 24.dp
    val page = 20.dp
    val touch = 48.dp
}
private val Dark = darkColorScheme(
    primary = Color(0xFF8B7CFF), onPrimary = Color(0xFF171044), primaryContainer = Color(0xFF33285E), onPrimaryContainer = Color(0xFFE7DFFF),
    secondary = Color(0xFF44D7B6), onSecondary = Color(0xFF002D25), secondaryContainer = Color(0xFF124D42), onSecondaryContainer = Color(0xFFB0F2DD),
    tertiary = Color(0xFFAAC7FF), onTertiary = Color(0xFF092E60), tertiaryContainer = Color(0xFF254577), onTertiaryContainer = Color(0xFFD6E3FF),
    background = Color(0xFF0B1020), onBackground = Color(0xFFF3F5FF), surface = Color(0xFF171E31), onSurface = Color(0xFFF3F5FF),
    surfaceVariant = Color(0xFF283149), onSurfaceVariant = Color(0xFFC1C7DB), outline = Color(0xFF8B93AB), outlineVariant = Color(0xFF3D465D),
    error = Color(0xFFFFB4AB), onError = Color(0xFF690005), errorContainer = Color(0xFF93000A), onErrorContainer = Color(0xFFFFDAD6),
    inverseSurface = Color(0xFFE2E5F2), inverseOnSurface = Color(0xFF202637), inversePrimary = Color(0xFF5543B9),
    surfaceDim = Color(0xFF0B1020), surfaceBright = Color(0xFF363E52), surfaceContainerLowest = Color(0xFF080D19),
    surfaceContainerLow = Color(0xFF12192A), surfaceContainer = Color(0xFF171E31), surfaceContainerHigh = Color(0xFF232B40), surfaceContainerHighest = Color(0xFF2E374D)
)
private val Light = lightColorScheme(
    primary = Color(0xFF5543B9), onPrimary = Color.White, primaryContainer = Color(0xFFE7DFFF), onPrimaryContainer = Color(0xFF1E0969),
    secondary = Color(0xFF006B56), onSecondary = Color.White, secondaryContainer = Color(0xFF9BF4D5), onSecondaryContainer = Color(0xFF002118),
    tertiary = Color(0xFF345E93), onTertiary = Color.White, tertiaryContainer = Color(0xFFD4E3FF), onTertiaryContainer = Color(0xFF001C3A),
    background = Color(0xFFF7F8FF), onBackground = Color(0xFF171E31), surface = Color(0xFFFEFBFF), onSurface = Color(0xFF171E31),
    surfaceVariant = Color(0xFFE4E4F0), onSurfaceVariant = Color(0xFF454958), outline = Color(0xFF74788B), outlineVariant = Color(0xFFC7CAD8),
    error = Color(0xFFBA1A1A), onError = Color.White, errorContainer = Color(0xFFFFDAD6), onErrorContainer = Color(0xFF410002)
)
@Composable fun AuvioraTheme(mode: ThemeMode, dynamic: Boolean, content: @Composable () -> Unit) {
    val dark = when (mode) { ThemeMode.DARK -> true; ThemeMode.LIGHT -> false; ThemeMode.SYSTEM -> isSystemInDarkTheme() }
    val colors = if (dynamic && Build.VERSION.SDK_INT >= 31) {
        if (dark) dynamicDarkColorScheme(LocalContext.current) else dynamicLightColorScheme(LocalContext.current)
    } else if (dark) Dark else Light
    MaterialTheme(colorScheme = colors, typography = Typography(), shapes = Shapes(
        small = RoundedCornerShape(8.dp), medium = RoundedCornerShape(16.dp), large = RoundedCornerShape(24.dp)
    ), content = content)
}
