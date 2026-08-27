package com.juko.app.feature.postride.presentation

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.juko.app.core.presentation.components.JukoButton
import com.juko.app.core.presentation.components.JukoGhostButton
import com.juko.app.core.presentation.theme.LocalSpacing
import kotlinx.datetime.toLocalDateTime

class PostRideRouteScreen : Screen {
    @Composable
    override fun Content() {
        val viewModel = getScreenModel<PostRideViewModel>()
        val state by viewModel.state.collectAsState()
        val navigator = LocalNavigator.currentOrThrow
        val spacing = LocalSpacing.current
        val scrollState = rememberScrollState()

        Scaffold(
            topBar = {
                Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
                    PostRideHeader(onBack = { navigator.pop() })
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
                            onClick = { navigator.push(PostRideDetailsScreen(viewModel)) },
                            modifier = Modifier.fillMaxWidth().height(56.dp)
                        )
                        JukoGhostButton(
                            text = "Save as Draft",
                            onClick = { viewModel.onEvent(PostRideEvent.SaveDraft) }
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
                RouteTimelineCard(state = state, onEvent = viewModel::onEvent)
                ScheduleCard(state = state, onEvent = viewModel::onEvent)
                PricingBreakdown(state = state, onEvent = viewModel::onEvent)
                InfoCard()
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
private fun RouteTimelineCard(state: PostRideState, onEvent: (PostRideEvent) -> Unit) {
    val spacing = LocalSpacing.current
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(spacing.md)) {
            TimelineRow(
                label = "START",
                city = state.origin,
                onCityChange = { onEvent(PostRideEvent.OriginChanged(it)) },
                icon = Icons.Outlined.TripOrigin,
                iconColor = MaterialTheme.colorScheme.primary,
                showTrack = true,
                placeholder = "Departure city"
            )
            state.stops.forEachIndexed { index, stop ->
                TimelineRow(
                    label = "STOP ${index + 1}",
                    city = stop,
                    onCityChange = { onEvent(PostRideEvent.UpdateStop(index, it)) },
                    icon = Icons.Outlined.Circle,
                    iconSize = 12.dp,
                    showTrack = true,
                    onRemove = { onEvent(PostRideEvent.RemoveStop(index)) },
                    placeholder = "Intermediate city"
                )
            }
            TimelineRow(
                label = "END",
                city = state.destination,
                onCityChange = { onEvent(PostRideEvent.DestinationChanged(it)) },
                icon = Icons.Outlined.LocationOn,
                iconColor = Color(0xFF36B37E),
                showTrack = false,
                placeholder = "Destination city"
            )
            Spacer(modifier = Modifier.height(spacing.md))
            OutlinedButton(
                onClick = { onEvent(PostRideEvent.AddStop("")) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color(0xFFC3C6D6))
            ) {
                Icon(Icons.Outlined.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(spacing.xs))
                Text("Add Stop", color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}

@Composable
private fun TimelineRow(
    label: String,
    city: String,
    onCityChange: (String) -> Unit,
    icon: ImageVector,
    iconColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    iconSize: Dp = 20.dp,
    showTrack: Boolean = false,
    onRemove: (() -> Unit)? = null,
    placeholder: String = ""
) {
    val spacing = LocalSpacing.current
    Row(modifier = Modifier.height(IntrinsicSize.Min)) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(40.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .padding(top = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(iconSize))
            }
            if (showTrack) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .width(2.dp)
                        .background(Color(0xFFC3C6D6))
                )
            }
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = if (showTrack) spacing.md else 0.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(label, style = MaterialTheme.typography.labelSmall, color = Color(0xFF737685))
                    BasicTextField(
                        value = city,
                        onValueChange = onCityChange,
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        decorationBox = { innerTextField ->
                            Box {
                                if (city.isEmpty()) {
                                    Text(
                                        text = placeholder,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = Color(0xFFC3C6D6)
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )
                }
                if (onRemove != null) {
                    IconButton(onClick = onRemove, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Outlined.Close, contentDescription = "Remove", modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScheduleCard(state: PostRideState, onEvent: (PostRideEvent) -> Unit) {
    val spacing = LocalSpacing.current
    
    var showDatePickerFor by remember { mutableStateOf<String?>(null) }
    var showTimePickerFor by remember { mutableStateOf<String?>(null) }
    
    val datePickerState = rememberDatePickerState()
    val timePickerState = rememberTimePickerState()
    
    if (showDatePickerFor != null) {
        DatePickerDialog(
            onDismissRequest = { showDatePickerFor = null },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val instant = kotlinx.datetime.Instant.fromEpochMilliseconds(millis)
                        val localDate = instant.toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault()).date
                        val formattedDate = "${localDate.dayOfMonth} ${localDate.month.name.take(3).lowercase().replaceFirstChar { it.uppercase() }}"
                        if (showDatePickerFor == "DEPARTURE") {
                            onEvent(PostRideEvent.DepartureDateChanged(formattedDate))
                        } else {
                            onEvent(PostRideEvent.ArrivalDateChanged(formattedDate))
                        }
                    }
                    showDatePickerFor = null
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePickerFor = null }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePickerFor != null) {
        AlertDialog(
            onDismissRequest = { showTimePickerFor = null },
            confirmButton = {
                TextButton(onClick = {
                    val hour = timePickerState.hour
                    val minute = timePickerState.minute
                    val amPm = if (hour >= 12) "PM" else "AM"
                    val hour12 = if (hour % 12 == 0) 12 else hour % 12
                    val formattedTime = "${hour12.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')} $amPm"
                    
                    if (showTimePickerFor == "DEPARTURE") {
                        onEvent(PostRideEvent.DepartureTimeChanged(formattedTime))
                    } else {
                        onEvent(PostRideEvent.ArrivalTimeChanged(formattedTime))
                    }
                    showTimePickerFor = null
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePickerFor = null }) { Text("Cancel") }
            },
            text = {
                TimePicker(state = timePickerState)
            }
        )
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
                onDateClick = { showDatePickerFor = "DEPARTURE" },
                onTimeClick = { showTimePickerFor = "DEPARTURE" }
            )
            HorizontalDivider(color = Color(0xFFF4F5F7))
            ScheduleRow(
                title = "ARRIVAL",
                date = state.arrivalDate,
                time = state.arrivalTime,
                icon = Icons.Outlined.Schedule,
                onDateClick = { showDatePickerFor = "ARRIVAL" },
                onTimeClick = { showTimePickerFor = "ARRIVAL" }
            )
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
                Text(date, style = MaterialTheme.typography.bodyMedium)
            }
            Row(
                verticalAlignment = Alignment.CenterVertically, 
                modifier = Modifier.weight(1f).clickable { onTimeClick() }.padding(vertical = 4.dp)
            ) {
                Icon(Icons.Outlined.AccessTime, contentDescription = null, tint = Color(0xFF737685), modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(spacing.xs))
                Text(time, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun PricingBreakdown(state: PostRideState, onEvent: (PostRideEvent) -> Unit) {
    val spacing = LocalSpacing.current
    Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Route Pricing", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Surface(color = Color(0xFFE8EDFF), shape = RoundedCornerShape(4.dp)) {
                Text(
                    "PRICE PER SEAT",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.W800
                )
            }
        }
        state.segmentPrices.forEach { (segment, price) ->
            SegmentPriceCard(segment = segment, price = price, onPriceChange = { newPrice ->
                onEvent(PostRideEvent.SegmentPriceChanged(segment, newPrice))
            })
        }
    }
}

@Composable
private fun SegmentPriceCard(segment: String, price: Int, onPriceChange: (Int) -> Unit) {
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
            Text(segment, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(spacing.md)) {
                IconButton(
                    onClick = { if (price > 100) onPriceChange(price - 50) },
                    modifier = Modifier.border(1.dp, Color(0xFFC3C6D6), CircleShape).size(28.dp)
                ) {
                    Icon(Icons.Outlined.Remove, contentDescription = null, modifier = Modifier.size(14.dp))
                }
                Text("₹$price", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                IconButton(
                    onClick = { onPriceChange(price + 50) },
                    modifier = Modifier.background(MaterialTheme.colorScheme.primary, CircleShape).size(28.dp)
                ) {
                    Icon(Icons.Outlined.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                }
            }
        }
    }
}

@Composable
private fun InfoCard() {
    val spacing = LocalSpacing.current
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                drawLine(
                    color = Color(0xFF0052CC),
                    start = Offset(0f, 0f),
                    end = Offset(0f, size.height),
                    strokeWidth = 4.dp.toPx()
                )
            },
        color = Color(0xFFE8EDFF).copy(alpha = 0.5f),
        shape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp)
    ) {
        Column(modifier = Modifier.padding(spacing.md), verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Info, contentDescription = null, tint = Color(0xFF0052CC), modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(spacing.xs))
                Text("How passenger prices work", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Color(0xFF0052CC))
            }
            Text(
                "Passengers pay for the segments they travel. E.g., if someone joins for 'Mumbai → Lonavala', they pay ₹250. If someone travels the full route, they pay the sum of all segments.",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF434654)
            )
        }
    }
}
