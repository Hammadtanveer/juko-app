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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
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
                    is PostRideSideEffect.NavigateToPublishedDetail -> {
                        sharedViewModel.onEvent(PostRideEvent.ResetForm)
                        navigator.popUntilRoot()
                        RideStateManager.selectRidesTab(0)
                        tabNavigator.current = YourRidesTab
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
                            text = "Continue to Review",
                            onClick = {
                                navigator.push(PostRideReviewScreen(sharedViewModel))
                            },
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
                // Capacity Card with 1 to 6 seats stepper
                CapacityCard(state = state, onEvent = sharedViewModel::onEvent)

                // Seat Preferences
                SeatPreferencesCard(state = state, onEvent = sharedViewModel::onEvent)

                // Whole Car Booking
                WholeCarBookingCard(state = state, onEvent = sharedViewModel::onEvent)

                // Additional Preferences
                AdditionalPreferencesCard(state = state, onEvent = sharedViewModel::onEvent)

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
                text = "Publish Ride",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        IconButton(onClick = { /* Notifications */ }) {
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
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        StepPill(text = "1 ROUTE & PRICING", isActive = step == 1, modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier
                .width(12.dp)
                .height(1.dp)
                .background(Color(0xFFC3C6D6))
        )
        StepPill(text = "2 DETAILS", isActive = step == 2, modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier
                .width(12.dp)
                .height(1.dp)
                .background(Color(0xFFC3C6D6))
        )
        StepPill(text = "3 REVIEW", isActive = step == 3, modifier = Modifier.weight(1f))
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

@Composable
private fun CapacityCard(state: PostRideState, onEvent: (PostRideEvent) -> Unit) {
    val canDecrease = state.availableSeats > 1
    val canIncrease = state.availableSeats < 6
    val primaryColor = MaterialTheme.colorScheme.primary

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "SEATS OFFERED",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF475569)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Offer between 1 and 6 passenger seats",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF64748B)
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                IconButton(
                    onClick = { if (canDecrease) onEvent(PostRideEvent.SeatsChanged(state.availableSeats - 1)) },
                    modifier = Modifier
                        .size(38.dp)
                        .border(
                            width = 1.2.dp,
                            color = if (canDecrease) Color(0xFFB0B8C8) else Color(0xFFE2E8F0),
                            shape = CircleShape
                        ),
                    enabled = canDecrease
                ) {
                    Icon(
                        Icons.Default.Remove,
                        contentDescription = "Decrease seats",
                        tint = if (canDecrease) Color(0xFF0F172A) else Color(0xFF94A3B8),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(
                    text = state.availableSeats.toString(),
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )

                IconButton(
                    onClick = { if (canIncrease) onEvent(PostRideEvent.SeatsChanged(state.availableSeats + 1)) },
                    modifier = Modifier
                        .size(38.dp)
                        .background(
                            color = if (canIncrease) primaryColor else Color(0xFFE2E8F0),
                            shape = CircleShape
                        ),
                    enabled = canIncrease
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Increase seats",
                        tint = if (canIncrease) Color.White else Color(0xFF94A3B8),
                        modifier = Modifier.size(20.dp)
                    )
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
                PriceAdjuster(
                    label = "Front Seat",
                    price = state.frontSeatPrice,
                    onPriceChange = { onEvent(PostRideEvent.FrontSeatPriceChanged(it)) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun PriceAdjuster(label: String, price: Int, onPriceChange: (Int) -> Unit, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = Color(0xFFF4F5F7),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = Color(0xFF737685))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { if (price >= 10) onPriceChange(price - 10) },
                    modifier = Modifier.size(28.dp).border(1.dp, Color(0xFFC3C6D6), CircleShape)
                ) {
                    Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(16.dp))
                }
                Text("₹$price", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                IconButton(
                    onClick = { onPriceChange(price + 10) },
                    modifier = Modifier.size(28.dp).background(MaterialTheme.colorScheme.primary, CircleShape)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }
        }
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
                        value = if (state.wholeCarPrice == 0) "" else state.wholeCarPrice.toString(),
                        onValueChange = { input ->
                            val digitsOnly = input.filter { it.isDigit() }.take(6)
                            val newVal = digitsOnly.toIntOrNull() ?: 0
                            onEvent(PostRideEvent.WholeCarPriceChanged(newVal))
                        },
                        prefix = {
                            Text(
                                "₹ ",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E1E1E)
                                )
                            )
                        },
                        placeholder = {
                            Text(
                                "0",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = Color(0xFFB0B3C1)
                                )
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        singleLine = true,
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
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("RIDE PREFERENCES", style = MaterialTheme.typography.labelSmall, color = Color(0xFF737685))
            PreferenceToggle(
                text = "Roof Rail / Carrier",
                checked = state.roofCarrierAvailable,
                onCheckedChange = { onEvent(PostRideEvent.ToggleRoofCarrier) }
            )
            HorizontalDivider(color = Color(0xFFF4F5F7))
            PreferenceToggle(
                text = "Auto Accept Booking",
                checked = state.autoAccept,
                onCheckedChange = { onEvent(PostRideEvent.ToggleAutoAccept) }
            )
        }
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
