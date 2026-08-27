package com.juko.app.feature.postride.presentation

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
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
import com.juko.app.core.presentation.components.JukoButton
import com.juko.app.core.presentation.theme.LocalSpacing
import com.juko.app.feature.main.SearchTab

class PostRideDetailsScreen(private val sharedViewModel: PostRideViewModel) : Screen {
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
                    is PostRideSideEffect.ShowError -> { /* Show error */ }
                    is PostRideSideEffect.ShowToast -> { /* Show toast */ }
                }
            }
        }

        Scaffold(
            topBar = {
                Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
                    PostRideHeader(onBack = { navigator.pop() })
                    StepIndicator(step = 2)
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
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                        ) {
                            Text("Save as Draft")
                        }
                        JukoButton(
                            text = "Review & Publish",
                            onClick = { sharedViewModel.onEvent(PostRideEvent.Submit) },
                            isLoading = state.isLoading,
                            modifier = Modifier.weight(1.4f).height(48.dp),
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
                // Capacity Card
                CapacityCard(state = state, onEvent = sharedViewModel::onEvent)

                // Seat Preferences
                SeatPreferencesCard(state = state, onEvent = sharedViewModel::onEvent)

                // Whole Car Booking
                WholeCarBookingCard(state = state, onEvent = sharedViewModel::onEvent)

                // Additional Preferences
                AdditionalPreferencesCard(state = state, onEvent = sharedViewModel::onEvent)

                // Ride Summary Card
                RideSummaryCard(state = state)

                Spacer(modifier = Modifier.height(spacing.xl))
            }
        }
    }
}

@Composable
private fun PostRideHeader(onBack: () -> Unit) {
    val spacing = LocalSpacing.current
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
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.width(spacing.xs))
            Text(
                text = "JUKO",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        IconButton(onClick = { /* TODO */ }) {
            Icon(Icons.Outlined.Notifications, contentDescription = "Notifications")
        }
    }
}

@Composable
private fun StepIndicator(step: Int) {
    val spacing = LocalSpacing.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.edgeMargin, vertical = spacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        StepPill(text = "1 ROUTE & PRICING", isActive = step == 1)
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(Color(0xFFC3C6D6))
        )
        StepPill(text = "2 RIDE DETAILS", isActive = step == 2)
    }
}

@Composable
private fun StepPill(text: String, isActive: Boolean) {
    val spacing = LocalSpacing.current
    Surface(
        color = if (isActive) MaterialTheme.colorScheme.primary else Color(0xFFF4F5F7),
        shape = RoundedCornerShape(percent = 50)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = spacing.sm, vertical = 6.dp),
            style = MaterialTheme.typography.labelSmall,
            color = if (isActive) Color.White else Color(0xFF737685),
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun CapacityCard(state: PostRideState, onEvent: (PostRideEvent) -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("AVAILABLE SEATS", style = MaterialTheme.typography.labelSmall, color = Color(0xFF5D5F5F))
                Text("How many passengers can you take?", style = MaterialTheme.typography.bodySmall, color = Color(0xFF737685))
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                IconButton(
                    onClick = { if (state.availableSeats > 1) onEvent(PostRideEvent.SeatsChanged(state.availableSeats - 1)) },
                    modifier = Modifier.border(1.dp, Color(0xFFC3C6D6), CircleShape).size(36.dp)
                ) {
                    Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(20.dp))
                }
                Text(state.availableSeats.toString(), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                IconButton(
                    onClick = { if (state.availableSeats < 8) onEvent(PostRideEvent.SeatsChanged(state.availableSeats + 1)) },
                    modifier = Modifier.background(MaterialTheme.colorScheme.primary, CircleShape).size(36.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

@Composable
private fun SeatPreferencesCard(state: PostRideState, onEvent: (PostRideEvent) -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.EventSeat, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Seat Preferences", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                }
                Switch(
                    checked = state.isSeatPreferencesEnabled,
                    onCheckedChange = { onEvent(PostRideEvent.ToggleSeatPreferences) }
                )
            }
            
            if (state.isSeatPreferencesEnabled) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    PriceAdjuster(
                        label = "Front Seat",
                        price = state.frontSeatPrice,
                        onValueChange = { onEvent(PostRideEvent.FrontSeatPriceChanged(it)) },
                        modifier = Modifier.weight(1f)
                    )
                    PriceAdjuster(
                        label = "Window Seat",
                        price = state.windowSeatPrice,
                        onValueChange = { onEvent(PostRideEvent.WindowSeatPriceChanged(it)) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun PriceAdjuster(label: String, price: Int, onValueChange: (Int) -> Unit, modifier: Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color(0xFF737685))
        OutlinedTextField(
            value = "+₹$price",
            onValueChange = { 
                val newVal = it.replace(Regex("[^0-9]"), "").toIntOrNull() ?: 0
                onValueChange(newVal)
            },
            modifier = Modifier.fillMaxWidth(),
            textStyle = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            shape = RoundedCornerShape(8.dp)
        )
    }
}

@Composable
private fun WholeCarBookingCard(state: PostRideState, onEvent: (PostRideEvent) -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.DirectionsCar, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Whole Car Booking", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                }
                Switch(
                    checked = state.isWholeCarBookingEnabled,
                    onCheckedChange = { onEvent(PostRideEvent.ToggleWholeCarBooking) }
                )
            }
            
            if (state.isWholeCarBookingEnabled) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("WHOLE CAR PRICE", style = MaterialTheme.typography.labelSmall, color = Color(0xFF737685))
                    OutlinedTextField(
                        value = "₹${state.wholeCarPrice}",
                        onValueChange = { 
                            val newVal = it.replace(Regex("[^0-9]"), "").toIntOrNull() ?: 0
                            onEvent(PostRideEvent.WholeCarPriceChanged(newVal))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AdditionalPreferencesCard(state: PostRideState, onEvent: (PostRideEvent) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        PreferenceToggle(
            text = "Roof Carrier Available",
            checked = state.roofCarrierAvailable,
            onCheckedChange = { onEvent(PostRideEvent.ToggleRoofCarrier) }
        )
        PreferenceToggle(
            text = "Auto Accept Booking",
            checked = state.autoAccept,
            onCheckedChange = { onEvent(PostRideEvent.ToggleAutoAccept) }
        )
    }
}

@Composable
private fun PreferenceToggle(text: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text, style = MaterialTheme.typography.bodyLarge)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun RideSummaryCard(state: PostRideState) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF041B3C),
        contentColor = Color.White
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("RIDE SUMMARY", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.6f))
                Surface(color = Color.White.copy(alpha = 0.1f), shape = RoundedCornerShape(4.dp)) {
                    Text("${state.stops.size} Stops", modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall)
                }
            }
            
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.TripOrigin, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(state.origin, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
                Box(modifier = Modifier.padding(start = 6.dp).width(2.dp).height(12.dp).background(Color.White.copy(alpha = 0.3f)))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.LocationOn, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFF36B37E))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(state.destination, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
            }
            
            HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Seats Available", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.6f))
                    Text(state.availableSeats.toString(), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Starting from", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.6f))
                    Text("₹${state.pricePerSeat}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                }
            }
            
            Surface(color = Color(0xFFE3FCEF), shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Info, contentDescription = null, tint = Color(0xFF006644), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Custom stop pricing active (3 segments)", style = MaterialTheme.typography.bodySmall, color = Color(0xFF006644), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
