package com.vitaminC.kanaflash.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = ForestGreen,
    onPrimary = WhiteText,
    secondary = SageGreen,
    onSecondary = WhiteText,
    tertiary = WarmTerracotta,
    onTertiary = WhiteText,
    background = SoftCream,
    onBackground = Ink,
    surface = Paper,
    onSurface = Ink,
    surfaceVariant = Mist,
    onSurfaceVariant = SoftInk,
    outline = SageGreen
)

private val DarkColorScheme = darkColorScheme(
    primary = NightSage,
    onPrimary = DeepForest,
    secondary = WarmClay,
    onSecondary = DeepForest,
    tertiary = WarmClay,
    onTertiary = DeepForest,
    background = DeepForest,
    onBackground = Paper,
    surface = DarkSurface,
    onSurface = Paper,
    surfaceVariant = DarkSurfaceAlt,
    onSurfaceVariant = Mist,
    outline = NightSage
)

@Composable
fun KanaFlashTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
