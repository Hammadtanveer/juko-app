package com.juko.app.feature.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.juko.app.feature.home.presentation.HomeScreen
import com.juko.app.feature.postride.presentation.PostRideRouteScreen

internal object SearchTab : Tab {
    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.Outlined.Search)
            return remember {
                TabOptions(
                    index = 0u,
                    title = "Search",
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        HomeScreen().Content()
    }
}

internal object PublishTab : Tab {
    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.Outlined.AddCircle)
            return remember {
                TabOptions(
                    index = 1u,
                    title = "Publish",
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        Navigator(PostRideRouteScreen())
    }
}

internal object YourRidesTab : Tab {
    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.Outlined.DirectionsCar)
            return remember {
                TabOptions(
                    index = 2u,
                    title = "Your Rides",
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        com.juko.app.feature.rides.presentation.MyRidesScreen().Content()
    }
}

internal object InboxTab : Tab {
    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.Outlined.ChatBubble)
            return remember {
                TabOptions(
                    index = 3u,
                    title = "Inbox",
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        // Placeholder
    }
}

internal object ProfileTab : Tab {
    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.Outlined.Person)
            return remember {
                TabOptions(
                    index = 4u,
                    title = "Profile",
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        // Placeholder
    }
}
