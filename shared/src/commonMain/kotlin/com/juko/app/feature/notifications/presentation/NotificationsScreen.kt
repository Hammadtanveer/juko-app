package com.juko.app.feature.notifications.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.juko.app.core.presentation.theme.LocalSpacing

data class NotificationItem(
    val id: String,
    val title: String,
    val timeAgo: String,
    val description: String,
    val icon: ImageVector,
    val iconBgColor: Color,
    val iconTint: Color,
    val isUnread: Boolean,
    val section: String
)

class NotificationsScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val spacing = LocalSpacing.current
        val primaryBlue = Color(0xFF0052CC)

        var notifications by remember { mutableStateOf(getInitialNotifications()) }

        val hasUnread = notifications.any { it.isUnread }

        Scaffold(
            topBar = {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 1.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .height(56.dp)
                            .padding(horizontal = spacing.edgeMargin),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(spacing.xs)
                        ) {
                            IconButton(onClick = { navigator.pop() }) {
                                Icon(
                                    Icons.AutoMirrored.Outlined.ArrowBack,
                                    contentDescription = "Back",
                                    tint = primaryBlue
                                )
                            }
                            Text(
                                text = "Notifications",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        TextButton(
                            onClick = {
                                notifications = notifications.map { it.copy(isUnread = false) }
                            },
                            enabled = hasUnread
                        ) {
                            Text(
                                text = if (hasUnread) "Mark all as read" else "All read",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (hasUnread) primaryBlue else MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            },
            containerColor = MaterialTheme.colorScheme.background,
            contentWindowInsets = WindowInsets(0, 0, 0, 0)
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = spacing.edgeMargin),
                contentPadding = PaddingValues(top = spacing.md, bottom = 40.dp),
                verticalArrangement = Arrangement.spacedBy(spacing.lg)
            ) {
                val sections = listOf("TODAY", "YESTERDAY", "THIS WEEK")

                sections.forEach { sectionName ->
                    val sectionItems = notifications.filter { it.section == sectionName }
                    if (sectionItems.isNotEmpty()) {
                        item(key = sectionName) {
                            Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                                Text(
                                    text = sectionName,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.outline,
                                    letterSpacing = 1.sp,
                                    modifier = Modifier.padding(start = 2.dp)
                                )

                                Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                                    sectionItems.forEach { item ->
                                        NotificationCard(
                                            item = item,
                                            onClick = {
                                                notifications = notifications.map {
                                                    if (it.id == item.id) it.copy(isUnread = false) else it
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationCard(
    item: NotificationItem,
    onClick: () -> Unit
) {
    val spacing = LocalSpacing.current
    val primaryBlue = Color(0xFF0052CC)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .then(
                if (item.isUnread) {
                    Modifier.drawBehind {
                        drawLine(
                            color = primaryBlue,
                            start = Offset(0f, 0f),
                            end = Offset(0f, size.height),
                            strokeWidth = 4.dp.toPx()
                        )
                    }
                } else {
                    Modifier
                }
            ),
        shape = RoundedCornerShape(12.dp),
        color = if (item.isUnread) Color(0xFFE0E8FF).copy(alpha = 0.5f) else Color.White,
        shadowElevation = if (item.isUnread) 2.dp else 1.dp,
        border = if (!item.isUnread) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE8EDFF)) else null
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.md)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.md),
                verticalAlignment = Alignment.Top
            ) {
                // Icon Avatar
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(item.iconBgColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        item.icon,
                        contentDescription = null,
                        tint = item.iconTint,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Title & Description
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = if (item.isUnread) FontWeight.Bold else FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = item.timeAgo,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.outline
                        )
                    }

                    Text(
                        text = item.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Unread Dot
            if (item.isUnread) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(primaryBlue)
                        .align(Alignment.BottomEnd)
                )
            }
        }
    }
}

private fun getInitialNotifications(): List<NotificationItem> {
    val primaryBlue = Color(0xFF0052CC)
    return listOf(
        // TODAY
        NotificationItem(
            id = "notif_1",
            title = "Booking Confirmed",
            timeAgo = "5m ago",
            description = "Your ride to Pune is confirmed for tomorrow 9:00 AM",
            icon = Icons.Outlined.DirectionsCar,
            iconBgColor = primaryBlue,
            iconTint = Color.White,
            isUnread = true,
            section = "TODAY"
        ),
        NotificationItem(
            id = "notif_2",
            title = "New Ride Request",
            timeAgo = "15m ago",
            description = "Rohan wants to join your ride to Delhi",
            icon = Icons.Outlined.Person,
            iconBgColor = primaryBlue,
            iconTint = Color.White,
            isUnread = true,
            section = "TODAY"
        ),
        NotificationItem(
            id = "notif_3",
            title = "Ride Starting Soon",
            timeAgo = "30m ago",
            description = "Your ride to Seohara starts in 30 minutes",
            icon = Icons.Outlined.Schedule,
            iconBgColor = Color(0xFFFEF3C7),
            iconTint = Color(0xFFD97706),
            isUnread = false,
            section = "TODAY"
        ),
        NotificationItem(
            id = "notif_4",
            title = "Driver is arriving",
            timeAgo = "2h ago",
            description = "Your driver is 2 minutes away from the pickup point",
            icon = Icons.Outlined.DirectionsCar,
            iconBgColor = primaryBlue,
            iconTint = Color.White,
            isUnread = false,
            section = "TODAY"
        ),

        // YESTERDAY
        NotificationItem(
            id = "notif_5",
            title = "Ride Completed",
            timeAgo = "1d ago",
            description = "Hope you had a great ride! Rate your experience.",
            icon = Icons.Outlined.CheckCircle,
            iconBgColor = Color(0xFFD1FAE5),
            iconTint = Color(0xFF059669),
            isUnread = false,
            section = "YESTERDAY"
        ),
        NotificationItem(
            id = "notif_6",
            title = "Booking Cancelled",
            timeAgo = "1d ago",
            description = "The passenger cancelled the request for your Delhi ride",
            icon = Icons.Outlined.Cancel,
            iconBgColor = Color(0xFFDFE0E0),
            iconTint = Color(0xFF5D5F5F),
            isUnread = false,
            section = "YESTERDAY"
        ),

        // THIS WEEK
        NotificationItem(
            id = "notif_7",
            title = "New Message",
            timeAgo = "2d ago",
            description = "Hey, where should I wait for the pickup?",
            icon = Icons.Outlined.Chat,
            iconBgColor = primaryBlue,
            iconTint = Color.White,
            isUnread = false,
            section = "THIS WEEK"
        )
    )
}
