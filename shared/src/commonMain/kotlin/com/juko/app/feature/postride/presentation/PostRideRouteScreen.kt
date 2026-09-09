package com.juko.app.feature.postride.presentation

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import com.juko.app.core.data.RideStateManager
import com.juko.app.core.presentation.components.JukoButton
import com.juko.app.core.presentation.components.JukoGhostButton
import com.juko.app.core.presentation.components.LocationAutocompleteRow
import com.juko.app.core.presentation.theme.LocalSpacing
import com.juko.app.feature.main.YourRidesTab
import com.juko.app.feature.sidebar.presentation.LocalDrawerController
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class PostRideRouteScreen : Screen {
    @Composable
    override fun Content() {
        val viewModel = getScreenModel<PostRideViewModel>()
        val state by viewModel.state.collectAsState()
        val navigator = LocalNavigator.currentOrThrow
        val tabNavigator = LocalTabNavigator.current
        val spacing = LocalSpacing.current
        val drawerController = LocalDrawerController.current
        val scrollState = rememberScrollState()
        val snackbarHostState = remember { SnackbarHostState() }
        val coroutineScope = rememberCoroutineScope()

        var showIncompleteProfileDialog by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    is PostRideSideEffect.ShowToast -> {
                        snackbarHostState.showSnackbar(effect.message)
                    }
                    is PostRideSideEffect.ShowError -> {
                        snackbarHostState.showSnackbar(effect.message)
                    }
                    is PostRideSideEffect.NavigateToPublishedDetail -> {
                        viewModel.onEvent(PostRideEvent.ResetForm)
                        RideStateManager.selectRidesTab(0)
                        tabNavigator.current = YourRidesTab
                    }
                    else -> {}
                }
            }
        }

        Scaffold(
            topBar = {
                Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
                    PostRideHeader(
                        onMenuOrBack = {
                            if (navigator.canPop) navigator.pop() else drawerController.open()
                        },
                        isBack = navigator.canPop
                    )
                    StepIndicator(step = 1)
                }
            },
            bottomBar = {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 8.dp,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Column(
                        modifier = Modifier
                            .padding(spacing.md)
                            .navigationBarsPadding(),
                        verticalArrangement = Arrangement.spacedBy(spacing.xs)
                    ) {
                        JukoButton(
                            text = "Continue to Ride Details",
                            onClick = {
                                val validationError = viewModel.validateStep1()
                                if (validationError != null) {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar(validationError)
                                    }
                                } else if (!com.juko.app.feature.profile.domain.DriverProfileManager.isProfileCompleteForPublishing()) {
                                    showIncompleteProfileDialog = true
                                } else {
                                    navigator.push(PostRideDetailsScreen(viewModel))
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp)
                        )
                        JukoGhostButton(
                            text = "Save as Draft",
                            onClick = { viewModel.onEvent(PostRideEvent.SaveDraft) }
                        )
                    }
                }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
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
                RouteTimelineCard(
                    state = state,
                    onEvent = viewModel::onEvent
                )

                ScheduleCard(
                    state = state,
                    onEvent = viewModel::onEvent
                )

                DestinationPricingBreakdown(
                    state = state,
                    onEvent = viewModel::onEvent
                )

                Spacer(modifier = Modifier.height(spacing.xl))
            }
        }

        if (showIncompleteProfileDialog) {
            ProfileIncompleteDialog(
                onDismiss = { showIncompleteProfileDialog = false },
                onCompleteClick = {
                    showIncompleteProfileDialog = false
                    navigator.push(com.juko.app.feature.profile.presentation.ProfileScreen(fromPublishRide = true))
                }
            )
        }
    }
}

@Composable
private fun ProfileIncompleteDialog(
    onDismiss: () -> Unit,
    onCompleteClick: () -> Unit
) {
    val spacing = LocalSpacing.current
    val primaryBlue = Color(0xFF0052CC)
    val manager = com.juko.app.feature.profile.domain.DriverProfileManager

    val isPhoneValid = manager.phoneNumber.length == 10
    val isLicenceValid = manager.frontLicenceUri != null || manager.backLicenceUri != null
    val isVehicleValid = manager.vehicles.isNotEmpty()

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFEF3C7)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.WarningAmber,
                    contentDescription = null,
                    tint = Color(0xFFD97706),
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        title = {
            Text(
                text = "Complete Driver Profile First",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(spacing.sm)
            ) {
                Text(
                    text = "To publish a ride and accept passengers, please complete your driver profile requirements:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF1F3FF),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(spacing.md),
                        verticalArrangement = Arrangement.spacedBy(spacing.xs)
                    ) {
                        RequirementItem(
                            title = "10-Digit Verified Phone",
                            isComplete = isPhoneValid
                        )
                        RequirementItem(
                            title = "Driver Licence Photo",
                            isComplete = isLicenceValid
                        )
                        RequirementItem(
                            title = "Registered Vehicle Details",
                            isComplete = isVehicleValid
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onCompleteClick,
                colors = ButtonDefaults.buttonColors(containerColor = primaryBlue),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Outlined.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Complete Profile Now", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Not Now, Go Back")
            }
        }
    )
}

@Composable
private fun RequirementItem(
    title: String,
    isComplete: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            if (isComplete) Icons.Outlined.CheckCircle else Icons.Outlined.Cancel,
            contentDescription = null,
            tint = if (isComplete) Color(0xFF006844) else Color(0xFFBA1A1A),
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isComplete) FontWeight.Medium else FontWeight.SemiBold,
            color = if (isComplete) Color(0xFF006844) else Color(0xFFBA1A1A)
        )
    }
}

@Composable
private fun PostRideHeader(
    onMenuOrBack: () -> Unit,
    isBack: Boolean
) {
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
            IconButton(onClick = onMenuOrBack) {
                if (isBack) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                } else {
                    Icon(Icons.Outlined.Menu, contentDescription = "Menu", tint = MaterialTheme.colorScheme.primary)
                }
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
private fun RouteTimelineCard(state: PostRideState, onEvent: (PostRideEvent) -> Unit) {
    val spacing = LocalSpacing.current
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(spacing.md)) {
            // Origin / Start Point
            LocationAutocompleteRow(
                label = "START LOCATION",
                city = state.origin,
                onQueryChange = { onEvent(PostRideEvent.OriginQueryChanged(it)) },
                suggestions = state.originSuggestions,
                onSelectSuggestion = { onEvent(PostRideEvent.OriginSelected(it)) },
                isConfirmed = state.originLocation?.isConfirmed == true,
                icon = Icons.Outlined.TripOrigin,
                iconColor = MaterialTheme.colorScheme.primary,
                showTrack = true,
                placeholder = "Enter departure city/place"
            )

            // Pickup Points (formerly "Stops")
            state.pickupPoints.forEachIndexed { index, pickupPoint ->
                LocationAutocompleteRow(
                    label = "PICKUP POINT ${index + 1}",
                    city = pickupPoint.name,
                    onQueryChange = { onEvent(PostRideEvent.PickupQueryChanged(index, it)) },
                    suggestions = state.activePickupSuggestions[index] ?: emptyList(),
                    onSelectSuggestion = { onEvent(PostRideEvent.PickupSelected(index, it)) },
                    isConfirmed = pickupPoint.isConfirmed,
                    icon = Icons.Outlined.Circle,
                    iconSize = 12.dp,
                    iconColor = Color(0xFF0052CC),
                    showTrack = true,
                    onRemove = { onEvent(PostRideEvent.RemovePickupPoint(index)) },
                    placeholder = "Enter intermediate pickup location"
                )
            }

            // Final Destination
            LocationAutocompleteRow(
                label = "FINAL DESTINATION",
                city = state.destination,
                onQueryChange = { onEvent(PostRideEvent.DestinationQueryChanged(it)) },
                suggestions = state.destinationSuggestions,
                onSelectSuggestion = { onEvent(PostRideEvent.DestinationSelected(it)) },
                isConfirmed = state.destinationLocation?.isConfirmed == true,
                icon = Icons.Outlined.LocationOn,
                iconColor = Color(0xFF36B37E),
                showTrack = false,
                placeholder = "Enter final destination"
            )

            Spacer(modifier = Modifier.height(spacing.md))

            // Add Pickup Point Button (Renamed from "Add Stop")
            OutlinedButton(
                onClick = { onEvent(PostRideEvent.AddPickupPoint) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color(0xFFC3C6D6))
            ) {
                Icon(Icons.Outlined.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(spacing.xs))
                Text("Add Pickup Point", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScheduleCard(state: PostRideState, onEvent: (PostRideEvent) -> Unit) {
    val spacing = LocalSpacing.current

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    // SelectableDates: Disable past dates (Only current epoch or future allowed)
    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val nowMillis = Clock.System.now().toEpochMilliseconds() - 86_400_000L
                return utcTimeMillis >= nowMillis
            }
        }
    )
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val instant = kotlinx.datetime.Instant.fromEpochMilliseconds(millis)
                        val localDate = instant.toLocalDateTime(TimeZone.currentSystemDefault()).date
                        val formattedDate = "${localDate.dayOfMonth} ${localDate.month.name.take(3).lowercase().replaceFirstChar { it.uppercase() }}"
                        onEvent(PostRideEvent.DepartureDateChanged(formattedDate))
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        val (parsedHour, parsedMinute) = remember(state.departureTime) {
            parseTimeString(state.departureTime)
        }
        val timePickerState = rememberTimePickerState(
            initialHour = parsedHour,
            initialMinute = parsedMinute,
            is24Hour = false
        )

        Dialog(
            onDismissRequest = { showTimePicker = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .padding(24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Select Departure Time",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )
                    TimePicker(state = timePickerState)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showTimePicker = false }) {
                            Text("Cancel")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(onClick = {
                            val hour = timePickerState.hour
                            val minute = timePickerState.minute
                            val amPm = if (hour >= 12) "PM" else "AM"
                            val hour12 = if (hour % 12 == 0) 12 else hour % 12
                            val formattedTime = "${hour12.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')} $amPm"
                            onEvent(PostRideEvent.DepartureTimeChanged(formattedTime))
                            showTimePicker = false
                        }) {
                            Text("OK")
                        }
                    }
                }
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(spacing.md), verticalArrangement = Arrangement.spacedBy(spacing.md)) {
            ScheduleRow(
                title = "DEPARTURE",
                date = state.departureDate,
                time = state.departureTime,
                icon = Icons.Outlined.CalendarToday,
                onDateClick = { showDatePicker = true },
                onTimeClick = { showTimePicker = true }
            )
            HorizontalDivider(color = Color(0xFFF4F5F7))
            
            // Auto-Calculated Arrival on the basis of route with Google API
            Column(verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("ESTIMATED ARRIVAL", style = MaterialTheme.typography.labelSmall, color = Color(0xFF737685))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Outlined.AutoAwesome,
                            contentDescription = null,
                            tint = Color(0xFF0052CC),
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "Calculated via Route",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                            color = Color(0xFF0052CC),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing.lg)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f).padding(vertical = 4.dp)
                    ) {
                        Icon(Icons.Outlined.CalendarToday, contentDescription = null, tint = Color(0xFF737685), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(spacing.xs))
                        Text(
                            text = state.arrivalDate.ifBlank { state.departureDate.ifBlank { "Date" } },
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f).padding(vertical = 4.dp)
                    ) {
                        Icon(Icons.Outlined.AccessTime, contentDescription = null, tint = Color(0xFF737685), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(spacing.xs))
                        Text(
                            text = state.arrivalTime.ifBlank { "--:-- --" },
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Surface(
                color = Color(0xFFE3FCEF),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = spacing.sm, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.Timer, contentDescription = null, tint = Color(0xFF006644), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(spacing.xs))
                    Text(
                        text = "Approx. journey time: ${state.journeyTime}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF006644),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun ScheduleRow(
    title: String,
    date: String,
    time: String,
    icon: ImageVector,
    onDateClick: () -> Unit,
    onTimeClick: () -> Unit
) {
    val spacing = LocalSpacing.current
    Column(verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
        Text(title, style = MaterialTheme.typography.labelSmall, color = Color(0xFF737685))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(spacing.lg)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f).clickable { onDateClick() }.padding(vertical = 4.dp)
            ) {
                Icon(icon, contentDescription = null, tint = Color(0xFF737685), modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(spacing.xs))
                Text(date.ifBlank { "Select date" }, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f).clickable { onTimeClick() }.padding(vertical = 4.dp)
            ) {
                Icon(Icons.Outlined.AccessTime, contentDescription = null, tint = Color(0xFF737685), modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(spacing.xs))
                Text(time.ifBlank { "Select time" }, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

/**
 * Destination-anchored Route Pricing:
 * Shows each boarding point -> Final Destination pricing card.
 * (e.g., Seohara -> Delhi: ₹450, Noorpur -> Delhi: ₹350, Chandpur -> Delhi: ₹250)
 */
@Composable
private fun DestinationPricingBreakdown(state: PostRideState, onEvent: (PostRideEvent) -> Unit) {
    val spacing = LocalSpacing.current
    val boardingPoints = listOf(state.origin) + state.pickupPoints.map { it.name }.filter { it.isNotBlank() }

    Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Route Pricing to Destination", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Surface(color = Color(0xFFE8EDFF), shape = RoundedCornerShape(4.dp)) {
                Text(
                    "FARE TO ${state.destination.uppercase()}",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.W800
                )
            }
        }

        boardingPoints.forEach { point ->
            val price = state.destinationPrices[point] ?: (if (point == state.origin) state.pricePerSeat else 250)
            DestinationPriceCard(
                fromPoint = point,
                toDestination = state.destination,
                price = price,
                onPriceChange = { newPrice ->
                    onEvent(PostRideEvent.DestinationPriceChanged(point, newPrice))
                }
            )
        }
    }
}

@Composable
private fun DestinationPriceCard(
    fromPoint: String,
    toDestination: String,
    price: Int,
    onPriceChange: (Int) -> Unit
) {
    val spacing = LocalSpacing.current
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(spacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = fromPoint,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        Icons.Outlined.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = Color(0xFF737685)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = toDestination,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF5D5F5F)
                    )
                }
                Text(
                    text = "Price per seat to final destination",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF737685)
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(spacing.sm)) {
                IconButton(
                    onClick = { if (price >= 50) onPriceChange(price - 50) else onPriceChange(0) },
                    modifier = Modifier.border(1.dp, Color(0xFFC3C6D6), CircleShape).size(28.dp)
                ) {
                    Icon(Icons.Outlined.Remove, contentDescription = "Decrease price", modifier = Modifier.size(14.dp))
                }

                Surface(
                    color = Color(0xFFF4F5F7),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color(0xFFE0E8FF))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "₹",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        BasicTextField(
                            value = if (price == 0) "" else price.toString(),
                            onValueChange = { input ->
                                val digitsOnly = input.filter { it.isDigit() }
                                val newPrice = digitsOnly.toIntOrNull() ?: 0
                                onPriceChange(newPrice)
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            ),
                            textStyle = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            singleLine = true,
                            modifier = Modifier.width(IntrinsicSize.Min)
                        )
                    }
                }

                IconButton(
                    onClick = { onPriceChange(price + 50) },
                    modifier = Modifier.background(MaterialTheme.colorScheme.primary, CircleShape).size(28.dp)
                ) {
                    Icon(Icons.Outlined.Add, contentDescription = "Increase price", tint = Color.White, modifier = Modifier.size(14.dp))
                }
            }
        }
    }
}

private fun parseTimeString(timeStr: String): Pair<Int, Int> {
    return try {
        val trimmed = timeStr.trim()
        val parts = trimmed.split(" ")
        val timePart = parts[0]
        val isPm = parts.getOrNull(1)?.equals("PM", ignoreCase = true) == true
        val isAm = parts.getOrNull(1)?.equals("AM", ignoreCase = true) == true
        val timeTokens = timePart.split(":")
        var hour = timeTokens[0].toIntOrNull() ?: 8
        val minute = timeTokens.getOrNull(1)?.toIntOrNull() ?: 0
        if (isPm && hour < 12) hour += 12
        if (isAm && hour == 12) hour = 0
        Pair(hour, minute)
    } catch (_: Exception) {
        Pair(8, 0)
    }
}
