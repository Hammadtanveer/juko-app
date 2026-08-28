package com.juko.app.feature.inbox.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.juko.app.core.presentation.components.JukoAvatar
import com.juko.app.core.presentation.theme.LocalSpacing
import kotlinx.coroutines.launch

data class ChatMessage(
    val id: String,
    val text: String,
    val timestamp: String,
    val isFromMe: Boolean
)

data class ChatScreen(
    val conversationId: String,
    val participantName: String,
    val participantAvatar: String? = null,
    val routeInfo: String = "Delhi → Seohara • Today 08:00 AM"
) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val spacing = LocalSpacing.current
        val coroutineScope = rememberCoroutineScope()
        val listState = rememberLazyListState()

        var messageText by remember { mutableStateOf("") }
        var messages by remember {
            mutableStateOf(
                listOf(
                    ChatMessage("1", "Hi, I booked a seat for the Delhi to Seohara ride.", "08:15 AM", isFromMe = false),
                    ChatMessage("2", "Hey! Yes, I got your booking request. Will pick you up from ISBT Gate 2.", "08:17 AM", isFromMe = true),
                    ChatMessage("3", "Great! What is the car color and plate number?", "08:19 AM", isFromMe = false),
                    ChatMessage("4", "White Swift Dzire, DL 01 AB 1234. See you soon!", "08:20 AM", isFromMe = true)
                )
            )
        }

        val primaryBlue = Color(0xFF0052CC)

        Scaffold(
            topBar = {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 2.dp
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .statusBarsPadding()
                                .height(64.dp)
                                .padding(horizontal = spacing.edgeMargin),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(spacing.sm)
                            ) {
                                IconButton(onClick = { navigator.pop() }) {
                                    Icon(
                                        Icons.AutoMirrored.Outlined.ArrowBack,
                                        contentDescription = "Back"
                                    )
                                }
                                JukoAvatar(imageUrl = participantAvatar, size = 40.dp)
                                Column {
                                    Text(
                                        text = participantName,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFF006844))
                                        )
                                        Text(
                                            text = "Online",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            IconButton(
                                onClick = { /* Make phone call */ },
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE8EDFF))
                            ) {
                                Icon(
                                    Icons.Outlined.Call,
                                    contentDescription = "Call",
                                    tint = primaryBlue,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        // Ride Context Banner
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = Color(0xFFF1F3FF)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = spacing.edgeMargin, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(spacing.xs)
                            ) {
                                Icon(
                                    Icons.Outlined.DirectionsCar,
                                    contentDescription = null,
                                    tint = primaryBlue,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = routeInfo,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = primaryBlue,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            },
            bottomBar = {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .imePadding(),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = spacing.edgeMargin, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(spacing.sm)
                    ) {
                        OutlinedTextField(
                            value = messageText,
                            onValueChange = { messageText = it },
                            placeholder = {
                                Text(
                                    "Type a message...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.outlineVariant
                                )
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(24.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFFF9F9FF),
                                unfocusedContainerColor = Color(0xFFF9F9FF),
                                focusedBorderColor = primaryBlue,
                                unfocusedBorderColor = Color(0xFFE0E8FF)
                            )
                        )

                        IconButton(
                            onClick = {
                                if (messageText.isNotBlank()) {
                                    val newMsg = ChatMessage(
                                        id = (messages.size + 1).toString(),
                                        text = messageText.trim(),
                                        timestamp = "Just now",
                                        isFromMe = true
                                    )
                                    messages = messages + newMsg
                                    messageText = ""
                                    coroutineScope.launch {
                                        listState.animateScrollToItem(messages.size - 1)
                                    }
                                }
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(if (messageText.isNotBlank()) primaryBlue else Color(0xFFE0E8FF))
                        ) {
                            Icon(
                                Icons.AutoMirrored.Outlined.Send,
                                contentDescription = "Send",
                                tint = if (messageText.isNotBlank()) Color.White else Color(0xFF737685),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            },
            containerColor = Color(0xFFF9F9FF),
            contentWindowInsets = WindowInsets(0, 0, 0, 0)
        ) { padding ->
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = spacing.edgeMargin),
                contentPadding = PaddingValues(vertical = spacing.md),
                verticalArrangement = Arrangement.spacedBy(spacing.sm)
            ) {
                items(messages) { message ->
                    ChatBubble(message = message)
                }
            }
        }
    }
}

@Composable
private fun ChatBubble(message: ChatMessage) {
    val primaryBlue = Color(0xFF0052CC)

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (message.isFromMe) Alignment.End else Alignment.Start
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (message.isFromMe) 16.dp else 4.dp,
                bottomEnd = if (message.isFromMe) 4.dp else 16.dp
            ),
            color = if (message.isFromMe) primaryBlue else Color.White,
            shadowElevation = 1.dp,
            border = if (!message.isFromMe) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE8EDFF)) else null,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (message.isFromMe) Color.White else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = message.timestamp,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = if (message.isFromMe) Color.White.copy(alpha = 0.7f) else Color(0xFF737685),
                    modifier = Modifier.align(Alignment.End).padding(top = 2.dp)
                )
            }
        }
    }
}
