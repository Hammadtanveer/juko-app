package com.juko.app.feature.rides.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.juko.app.core.data.RideStateManager
import com.juko.app.core.model.RouteLocation
import com.juko.app.core.presentation.components.JukoAvatar
import com.juko.app.core.presentation.components.JukoButton
import com.juko.app.core.presentation.theme.LocalSpacing
import com.juko.app.feature.inbox.presentation.ChatScreen
import com.juko.app.feature.profile.presentation.ProfileRole
import com.juko.app.feature.profile.presentation.PublicUserProfileScreen
import kotlinx.coroutines.launch

/**
 * Dedicated Ride Details Screen for Confirmed Passenger Bookings.
 * Opened when a passenger clicks on a ride route (e.g., "Chandpur -> Delhi")
 * in Your Rides -> REQUEST / BOOKING tab.
 */
data class BookedRideDetailScreen(
    val bookingId: String
) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val spacing = LocalSpacing.current
        val scrollState = rememberScrollState()
        val snackbarHostState = remember { SnackbarHostState() }
        val coroutineScope = rememberCoroutineScope()

        val bookingsList by RideStateManager.customerBookings.collectAsState()
        val publishedRides by RideStateManager.publishedRides.collectAsState()
        val booking = bookingsList.find { it.id == bookingId }

        var showCancelDialog by remember { mutableStateOf(false) }

        val primaryBlue = Color(0xFF0052CC)

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
                                text = "Ride Details",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Surface(
                            color = Color(0xFFE3FCEF),
                            shape = RoundedCornerShape(percent = 50)
                        ) {
                            Text(
                                text = booking?.status?.uppercase() ?: "CONFIRMED",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF006644),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            },
            bottomBar = {
                if (booking != null) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shadowElevation = 8.dp,
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(spacing.md)
                                .navigationBarsPadding(),
                            horizontalArrangement = Arrangement.spacedBy(spacing.md)
                        ) {
                            OutlinedButton(
                                onClick = { showCancelDialog = true },
                                modifier = Modifier.weight(1f).height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
                            ) {
                                Text(
                                    text = "Cancel Booking",
                                    color = MaterialTheme.colorScheme.error,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Button(
                                onClick = {
                                    navigator.push(
                                        ChatScreen(
                                            conversationId = "chat_${booking.id}",
                                            participantName = booking.driverName,
                                            participantAvatar = booking.driverAvatar,
                                            routeInfo = "${booking.boardingStop} → ${booking.destination}"
                                        )
                                    )
                                },
                                modifier = Modifier.weight(1.2f).height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = primaryBlue)
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Outlined.Chat,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Chat Driver", fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = MaterialTheme.colorScheme.background
        ) { padding ->
            if (booking == null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(spacing.sm)
                    ) {
                        Icon(
                            Icons.Outlined.Info,
                            contentDescription = null,
                            tint = Color(0xFF737685),
                            modifier = Modifier.size(48.dp)
                        )
                        Text("Booking not found or already cancelled", style = MaterialTheme.typography.bodyLarge)
                        Button(onClick = { navigator.pop() }) {
                            Text("Back to Rides")
                        }
                    }
                }
            } else {
                val relatedRide = publishedRides.find { it.id == booking.rideId }
                val routeLocations = remember(relatedRide, booking) {
                    if (relatedRide != null && relatedRide.routeLocations.isNotEmpty()) {
                        relatedRide.routeLocations
                    } else {
                        listOf(
                            RouteLocation(id = "start", name = booking.origin.ifBlank { booking.boardingStop }, order = 0, isSource = true, estimatedTime = booking.departureTime),
                            RouteLocation(id = "pickup", name = booking.boardingStop, order = 1, estimatedTime = booking.departureTime),
                            RouteLocation(id = "end", name = booking.destination, order = 2, isDestination = true, estimatedTime = "Arrival")
                        ).distinctBy { it.name }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(scrollState)
                        .padding(spacing.md),
                    verticalArrangement = Arrangement.spacedBy(spacing.md)
                ) {
                    // 1. Confirmed Status Banner
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFFE3FCEF),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF006644)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Outlined.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                            Column {
                                Text(
                                    text = "Your Booking is Confirmed!",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF006644)
                                )
                                Text(
                                    text = "Be at the boarding station 10 mins before departure.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF006644).copy(alpha = 0.85f)
                                )
                            }
                        }
                    }

                    // 2. Booking Pass & PIN Card
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
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(spacing.xs)
                                    ) {
                                        Icon(
                                            Icons.Outlined.NearMe,
                                            contentDescription = null,
                                            tint = primaryBlue,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Text(
                                            text = "${booking.boardingStop} → ${booking.destination}",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${booking.departureDate} • ${booking.departureTime}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color(0xFF737685)
                                    )
                                }
                            }

                            // Boarding Passcode PIN Box
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFF1F5FE),
                                border = BorderStroke(1.dp, Color(0xFFD0DCFF))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                Icons.Outlined.Pin,
                                                contentDescription = null,
                                                tint = primaryBlue,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Text(
                                                text = "BOARDING PIN (OTP)",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = primaryBlue
                                            )
                                        }
                                        Text(
                                            text = "Share with driver at pickup",
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                            color = Color(0xFF737685)
                                        )
                                    }

                                    Surface(
                                        color = primaryBlue,
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = booking.boardingPin,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                            style = MaterialTheme.typography.titleMedium.copy(letterSpacing = 2.sp),
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }

                            HorizontalDivider(color = Color(0xFFF1F3FF))

                            // Seats & Fare Breakdown
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "SEATS BOOKED",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFF737685),
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${booking.seatsBooked} ${if (booking.seatsBooked == 1) "Seat" else "Seats"}",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "TOTAL FARE",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFF737685),
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "₹${booking.totalFare}",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = primaryBlue
                                    )
                                }
                            }
                        }
                    }

                    // 3. Complete Route & Stops Timeline Card
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
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Route & Stops",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Surface(
                                    color = Color(0xFFF1F5FE),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "${routeLocations.size} STOPS",
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = primaryBlue,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            routeLocations.forEachIndexed { index, loc ->
                                val isBoardingStop = loc.name.equals(booking.boardingStop, ignoreCase = true)
                                val isDestination = loc.name.equals(booking.destination, ignoreCase = true) || (index == routeLocations.size - 1)

                                Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.width(28.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(if (isBoardingStop) 20.dp else 16.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    when {
                                                        isBoardingStop -> primaryBlue
                                                        isDestination -> Color(0xFF006644)
                                                        loc.isSource -> Color(0xFF737685)
                                                        else -> Color(0xFF90A4AE)
                                                    }
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(if (isBoardingStop) 8.dp else 6.dp)
                                                    .clip(CircleShape)
                                                    .background(Color.White)
                                            )
                                        }
                                        if (index < routeLocations.size - 1) {
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .width(2.dp)
                                                    .background(Color(0xFFD0DCFF))
                                            )
                                        }
                                    }

                                    Column(
                                        modifier = Modifier
                                            .padding(start = 8.dp, bottom = if (index < routeLocations.size - 1) 14.dp else 0.dp)
                                            .weight(1f)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(
                                                text = when {
                                                    isBoardingStop -> "YOUR BOARDING STOP"
                                                    isDestination -> "FINAL DESTINATION"
                                                    loc.isSource -> "ORIGIN"
                                                    else -> "STOP ${index + 1}"
                                                },
                                                style = MaterialTheme.typography.labelSmall,
                                                color = if (isBoardingStop) primaryBlue else if (isDestination) Color(0xFF006644) else Color(0xFF737685),
                                                fontSize = 10.sp,
                                                fontWeight = if (isBoardingStop || isDestination) FontWeight.Bold else FontWeight.Normal
                                            )
                                            if (isBoardingStop) {
                                                Surface(
                                                    color = Color(0xFFE8EDFF),
                                                    shape = RoundedCornerShape(4.dp)
                                                ) {
                                                    Text(
                                                        text = "Pickup Point",
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp),
                                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                                        color = primaryBlue,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                        }
                                        Text(
                                            text = loc.name,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = if (isBoardingStop || isDestination) FontWeight.Bold else FontWeight.Normal
                                        )
                                        if (loc.estimatedTime.isNotBlank()) {
                                            Text(
                                                text = "Estimated Time: ${loc.estimatedTime}",
                                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                                color = Color(0xFF737685)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 4. Driver & Vehicle Card
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
                            Text("Driver & Vehicle", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        navigator.push(
                                            PublicUserProfileScreen(
                                                userName = booking.driverName,
                                                userAvatar = booking.driverAvatar,
                                                role = ProfileRole.DRIVER,
                                                vehicleModel = booking.vehicleName
                                            )
                                        )
                                    },
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    JukoAvatar(imageUrl = booking.driverAvatar, size = 50.dp)
                                    Column {
                                        Text(
                                            text = booking.driverName,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = booking.vehicleName,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFF737685)
                                        )
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                                        ) {
                                            Icon(
                                                Icons.Filled.Star,
                                                contentDescription = null,
                                                tint = Color(0xFFF59E0B),
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Text(
                                                text = "4.8 • Verified Member",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color(0xFF737685)
                                            )
                                        }
                                    }
                                }

                                OutlinedButton(
                                    onClick = {
                                        navigator.push(
                                            PublicUserProfileScreen(
                                                userName = booking.driverName,
                                                userAvatar = booking.driverAvatar,
                                                role = ProfileRole.DRIVER,
                                                vehicleModel = booking.vehicleName
                                            )
                                        )
                                    },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("View Profile", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }

                    // 5. Ride Policies & Safety
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        shadowElevation = 2.dp,
                        border = BorderStroke(1.dp, Color(0xFFE8EDFF))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text("Trip Information", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)

                            AmenityItem(icon = Icons.Outlined.VerifiedUser, label = "Verified Driver Profile & Vehicle")
                            AmenityItem(icon = Icons.Outlined.Roofing, label = "Roof Rail / Luggage Carrier Available")
                            AmenityItem(icon = Icons.Outlined.Payments, label = "Pay ₹${booking.totalFare} directly to driver via Cash or UPI")
                            AmenityItem(icon = Icons.Outlined.Lock, label = "Secure Boarding Verification with PIN ${booking.boardingPin}")
                        }
                    }
                }
            }
        }

        // Cancel Booking Confirmation Dialog
        if (showCancelDialog && booking != null) {
            AlertDialog(
                onDismissRequest = { showCancelDialog = false },
                icon = {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFEBEE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Outlined.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                },
                title = {
                    Text(
                        "Cancel Booking?",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                text = {
                    Text(
                        "Are you sure you want to cancel your booking from ${booking.boardingStop} to ${booking.destination}? Your ${booking.seatsBooked} seat(s) will be released back to the driver.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showCancelDialog = false
                            RideStateManager.cancelBooking(booking.id)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Booking cancelled successfully")
                            }
                            navigator.pop()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Yes, Cancel", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCancelDialog = false }) {
                        Text("Keep Booking")
                    }
                }
            )
        }
    }

    @Composable
    private fun AmenityItem(icon: ImageVector, label: String) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = Color(0xFF0052CC),
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF333333)
            )
        }
    }
}
