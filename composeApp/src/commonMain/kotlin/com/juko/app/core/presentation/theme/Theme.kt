package com.juko.app.core.presentation.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

private val JukoLightColors: ColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = OnPrimaryContainer,

    secondary = Secondary,
    onSecondary = OnSecondary,
    secondaryContainer = SecondaryContainer,
    onSecondaryContainer = OnSecondaryContainer,

    tertiary = Tertiary,
    background = Background,
    onBackground = OnBackground,
    surface = Surface,
    onSurface = OnBackground, // using OnBackground for primary text color
    error = Error,
    onError = OnError,
    surfaceVariant = SurfaceVariant,
    outline = Outline
)

@Composable
fun JukoTheme(
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalSpacing provides DefaultSpacing
    ) {
        MaterialTheme(
            colorScheme = JukoLightColors,
            typography = JukoTypography,
            shapes = JukoShapes,
            content = content
        )
    }
}
