package com.juko.app.feature.inbox.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.juko.app.core.presentation.components.JukoAvatar
import com.juko.app.core.presentation.theme.LocalSpacing

data class ConversationItem(
    val id: String,
    val participantName: String,
    val avatarUrl: String?,
    val route: String,
    val lastMessage: String,
    val timeAgo: String,
    val isUnread: Boolean,
    val isOnline: Boolean = false
)

class InboxScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val spacing = LocalSpacing.current
        val primaryBlue = Color(0xFF0052CC)

        val conversations = remember { getInitialConversations() }

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
                        Text(
                            text = "Juko",
                            style = MaterialTheme.typography.titleLarge.copy(fontSize = 22.sp),
                            fontWeight = FontWeight.Bold,
                            color = primaryBlue
                        )
                        IconButton(onClick = {
                            navigator.push(com.juko.app.feature.notifications.presentation.NotificationsScreen())
                        }) {
                            Icon(
                                Icons.Outlined.Notifications,
                                contentDescription = "Notifications",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            },
            containerColor = MaterialTheme.colorScheme.background,
            contentWindowInsets = WindowInsets(0, 0, 0, 0)
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 96.dp)
                ) {
                // Title and Search Button Row
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = spacing.edgeMargin)
                            .padding(top = spacing.lg, bottom = spacing.md),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = "Inbox",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        IconButton(
                            onClick = { /* Search inbox */ },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.5f))
                        ) {
                            Icon(
                                Icons.Outlined.Search,
                                contentDescription = "Search",
                                tint = primaryBlue,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }
                }

                // Filter Pill: All Messages
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = spacing.edgeMargin)
                            .padding(bottom = spacing.md)
                    ) {
                        Surface(
                            color = Color(0xFFDFE0E0),
                            shape = RoundedCornerShape(percent = 50),
                            modifier = Modifier.fillMaxWidth().height(40.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(3.dp)
                                    .clip(RoundedCornerShape(percent = 50))
                                    .background(Color.White),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "All Messages",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = primaryBlue
                                )
                            }
                        }
                    }
                }

                // Conversation List
                itemsIndexed(conversations) { index, item ->
                    ConversationRow(
                        item = item,
                        onClick = {
                            navigator.push(
                                ChatScreen(
                                    conversationId = item.id,
                                    participantName = item.participantName,
                                    participantAvatar = item.avatarUrl,
                                    routeInfo = item.route
                                )
                            )
                        }
                    )
                    if (index < conversations.size - 1) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = spacing.edgeMargin),
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                            thickness = 0.5.dp
                        )
                    }
                }

                // End of Messages Footer
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = spacing.xl, bottom = spacing.lg),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(spacing.xs)
                    ) {
                        Icon(
                            Icons.Outlined.ChatBubbleOutline,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                            modifier = Modifier.size(40.dp)
                        )
                        Text(
                            text = "END OF MESSAGES",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
                            letterSpacing = 1.sp
                        )
                    }
                }
            }

            // Floating Action Button for New Message
            FloatingActionButton(
                onClick = { /* New Message / Compose */ },
                shape = RoundedCornerShape(16.dp),
                containerColor = primaryBlue,
                contentColor = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = spacing.edgeMargin, bottom = spacing.edgeMargin)
                    .size(56.dp),
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp)
            ) {
                Icon(
                    Icons.Outlined.EditNote,
                    contentDescription = "New Message",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
private fun ConversationRow(
    item: ConversationItem,
    onClick: () -> Unit
) {
    val spacing = LocalSpacing.current
    val primaryBlue = Color(0xFF0052CC)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = spacing.edgeMargin, vertical = 14.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Avatar with Online indicator
        Box(modifier = Modifier.size(56.dp)) {
            JukoAvatar(
                imageUrl = item.avatarUrl,
                size = 56.dp
            )
            if (item.isOnline) {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .align(Alignment.BottomEnd)
                        .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                        .clip(CircleShape)
                        .background(Color(0xFF22C55E))
                )
            }
        }

        // Message Details
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
                    text = item.participantName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = if (item.isUnread) FontWeight.Bold else FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = item.timeAgo,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = if (item.isUnread) FontWeight.Bold else FontWeight.Normal,
                    color = if (item.isUnread) primaryBlue else MaterialTheme.colorScheme.outline
                )
            }

            // Route Info
            Text(
                text = item.route.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                color = MaterialTheme.colorScheme.outline,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                letterSpacing = 0.5.sp
            )

            // Message text and unread dot
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.lastMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (item.isUnread) FontWeight.Bold else FontWeight.Normal,
                    color = if (item.isUnread) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f).padding(end = spacing.xs)
                )

                if (item.isUnread) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(primaryBlue)
                    )
                }
            }
        }
    }
}

private fun getInitialConversations(): List<ConversationItem> {
    return listOf(
        ConversationItem(
            id = "conv_1",
            participantName = "Aditya Sharma",
            avatarUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDP6_tmHefBBp-ZoaeWaF_JuP_q5PUCPJvOi5sgOdTzUQqAtTSxQwr4scrmK2hr3_Xnwfgnd0kRERg_Z2dkhlB9Qp6g8iyp4i1wcL0u75eD9KTDltLW8VPlrtalmnXB-s_xzbOGipNQHWg6LGvmSI8e-azvLxwfyt-iRw0wWw-lyeOfce7ts0i850SAkZAe8_B1eCOF8Zr4UKTOyQqqenQ1dtG6_tO09Ln6HSYWLOZ1VGtjwSW31p5fa9aelVABqOLCWCjxCMM19BA",
            route = "Mumbai → Pune, Today 14:30",
            lastMessage = "Can you pick me up at the station?",
            timeAgo = "2m ago",
            isUnread = true,
            isOnline = true
        ),
        ConversationItem(
            id = "conv_2",
            participantName = "Sneha Gupta",
            avatarUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuAgzO3-re0ayhmU5awNoMtfQ941U2kzXIWCXbxD-kuv9thOeqwxYpZTsAc3ZCyWRLS-plSgM_eKYJKN-ONYODFudsSZ4RyFcrEu8Y9LSLySDbrX9q1gkC_3aG-acuITw9ER580ydJpWzk49UIL_AAZwQxGjNhXg0kSkUZT-RTo5801uFy68w-StWIb-SDs57cUOkQYDh3Xl9DMQeHRAI6o8PUOyyLCUs64_DFDWcWfgyJntlYKFtUrdqSmYDGY8zU_ooGbDcLcxQX0",
            route = "Seohara → Delhi, Tomorrow 09:00",
            lastMessage = "Perfect, see you then!",
            timeAgo = "1h ago",
            isUnread = false,
            isOnline = false
        ),
        ConversationItem(
            id = "conv_3",
            participantName = "Rahul Verma",
            avatarUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuAsI8HdZOOkcdAvSXFJEcxIM7yHeemB8N942_M97aS5JAMdLg--ZS4DfUBK4J28vPuOjjD9cqgCJoWhTKppXn1_RLyIN47D1LJ_t0fPDtjku4_nNhQXT-RtYtk12TXbPmXTA8a3xP1SMRn6VsWOuFPpKlFQPTrCKdWGSBrQdpDJqXqq0Ds2Z2RuxqaLkAdJQ3o9lAC45U5R4Wp6TGDckJX0LVauHbBulx7o4NE0ihQIjn8yWdamTale3K_0I-cFbZ7J0g5nG1ykqn0",
            route = "Delhi → Jaipur, 24 Oct",
            lastMessage = "Is there space for one more bag?",
            timeAgo = "3h ago",
            isUnread = true,
            isOnline = false
        ),
        ConversationItem(
            id = "conv_4",
            participantName = "Priya Das",
            avatarUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuAX1lQok_A_DJCfzqe35rSuIcIRBZzbAzg4S8DAYr3lGuDeb0HGCIyTsnGyaOcs-KJnNQsff1HZnOpZdivAKuYklN60PiG4KwJvAPbMJU8uYe3TUEqmb0klq6A_EVi2mQe6WAa5CyyNWwR-OuLGs7q-DkHc9vCfC_z4krTJLaZutttihDFlHRQRygVU0iYYxEYfu48psuD10I92Wopy0_vTF9Tab99II4EC4m53XkLIVaHof-_evaIlRV8RoKlk9egFqN8BkKVzm_k",
            route = "Pune → Mumbai, Yesterday",
            lastMessage = "Thanks for the ride!",
            timeAgo = "1d ago",
            isUnread = false,
            isOnline = false
        ),
        ConversationItem(
            id = "conv_5",
            participantName = "Karan Singh",
            avatarUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCtfhQEoPqnAZPFg3V5uM9qF3xXrkDb91Rs7NHQBgvAZ5AfIwlbvAa1YpYnrh2MVRtA8HPJTx4l0oZAUL8BpfdJj6GIJyeLCHzauCnbbNoQyA9g6X6SG8-uBR8pFS877pLgOsQPBBHCuD5UNFsU3OccIVGL1RJPw7K0_YpQbjKeDFVPTPIhnUbNDeUC6abAUHtX1YdXwOYXudeLv6FYjtMULakYSkGk_wVPBkvITJuYMHOUXnurkMK5qxKC8yMNiOcyKunJ0BIxgBQ",
            route = "Chandigarh → Delhi, 18 Oct",
            lastMessage = "Will be at the airport terminal 2.",
            timeAgo = "2d ago",
            isUnread = false,
            isOnline = false
        )
    )
}
}
