package com.juko.app.core.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

private val JukoLightColorScheme = lightColorScheme(
    primary = JukoPrimary,
    onPrimary = JukoOnPrimary,
    primaryContainer = JukoPrimaryContainer,
    secondary = JukoSecondary,
    secondaryContainer = JukoSecondaryContainer,
    tertiary = JukoTertiary,
    tertiaryContainer = JukoTertiaryContainer,
    error = JukoError,
    surface = JukoSurface,
    onSurface = JukoOnSurface,
    onSurfaceVariant = JukoOnSurfaceVariant,
    outline = JukoOutline,
    outlineVariant = JukoOutlineVariant,
    background = JukoBackground,
    onBackground = JukoOnBackground
)

@Composable
fun JukoTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalSpacing provides DefaultSpacing) {
        MaterialTheme(
            colorScheme = JukoLightColorScheme,
            typography = JukoTypography,
            shapes = JukoShapes,
            content = content
        )
    }
}
