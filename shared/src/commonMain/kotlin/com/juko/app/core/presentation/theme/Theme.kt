package com.juko.app.core.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

private val JukoLightColorScheme = lightColorScheme(
    primary = JukoPrimary,
    background = JukoBackground
)

@Composable
fun JukoTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalSpacing provides DefaultSpacing) {
        MaterialTheme(
            colorScheme = JukoLightColorScheme,
            content = content
        )
    }
}
