package com.regt17fd.manpower.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// "Dark Military" is the default and, for Milestone 1, the only theme —
// spec section 5 allows a Dark/Light toggle in Admin Settings later
// (Milestone 2); the color scheme always resolves to this dark set for now.
private val ArtilleryDarkScheme = darkColorScheme(
    primary = AccentGold,
    onPrimary = BackgroundPrimary,
    secondary = AccentRed,
    onSecondary = TextPrimary,
    background = BackgroundPrimary,
    onBackground = TextPrimary,
    surface = CardBackground,
    onSurface = TextPrimary,
    surfaceVariant = CardBackground,
    onSurfaceVariant = TextSecondary,
    error = DangerRed,
    onError = TextPrimary,
    outline = AccentGold,
)

@Composable
fun ManpowerTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        val activity = view.context as? android.app.Activity
        activity?.window?.let { window ->
            window.statusBarColor = BackgroundPrimary.toArgb()
            window.navigationBarColor = BackgroundPrimary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }
    MaterialTheme(
        colorScheme = ArtilleryDarkScheme,
        typography = ManpowerTypography,
        shapes = ManpowerShapes,
        content = content,
    )
}
