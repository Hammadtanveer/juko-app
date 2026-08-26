package com.juko.app.core.presentation.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Spacing(
    val base: Dp = 4.dp,
    val xs: Dp = 8.dp,
    val sm: Dp = 12.dp,
    val md: Dp = 16.dp,
    val lg: Dp = 24.dp,
    val xl: Dp = 32.dp,
    val edgeMargin: Dp = 16.dp,
    val stackGap: Dp = 12.dp
)

val LocalSpacing = staticCompositionLocalOf { Spacing() }

val DefaultSpacing = Spacing()
