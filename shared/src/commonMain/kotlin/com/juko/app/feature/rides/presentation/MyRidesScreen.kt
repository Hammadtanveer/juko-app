package com.juko.app.feature.rides.presentation

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.juko.app.core.data.RideStateManager
import com.juko.app.core.model.*
import com.juko.app.core.presentation.components.JukoAvatar
import com.juko.app.core.presentation.components.JukoButton
import com.juko.app.core.presentation.components.JukoSegmentedControl
import com.juko.app.core.presentation.theme.LocalSpacing
import com.juko.app.feature.inbox.presentation.ChatScreen
import com.juko.app.feature.sidebar.presentation.LocalDrawerController
import kotlinx.coroutines.launch

class MyRidesScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val spacing = LocalSpacing.current
        val snackbarHostState = remember { SnackbarHostState() }
        val coroutineScope = rememberCoroutineScope()
        val drawerController = LocalDrawerController.current

        // 0: PUBLISH, 1: REQUEST / BOOKING, 2: HISTORY (Default is 2: HISTORY)
        var selectedTab by remember { mutableStateOf(2) }

        // Reactive State from RideStateManager
        val bookingsList by RideStateManager.customerBookings.collectAsState()
        val publishedList by RideStateManager.publishedRides.collectAsState()
        val requestsList by RideStateManager.bookingRequests.collectAsState()
        val historyList by RideStateManager.rideHistory.collectAsState()

        // Modals & Dialogs State
        var selectedRideForPassengers by remember { mutableStateOf<PublishedRideModel?>(null) }
        var lockedEditRideMessage by remember { mutableStateOf<String?>(null) }
        var reviewTargetRide by remember { mutableStateOf<HistoryRideModel?>(null) }
        var statusFilter by remember { mutableStateOf("All") }
        var showStatusFilterMenu by remember { mutableStateOf(false) }

        val primaryBlue = Color(0xFF0052CC)

        Scaffold(
            topBar = {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 1.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = spacing.edgeMargin)
                    ) {
                        // Header with Hamburger Menu and Notifications
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                IconButton(
                                    onClick = { drawerController.open() },
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.surfaceContainer)
                                ) {
                                    Icon(
                                        Icons.Outlined.Menu,
                                        contentDescription = "Open Drawer",
                                        tint = primaryBlue,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Text(
                                    text = "Your Rides",
                                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 22.sp),
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Box(contentAlignment = Alignment.TopEnd) {
                                IconButton(
                                    onClick = {
                                        navigator.push(com.juko.app.feature.notifications.presentation.NotificationsScreen())
                                    },
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.surfaceContainer)
                                ) {
                                    Icon(
                                        Icons.Outlined.Notifications,
                                        contentDescription = "Notifications",
                                        tint = primaryBlue,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                // Unread notification dot badge
                                Box(
                                    modifier = Modifier
                                        .padding(top = 6.dp, end = 6.dp)
                                        .size(9.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.surface)
                                        .padding(1.5.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFEF4444))
                                )
                            }
                        }

                        // 3 Sub-Tabs: PUBLISH | REQUEST / BOOKING | HISTORY
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                        ) {
                            SubTabItem(
                                title = "PUBLISH",
                                badgeCount = publishedList.size,
                                selected = selectedTab == 0,
                                onClick = { selectedTab = 0 },
                                modifier = Modifier.weight(1f)
                            )
                            SubTabItem(
                                title = "REQUEST / BOOKING",
                                badgeCount = requestsList.size + bookingsList.size,
                                selected = selectedTab == 1,
                                onClick = { selectedTab = 1 },
                                modifier = Modifier.weight(1.35f)
                            )
                            SubTabItem(
                                title = "HISTORY",
                                selected = selectedTab == 2,
                                onClick = { selectedTab = 2 },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = MaterialTheme.colorScheme.background,
            contentWindowInsets = WindowInsets(0, 0, 0, 0)
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                when (selectedTab) {
                    0 -> {
                        // 0: PUBLISH TAB (Driver Published Rides)
                        if (publishedList.isEmpty()) {
                            EmptyStateCard(
                                icon = Icons.Outlined.AddCircleOutline,
                                title = "No Published Rides",
                                subtitle = "Offer a ride and share travel costs with passengers on your route."
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = spacing.edgeMargin),
                                contentPadding = PaddingValues(top = spacing.md, bottom = 96.dp),
                                verticalArrangement = Arrangement.spacedBy(spacing.md)
                            ) {
                                items(publishedList) { ride ->
                                    DriverPublishedCard(
                                        ride = ride,
                                        onEditClick = {
                                            if (ride.filledSeats > 0) {
                                                lockedEditRideMessage = "Ride editing is locked because ${ride.filledSeats} passenger(s) have already booked seats."
                                            } else {
                                                coroutineScope.launch {
                                                    snackbarHostState.showSnackbar("Opening ride editor for ${ride.origin} → ${ride.destination}")
                                                }
                                            }
                                        },
                                        onViewPassengers = { selectedRideForPassengers = ride },
                                        onDelete = {
                                            RideStateManager.deletePublishedRide(ride.id)
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("Published ride removed")
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                    1 -> {
                        // 1: REQUEST / BOOKING TAB (Incoming Requests & Confirmed Bookings)
                        if (requestsList.isEmpty() && bookingsList.isEmpty()) {
                            EmptyStateCard(
                                icon = Icons.Outlined.DirectionsCar,
                                title = "No Requests or Bookings",
                                subtitle = "You don't have any active booking requests or confirmed rides."
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = spacing.edgeMargin),
                                contentPadding = PaddingValues(top = spacing.md, bottom = 96.dp),
                                verticalArrangement = Arrangement.spacedBy(spacing.md)
                            ) {
                                if (requestsList.isNotEmpty()) {
                                    item {
                                        Text(
                                            text = "INCOMING REQUESTS (${requestsList.size})",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = primaryBlue,
                                            modifier = Modifier.padding(top = spacing.xs, bottom = 2.dp)
                                        )
                                    }
                                    items(requestsList) { request ->
                                        BookingRequestCard(
                                            request = request,
                                            onAccept = {
                                                RideStateManager.acceptRequest(request.id)
                                                coroutineScope.launch {
                                                    snackbarHostState.showSnackbar("Booking accepted for ${request.passengerName}!")
                                                }
                                            },
                                            onReject = {
                                                RideStateManager.rejectRequest(request.id)
                                                coroutineScope.launch {
                                                    snackbarHostState.showSnackbar("Request declined")
                                                }
                                            }
                                        )
                                    }
                                }

                                if (bookingsList.isNotEmpty()) {
                                    item {
                                        Text(
                                            text = "YOUR CONFIRMED BOOKINGS (${bookingsList.size})",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = primaryBlue,
                                            modifier = Modifier.padding(top = if (requestsList.isNotEmpty()) spacing.md else spacing.xs, bottom = 2.dp)
                                        )
                                    }
                                    items(bookingsList) { booking ->
                                        PassengerBookingCard(
                                            booking = booking,
                                            onChat = {
                                                navigator.push(
                                                    ChatScreen(
                                                        conversationId = "chat_${booking.id}",
                                                        participantName = booking.driverName,
                                                        participantAvatar = booking.driverAvatar,
                                                        routeInfo = "${booking.boardingStop} → ${booking.destination}"
                                                    )
                                                )
                                            },
                                            onCancel = {
                                                RideStateManager.cancelBooking(booking.id)
                                                coroutineScope.launch {
                                                    snackbarHostState.showSnackbar("Booking cancelled successfully")
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                    2 -> {
                        // 2: HISTORY TAB (Default Tab)
                        HistorySection(
                            historyList = historyList,
                            statusFilter = statusFilter,
                            onFilterChange = { statusFilter = it },
                            showFilterMenu = showStatusFilterMenu,
                            onToggleFilterMenu = { showStatusFilterMenu = it },
                            onReviewClick = { ride -> reviewTargetRide = ride }
                        )
                    }
                }
            }
        }

        // View Passengers Bottom Sheet
        if (selectedRideForPassengers != null) {
            ViewPassengersDialog(
                ride = selectedRideForPassengers!!,
                onDismiss = { selectedRideForPassengers = null },
                onChatPassenger = { passengerName ->
                    selectedRideForPassengers = null
                    navigator.push(
                        ChatScreen(
                            conversationId = "chat_pass_$passengerName",
                            participantName = passengerName,
                            participantAvatar = null,
                            routeInfo = "Confirmed passenger on your ride"
                        )
                    )
                }
            )
        }

        // Locked Ride Edit Warning Dialog
        if (lockedEditRideMessage != null) {
            AlertDialog(
                onDismissRequest = { lockedEditRideMessage = null },
                icon = {
                    Box(
                        modifier = Modifier.size(48.dp).clip(CircleShape).background(Color(0xFFFEF3C7)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.Lock, contentDescription = null, tint = Color(0xFFD97706))
                    }
                },
                title = {
                    Text("Ride Editing Locked", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                },
                text = {
                    Text(
                        lockedEditRideMessage ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                confirmButton = {
                    Button(
                        onClick = { lockedEditRideMessage = null },
                        colors = ButtonDefaults.buttonColors(containerColor = primaryBlue)
                    ) {
                        Text("Understood")
                    }
                }
            )
        }

        // Two-Way Review Dialog (Customer <-> Driver)
        if (reviewTargetRide != null) {
            ReviewSubmissionDialog(
                ride = reviewTargetRide!!,
                isReviewingDriver = reviewTargetRide!!.userRole == "PASSENGER",
                onDismiss = { reviewTargetRide = null },
                onSubmit = { rating, comment ->
                    RideStateManager.submitReview(reviewTargetRide!!.id, rating, comment)
                    reviewTargetRide = null
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Thank you! Review submitted successfully.")
                    }
                }
            )
        }
    }
}

@Composable
private fun SubTabItem(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    badgeCount: Int = 0
) {
    val primaryColor = Color(0xFF0052CC)
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 2.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.5.sp,
                    letterSpacing = 0.2.sp
                ),
                fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
                color = if (selected) primaryColor else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
            if (badgeCount > 0) {
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFEF4444),
                    shadowElevation = 0.5.dp
                ) {
                    Box(
                        modifier = Modifier
                            .height(18.dp)
                            .defaultMinSize(minWidth = 18.dp)
                            .padding(horizontal = 5.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (badgeCount > 99) "99+" else badgeCount.toString(),
                            color = Color.White,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 10.5.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
        if (selected) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.5.dp)
                    .clip(RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp))
                    .background(primaryColor)
                    .align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
private fun PassengerBookingCard(
    booking: CustomerBookingModel,
    onChat: () -> Unit,
    onCancel: () -> Unit
) {
    val spacing = LocalSpacing.current
    val primaryBlue = Color(0xFF0052CC)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, Color(0xFFE8EDFF))
    ) {
        Column(
            modifier = Modifier.padding(spacing.md),
            verticalArrangement = Arrangement.spacedBy(spacing.sm)
        ) {
            // Header: Route & Status
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
                        Icon(Icons.Outlined.NearMe, contentDescription = null, tint = primaryBlue, modifier = Modifier.size(18.dp))
                        Text(
                            text = "${booking.boardingStop} → ${booking.destination}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "${booking.departureDate} • ${booking.departureTime}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF737685)
                    )
                }

                Surface(
                    color = Color(0xFFE3FCEF),
                    shape = RoundedCornerShape(percent = 50)
                ) {
                    Text(
                        text = "CONFIRMED",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = Color(0xFF006644),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            HorizontalDivider(color = Color(0xFFF1F3FF))

            // Boarding Stop & Driver Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(spacing.sm)
                ) {
                    JukoAvatar(imageUrl = booking.driverAvatar, size = 42.dp)
                    Column {
                        Text(text = booking.driverName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        Text(text = booking.vehicleName, style = MaterialTheme.typography.bodySmall, color = Color(0xFF737685))
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "₹${booking.totalFare}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = primaryBlue
                    )
                    Text(
                        text = "${booking.seatsBooked} ${if (booking.seatsBooked == 1) "Seat" else "Seats"}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF737685)
                    )
                }
            }

            // Boarding Location Callout (Direct Boarding - No OTP)
            Surface(
                color = Color(0xFFF1F5FE),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Outlined.PinDrop, contentDescription = null, tint = primaryBlue, modifier = Modifier.size(16.dp))
                    Text(
                        text = "Boarding: ${booking.boardingStop} (${booking.departureTime}) • Direct Boarding (No OTP)",
                        style = MaterialTheme.typography.labelSmall,
                        color = primaryBlue,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Actions: Cancel & Chat
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(spacing.sm)
            ) {
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f).height(40.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
                ) {
                    Text("Cancel Booking", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = onChat,
                    modifier = Modifier.weight(1f).height(40.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryBlue)
                ) {
                    Icon(Icons.Outlined.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Chat Driver", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun DriverPublishedCard(
    ride: PublishedRideModel,
    onEditClick: () -> Unit,
    onViewPassengers: () -> Unit,
    onDelete: () -> Unit
) {
    val spacing = LocalSpacing.current
    val primaryBlue = Color(0xFF0052CC)
    val isDraft = ride.status == "Draft"
    val isLocked = ride.filledSeats > 0

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, Color(0xFFE8EDFF))
    ) {
        Column(
            modifier = Modifier.padding(spacing.md),
            verticalArrangement = Arrangement.spacedBy(spacing.sm)
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
                        Icon(Icons.Outlined.NearMe, contentDescription = null, tint = primaryBlue, modifier = Modifier.size(18.dp))
                        Text(
                            text = "${ride.origin} → ${ride.destination}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = ride.dateTime,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF737685)
                    )
                }

                Surface(
                    color = if (isDraft) Color(0xFFDFE0E0) else primaryBlue,
                    shape = RoundedCornerShape(percent = 50)
                ) {
                    Text(
                        text = ride.status.uppercase(),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isDraft) Color(0xFF616363) else Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Seats progress & Price
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "${ride.filledSeats}/${ride.totalSeats} Seats Booked",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isLocked) Color(0xFF006844) else primaryBlue
                    )
                    Box(
                        modifier = Modifier
                            .width(130.dp)
                            .height(8.dp)
                            .clip(RoundedCornerShape(percent = 50))
                            .background(Color(0xFFE0E8FF))
                    ) {
                        val progress = if (ride.totalSeats > 0) ride.filledSeats.toFloat() / ride.totalSeats.toFloat() else 0f
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progress)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(percent = 50))
                                .background(if (isLocked) Color(0xFF006844) else primaryBlue)
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "₹${ride.pricePerSeat}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD97706)
                    )
                    Text(
                        text = "FULL FARE",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = Color(0xFF737685)
                    )
                }
            }

            HorizontalDivider(color = Color(0xFFF1F3FF))

            // Actions: Edit (with lock validation) & View Passengers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(spacing.xs)) {
                    IconButton(onClick = onEditClick) {
                        Icon(
                            if (isLocked) Icons.Outlined.Lock else Icons.Outlined.Edit,
                            contentDescription = "Edit Ride",
                            tint = if (isLocked) Color(0xFFD97706) else primaryBlue
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Outlined.Close, contentDescription = "Delete Ride", tint = MaterialTheme.colorScheme.error)
                    }
                }

                Button(
                    onClick = onViewPassengers,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryBlue),
                    modifier = Modifier.height(38.dp)
                ) {
                    Icon(Icons.Outlined.Group, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("View Passengers (${ride.filledSeats})", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun HistorySection(
    historyList: List<HistoryRideModel>,
    statusFilter: String,
    onFilterChange: (String) -> Unit,
    showFilterMenu: Boolean,
    onToggleFilterMenu: (Boolean) -> Unit,
    onReviewClick: (HistoryRideModel) -> Unit
) {
    val spacing = LocalSpacing.current
    val filteredHistory = when (statusFilter) {
        "Completed" -> historyList.filter { it.status == "Completed" }
        "Cancelled" -> historyList.filter { it.status == "Cancelled" }
        else -> historyList
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = spacing.edgeMargin),
        contentPadding = PaddingValues(top = spacing.sm, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(spacing.md)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "FILTER: ${statusFilter.uppercase()}",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF737685)
                )
                Box {
                    IconButton(onClick = { onToggleFilterMenu(true) }) {
                        Icon(Icons.Outlined.FilterList, contentDescription = "Filter", tint = Color(0xFF5D5F5F))
                    }
                    DropdownMenu(
                        expanded = showFilterMenu,
                        onDismissRequest = { onToggleFilterMenu(false) }
                    ) {
                        listOf("All", "Completed", "Cancelled").forEach { opt ->
                            DropdownMenuItem(
                                text = { Text(opt) },
                                onClick = {
                                    onFilterChange(opt)
                                    onToggleFilterMenu(false)
                                }
                            )
                        }
                    }
                }
            }
        }

        items(filteredHistory) { historyItem ->
            HistoryCardItem(
                history = historyItem,
                isPassengerView = historyItem.userRole == "PASSENGER",
                onReviewClick = { onReviewClick(historyItem) }
            )
        }
    }
}

@Composable
private fun HistoryCardItem(
    history: HistoryRideModel,
    isPassengerView: Boolean,
    onReviewClick: () -> Unit
) {
    val spacing = LocalSpacing.current
    val primaryBlue = Color(0xFF0052CC)
    val isCancelled = history.status == "Cancelled"

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        shadowElevation = 1.dp,
        border = BorderStroke(1.dp, Color(0xFFE8EDFF))
    ) {
        Column(
            modifier = Modifier.padding(spacing.md),
            verticalArrangement = Arrangement.spacedBy(spacing.sm)
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
                        Icon(Icons.Outlined.NearMe, contentDescription = null, tint = if (isCancelled) Color(0xFF5D5F5F) else primaryBlue, modifier = Modifier.size(18.dp))
                        Text(
                            text = "${history.origin} → ${history.destination}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(text = history.dateTime, style = MaterialTheme.typography.bodySmall, color = Color(0xFF737685))
                }

                Surface(
                    color = if (isCancelled) Color(0xFFFFDAD6) else Color(0xFFE3FCEF),
                    shape = RoundedCornerShape(percent = 50)
                ) {
                    Text(
                        text = history.status.uppercase(),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = if (isCancelled) Color(0xFFBA1A1A) else Color(0xFF006644),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "₹${history.price}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isCancelled) Color(0xFF9E9E9E) else Color(0xFFD97706),
                    textDecoration = if (isCancelled) TextDecoration.LineThrough else TextDecoration.None
                )

                if (!isCancelled) {
                    if (history.isReviewed) {
                        Surface(
                            color = Color(0xFFFEF3C7),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Icon(Icons.Filled.Star, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(14.dp))
                                Text(
                                    text = "Reviewed (${history.userSubmittedRating}★)",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFD97706)
                                )
                            }
                        }
                    } else {
                        Button(
                            onClick = onReviewClick,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = primaryBlue),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Icon(Icons.Outlined.StarRate, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isPassengerView) "Review Driver" else "Review Passenger",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BookingRequestCard(
    request: BookingRequestModel,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    val spacing = LocalSpacing.current
    val primaryBlue = Color(0xFF0052CC)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, Color(0xFFE8EDFF))
    ) {
        Column(
            modifier = Modifier.padding(spacing.md),
            verticalArrangement = Arrangement.spacedBy(spacing.sm)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(spacing.sm)
                ) {
                    JukoAvatar(imageUrl = request.avatarUrl, size = 44.dp)
                    Column {
                        Text(text = request.passengerName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                            Icon(Icons.Filled.Star, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(14.dp))
                            Text(text = "${request.rating} • ${request.ridesCount} rides", style = MaterialTheme.typography.bodySmall, color = Color(0xFF737685))
                        }
                    }
                }

                Surface(color = Color(0xFFE8EDFF), shape = RoundedCornerShape(4.dp)) {
                    Text(
                        text = "${request.seatsRequested} Seats",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = primaryBlue,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Surface(color = Color(0xFFF1F5FE), shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Outlined.LocationOn, contentDescription = null, tint = primaryBlue, modifier = Modifier.size(16.dp))
                    Text(
                        text = "Boarding: ${request.pickupStation} (${request.distanceAway} away)",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = primaryBlue
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "To Earn: ₹${request.toEarn}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = primaryBlue
                )
                Text(
                    text = request.timeAgo,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF9E9E9E)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(spacing.sm)
            ) {
                OutlinedButton(
                    onClick = onReject,
                    modifier = Modifier.weight(1f).height(40.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
                ) {
                    Text("Decline", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = onAccept,
                    modifier = Modifier.weight(1f).height(40.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryBlue)
                ) {
                    Text("Accept Booking", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}



@Composable
private fun ViewPassengersDialog(
    ride: PublishedRideModel,
    onDismiss: () -> Unit,
    onChatPassenger: (String) -> Unit
) {
    val primaryBlue = Color(0xFF0052CC)
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Outlined.Group, contentDescription = null, tint = primaryBlue, modifier = Modifier.size(24.dp))
                Text("Confirmed Passengers", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Passengers organized by Boarding Stop:",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF737685)
                )

                if (ride.passengersList.isEmpty()) {
                    Text("No passengers booked yet for this ride.", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF9E9E9E))
                } else {
                    ride.passengersList.forEach { p ->
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFF1F5FE)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(text = p.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                    Text(text = "Boarding: ${p.boardingStop} • ${p.seats} seat(s)", style = MaterialTheme.typography.bodySmall, color = primaryBlue)
                                }
                                IconButton(onClick = { onChatPassenger(p.name) }) {
                                    Icon(Icons.Outlined.Chat, contentDescription = "Chat", tint = primaryBlue, modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = primaryBlue)
            ) {
                Text("Done")
            }
        }
    )
}

@Composable
private fun ReviewSubmissionDialog(
    ride: HistoryRideModel,
    isReviewingDriver: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (Int, String) -> Unit
) {
    var selectedStars by remember { mutableStateOf(5) }
    var commentText by remember { mutableStateOf("") }
    val primaryBlue = Color(0xFF0052CC)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isReviewingDriver) "Rate & Review Driver" else "Rate & Review Passenger",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "How was your experience on the ${ride.origin} → ${ride.destination} ride?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Star Rating Stepper
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(5) { index ->
                        val starNumber = index + 1
                        IconButton(onClick = { selectedStars = starNumber }) {
                            Icon(
                                Icons.Filled.Star,
                                contentDescription = "$starNumber stars",
                                tint = if (starNumber <= selectedStars) Color(0xFFF59E0B) else Color(0xFFC3C6D6),
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = commentText,
                    onValueChange = { commentText = it },
                    placeholder = { Text("Write your feedback...", color = Color(0xFFC3C6D6)) },
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    shape = RoundedCornerShape(8.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(selectedStars, commentText) },
                colors = ButtonDefaults.buttonColors(containerColor = primaryBlue)
            ) {
                Text("Submit Review")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun EmptyStateCard(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Box(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(icon, contentDescription = null, tint = Color(0xFFC3C6D6), modifier = Modifier.size(64.dp))
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF737685), textAlign = TextAlign.Center)
        }
    }
}


