package com.juko.app.feature.main

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.juko.app.feature.sidebar.presentation.DrawerController
import com.juko.app.feature.sidebar.presentation.LocalDrawerController
import com.juko.app.feature.sidebar.presentation.MainDrawerContent
import kotlinx.coroutines.launch

class MainContainerScreen : Screen {
    @Composable
    override fun Content() {
        val rootNavigator = LocalNavigator.currentOrThrow
        val drawerState = rememberDrawerState(DrawerValue.Closed)
        val coroutineScope = rememberCoroutineScope()

        val drawerController = remember {
            DrawerController(
                open = { coroutineScope.launch { drawerState.open() } },
                close = { coroutineScope.launch { drawerState.close() } }
            )
        }

        CompositionLocalProvider(LocalDrawerController provides drawerController) {
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    MainDrawerContent(
                        navigator = rootNavigator,
                        onCloseDrawer = {
                            coroutineScope.launch { drawerState.close() }
                        }
                    )
                }
            ) {
                TabNavigator(SearchTab) {
                    Scaffold(
                        bottomBar = {
                            JukoBottomNavigation()
                        },
                        contentWindowInsets = WindowInsets(0, 0, 0, 0)
                    ) { padding ->
                        Box(modifier = Modifier.padding(padding)) {
                            CurrentTab()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun JukoBottomNavigation() {
    val tabNavigator = LocalTabNavigator.current

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .drawBehind {
                drawLine(
                    color = Color(0xFFC3C6D6),
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 1.dp.toPx()
                )
            },
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            TabNavigationItem(SearchTab)
            TabNavigationItem(PublishTab)
            TabNavigationItem(YourRidesTab)
            TabNavigationItem(InboxTab)
            TabNavigationItem(ProfileTab)
        }
    }
}

@Composable
private fun RowScope.TabNavigationItem(tab: Tab) {
    val tabNavigator = LocalTabNavigator.current
    val selected = tabNavigator.current == tab
    
    val activeColor = MaterialTheme.colorScheme.primary
    val inactiveColor = Color(0xFF5D5F5F)

    NavigationBarItem(
        selected = selected,
        onClick = { tabNavigator.current = tab },
        icon = {
            val icon = if (selected) {
                when (tab) {
                    SearchTab -> Icons.Default.Search
                    PublishTab -> Icons.Default.AddCircle
                    YourRidesTab -> Icons.Default.DirectionsCar
                    InboxTab -> Icons.Default.ChatBubble
                    ProfileTab -> Icons.Default.Person
                    else -> null
                }
            } else null

            Icon(
                painter = if (icon != null) rememberVectorPainter(icon) else tab.options.icon!!,
                contentDescription = tab.options.title,
                tint = if (selected) activeColor else inactiveColor
            )
        },
        label = {
            Text(
                text = tab.options.title,
                style = MaterialTheme.typography.labelSmall,
                color = if (selected) activeColor else inactiveColor,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            )
        },
        colors = NavigationBarItemDefaults.colors(
            indicatorColor = Color.Transparent,
            selectedIconColor = activeColor,
            unselectedIconColor = inactiveColor,
            selectedTextColor = activeColor,
            unselectedTextColor = inactiveColor
        )
    )
}
