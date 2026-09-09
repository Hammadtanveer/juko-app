package com.juko.app.feature.profile.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.juko.app.core.presentation.components.JukoButton
import com.juko.app.core.presentation.theme.LocalSpacing
import com.juko.app.feature.inbox.presentation.ChatScreen

enum class ProfileRole {
    PASSENGER,
    DRIVER
}

data class PublicUserProfileScreen(
    val userName: String,
    val userAvatar: String? = null,
    val role: ProfileRole = ProfileRole.PASSENGER,
    val rating: Double = 4.9,
    val ridesCount: Int = 24,
    val bio: String? = null,
    val memberSince: String = "Member since March 2024",
    val phoneVerified: Boolean = true,
    val idVerified: Boolean = true,
    val emailVerified: Boolean = true,
    val vehicleModel: String? = null,
    val vehiclePlate: String? = null,
    val boardingStop: String? = null,
    val seatsBooked: Int? = null
) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val spacing = LocalSpacing.current
        val primaryBlue = Color(0xFF0052CC)

        val defaultBio = if (role == ProfileRole.DRIVER) {
            "Daily commuter offering safe and comfortable carpooling to share travel costs and reduce traffic. Punctual departure and polite driving."
        } else {
            "Regular commuter traveling between regional stops and cities. Respects driver guidelines, always punctual at pickup points."
        }

        Scaffold(
            topBar = {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White,
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { navigator.pop() }) {
                                Icon(
                                    Icons.AutoMirrored.Outlined.ArrowBack,
                                    contentDescription = "Back",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Spacer(modifier = Modifier.width(spacing.xs))
                            Text(
                                text = if (role == ProfileRole.DRIVER) "Driver Profile" else "Passenger Profile",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (role == ProfileRole.DRIVER) Color(0xFFE8EDFF) else Color(0xFFE3FCEF)
                        ) {
                            Text(
                                text = if (role == ProfileRole.DRIVER) "DRIVER" else "PASSENGER",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (role == ProfileRole.DRIVER) primaryBlue else Color(0xFF006644)
                            )
                        }
                    }
                }
            },
            bottomBar = {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White,
                    shadowElevation = 8.dp
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(horizontal = spacing.edgeMargin, vertical = 12.dp)
                    ) {
                        JukoButton(
                            text = "Chat with ${userName.split(" ").firstOrNull() ?: userName}",
                            onClick = {
                                val convId = "chat_${userName.lowercase().replace(" ", "_")}"
                                val routeText = if (boardingStop != null) "Boarding at $boardingStop" else "Trip Discussion"
                                navigator.push(
                                    ChatScreen(
                                        conversationId = convId,
                                        participantName = userName,
                                        participantAvatar = userAvatar,
                                        routeInfo = routeText
                                    )
                                )
                            },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            leadingIcon = {
                                Icon(Icons.Outlined.Chat, contentDescription = null, tint = Color.White)
                            }
                        )
                    }
                }
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = spacing.edgeMargin, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Profile Header Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    shadowElevation = 2.dp,
                    border = BorderStroke(1.dp, Color(0xFFE8EDFF))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Box {
                                JukoAvatar(
                                    initials = userName.take(2),
                                    imageUrl = userAvatar,
                                    size = 72.dp
                                )
                                Icon(
                                    Icons.Filled.Verified,
                                    contentDescription = "Verified",
                                    tint = Color(0xFF0052CC),
                                    modifier = Modifier
                                        .size(22.dp)
                                        .align(Alignment.BottomEnd)
                                        .background(Color.White, CircleShape)
                                        .border(2.dp, Color.White, CircleShape)
                                )
                            }

                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = userName,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Text(
                                    text = memberSince,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }
                    }
                }

                // 2. Active Trip / Boarding Info (if passenger with active stop)
                if (boardingStop != null || seatsBooked != null) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFF0F5FF),
                        border = BorderStroke(1.dp, Color(0xFFD0E0FF))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color.White),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Outlined.TripOrigin,
                                    contentDescription = null,
                                    tint = primaryBlue,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "BOOKING ON CURRENT RIDE",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = primaryBlue
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Boarding at ${boardingStop ?: "Origin"} • ${seatsBooked ?: 1} seat(s)",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                            }
                        }
                    }
                }

                // 3. Quick Stats Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ProfileStatBox(
                        title = "Rating",
                        value = "★ $rating",
                        subtitle = "From users",
                        modifier = Modifier.weight(1f)
                    )
                    ProfileStatBox(
                        title = "Rides",
                        value = "$ridesCount",
                        subtitle = "Completed",
                        modifier = Modifier.weight(1f)
                    )
                }

                // 4. Vehicle Details (Driver only)
                if (role == ProfileRole.DRIVER) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        shadowElevation = 2.dp,
                        border = BorderStroke(1.dp, Color(0xFFE8EDFF))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Outlined.DirectionsCar,
                                    contentDescription = null,
                                    tint = primaryBlue
                                )
                                Text(
                                    text = "Vehicle Details",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            HorizontalDivider(color = Color(0xFFF1F3FF))

                            val cleanModelName = vehicleModel
                                ?.substringBefore(" (")
                                ?.substringBefore(" -")
                                ?.trim()
                                ?.ifBlank { "Swift Dzire" }
                                ?: "Swift Dzire"

                            val cleanPlateNumber = if (!vehiclePlate.isNullOrBlank()) {
                                vehiclePlate
                            } else if (vehicleModel != null && vehicleModel.contains("(") && vehicleModel.contains(")")) {
                                vehicleModel.substringAfter("(").substringBefore(")").trim()
                            } else {
                                "DL 01 AB 1234"
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = cleanModelName,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Registered Vehicle",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF737685)
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFF4F5F7),
                                    border = BorderStroke(1.dp, Color(0xFFD1D5DB))
                                ) {
                                    Text(
                                        text = cleanPlateNumber,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1F2937)
                                    )
                                }
                            }
                        }
                    }
                }

                // 6. About / Bio Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    shadowElevation = 2.dp,
                    border = BorderStroke(1.dp, Color(0xFFE8EDFF))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "About",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = bio ?: defaultBio,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF475569),
                            lineHeight = 22.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun ProfileStatBox(
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        shadowElevation = 1.dp,
        border = BorderStroke(1.dp, Color(0xFFE8EDFF))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1E293B)
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF94A3B8),
                fontSize = 11.sp
            )
        }
    }
}
