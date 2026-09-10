package com.juko.app.feature.rides.presentation

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.juko.app.core.data.RideStateManager
import com.juko.app.core.location.DefaultPlacesAutocompleteService
import com.juko.app.core.location.PlaceLocation
import com.juko.app.core.presentation.components.JukoButton
import com.juko.app.core.presentation.theme.LocalSpacing
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * Screen to edit an existing published ride when no seats have been booked yet.
 */
data class EditPublishedRideScreen(val rideId: String) : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val spacing = LocalSpacing.current
        val coroutineScope = rememberCoroutineScope()
        val snackbarHostState = remember { SnackbarHostState() }
        val primaryBlue = Color(0xFF0052CC)

        val publishedList by RideStateManager.publishedRides.collectAsState()
        val ride = remember(publishedList, rideId) {
            publishedList.find { it.id == rideId }
        }

        if (ride == null) {
            Scaffold(
                topBar = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .height(56.dp)
                            .padding(horizontal = spacing.edgeMargin),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                        }
                        Text("Edit Ride", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                }
            ) { padding ->
                Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Text("Ride not found or removed", style = MaterialTheme.typography.bodyLarge)
                }
            }
            return
        }

        // Editable state fields
        var origin by remember { mutableStateOf(ride.origin) }
        var destination by remember { mutableStateOf(ride.destination) }
        var intermediateStops by remember { mutableStateOf(ride.intermediateStops) }
        var departureDate by remember { mutableStateOf(ride.departureDate) }
        var departureTime by remember { mutableStateOf(ride.departureTime) }
        var totalSeats by remember { mutableStateOf(ride.totalSeats) }
        var pricePerSeat by remember { mutableStateOf(ride.pricePerSeat.toString()) }
        var newStopInput by remember { mutableStateOf("") }
        var showAddStopDialog by remember { mutableStateOf(false) }

        fun calculateDefaultDestinationPrices(
            currentOrigin: String,
            currentStops: List<String>,
            baseFare: Int,
            existingMap: Map<String, Int> = emptyMap()
        ): Map<String, Int> {
            val boardingPoints = listOf(currentOrigin) + currentStops.filter { it.isNotBlank() }
            val totalPoints = boardingPoints.size
            val map = mutableMapOf<String, Int>()

            boardingPoints.forEachIndexed { index, point ->
                val existing = existingMap[point]
                if (existing != null && index != 0) {
                    map[point] = existing
                } else if (index == 0) {
                    map[point] = baseFare
                } else {
                    val fraction = (totalPoints - index).toFloat() / totalPoints.toFloat()
                    val calc = ((baseFare * fraction) / 10).toInt() * 10
                    map[point] = calc.coerceAtLeast(50)
                }
            }
            return map
        }

        var destinationPrices by remember {
            mutableStateOf(
                calculateDefaultDestinationPrices(
                    currentOrigin = ride.origin,
                    currentStops = ride.intermediateStops,
                    baseFare = ride.pricePerSeat,
                    existingMap = ride.destinationPrices
                )
            )
        }

        fun onBasePriceChanged(newBase: Int) {
            pricePerSeat = newBase.toString()
            val boardingPoints = listOf(origin) + intermediateStops.filter { it.isNotBlank() }
            val totalPoints = boardingPoints.size
            val updated = mutableMapOf<String, Int>()

            boardingPoints.forEachIndexed { index, point ->
                if (index == 0) {
                    updated[point] = newBase
                } else {
                    val fraction = (totalPoints - index).toFloat() / totalPoints.toFloat()
                    val calc = ((newBase * fraction) / 10).toInt() * 10
                    updated[point] = calc.coerceAtLeast(50)
                }
            }
            destinationPrices = updated
        }

        // Route Calculation State
        var estimatedArrival by remember { mutableStateOf("") }
        var approxJourneyTime by remember { mutableStateOf("") }
        val placesService = remember { DefaultPlacesAutocompleteService() }

        fun recalculateSchedule() {
            coroutineScope.launch {
                val dummyPickup = intermediateStops.map { PlaceLocation(name = it, isConfirmed = true) }
                val durationMins = placesService.calculateRouteDurationMinutes(
                    origin = origin,
                    destination = destination,
                    pickupPoints = dummyPickup
                )
                val hours = durationMins / 60
                val mins = durationMins % 60
                approxJourneyTime = if (hours > 0) "${hours}h ${mins}m" else "${mins}m"

                // Calculate Arrival Time
                val cleanTime = departureTime.trim().uppercase()
                val isPm = cleanTime.contains("PM")
                val isAm = cleanTime.contains("AM")
                val raw = cleanTime.replace("AM", "").replace("PM", "").trim()
                val parts = raw.split(":")
                val rawHour = parts.getOrNull(0)?.toIntOrNull() ?: 8
                val minute = parts.getOrNull(1)?.toIntOrNull() ?: 0
                val startHour = when {
                    isPm && rawHour < 12 -> rawHour + 12
                    isAm && rawHour == 12 -> 0
                    else -> rawHour
                }
                val totalMins = startHour * 60 + minute + durationMins
                val arrHour24 = (totalMins % (24 * 60)) / 60
                val arrMin = totalMins % 60
                val arrAmPm = if (arrHour24 >= 12) "PM" else "AM"
                val arrHour12 = when {
                    arrHour24 == 0 -> 12
                    arrHour24 > 12 -> arrHour24 - 12
                    else -> arrHour24
                }
                estimatedArrival = "${arrHour12.toString().padStart(2, '0')}:${arrMin.toString().padStart(2, '0')} $arrAmPm"
            }
        }

        LaunchedEffect(origin, destination, intermediateStops, departureDate, departureTime) {
            recalculateSchedule()
        }

        // Date & Time Picker Dialogs
        var showDatePicker by remember { mutableStateOf(false) }
        var showTimePicker by remember { mutableStateOf(false) }

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
                            departureDate = "${localDate.dayOfMonth} ${localDate.month.name.take(3).lowercase().replaceFirstChar { it.uppercase() }}"
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
            val isPm = departureTime.contains("PM", ignoreCase = true)
            val isAm = departureTime.contains("AM", ignoreCase = true)
            val raw = departureTime.replace("AM", "", ignoreCase = true).replace("PM", "", ignoreCase = true).trim()
            val timeParts = raw.split(":")
            val rawH = timeParts.getOrNull(0)?.toIntOrNull() ?: 8
            val rawM = timeParts.getOrNull(1)?.toIntOrNull() ?: 0
            val initH = when {
                isPm && rawH < 12 -> rawH + 12
                isAm && rawH == 12 -> 0
                else -> rawH
            }
            val timePickerState = rememberTimePickerState(initialHour = initH, initialMinute = rawM, is24Hour = false)

            Dialog(
                onDismissRequest = { showTimePicker = false },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp,
                    modifier = Modifier.wrapContentWidth().wrapContentHeight().padding(24.dp)
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
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                        )
                        TimePicker(state = timePickerState)
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { showTimePicker = false }) { Text("Cancel") }
                            Spacer(modifier = Modifier.width(8.dp))
                            TextButton(onClick = {
                                val h = timePickerState.hour
                                val m = timePickerState.minute
                                val amPm = if (h >= 12) "PM" else "AM"
                                val h12 = if (h % 12 == 0) 12 else h % 12
                                departureTime = "${h12.toString().padStart(2, '0')}:${m.toString().padStart(2, '0')} $amPm"
                                showTimePicker = false
                            }) { Text("OK") }
                        }
                    }
                }
            }
        }

        // Add Stop Dialog
        if (showAddStopDialog) {
            AlertDialog(
                onDismissRequest = { showAddStopDialog = false },
                title = { Text("Add Pickup Stop", fontWeight = FontWeight.Bold) },
                text = {
                    OutlinedTextField(
                        value = newStopInput,
                        onValueChange = { newStopInput = it },
                        label = { Text("Stop / Town Name") },
                        placeholder = { Text("e.g. Chandpur, Noorpur") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newStopInput.isNotBlank()) {
                                val stopName = newStopInput.trim()
                                val updatedStops = intermediateStops + stopName
                                intermediateStops = updatedStops
                                destinationPrices = calculateDefaultDestinationPrices(
                                    currentOrigin = origin,
                                    currentStops = updatedStops,
                                    baseFare = pricePerSeat.toIntOrNull() ?: ride.pricePerSeat,
                                    existingMap = destinationPrices
                                )
                                newStopInput = ""
                                showAddStopDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = primaryBlue)
                    ) {
                        Text("Add")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddStopDialog = false }) { Text("Cancel") }
                }
            )
        }

        Scaffold(
            topBar = {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 2.dp
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
                            IconButton(onClick = { navigator.pop() }) {
                                Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                            }
                            Spacer(modifier = Modifier.width(spacing.xs))
                            Text(
                                text = "Edit Published Ride",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Surface(
                            color = if (ride.status == "Active") Color(0xFFE3FCEF) else Color(0xFFDFE0E0),
                            shape = RoundedCornerShape(percent = 50)
                        ) {
                            Text(
                                text = ride.status.uppercase(),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = if (ride.status == "Active") Color(0xFF006644) else Color(0xFF616363),
                                fontWeight = FontWeight.Bold
                            )
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
                            onClick = { navigator.pop() },
                            modifier = Modifier.weight(1f).height(50.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Cancel")
                        }

                        JukoButton(
                            text = "Save Changes",
                            onClick = {
                                val priceInt = pricePerSeat.toIntOrNull()
                                if (priceInt == null || priceInt <= 0) {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Please enter a valid price per seat")
                                    }
                                    return@JukoButton
                                }
                                if (origin.isBlank() || destination.isBlank()) {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Origin and destination cannot be blank")
                                    }
                                    return@JukoButton
                                }

                                RideStateManager.updatePublishedRide(
                                    rideId = ride.id,
                                    origin = origin.trim(),
                                    destination = destination.trim(),
                                    intermediateStops = intermediateStops,
                                    departureDate = departureDate,
                                    departureTime = departureTime,
                                    totalSeats = totalSeats,
                                    pricePerSeat = priceInt,
                                    destinationPrices = destinationPrices
                                )

                                navigator.pop()
                            },
                            modifier = Modifier.weight(1.5f).height(50.dp),
                            shape = RoundedCornerShape(12.dp)
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
                    .verticalScroll(rememberScrollState())
                    .padding(spacing.md),
                verticalArrangement = Arrangement.spacedBy(spacing.md)
            ) {
                // Route Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("ROUTE & STOPS", style = MaterialTheme.typography.labelSmall, color = Color(0xFF737685), fontWeight = FontWeight.Bold)

                        OutlinedTextField(
                            value = origin,
                            onValueChange = { origin = it },
                            label = { Text("Departure Origin") },
                            leadingIcon = { Icon(Icons.Outlined.TripOrigin, contentDescription = null, tint = primaryBlue) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        // Intermediate Stops List
                        if (intermediateStops.isNotEmpty()) {
                            Text("Intermediate Pickup Stops", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, color = Color(0xFF737685))
                            intermediateStops.forEachIndexed { index, stopName ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFFF8F9FA), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Icon(Icons.Outlined.LocationOn, contentDescription = null, tint = Color(0xFF0052CC), modifier = Modifier.size(18.dp))
                                        Text("${index + 1}. $stopName", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                                    }
                                    IconButton(
                                        onClick = {
                                            val updatedStops = intermediateStops.toMutableList().also { it.removeAt(index) }
                                            intermediateStops = updatedStops
                                            destinationPrices = calculateDefaultDestinationPrices(
                                                currentOrigin = origin,
                                                currentStops = updatedStops,
                                                baseFare = pricePerSeat.toIntOrNull() ?: ride.pricePerSeat,
                                                existingMap = destinationPrices
                                            )
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Outlined.Close, contentDescription = "Remove", tint = Color.Gray, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }

                        OutlinedButton(
                            onClick = { showAddStopDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Outlined.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("+ Add Pickup Stop", style = MaterialTheme.typography.labelMedium)
                        }

                        OutlinedTextField(
                            value = destination,
                            onValueChange = { destination = it },
                            label = { Text("Final Destination") },
                            leadingIcon = { Icon(Icons.Outlined.Place, contentDescription = null, tint = Color(0xFF006844)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                }

                // Schedule Card with automatic route calculation
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("SCHEDULE", style = MaterialTheme.typography.labelSmall, color = Color(0xFF737685), fontWeight = FontWeight.Bold)

                        // DEPARTURE
                        Text("DEPARTURE", style = MaterialTheme.typography.labelSmall, color = Color(0xFF737685))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(spacing.md)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .weight(1f)
                                    .border(1.dp, Color(0xFFE8EDFF), RoundedCornerShape(8.dp))
                                    .clickable { showDatePicker = true }
                                    .padding(12.dp)
                            ) {
                                Icon(Icons.Outlined.CalendarToday, contentDescription = null, tint = primaryBlue, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(spacing.xs))
                                Text(departureDate, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .weight(1f)
                                    .border(1.dp, Color(0xFFE8EDFF), RoundedCornerShape(8.dp))
                                    .clickable { showTimePicker = true }
                                    .padding(12.dp)
                            ) {
                                Icon(Icons.Outlined.AccessTime, contentDescription = null, tint = primaryBlue, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(spacing.xs))
                                Text(departureTime, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        HorizontalDivider(color = Color(0xFFF1F3FF))

                        // ESTIMATED ARRIVAL (Auto-calculated on the basis of route)
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("ESTIMATED ARRIVAL", style = MaterialTheme.typography.labelSmall, color = Color(0xFF737685))
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(Icons.Outlined.AutoAwesome, contentDescription = null, tint = primaryBlue, modifier = Modifier.size(12.dp))
                                    Text("Calculated via Route", style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp), color = primaryBlue)
                                }
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(spacing.md)) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f).padding(vertical = 4.dp)) {
                                    Icon(Icons.Outlined.CalendarToday, contentDescription = null, tint = Color(0xFF737685), modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(spacing.xs))
                                    Text(departureDate, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f).padding(vertical = 4.dp)) {
                                    Icon(Icons.Outlined.AccessTime, contentDescription = null, tint = Color(0xFF737685), modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(spacing.xs))
                                    Text(estimatedArrival.ifBlank { "--:-- --" }, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }

                        Surface(color = Color(0xFFE3FCEF), shape = RoundedCornerShape(8.dp)) {
                            Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Outlined.Timer, contentDescription = null, tint = Color(0xFF006644), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Approx. journey time: ${approxJourneyTime.ifBlank { "calculating..." }}", style = MaterialTheme.typography.labelSmall, color = Color(0xFF006644), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Seats & Pricing Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text("SEATS & PRICING", style = MaterialTheme.typography.labelSmall, color = Color(0xFF737685), fontWeight = FontWeight.Bold)

                        // Seats Stepper
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Passenger Seats Offered", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                Text("Available for passengers", style = MaterialTheme.typography.bodySmall, color = Color(0xFF737685))
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                IconButton(
                                    onClick = { if (totalSeats > 1) totalSeats-- },
                                    modifier = Modifier.size(36.dp).background(Color(0xFFF4F5F7), CircleShape)
                                ) {
                                    Icon(Icons.Outlined.Remove, contentDescription = "Decrease", tint = if (totalSeats > 1) Color.Black else Color.Gray)
                                }
                                Text("$totalSeats", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                IconButton(
                                    onClick = { if (totalSeats < 6) totalSeats++ },
                                    modifier = Modifier.size(36.dp).background(Color(0xFFF4F5F7), CircleShape)
                                ) {
                                    Icon(Icons.Outlined.Add, contentDescription = "Increase", tint = if (totalSeats < 6) Color.Black else Color.Gray)
                                }
                            }
                        }

                        HorizontalDivider(color = Color(0xFFF1F3FF))

                        // Full Fare per Seat (Base: Origin to Destination)
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            OutlinedTextField(
                                value = pricePerSeat,
                                onValueChange = { input ->
                                    val digits = input.filter { ch -> ch.isDigit() }.take(5)
                                    pricePerSeat = digits
                                    val intVal = digits.toIntOrNull() ?: 0
                                    if (intVal > 0) {
                                        onBasePriceChanged(intVal)
                                    }
                                },
                                label = { Text("Price per Seat (Full Fare: $origin → $destination)") },
                                prefix = { Text("₹ ", fontWeight = FontWeight.Bold, color = primaryBlue) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            Text(
                                "Base fare from origin to final destination",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = Color(0xFF737685)
                            )
                        }

                        // Dynamic Pickup Points Pricing Section
                        if (intermediateStops.isNotEmpty()) {
                            HorizontalDivider(color = Color(0xFFF1F3FF))

                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    "PICKUP POINT FARES (TO $destination)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF737685),
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "Dynamically calculated based on route distance. You can adjust each stop's price individually:",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = Color(0xFF8C8E99)
                                )

                                intermediateStops.forEach { stopName ->
                                    val stopPrice = destinationPrices[stopName] ?: (((pricePerSeat.toIntOrNull() ?: 350) * 2 / 3) / 10 * 10).coerceAtLeast(50)
                                    Surface(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(10.dp),
                                        color = Color(0xFFF8F9FD),
                                        border = BorderStroke(1.dp, Color(0xFFE8EDFF))
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(stopName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Icon(Icons.Outlined.ArrowForward, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color(0xFF737685))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text(destination, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF5D5F5F))
                                                }
                                                Text("Pickup to final destination", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = Color(0xFF737685))
                                            }

                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                IconButton(
                                                    onClick = {
                                                        val updated = destinationPrices.toMutableMap()
                                                        val curr = updated[stopName] ?: stopPrice
                                                        updated[stopName] = (curr - 20).coerceAtLeast(50)
                                                        destinationPrices = updated
                                                    },
                                                    modifier = Modifier
                                                        .size(28.dp)
                                                        .background(Color.White, CircleShape)
                                                        .border(1.dp, Color(0xFFC3C6D6), CircleShape)
                                                ) {
                                                    Icon(Icons.Outlined.Remove, contentDescription = "Decrease", modifier = Modifier.size(14.dp))
                                                }

                                                Surface(
                                                    color = Color.White,
                                                    shape = RoundedCornerShape(6.dp),
                                                    border = BorderStroke(1.dp, Color(0xFFD0D7F5)),
                                                    modifier = Modifier.width(68.dp).height(32.dp)
                                                ) {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.Center,
                                                        modifier = Modifier.padding(horizontal = 4.dp)
                                                    ) {
                                                        Text("₹", fontWeight = FontWeight.Bold, color = primaryBlue, fontSize = 13.sp)
                                                        Spacer(modifier = Modifier.width(2.dp))
                                                        BasicTextField(
                                                            value = stopPrice.toString(),
                                                            onValueChange = { input ->
                                                                val num = input.filter { ch -> ch.isDigit() }.take(5).toIntOrNull() ?: 0
                                                                val updated = destinationPrices.toMutableMap()
                                                                updated[stopName] = num
                                                                destinationPrices = updated
                                                            },
                                                            keyboardOptions = KeyboardOptions(
                                                                keyboardType = KeyboardType.Number,
                                                                imeAction = ImeAction.Done
                                                            ),
                                                            textStyle = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                                            singleLine = true
                                                        )
                                                    }
                                                }

                                                IconButton(
                                                    onClick = {
                                                        val updated = destinationPrices.toMutableMap()
                                                        val curr = updated[stopName] ?: stopPrice
                                                        updated[stopName] = curr + 20
                                                        destinationPrices = updated
                                                    },
                                                    modifier = Modifier
                                                        .size(28.dp)
                                                        .background(primaryBlue, CircleShape)
                                                ) {
                                                    Icon(Icons.Outlined.Add, contentDescription = "Increase", tint = Color.White, modifier = Modifier.size(14.dp))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Vehicle Info
                        Row(
                            modifier = Modifier.fillMaxWidth().background(Color(0xFFF8F9FA), RoundedCornerShape(8.dp)).padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Outlined.DirectionsCar, contentDescription = null, tint = primaryBlue)
                            Text(ride.vehicleName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}
