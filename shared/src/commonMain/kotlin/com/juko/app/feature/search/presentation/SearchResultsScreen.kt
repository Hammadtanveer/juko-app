package com.juko.app.feature.search.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.*
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
import com.juko.app.core.data.RideStateManager
import com.juko.app.core.model.RouteLocation
import com.juko.app.core.model.SearchRideItem
import com.juko.app.core.presentation.components.JukoAvatar
import com.juko.app.core.presentation.theme.LocalSpacing
import com.juko.app.feature.profile.presentation.ProfileRole
import com.juko.app.feature.profile.presentation.PublicUserProfileScreen

data class SearchResultsScreen(
    val origin: String = "Delhi",
    val destination: String = "Seohara",
    val date: String = "Today",
    val passengers: Int = 1
) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val spacing = LocalSpacing.current

        var selectedSortOption by remember { mutableStateOf(SearchSortOption.EARLIEST_DEPARTURE) }
        var verifiedProfileOnly by remember { mutableStateOf(false) }
        var showFilterDialog by remember { mutableStateOf(false) }

        val primaryBlue = Color(0xFF0052CC)
        val publishedRidesState by RideStateManager.publishedRides.collectAsState()
        var currentOrigin by remember { mutableStateOf(origin) }
        var currentDestination by remember { mutableStateOf(destination) }

        val rawResults = remember(publishedRidesState, currentOrigin, currentDestination) {
            RideStateManager.searchRides(currentOrigin, currentDestination)
        }

        val searchResults = remember(rawResults, selectedSortOption, verifiedProfileOnly) {
            var list = rawResults
            if (verifiedProfileOnly) {
                list = list.filter { it.isDriverVerified }
            }
            when (selectedSortOption) {
                SearchSortOption.EARLIEST_DEPARTURE -> list.sortedBy { parseTimeToMinutes(it.departureTime) }
                SearchSortOption.LOWEST_PRICE -> list.sortedBy { it.price }
                SearchSortOption.CLOSE_TO_DEPARTURE -> list.sortedBy { it.departureDistanceKm }
                SearchSortOption.CLOSE_TO_ARRIVAL -> list.sortedBy { it.arrivalDistanceKm }
            }
        }

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
                        // Header
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            IconButton(onClick = { navigator.pop() }) {
                                Icon(
                                    Icons.AutoMirrored.Outlined.ArrowBack,
                                    contentDescription = "Back",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Text(
                                text = "Search Results",
                                style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Box(contentAlignment = Alignment.TopEnd) {
                                IconButton(onClick = {
                                    navigator.push(com.juko.app.feature.notifications.presentation.NotificationsScreen())
                                }) {
                                    Icon(
                                        Icons.Outlined.Notifications,
                                        contentDescription = "Alerts",
                                        tint = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .padding(top = 8.dp, end = 8.dp)
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                        .padding(1.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFEF4444))
                                )
                            }
                        }

                        // Route Summary Box
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = spacing.xs),
                            color = Color(0xFFE0E8FF),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(spacing.md),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(spacing.xs)
                                    ) {
                                        Text(
                                            text = currentOrigin.ifBlank { "All Origins" },
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Icon(
                                            Icons.AutoMirrored.Outlined.ArrowForward,
                                            contentDescription = "to",
                                            modifier = Modifier.size(16.dp),
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = currentDestination.ifBlank { "All Destinations" },
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    Text(
                                        text = "$date • $passengers ${if (passengers == 1) "Passenger" else "Passengers"} • ${searchResults.size} rides",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                IconButton(
                                    onClick = { navigator.pop() },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                ) {
                                    Icon(
                                        Icons.Outlined.Edit,
                                        contentDescription = "Edit Search",
                                        tint = primaryBlue,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        // Filter / Sort Bar: Strictly SORT BY + TRUST AND SAFETY
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = spacing.sm),
                            horizontalArrangement = Arrangement.spacedBy(spacing.xs)
                        ) {
                            // 1. Sort By Trigger (opens dialog)
                            item {
                                FilterChipPill(
                                    label = "Sort: ${selectedSortOption.title}",
                                    icon = Icons.Outlined.Sort,
                                    hasDropdown = true,
                                    isSelected = true,
                                    onClick = { showFilterDialog = true }
                                )
                            }

                            // 2. Trust & Safety: Verified profile
                            item {
                                FilterChipPill(
                                    label = "Verified Profile",
                                    icon = if (verifiedProfileOnly) Icons.Outlined.CheckCircle else Icons.Outlined.VerifiedUser,
                                    isSelected = verifiedProfileOnly,
                                    onClick = { verifiedProfileOnly = !verifiedProfileOnly }
                                )
                            }

                            // 3. Quick Sort: Earliest departure
                            item {
                                FilterChipPill(
                                    label = "Earliest departure",
                                    isSelected = selectedSortOption == SearchSortOption.EARLIEST_DEPARTURE,
                                    onClick = { selectedSortOption = SearchSortOption.EARLIEST_DEPARTURE }
                                )
                            }

                            // 4. Quick Sort: Lowest price
                            item {
                                FilterChipPill(
                                    label = "Lowest price",
                                    isSelected = selectedSortOption == SearchSortOption.LOWEST_PRICE,
                                    onClick = { selectedSortOption = SearchSortOption.LOWEST_PRICE }
                                )
                            }

                            // 5. Quick Sort: Close to departure point
                            item {
                                FilterChipPill(
                                    label = "Close to departure point",
                                    isSelected = selectedSortOption == SearchSortOption.CLOSE_TO_DEPARTURE,
                                    onClick = { selectedSortOption = SearchSortOption.CLOSE_TO_DEPARTURE }
                                )
                            }

                            // 7. Quick Sort: Close to arrival point
                            item {
                                FilterChipPill(
                                    label = "Close to arrival point",
                                    isSelected = selectedSortOption == SearchSortOption.CLOSE_TO_ARRIVAL,
                                    onClick = { selectedSortOption = SearchSortOption.CLOSE_TO_ARRIVAL }
                                )
                            }
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
                contentPadding = PaddingValues(top = spacing.md, bottom = 48.dp),
                verticalArrangement = Arrangement.spacedBy(spacing.md)
            ) {
                if (searchResults.isEmpty()) {
                    item {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = spacing.xl),
                            shape = RoundedCornerShape(16.dp),
                            color = Color.White,
                            shadowElevation = 1.dp,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE8EDFF))
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFF1F5FE)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Outlined.SearchOff,
                                        contentDescription = null,
                                        tint = primaryBlue,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                                Text(
                                    text = "No rides found",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (verifiedProfileOnly)
                                        "No rides match your criteria with verified profiles. Try unchecking 'Verified Profile' to see more drivers."
                                    else
                                        "No available rides matched '${currentOrigin.ifBlank { "Any" }} → ${currentDestination.ifBlank { "Any" }}'. Try searching from a nearby stop or view all available rides.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF737685),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                                Button(
                                    onClick = {
                                        currentOrigin = ""
                                        currentDestination = ""
                                        verifiedProfileOnly = false
                                        selectedSortOption = SearchSortOption.EARLIEST_DEPARTURE
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = primaryBlue),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Reset Filters & View All Rides", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                } else {
                    items(searchResults) { ride ->
                        SearchResultCard(
                            ride = ride,
                            onClick = {
                                navigator.push(RideDetailsScreen(ride = ride))
                            },
                            onDriverClick = {
                                navigator.push(
                                    PublicUserProfileScreen(
                                        userName = ride.driverName,
                                        userAvatar = ride.driverAvatar,
                                        role = ProfileRole.DRIVER,
                                        rating = ride.driverRating,
                                        vehicleModel = ride.vehicleModel,
                                        vehiclePlate = ride.vehiclePlate
                                    )
                                )
                            }
                        )
                    }
                }
                item {
                    Text(
                        text = "POWERED BY JUKO TECHNOLOGIES",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = spacing.lg),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        letterSpacing = 2.sp
                    )
                }
            }
        }

        // Filter & Sort Bottom Sheet / Modal
        if (showFilterDialog) {
            FilterSortDialog(
                currentSort = selectedSortOption,
                verifiedOnly = verifiedProfileOnly,
                onApply = { newSort, newVerified ->
                    selectedSortOption = newSort
                    verifiedProfileOnly = newVerified
                },
                onDismiss = { showFilterDialog = false }
            )
        }
    }
}

@Composable
private fun FilterChipPill(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    hasDropdown: Boolean = false,
    isSelected: Boolean = false,
    onClick: () -> Unit
) {
    val primaryBlue = Color(0xFF0052CC)
    Surface(
        color = if (isSelected) Color(0xFFDAE2FF).copy(alpha = 0.5f) else Color.White,
        shape = RoundedCornerShape(percent = 50),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) primaryBlue.copy(alpha = 0.4f) else Color(0xFFE0E8FF)
        ),
        modifier = Modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (icon != null) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurface)
            }
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isSelected) primaryBlue else MaterialTheme.colorScheme.onSurface,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
            )
            if (hasDropdown) {
                Icon(
                    Icons.Outlined.ExpandMore,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun SearchResultCard(
    ride: SearchRideItem,
    onClick: () -> Unit,
    onDriverClick: () -> Unit = {}
) {
    val spacing = LocalSpacing.current
    val primaryBlue = Color(0xFF0052CC)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE8EDFF))
    ) {
        Column(
            modifier = Modifier.padding(spacing.md),
            verticalArrangement = Arrangement.spacedBy(spacing.sm)
        ) {
            // Top Section: Timeline & Price
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(spacing.md)) {
                    // Timeline Graphic
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .border(2.dp, primaryBlue, CircleShape)
                        )
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(40.dp)
                                .background(Color(0xFFE0E8FF))
                        )
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .border(2.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                        )
                    }

                    // Times & Locations
                    Column(verticalArrangement = Arrangement.SpaceBetween) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = ride.departureTime,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(spacing.xs))
                            Text(
                                text = ride.departureLocation,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Text(
                            text = "via ${ride.viaStops} • ${ride.duration}",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = ride.arrivalTime,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(spacing.xs))
                            Text(
                                text = ride.arrivalLocation,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Price
                Text(
                    text = "₹${ride.price}",
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 24.sp),
                    fontWeight = FontWeight.Bold,
                    color = primaryBlue
                )
            }

            HorizontalDivider(color = Color(0xFFE8EDFF), modifier = Modifier.padding(vertical = 4.dp))

            // Bottom Section: Driver & Seats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onDriverClick() }
                        .padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(spacing.sm)
                ) {
                    JukoAvatar(
                        imageUrl = ride.driverAvatar,
                        size = 38.dp
                    )
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = ride.driverName,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            if (ride.isDriverVerified) {
                                Icon(
                                    Icons.Outlined.CheckCircle,
                                    contentDescription = "Verified Driver",
                                    tint = Color(0xFF0284C7),
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Icon(
                                Icons.Outlined.Star,
                                contentDescription = null,
                                tint = Color(0xFF006844),
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = ride.driverRating.toString(),
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Surface(
                    color = if (ride.seatsLeft <= 1) Color(0xFFDFE0E0) else Color(0xFFDAE2FF).copy(alpha = 0.5f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Outlined.Group,
                            contentDescription = null,
                            tint = if (ride.seatsLeft <= 1) Color(0xFF5D5F5F) else primaryBlue,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "${ride.seatsLeft} ${if (ride.seatsLeft == 1) "seat left" else "seats left"}",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            fontWeight = FontWeight.SemiBold,
                            color = if (ride.seatsLeft <= 1) Color(0xFF5D5F5F) else primaryBlue
                        )
                    }
                }
            }
        }
    }
}

enum class SearchSortOption(val title: String) {
    EARLIEST_DEPARTURE("Earliest departure"),
    LOWEST_PRICE("Lowest price"),
    CLOSE_TO_DEPARTURE("Close to departure point"),
    CLOSE_TO_ARRIVAL("Close to arrival point")
}

private fun parseTimeToMinutes(timeStr: String): Int {
    val clean = timeStr.trim().uppercase()
    val isPm = clean.contains("PM")
    val isAm = clean.contains("AM")
    val raw = clean.replace("AM", "").replace("PM", "").trim()
    val parts = raw.split(":")
    val rawHour = parts.getOrNull(0)?.toIntOrNull() ?: 8
    val minute = parts.getOrNull(1)?.toIntOrNull() ?: 0
    val hour = when {
        isPm && rawHour < 12 -> rawHour + 12
        isAm && rawHour == 12 -> 0
        else -> rawHour
    }
    return hour * 60 + minute
}

@Composable
private fun FilterSortDialog(
    currentSort: SearchSortOption,
    verifiedOnly: Boolean,
    onApply: (SearchSortOption, Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    var tempSort by remember { mutableStateOf(currentSort) }
    var tempVerified by remember { mutableStateOf(verifiedOnly) }
    val primaryBlue = Color(0xFF0052CC)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filter & Sort",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                TextButton(
                    onClick = {
                        tempSort = SearchSortOption.EARLIEST_DEPARTURE
                        tempVerified = false
                    }
                ) {
                    Text("Reset", color = primaryBlue, fontWeight = FontWeight.SemiBold)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Section 1: SORT BY
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "SORT BY",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF737685)
                    )
                    SearchSortOption.entries.forEach { option ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { tempSort = option }
                                .padding(vertical = 4.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = option.title,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (tempSort == option) FontWeight.Bold else FontWeight.Normal,
                                color = if (tempSort == option) primaryBlue else MaterialTheme.colorScheme.onSurface
                            )
                            RadioButton(
                                selected = tempSort == option,
                                onClick = { tempSort = option },
                                colors = RadioButtonDefaults.colors(selectedColor = primaryBlue)
                            )
                        }
                    }
                }

                HorizontalDivider(color = Color(0xFFE8EDFF))

                // Section 2: TRUST & SAFETY
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "TRUST AND SAFETY",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF737685)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { tempVerified = !tempVerified }
                            .padding(vertical = 6.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                Icons.Outlined.VerifiedUser,
                                contentDescription = null,
                                tint = if (tempVerified) primaryBlue else Color(0xFF737685),
                                modifier = Modifier.size(20.dp)
                            )
                            Column {
                                Text(
                                    text = "Verified profile",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (tempVerified) FontWeight.Bold else FontWeight.Medium,
                                    color = if (tempVerified) primaryBlue else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Government ID & licence verified drivers",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = Color(0xFF737685)
                                )
                            }
                        }
                        Checkbox(
                            checked = tempVerified,
                            onCheckedChange = { tempVerified = it },
                            colors = CheckboxDefaults.colors(checkedColor = primaryBlue)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onApply(tempSort, tempVerified)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = primaryBlue),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Apply", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}


