package com.nutrition.tracker.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val primaryGreen = Color(0xFF4CAF50)
private val secondaryTeal = Color(0xFF009688)
private val tertiaryOrange = Color(0xFFFF9800)

private val LightColors = lightColorScheme(
    primary = primaryGreen,
    secondary = secondaryTeal,
    tertiary = tertiaryOrange,
    background = Color(0xFFF9FBF9),
    surface = Color.White,
    onPrimary = Color.White,
    onBackground = Color(0xFF1C1C1C),
    onSurface = Color(0xFF1C1C1C)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF81C784),
    secondary = Color(0xFF4DB6AC),
    tertiary = Color(0xFFFFB74D),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
)

@Composable
fun NutritionTrackerTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}
