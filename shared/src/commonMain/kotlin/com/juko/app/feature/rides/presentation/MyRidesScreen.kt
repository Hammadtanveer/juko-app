package com.juko.app.feature.rides.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import com.juko.app.core.presentation.components.JukoAvatar
import com.juko.app.core.presentation.theme.LocalSpacing

enum class MyRidesTab {
    PUBLISHED, REQUESTS, HISTORY
}

class MyRidesScreen : Screen {
    @Composable
    override fun Content() {
        val spacing = LocalSpacing.current
        var currentTab by remember { mutableStateOf(MyRidesTab.PUBLISHED) }
        var publishedFilter by remember { mutableStateOf("All") }
        var showFilterMenu by remember { mutableStateOf(false) }

        // Mock State Lists
        var publishedList by remember { mutableStateOf(getInitialPublishedRides()) }
        var requestsList by remember { mutableStateOf(getInitialRequests()) }
        val historyList by remember { mutableStateOf(getInitialHistory()) }

        val primaryBlue = Color(0xFF0052CC)
        val amberCustom = Color(0xFFD97706)

        Scaffold(
            topBar = {
                Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
                    // Header
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
                        IconButton(
                            onClick = { /* TODO */ },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceContainer)
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                        ) {
                            Icon(
                                Icons.Outlined.Notifications,
                                contentDescription = "Notifications",
                                tint = primaryBlue
                            )
                        }
                    }

                    // Tab Navigation Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .padding(horizontal = spacing.edgeMargin)
                    ) {
                        TabItem(
                            title = "PUBLISHED",
                            selected = currentTab == MyRidesTab.PUBLISHED,
                            onClick = { currentTab = MyRidesTab.PUBLISHED },
                            modifier = Modifier.weight(1f)
                        )
                        TabItem(
                            title = "REQUESTS",
                            badgeCount = requestsList.size,
                            selected = currentTab == MyRidesTab.REQUESTS,
                            onClick = { currentTab = MyRidesTab.REQUESTS },
                            modifier = Modifier.weight(1f)
                        )
                        TabItem(
                            title = "HISTORY",
                            selected = currentTab == MyRidesTab.HISTORY,
                            onClick = { currentTab = MyRidesTab.HISTORY },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
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
                when (currentTab) {
                    MyRidesTab.PUBLISHED -> {
                        val filteredList = when (publishedFilter) {
                            "Active" -> publishedList.filter { it.status == "Active" }
                            "Draft" -> publishedList.filter { it.status == "Draft" }
                            else -> publishedList
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
                                        text = "YOUR LISTINGS",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Box {
                                        IconButton(onClick = { showFilterMenu = true }) {
                                            Icon(
                                                Icons.Outlined.FilterList,
                                                contentDescription = "Filter",
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        DropdownMenu(
                                            expanded = showFilterMenu,
                                            onDismissRequest = { showFilterMenu = false }
                                        ) {
                                            listOf("All", "Active", "Draft").forEach { option ->
                                                DropdownMenuItem(
                                                    text = { Text(option) },
                                                    onClick = {
                                                        publishedFilter = option
                                                        showFilterMenu = false
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            items(filteredList) { ride ->
                                PublishedRideCard(
                                    ride = ride,
                                    onDelete = {
                                        publishedList = publishedList.filter { it.id != ride.id }
                                    }
                                )
                            }
                        }
                    }

                    MyRidesTab.REQUESTS -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = spacing.edgeMargin),
                            contentPadding = PaddingValues(top = spacing.sm, bottom = 96.dp),
                            verticalArrangement = Arrangement.spacedBy(spacing.md)
                        ) {
                            items(requestsList) { request ->
                                BookingRequestCard(
                                    request = request,
                                    onAccept = {
                                        requestsList = requestsList.filter { it.id != request.id }
                                    },
                                    onReject = {
                                        requestsList = requestsList.filter { it.id != request.id }
                                    }
                                )
                            }
                        }
                    }

                    MyRidesTab.HISTORY -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = spacing.edgeMargin),
                            contentPadding = PaddingValues(top = spacing.sm, bottom = 96.dp),
                            verticalArrangement = Arrangement.spacedBy(spacing.md)
                        ) {
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = spacing.xs),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(spacing.xs)
                                    ) {
                                        Icon(
                                            Icons.Outlined.CalendarMonth,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.outline
                                        )
                                        Text(
                                            text = "May 2024",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    IconButton(onClick = { /* Filter */ }) {
                                        Icon(
                                            Icons.Outlined.FilterList,
                                            contentDescription = "Filter",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            items(historyList) { history ->
                                HistoryRideCard(history)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TabItem(
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
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = if (selected) primaryColor else MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (badgeCount > 0) {
                Spacer(modifier = Modifier.width(4.dp))
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.error),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = badgeCount.toString(),
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        if (selected) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(primaryColor)
                    .align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
private fun PublishedRideCard(ride: PublishedRideModel, onDelete: () -> Unit) {
    val spacing = LocalSpacing.current
    val primaryBlue = Color(0xFF0052CC)
    val isDraft = ride.status == "Draft"

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                RoundedCornerShape(8.dp)
            ),
        shape = RoundedCornerShape(8.dp),
        color = Color.White,
        shadowElevation = if (isDraft) 0.dp else 1.dp
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
                        Icon(
                            Icons.Outlined.NearMe,
                            contentDescription = null,
                            tint = if (isDraft) Color(0xFF5D5F5F) else primaryBlue,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "${ride.origin} → ${ride.destination}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Text(
                        text = ride.dateTime,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 2.dp)
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

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "${ride.filledSeats}/${ride.totalSeats} Seats Filled",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDraft) Color(0xFF5D5F5F) else primaryBlue
                    )
                    Box(
                        modifier = Modifier
                            .width(120.dp)
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
                                .background(if (isDraft) Color(0xFF5D5F5F) else primaryBlue)
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
                        text = "PER SEAT",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                modifier = Modifier.padding(top = 8.dp)
            )

            if (isDraft) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = { /* Resume */ }) {
                        Text(
                            text = "RESUME PUBLISHING",
                            fontWeight = FontWeight.Bold,
                            color = primaryBlue,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(spacing.xs)) {
                        IconButton(onClick = { /* Edit */ }) {
                            Icon(Icons.Outlined.Edit, contentDescription = "Edit", tint = primaryBlue)
                        }
                        IconButton(onClick = onDelete) {
                            Icon(Icons.Outlined.Close, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                        }
                    }

                    Button(
                        onClick = { /* View Passengers */ },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryBlue)
                    ) {
                        Icon(Icons.Outlined.Group, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("View Passengers", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
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
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                RoundedCornerShape(8.dp)
            ),
        shape = RoundedCornerShape(8.dp),
        color = Color.White,
        shadowElevation = 1.dp
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
                    JukoAvatar(
                        imageUrl = request.avatarUrl,
                        size = 48.dp
                    )
                    Column {
                        Text(
                            text = request.passengerName,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                Icons.Outlined.Star,
                                contentDescription = null,
                                tint = Color(0xFFF4B400),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = request.rating.toString(),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "• ${request.ridesCount} rides",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }
                }

                Surface(
                    color = Color(0xFFF1F3FF),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Outlined.AirlineSeatReclineNormal,
                            contentDescription = null,
                            tint = primaryBlue,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "${request.seatsRequested} Seats",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = primaryBlue
                        )
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    Icons.Outlined.LocationOn,
                    contentDescription = null,
                    tint = primaryBlue,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "${request.distanceAway} away • ${request.pickupStation}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Route Stops
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                request.routeStops.forEachIndexed { index, stop ->
                    Text(
                        text = stop,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (index < request.routeStops.size - 1) {
                        Icon(
                            Icons.AutoMirrored.Outlined.ArrowForward,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            // Date & Time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.md)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Outlined.CalendarToday, contentDescription = null, modifier = Modifier.size(14.dp))
                    Text(request.date, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Outlined.Schedule, contentDescription = null, modifier = Modifier.size(14.dp))
                    Text(request.timeSlot, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            // Badges
            Row(horizontalArrangement = Arrangement.spacedBy(spacing.xs)) {
                if (request.isWholeCar) {
                    Surface(color = Color(0xFF006844), shape = RoundedCornerShape(4.dp)) {
                        Text(
                            "WHOLE CAR BOOKING",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Surface(color = Color(0xFFE8EDFF), shape = RoundedCornerShape(4.dp)) {
                        Text(
                            "REGULAR BOOKING",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                if (request.seatPreference != null) {
                    Surface(color = Color(0xFFE8EDFF), shape = RoundedCornerShape(4.dp)) {
                        Text(
                            request.seatPreference,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = primaryBlue
                        )
                    }
                }
            }

            // Earnings
            Column(modifier = Modifier.padding(top = 4.dp)) {
                Text(
                    text = "TO EARN ₹${request.toEarn}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = primaryBlue
                )
                if (request.fareBreakdown != null) {
                    Text(
                        text = request.fareBreakdown,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Text(
                text = "Requested ${request.timeAgo}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )

            // Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(spacing.md)
            ) {
                OutlinedButton(
                    onClick = onReject,
                    modifier = Modifier.weight(1f).height(44.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, primaryBlue.copy(alpha = 0.2f))
                ) {
                    Text("Reject", color = primaryBlue, fontWeight = FontWeight.SemiBold)
                }
                Button(
                    onClick = onAccept,
                    modifier = Modifier.weight(1f).height(44.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryBlue)
                ) {
                    Text("Accept", color = Color.White, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun HistoryRideCard(history: HistoryRideModel) {
    val spacing = LocalSpacing.current
    val primaryBlue = Color(0xFF0052CC)
    val isCancelled = history.status == "Cancelled"

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f),
                RoundedCornerShape(8.dp)
            ),
        shape = RoundedCornerShape(8.dp),
        color = Color.White,
        shadowElevation = if (isCancelled) 0.dp else 1.dp
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
                        Icon(
                            Icons.Outlined.NearMe,
                            contentDescription = null,
                            tint = if (isCancelled) Color(0xFF5D5F5F) else primaryBlue,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "${history.origin} → ${history.destination}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Text(
                        text = history.dateTime,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                Surface(
                    color = if (isCancelled) Color(0xFFFFDAD6) else Color(0xFFE8EDFF),
                    shape = RoundedCornerShape(percent = 50)
                ) {
                    Text(
                        text = history.status.uppercase(),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isCancelled) Color(0xFFBA1A1A) else primaryBlue,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "₹${history.price}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (isCancelled) MaterialTheme.colorScheme.outline else Color(0xFFD97706),
                    textDecoration = if (isCancelled) TextDecoration.LineThrough else TextDecoration.None
                )
            }

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                modifier = Modifier.padding(top = 4.dp)
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = { /* View Details */ }) {
                    Text(
                        text = "VIEW DETAILS",
                        fontWeight = FontWeight.Bold,
                        color = primaryBlue,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

// Data Models
data class PublishedRideModel(
    val id: String,
    val origin: String,
    val destination: String,
    val dateTime: String,
    val status: String,
    val filledSeats: Int,
    val totalSeats: Int,
    val pricePerSeat: Int
)

data class BookingRequestModel(
    val id: String,
    val passengerName: String,
    val rating: Double,
    val ridesCount: Int,
    val seatsRequested: Int,
    val distanceAway: String,
    val pickupStation: String,
    val routeStops: List<String>,
    val date: String,
    val timeSlot: String,
    val isWholeCar: Boolean,
    val seatPreference: String?,
    val toEarn: Int,
    val fareBreakdown: String?,
    val timeAgo: String,
    val avatarUrl: String?
)

data class HistoryRideModel(
    val id: String,
    val origin: String,
    val destination: String,
    val dateTime: String,
    val price: Int,
    val status: String
)

private fun getInitialPublishedRides() = listOf(
    PublishedRideModel("1", "Mumbai", "Pune", "20 May, 08:00 AM", "Active", 2, 4, 450),
    PublishedRideModel("2", "Pune", "Lonavala", "22 May, 04:30 PM", "Draft", 0, 3, 280)
)

private fun getInitialRequests() = listOf(
    BookingRequestModel(
        id = "req_1",
        passengerName = "Sarah Jenkins",
        rating = 4.8,
        ridesCount = 12,
        seatsRequested = 2,
        distanceAway = "0.8 miles",
        pickupStation = "Downtown Station",
        routeStops = listOf("Delhi", "Noida", "Gurugram"),
        date = "Oct 25",
        timeSlot = "08:30 AM - 09:45 AM",
        isWholeCar = false,
        seatPreference = "Seat Preference: Front Seat × 1",
        toEarn = 950,
        fareBreakdown = "₹900 Base Fare + ₹50 Seat Preference",
        timeAgo = "12 min ago",
        avatarUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCuDOzLNc6hwnmc3ku7wiUEVxN-jxlnftdYPPrV3wORA0UJGq68VyDCef6aZZA502iroF4IilCFiWoTaB2A4k0n7qziv0ciVr86CcR57yJjxTmoqayhtopywImSelng0h-iBy-R-gI0xBFp5Hc5CAL5HLBo_0wM6lQbpy5D_c62kPYzxRu-QW6JtYOlykVUPZznRwESBNY3dcQlktNzNNo7spqZ2crHGxy3TRCPi-r4bdN9FJ9bcaxq"
    ),
    BookingRequestModel(
        id = "req_2",
        passengerName = "Michael Chen",
        rating = 4.5,
        ridesCount = 3,
        seatsRequested = 1,
        distanceAway = "1.4 miles",
        pickupStation = "North Gate",
        routeStops = listOf("Delhi", "Seohar"),
        date = "Oct 25",
        timeSlot = "09:00 AM - 09:30 AM",
        isWholeCar = true,
        seatPreference = null,
        toEarn = 4500,
        fareBreakdown = null,
        timeAgo = "25 min ago",
        avatarUrl = null
    ),
    BookingRequestModel(
        id = "req_3",
        passengerName = "Elena Rodriguez",
        rating = 4.9,
        ridesCount = 28,
        seatsRequested = 3,
        distanceAway = "2.2 miles",
        pickupStation = "East Mall Terminal",
        routeStops = listOf("Delhi", "Gurugram"),
        date = "Oct 25",
        timeSlot = "10:00 AM - 11:15 AM",
        isWholeCar = false,
        seatPreference = null,
        toEarn = 1200,
        fareBreakdown = null,
        timeAgo = "1 hour ago",
        avatarUrl = null
    )
)

private fun getInitialHistory() = listOf(
    HistoryRideModel("h1", "Mumbai", "Pune", "15 May, 09:00 AM", 450, "Completed"),
    HistoryRideModel("h2", "Bangalore", "Mysore", "12 May, 06:30 AM", 320, "Completed"),
    HistoryRideModel("h3", "Delhi", "Agra", "10 May, 08:00 AM", 500, "Cancelled")
)
