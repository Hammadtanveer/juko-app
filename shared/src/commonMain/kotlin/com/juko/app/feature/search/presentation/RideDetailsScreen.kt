package com.juko.app.feature.search.presentation

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.juko.app.core.data.RideStateManager
import com.juko.app.core.model.CustomerBookingModel
import com.juko.app.core.model.RouteHelper
import com.juko.app.core.model.RouteLocation
import com.juko.app.core.model.RouteLocationState
import com.juko.app.core.model.SearchRideItem
import com.juko.app.core.presentation.components.JukoAvatar
import com.juko.app.core.presentation.components.JukoButton
import com.juko.app.core.presentation.theme.LocalSpacing
import com.juko.app.feature.inbox.presentation.ChatScreen
import kotlinx.coroutines.launch

data class RideDetailsScreen(
    val ride: SearchRideItem
) : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val spacing = LocalSpacing.current
        val snackbarHostState = remember { SnackbarHostState() }
        val coroutineScope = rememberCoroutineScope()

        val primaryBlue = Color(0xFF0052CC)

        // Route locations list (fallback if empty)
        val routeLocations = remember(ride) {
            if (ride.routeLocations.isNotEmpty()) {
                ride.routeLocations
            } else {
                listOf(
                    RouteLocation(id = "loc_start", name = ride.departureLocation, order = 0, isSource = true, estimatedTime = ride.departureTime),
                    RouteLocation(id = "loc_end", name = ride.arrivalLocation, order = 1, isDestination = true, estimatedTime = ride.arrivalTime)
                )
            }
        }

        val maxPickupIndex = (routeLocations.size - 2).coerceAtLeast(0)
        var selectedPickupIndex by remember {
            mutableStateOf(ride.initialSelectedStopIndex.coerceIn(0, maxPickupIndex))
        }
        var selectedSeats by remember { mutableStateOf(1) }
        var isFrontSeatSelected by remember { mutableStateOf(false) }
        var isWindowSeatSelected by remember { mutableStateOf(false) }
        var confirmedBooking by remember { mutableStateOf<CustomerBookingModel?>(null) }

        // Dynamic forward-only states
        val routeStates = remember(routeLocations, selectedPickupIndex) {
            RouteHelper.getRouteLocationStates(routeLocations, selectedPickupIndex)
        }

        val selectedPickupLocation = routeLocations.getOrNull(selectedPickupIndex) ?: routeLocations.first()
        val destinationLocation = routeLocations.last()

        val currentPricePerSeat = remember(routeLocations, selectedPickupIndex, ride.price) {
            RouteHelper.calculateRidePrice(routeLocations, selectedPickupIndex, ride.price)
        }

        val basePrice = currentPricePerSeat * selectedSeats
        val extraCost = (if (isFrontSeatSelected) 50 else 0) + (if (isWindowSeatSelected) 30 else 0)
        val totalPrice = basePrice + extraCost

        var showBookingSuccessDialog by remember { mutableStateOf(false) }

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
                                text = "Ride Details",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        IconButton(onClick = {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Ride link copied to clipboard")
                            }
                        }) {
                            Icon(
                                Icons.Outlined.Share,
                                contentDescription = "Share",
                                tint = primaryBlue
                            )
                        }
                    }
                }
            },
            bottomBar = {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = spacing.edgeMargin, vertical = spacing.sm),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "TOTAL PRICE",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF737685),
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "₹$totalPrice",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = primaryBlue
                            )
                        }

                        Button(
                            onClick = {
                                val result = RideStateManager.bookRide(
                                    rideId = ride.id,
                                    pickupStopIndex = selectedPickupIndex,
                                    seats = selectedSeats,
                                    isFrontSeat = isFrontSeatSelected,
                                    isWindowSeat = isWindowSeatSelected,
                                    passengerName = "Alex Rivera"
                                )
                                result.onSuccess { booking ->
                                    confirmedBooking = booking
                                    showBookingSuccessDialog = true
                                }.onFailure { error ->
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar(error.message ?: "Booking failed")
                                    }
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = primaryBlue),
                            modifier = Modifier.height(48.dp)
                        ) {
                            Text(
                                text = "Book $selectedSeats Seat${if (selectedSeats > 1) "s" else ""}",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = MaterialTheme.colorScheme.background,
            contentWindowInsets = WindowInsets(0, 0, 0, 0)
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = spacing.edgeMargin),
                contentPadding = PaddingValues(top = spacing.md, bottom = spacing.xl),
                verticalArrangement = Arrangement.spacedBy(spacing.md)
            ) {
                // Route & Dynamic Pickup Selection Card
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        shadowElevation = 2.dp,
                        border = BorderStroke(1.dp, Color(0xFFE8EDFF))
                    ) {
                        Column(
                            modifier = Modifier.padding(spacing.md),
                            verticalArrangement = Arrangement.spacedBy(spacing.md)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = Color(0xFFE8EDFF),
                                    shape = RoundedCornerShape(percent = 50)
                                ) {
                                    Text(
                                        text = "TODAY • ${ride.duration}",
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = primaryBlue,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Text(
                                    text = "${ride.seatsLeft} seats available",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF006844),
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            HorizontalDivider(color = Color(0xFFF1F3FF))

                            // Boarding Point Selection Header
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(
                                    text = "SELECT BOARDING POINT",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = primaryBlue
                                )
                                Text(
                                    text = "Tap a stop below to choose your pickup location. Travel is forward-only.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF737685)
                                )
                            }

                            // Interactive Timeline of Stops
                            Column(modifier = Modifier.fillMaxWidth()) {
                                routeStates.forEachIndexed { index, stateItem ->
                                    val isLast = index == routeStates.size - 1
                                    BoardingStopTimelineRow(
                                        stateItem = stateItem,
                                        isLast = isLast,
                                        onSelect = {
                                            if (!stateItem.location.isDestination && !stateItem.isDisabled) {
                                                selectedPickupIndex = index
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // Driver & Vehicle Card
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        shadowElevation = 2.dp,
                        border = BorderStroke(1.dp, Color(0xFFE8EDFF))
                    ) {
                        Column(
                            modifier = Modifier.padding(spacing.md),
                            verticalArrangement = Arrangement.spacedBy(spacing.md)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    JukoAvatar(imageUrl = ride.driverAvatar, size = 52.dp)
                                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                        Text(
                                            text = ride.driverName,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                Icons.Filled.Star,
                                                contentDescription = null,
                                                tint = Color(0xFFF59E0B),
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text(
                                                text = "${ride.driverRating} · Verified Driver",
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = FontWeight.SemiBold,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }

                                OutlinedButton(
                                    onClick = {
                                        navigator.push(
                                            ChatScreen(
                                                conversationId = "chat_${ride.id}",
                                                participantName = ride.driverName,
                                                participantAvatar = ride.driverAvatar,
                                                routeInfo = "${selectedPickupLocation.name} → ${destinationLocation.name}"
                                            )
                                        )
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, primaryBlue)
                                ) {
                                    Icon(
                                        Icons.Outlined.Chat,
                                        contentDescription = null,
                                        tint = primaryBlue,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Chat", color = primaryBlue, fontWeight = FontWeight.Bold)
                                }
                            }

                            HorizontalDivider(color = Color(0xFFF1F3FF))

                            // Vehicle & Amenities
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(spacing.xs)
                                ) {
                                    Icon(
                                        Icons.Outlined.DirectionsCar,
                                        contentDescription = null,
                                        tint = primaryBlue,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = ride.vehicleName,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AmenityBadge(icon = Icons.Outlined.Luggage, label = "Luggage allowed")
                                AmenityBadge(icon = Icons.Outlined.SmokeFree, label = "No smoking")
                            }
                        }
                    }
                }

                // Interactive Seat Selection Card
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        shadowElevation = 2.dp,
                        border = BorderStroke(1.dp, Color(0xFFE8EDFF))
                    ) {
                        Column(
                            modifier = Modifier.padding(spacing.md),
                            verticalArrangement = Arrangement.spacedBy(spacing.md)
                        ) {
                            Text(
                                text = "Select Seats & Add-ons",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            // Seat Count Stepper
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Number of Seats", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                    Text("₹$currentPricePerSeat per seat from ${selectedPickupLocation.name}", style = MaterialTheme.typography.bodySmall, color = Color(0xFF737685))
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(spacing.sm)
                                ) {
                                    IconButton(
                                        onClick = { if (selectedSeats > 1) selectedSeats-- },
                                        modifier = Modifier.border(1.dp, Color(0xFFC3C6D6), CircleShape).size(32.dp)
                                    ) {
                                        Icon(Icons.Outlined.Remove, contentDescription = "Decrease", modifier = Modifier.size(16.dp))
                                    }

                                    Text(
                                        text = selectedSeats.toString(),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )

                                    IconButton(
                                        onClick = { if (selectedSeats < ride.seatsLeft) selectedSeats++ },
                                        modifier = Modifier.background(primaryBlue, CircleShape).size(32.dp)
                                    ) {
                                        Icon(Icons.Outlined.Add, contentDescription = "Increase", tint = Color.White, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }

                            HorizontalDivider(color = Color(0xFFF1F3FF))

                            // Seat Add-on Options
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Front Seat Guarantee", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                    Text("+₹50 extra comfort", style = MaterialTheme.typography.bodySmall, color = Color(0xFF737685))
                                }
                                Switch(
                                    checked = isFrontSeatSelected,
                                    onCheckedChange = { isFrontSeatSelected = it }
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Window Seat Preference", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                    Text("+₹30 extra view", style = MaterialTheme.typography.bodySmall, color = Color(0xFF737685))
                                }
                                Switch(
                                    checked = isWindowSeatSelected,
                                    onCheckedChange = { isWindowSeatSelected = it }
                                )
                            }
                        }
                    }
                }

                // Safety & Cancellation Info
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFFE8EDFF).copy(alpha = 0.5f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFFDAE2FF))
                    ) {
                        Column(
                            modifier = Modifier.padding(spacing.md),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Outlined.Shield, contentDescription = null, tint = primaryBlue, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Juko Safety Guarantee", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = primaryBlue)
                            }
                            Text(
                                text = "Free cancellation up to 2 hours before departure. Drivers and passengers are verified via Government ID.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF434654)
                            )
                        }
                    }
                }
            }
        }

        // Booking Success Dialog
        if (showBookingSuccessDialog) {
            AlertDialog(
                onDismissRequest = { showBookingSuccessDialog = false },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Outlined.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF006844),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Booking Confirmed!", fontWeight = FontWeight.Bold)
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("You have booked $selectedSeats seat(s) with ${ride.driverName} for ₹$totalPrice.")
                        
                        Surface(
                            color = Color(0xFFE3FCEF),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    Icons.Outlined.Check,
                                    contentDescription = null,
                                    tint = Color(0xFF006644),
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    "Direct Boarding: No OTP or PIN required. Simply arrive at your boarding stop and board the vehicle!",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF006644),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Text(
                            "Boarding: ${selectedPickupLocation.name} (${selectedPickupLocation.estimatedTime})",
                            fontWeight = FontWeight.SemiBold,
                            color = primaryBlue
                        )
                        Text(
                            "Drop-off: ${destinationLocation.name} (${destinationLocation.estimatedTime})",
                            fontWeight = FontWeight.SemiBold
                        )
                        Text("You can chat with your driver anytime from 'Your Rides'.", color = Color(0xFF737685), fontSize = 13.sp)
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showBookingSuccessDialog = false
                            navigator.pop()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = primaryBlue)
                    ) {
                        Text("Done")
                    }
                }
            )
        }
    }
}

@Composable
private fun BoardingStopTimelineRow(
    stateItem: RouteLocationState,
    isLast: Boolean,
    onSelect: () -> Unit
) {
    val loc = stateItem.location
    val primaryBlue = Color(0xFF0052CC)
    val passedGray = Color(0xFF9E9E9E)
    val trackColor = if (stateItem.isDisabled) Color(0xFFE0E0E0) else Color(0xFFD0DCFF)

    val isDestination = loc.isDestination
    val isClickable = !isDestination && !stateItem.isDisabled

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .then(
                if (isClickable) {
                    Modifier.clickable { onSelect() }
                } else Modifier
            )
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Timeline Indicator Column
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(32.dp).padding(top = 2.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            stateItem.isSelected -> primaryBlue
                            stateItem.isDisabled -> Color(0xFFEEEEEE)
                            isDestination -> Color(0xFFE3FCEF)
                            else -> Color(0xFFE8EDFF)
                        }
                    )
                    .border(
                        2.dp,
                        when {
                            stateItem.isSelected -> primaryBlue
                            stateItem.isDisabled -> passedGray
                            isDestination -> Color(0xFF36B37E)
                            else -> primaryBlue
                        },
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (stateItem.isSelected) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color.White))
                } else if (stateItem.isDisabled) {
                    Icon(
                        Icons.Outlined.Close,
                        contentDescription = null,
                        tint = passedGray,
                        modifier = Modifier.size(10.dp)
                    )
                } else if (isDestination) {
                    Icon(
                        Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint = Color(0xFF36B37E),
                        modifier = Modifier.size(12.dp)
                    )
                } else {
                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(primaryBlue))
                }
            }

            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(44.dp)
                        .background(trackColor)
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Content Column
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = if (!isLast) 12.dp else 0.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = loc.name,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = if (stateItem.isSelected || isDestination) FontWeight.Bold else FontWeight.SemiBold,
                                color = when {
                                    stateItem.isDisabled -> passedGray
                                    stateItem.isSelected -> primaryBlue
                                    else -> MaterialTheme.colorScheme.onSurface
                                }
                            )
                        )

                        if (loc.isSource) {
                            Surface(
                                color = Color(0xFFE8EDFF),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "ORIGIN",
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = primaryBlue,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else if (isDestination) {
                            Surface(
                                color = Color(0xFFE3FCEF),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "DESTINATION",
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = Color(0xFF006644),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Text(
                        text = if (loc.estimatedTime.isNotBlank()) "Est. time: ${loc.estimatedTime}" else "Scheduled stop",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (stateItem.isDisabled) passedGray else Color(0xFF737685)
                    )
                }

                // Action / Status Badge
                when {
                    stateItem.isSelected -> {
                        Surface(
                            color = primaryBlue,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Outlined.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                                Text(
                                    text = "Selected Pickup",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    stateItem.isDisabled -> {
                        Surface(
                            color = Color(0xFFF5F5F5),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "Passed (No backward travel)",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = passedGray,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    isDestination -> {
                        Text(
                            text = "Drop-off Only",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF006644),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    else -> {
                        Surface(
                            color = Color(0xFFF1F5FE),
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(1.dp, Color(0xFFD0DCFF))
                        ) {
                            Text(
                                text = "Tap to Board",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                color = primaryBlue,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AmenityBadge(icon: ImageVector, label: String) {
    Surface(
        color = Color(0xFFF1F3FF),
        shape = RoundedCornerShape(percent = 50)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(icon, contentDescription = null, tint = Color(0xFF0052CC), modifier = Modifier.size(14.dp))
            Text(label, style = MaterialTheme.typography.labelSmall, color = Color(0xFF041B3C), fontWeight = FontWeight.Medium)
        }
    }
}
