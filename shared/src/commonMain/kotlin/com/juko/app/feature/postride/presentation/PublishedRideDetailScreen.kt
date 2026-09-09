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
import kotlinx.coroutines.launch

/**
 * Dedicated Full Screen details for a Published Ride.
 * Accessible:
 * 1. Immediately following successful ride publishing (Step 3 redirect).
 * 2. By tapping any card in Your Rides -> PUBLISH tab.
 */
class PublishedRideDetailScreen(
    private val rideId: String
) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val tabNavigator = LocalTabNavigator.current
        val spacing = LocalSpacing.current
        val scrollState = rememberScrollState()
        val snackbarHostState = remember { SnackbarHostState() }
        val coroutineScope = rememberCoroutineScope()

        val publishedList by RideStateManager.publishedRides.collectAsState()
        val ride = publishedList.find { it.id == rideId }

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
                            IconButton(onClick = {
                                RideStateManager.selectRidesTab(0)
                                tabNavigator.current = YourRidesTab
                                if (navigator.canPop) {
                                    navigator.popUntilRoot()
                                }
                            }) {
                                Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                            }
                            Spacer(modifier = Modifier.width(spacing.xs))
                            Text(
                                text = "Published Ride Details",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (ride != null) {
                                if (ride.filledSeats == 0) {
                                    IconButton(
                                        onClick = {
                                            navigator.push(com.juko.app.feature.rides.presentation.EditPublishedRideScreen(ride.id))
                                        },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(Icons.Outlined.Edit, contentDescription = "Edit Ride", tint = primaryBlue, modifier = Modifier.size(20.dp))
                                    }
                                }
                                Surface(
                                    color = if (ride.status == "Active") Color(0xFFE3FCEF) else Color(0xFFFFF0B5),
                                    shape = RoundedCornerShape(percent = 50)
                                ) {
                                    Text(
                                        text = ride.status.uppercase(),
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (ride.status == "Active") Color(0xFF006644) else Color(0xFF974F0C),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
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
                            onClick = {
                                if (ride != null) {
                                    RideStateManager.deletePublishedRide(ride.id)
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Ride cancelled")
                                        RideStateManager.selectRidesTab(0)
                                        tabNavigator.current = YourRidesTab
                                        navigator.popUntilRoot()
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
                        ) {
                            Text("Cancel Ride", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                        }

                        JukoButton(
                            text = "Back to Rides",
                            onClick = {
                                RideStateManager.selectRidesTab(0)
                                tabNavigator.current = YourRidesTab
                                navigator.popUntilRoot()
                            },
                            modifier = Modifier.weight(1.2f).height(48.dp),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = MaterialTheme.colorScheme.background
        ) { padding ->
            if (ride == null) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Ride not found or removed", style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(scrollState)
                        .padding(spacing.md),
                    verticalArrangement = Arrangement.spacedBy(spacing.md)
                ) {
                    // Success Banner
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
                                modifier = Modifier.size(32.dp).clip(CircleShape).background(Color(0xFF006644)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Outlined.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                            Column {
                                Text(
                                    text = "Ride Published Successfully!",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF006644)
                                )
                                Text(
                                    text = "Passengers can now search and book seats on your route.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF006644).copy(alpha = 0.8f)
                                )
                            }
                        }
                    }

                    // Route Timeline Card
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
                                Text("Complete Route", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                Surface(color = Color(0xFFF1F5FE), shape = RoundedCornerShape(4.dp)) {
                                    Text(
                                        "${ride.routeLocations.size} STOPS",
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = primaryBlue,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            ride.routeLocations.forEachIndexed { index, loc ->
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
                                                        loc.isSource -> primaryBlue
                                                        loc.isDestination -> Color(0xFF36B37E)
                                                        else -> Color(0xFF0052CC)
                                                    }
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color.White))
                                        }
                                        if (index < ride.routeLocations.size - 1) {
                                            Box(modifier = Modifier.weight(1f).width(2.dp).background(Color(0xFFC3C6D6)))
                                        }
                                    }
                                    Column(modifier = Modifier.padding(start = 8.dp, bottom = if (index < ride.routeLocations.size - 1) 14.dp else 0.dp)) {
                                        Text(
                                            text = when {
                                                loc.isSource -> "START LOCATION"
                                                loc.isDestination -> "FINAL DESTINATION"
                                                else -> "PICKUP POINT $index"
                                            },
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color(0xFF737685),
                                            fontSize = 10.sp
                                        )
                                        Text(loc.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    // Destination-Anchored Pricing Breakdown
                    if (ride.destinationPrices.isNotEmpty()) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = Color.White,
                            shadowElevation = 2.dp,
                            border = BorderStroke(1.dp, Color(0xFFE8EDFF))
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Pricing to Destination", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                    Text("FARE", style = MaterialTheme.typography.labelSmall, color = Color(0xFF737685), fontWeight = FontWeight.Bold)
                                }

                                ride.destinationPrices.forEach { (point, fare) ->
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
                                            Text(ride.destination, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF5D5F5F))
                                        }
                                        Text("₹$fare / seat", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = primaryBlue)
                                    }
                                }
                            }
                        }
                    }

                    // Ride Info Card
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        shadowElevation = 2.dp,
                        border = BorderStroke(1.dp, Color(0xFFE8EDFF))
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("Ride Info", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("SEATS OFFERED", style = MaterialTheme.typography.labelSmall, color = Color(0xFF737685))
                                    Text("${ride.totalSeats} seats", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("STATUS", style = MaterialTheme.typography.labelSmall, color = Color(0xFF737685))
                                    Text(ride.status, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = if (ride.status == "Active") Color(0xFF006644) else Color(0xFF974F0C))
                                }
                            }

                            HorizontalDivider(color = Color(0xFFF1F3FF))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("DEPARTURE DATE & TIME", style = MaterialTheme.typography.labelSmall, color = Color(0xFF737685))
                                    Text(ride.dateTime, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(spacing.xl))
                }
            }
        }
    }
}
