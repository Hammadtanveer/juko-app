package com.juko.app.feature.sidebar.presentation

import androidx.compose.runtime.compositionLocalOf

class DrawerController(
    val open: () -> Unit = {},
    val close: () -> Unit = {}
)

val LocalDrawerController = compositionLocalOf { DrawerController() }
