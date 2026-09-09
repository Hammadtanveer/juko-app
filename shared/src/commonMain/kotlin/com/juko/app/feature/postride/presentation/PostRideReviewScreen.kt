package com.juko.app.feature.postride.presentation

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import com.juko.app.core.data.RideStateManager
import com.juko.app.core.presentation.components.JukoButton
import com.juko.app.core.presentation.theme.LocalSpacing
import com.juko.app.feature.main.SearchTab
import com.juko.app.feature.main.YourRidesTab

/**
 * Step 3: Review & Publish Screen
 * Allows drivers to review their full route, intermediate pickup points,
 * destination-anchored pricing, schedule, vehicle, and preferences before publishing.
 * Provides granular [Edit] buttons returning to Step 1 or Step 2 with data preserved.
 */
class PostRideReviewScreen(
    private val sharedViewModel: PostRideViewModel
) : Screen {

    @Composable
    override fun Content() {
        val state by sharedViewModel.state.collectAsState()
        val navigator = LocalNavigator.currentOrThrow
        val tabNavigator = LocalTabNavigator.current
        val spacing = LocalSpacing.current
        val scrollState = rememberScrollState()

        LaunchedEffect(Unit) {
            sharedViewModel.effect.collect { effect ->
                when (effect) {
                    PostRideSideEffect.NavigateToHome -> {
                        tabNavigator.current = SearchTab
                        navigator.popUntilRoot()
                    }
                    is PostRideSideEffect.NavigateToPublishedDetail -> {
                        sharedViewModel.onEvent(PostRideEvent.ResetForm)
                        navigator.popUntilRoot()
                        RideStateManager.selectRidesTab(0)
                        tabNavigator.current = YourRidesTab
                    }
                    is PostRideSideEffect.ShowError -> { /* Handled via snackbar/state */ }
                    is PostRideSideEffect.ShowToast -> { /* Handled via toast */ }
                }
            }
        }

        Scaffold(
            topBar = {
                Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
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
                                Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                            }
                            Spacer(modifier = Modifier.width(spacing.xs))
                            Text(
                                text = "Review & Publish",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // 3-Step Indicator on Review Screen
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = spacing.edgeMargin, vertical = spacing.sm),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        StepPill(text = "1 ROUTE & PRICING", isActive = false, modifier = Modifier.weight(1f))
                        Box(modifier = Modifier.width(12.dp).height(1.dp).background(Color(0xFFC3C6D6)))
                        StepPill(text = "2 DETAILS", isActive = false, modifier = Modifier.weight(1f))
                        Box(modifier = Modifier.width(12.dp).height(1.dp).background(Color(0xFFC3C6D6)))
                        StepPill(text = "3 REVIEW", isActive = true, modifier = Modifier.weight(1f))
                    }
                }
            },
            bottomBar = {
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
                            onClick = { sharedViewModel.onEvent(PostRideEvent.SaveDraft) },
                            modifier = Modifier.weight(1f).height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                        ) {
                            Text("Save Draft", fontWeight = FontWeight.SemiBold)
                        }
                        JukoButton(
                            text = "Publish Ride",
                            onClick = { sharedViewModel.onEvent(PostRideEvent.Submit) },
                            isLoading = state.isLoading,
                            modifier = Modifier.weight(1.5f).height(52.dp),
                            shape = RoundedCornerShape(12.dp)
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
                    .verticalScroll(scrollState)
                    .padding(spacing.md),
                verticalArrangement = Arrangement.spacedBy(spacing.lg)
            ) {
                // Section 1: Route & Schedule Review (with [Edit] -> Pop to Step 1)
                ReviewCard(
                    title = "Route & Schedule",
                    icon = Icons.Outlined.AltRoute,
                    onEditClick = {
                        // Pop twice back to Step 1
                        if (navigator.size > 2) {
                            navigator.popUntil { it is PostRideRouteScreen }
                        } else {
                            navigator.pop()
                        }
                    }
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        // Chronological Route Timeline
                        RoutePointItem(
                            label = "ORIGIN",
                            name = state.origin,
                            isSource = true,
                            showLine = state.pickupPoints.isNotEmpty() || state.destination.isNotBlank()
                        )
                        state.pickupPoints.forEachIndexed { index, point ->
                            RoutePointItem(
                                label = "PICKUP POINT ${index + 1}",
                                name = point.name,
                                isPickup = true,
                                showLine = true
                            )
                        }
                        RoutePointItem(
                            label = "DESTINATION",
                            name = state.destination,
                            isDest = true,
                            showLine = false
                        )

                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = Color(0xFFF1F3FF))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("DEPARTURE", style = MaterialTheme.typography.labelSmall, color = Color(0xFF737685))
                                Text("${state.departureDate} at ${state.departureTime}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("ARRIVAL", style = MaterialTheme.typography.labelSmall, color = Color(0xFF737685))
                                Text("${state.arrivalDate} at ${state.arrivalTime}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Section 2: Destination-Anchored Pricing Review (with [Edit] -> Pop to Step 1)
                ReviewCard(
                    title = "Destination Pricing",
                    icon = Icons.Outlined.Payments,
                    onEditClick = {
                        if (navigator.size > 2) {
                            navigator.popUntil { it is PostRideRouteScreen }
                        } else {
                            navigator.pop()
                        }
                    }
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        val boardingPoints = listOf(state.origin) + state.pickupPoints.map { it.name }.filter { it.isNotBlank() }
                        boardingPoints.forEach { point ->
                            val fare = state.destinationPrices[point] ?: (if (point == state.origin) state.pricePerSeat else 250)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFF9FAFB), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(point, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(Icons.Outlined.ArrowForward, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color(0xFF737685))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(state.destination, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF5D5F5F))
                                }
                                Text("₹$fare / seat", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }

                // Section 3: Seats Offered Review (with [Edit] -> Pop to Step 2)
                ReviewCard(
                    title = "Seats Offered",
                    icon = Icons.Outlined.EventSeat,
                    onEditClick = { navigator.pop() }
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("${state.availableSeats} Passenger Seats Offered", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                            }
                            Surface(color = Color(0xFFE3FCEF), shape = RoundedCornerShape(6.dp)) {
                                Text(
                                    "${state.availableSeats} SEATS",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF006644),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        if (state.isWholeCarBookingEnabled) {
                            HorizontalDivider(color = Color(0xFFF1F3FF))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Whole Car Booking Price", style = MaterialTheme.typography.bodySmall, color = Color(0xFF5D5F5F))
                                Text("₹${state.wholeCarPrice}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Section 4: Preferences Review (with [Edit] -> Pop to Step 2)
                ReviewCard(
                    title = "Ride Preferences",
                    icon = Icons.Outlined.Tune,
                    onEditClick = { navigator.pop() }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PreferenceBadge(label = "Auto Accept", isEnabled = state.autoAccept, icon = Icons.Outlined.Bolt)
                        PreferenceBadge(label = "Roof Rail", isEnabled = state.roofCarrierAvailable, icon = Icons.Outlined.Roofing)
                    }
                }

                Spacer(modifier = Modifier.height(spacing.xl))
            }
        }
    }
}

@Composable
private fun ReviewCard(
    title: String,
    icon: ImageVector,
    onEditClick: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, Color(0xFFE8EDFF))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                    Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                }
                TextButton(
                    onClick = onEditClick,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Icon(Icons.Outlined.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
            content()
        }
    }
}

@Composable
private fun RoutePointItem(
    label: String,
    name: String,
    isSource: Boolean = false,
    isPickup: Boolean = false,
    isDest: Boolean = false,
    showLine: Boolean = true
) {
    Row(modifier = Modifier.height(IntrinsicSize.Min)) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(28.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isSource -> MaterialTheme.colorScheme.primary
                            isDest -> Color(0xFF36B37E)
                            else -> Color(0xFF0052CC)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color.White))
            }
            if (showLine) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .width(2.dp)
                        .background(Color(0xFFC3C6D6))
                )
            }
        }
        Column(modifier = Modifier.padding(start = 8.dp, bottom = if (showLine) 12.dp else 0.dp)) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = Color(0xFF737685), fontSize = 10.sp)
            Text(name.ifBlank { "Unspecified" }, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun PreferenceBadge(
    label: String,
    isEnabled: Boolean,
    icon: ImageVector
) {
    Surface(
        color = if (isEnabled) Color(0xFFE8EDFF) else Color(0xFFF4F5F7),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, if (isEnabled) Color(0xFFC5D4FF) else Color(0xFFE0E0E0))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = if (isEnabled) MaterialTheme.colorScheme.primary else Color(0xFF9E9E9E),
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = if (isEnabled) MaterialTheme.colorScheme.primary else Color(0xFF737685),
                fontWeight = if (isEnabled) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
private fun StepPill(text: String, isActive: Boolean, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = if (isActive) MaterialTheme.colorScheme.primary else Color(0xFFF4F5F7),
        shape = RoundedCornerShape(percent = 50)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = if (isActive) Color.White else Color(0xFF737685),
            fontWeight = FontWeight.Bold,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}
