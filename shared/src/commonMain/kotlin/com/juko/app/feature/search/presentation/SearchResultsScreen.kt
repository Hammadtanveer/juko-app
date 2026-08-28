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
import com.juko.app.core.presentation.components.JukoAvatar
import com.juko.app.core.presentation.theme.LocalSpacing

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

        var selectedSort by remember { mutableStateOf("Sort") }
        var selectedPriceFilter by remember { mutableStateOf<String?>(null) }
        var selectedTimeFilter by remember { mutableStateOf<String?>(null) }
        var selectedSeatsFilter by remember { mutableStateOf<String?>(null) }

        val primaryBlue = Color(0xFF0052CC)
        val searchResults = remember { getDummySearchResults(origin, destination) }

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
                            Box {
                                IconButton(onClick = { /* Notifications */ }) {
                                    Icon(
                                        Icons.Outlined.Notifications,
                                        contentDescription = "Alerts",
                                        tint = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .align(Alignment.TopEnd)
                                        .offset(x = (-10).dp, y = 10.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.error)
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
                                            text = origin.ifBlank { "Delhi" },
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
                                            text = destination.ifBlank { "Seohara" },
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    Text(
                                        text = "$date • $passengers ${if (passengers == 1) "Passenger" else "Passengers"}",
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

                        // Filter / Sort Bar
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = spacing.sm),
                            horizontalArrangement = Arrangement.spacedBy(spacing.xs)
                        ) {
                            item {
                                FilterChipPill(
                                    label = "Sort",
                                    icon = Icons.Outlined.Sort,
                                    isSelected = selectedSort != "Sort",
                                    onClick = { /* Handle sort */ }
                                )
                            }
                            item {
                                FilterChipPill(
                                    label = "Price",
                                    hasDropdown = true,
                                    isSelected = selectedPriceFilter != null,
                                    onClick = { /* Handle price */ }
                                )
                            }
                            item {
                                FilterChipPill(
                                    label = "Time",
                                    hasDropdown = true,
                                    isSelected = selectedTimeFilter != null,
                                    onClick = { /* Handle time */ }
                                )
                            }
                            item {
                                FilterChipPill(
                                    label = "Seats",
                                    hasDropdown = true,
                                    isSelected = selectedSeatsFilter != null,
                                    onClick = { /* Handle seats */ }
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
                items(searchResults) { ride ->
                    SearchResultCard(ride = ride, onClick = { /* TODO: Open Ride Details */ })
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
private fun SearchResultCard(ride: SearchRideItem, onClick: () -> Unit) {
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
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(spacing.sm)
                ) {
                    JukoAvatar(
                        imageUrl = ride.driverAvatar,
                        size = 38.dp
                    )
                    Column {
                        Text(
                            text = ride.driverName,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
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

data class SearchRideItem(
    val id: String,
    val departureTime: String,
    val departureLocation: String,
    val viaStops: String,
    val duration: String,
    val arrivalTime: String,
    val arrivalLocation: String,
    val price: Int,
    val driverName: String,
    val driverRating: Double,
    val driverAvatar: String?,
    val seatsLeft: Int
)

private fun getDummySearchResults(origin: String, destination: String): List<SearchRideItem> {
    val fromCity = origin.ifBlank { "Delhi" }
    val toCity = destination.ifBlank { "Seohara" }

    return listOf(
        SearchRideItem(
            id = "res_1",
            departureTime = "08:00",
            departureLocation = "$fromCity (ISBT)",
            viaStops = "Chandpur",
            duration = "4h 30m",
            arrivalTime = "12:30",
            arrivalLocation = toCity,
            price = 450,
            driverName = "Rahul S.",
            driverRating = 4.8,
            driverAvatar = "https://lh3.googleusercontent.com/aida-public/AB6AXuCKYEb47azE7KIsX7pIzB9mjz1RKlj_e8gPNrELvoNxr4a4ZbO81La7WXxWwGuBD-2oQWPHrwDuTRXv1G8uEuA-RFEFlIrMbuUqxPqEyND6lnxpPkr390ck8Lk66bqK1ziDQZwg5V9JSommvmFtM0wURjqHnMp9lErkm5-rTMsXV6xmevXkm-vngAc2TmsP1ntYMnk-QMM6UNewnh-dVrA9XA3G7Y1Td4TGZpheU9qWZsJ0O5IwK7ZU",
            seatsLeft = 2
        ),
        SearchRideItem(
            id = "res_2",
            departureTime = "08:00",
            departureLocation = "$fromCity (JMI)",
            viaStops = "Chandpur",
            duration = "4h 30m",
            arrivalTime = "12:30",
            arrivalLocation = "$toCity (Main Stand)",
            price = 380,
            driverName = "Anita K.",
            driverRating = 4.9,
            driverAvatar = "https://lh3.googleusercontent.com/aida-public/AB6AXuDjCFi6hSeikXO26byFKauht4PmxZK204AR3XCqdXpOuM5L__mJ2cTmXDvEcl_G59mMY5F1ZCFx3mDLA5t_LhbfFRCcVN0OADih56H0naDNOo8O80lHswNiCLVi9_wrMvpla3t4r3yZ9nfpKnmLpJJPO7F1Xqg4V1JoPCrzXjc5--k7En9ONj0L9ibdyah-3MncNX0gjvGcHgaPoTqSHFKGhFOl92QEL9O7jfz4ixs01mRBBK9SaEN0",
            seatsLeft = 1
        ),
        SearchRideItem(
            id = "res_3",
            departureTime = "08:00",
            departureLocation = "$fromCity (Kale Khan)",
            viaStops = "Chandpur",
            duration = "4h 30m",
            arrivalTime = "12:30",
            arrivalLocation = toCity,
            price = 500,
            driverName = "Vikram M.",
            driverRating = 4.6,
            driverAvatar = "https://lh3.googleusercontent.com/aida-public/AB6AXuAzRflz8ZOhS_ubE7CLsRLWaTuwXxOwkKmx_r9WvvYDeKeKoSu81e41n0dn2HdioLA3eRHuc6VwnblnJi0CnpVfPZ5DZxOmz5jI4RpxKGlELzGrNG2u-G5Bxyuk0VJ4Yo16GPZ4SDli21_imo90sbVbK2XKuJt7TUQeW19Uibm3ugwJupwHgjVaeizjdwVpAxl4f8UXwCAAztao3hRZBLxFL16rd4xuUufYf6aSYTey65FLymTlVpUi",
            seatsLeft = 3
        )
    )
}
