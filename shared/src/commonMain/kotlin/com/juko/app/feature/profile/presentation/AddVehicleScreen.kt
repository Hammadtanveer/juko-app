package com.juko.app.feature.profile.presentation

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.juko.app.core.presentation.components.JukoButton
import com.juko.app.core.presentation.theme.LocalSpacing

data class AddVehicleScreen(
    val existingVehicle: VehicleItem? = null,
    val onVehicleSaved: (VehicleItem) -> Unit
) : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val spacing = LocalSpacing.current
        val primaryBlue = Color(0xFF0052CC)

        val isEditMode = existingVehicle != null

        // Initial values extracted from existingVehicle if in Edit mode
        val brands = listOf("Toyota", "Honda", "Maruti Suzuki", "Hyundai", "Tata", "Mahindra", "Kia", "MG", "Tesla", "Ford", "Other")
        
        var selectedBrand by remember {
            mutableStateOf(
                if (existingVehicle != null && existingVehicle.brand.isNotBlank()) {
                    existingVehicle.brand
                } else if (existingVehicle != null) {
                    brands.firstOrNull { existingVehicle.model.startsWith(it, ignoreCase = true) } ?: "Toyota"
                } else {
                    "Toyota"
                }
            )
        }
        var brandExpanded by remember { mutableStateOf(false) }

        var model by remember {
            mutableStateOf(
                if (existingVehicle != null) {
                    existingVehicle.model.removePrefix(selectedBrand).trim()
                } else {
                    ""
                }
            )
        }
        var plateNumber by remember { mutableStateOf(existingVehicle?.plateNumber ?: "") }
        var seatingCapacity by remember { mutableStateOf(existingVehicle?.seatingCapacity ?: 5) } // 5 or 7
        var hasRoofRack by remember { mutableStateOf(false) }

        // Photos list (up to 3 photos)
        var photos by remember {
            mutableStateOf(
                if (existingVehicle != null && existingVehicle.photos.isNotEmpty()) {
                    existingVehicle.photos
                } else if (existingVehicle?.imageUrl != null) {
                    listOf(existingVehicle.imageUrl)
                } else {
                    emptyList()
                }
            )
        }

        var validationError by remember { mutableStateOf<String?>(null) }
        val scrollState = rememberScrollState()

        Scaffold(
            topBar = {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 1.dp
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .statusBarsPadding()
                                .height(56.dp)
                                .padding(horizontal = spacing.edgeMargin),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(spacing.xs)
                            ) {
                                Text(
                                    text = "Juko",
                                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 22.sp),
                                    fontWeight = FontWeight.Bold,
                                    color = primaryBlue
                                )
                            }

                            IconButton(onClick = {
                                navigator.push(com.juko.app.feature.notifications.presentation.NotificationsScreen())
                            }) {
                                Icon(
                                    Icons.Outlined.Notifications,
                                    contentDescription = "Notifications",
                                    tint = primaryBlue
                                )
                            }
                        }
                    }
                }
            },
            containerColor = MaterialTheme.colorScheme.background,
            contentWindowInsets = WindowInsets(0, 0, 0, 0)
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(scrollState)
                    .padding(horizontal = spacing.edgeMargin)
                    .padding(top = spacing.md, bottom = 40.dp),
                verticalArrangement = Arrangement.spacedBy(spacing.md)
            ) {
                // Header Row with Back Button and Title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(spacing.xs),
                    modifier = Modifier.padding(bottom = spacing.xs)
                ) {
                    IconButton(onClick = { navigator.pop() }) {
                        Icon(
                            Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = if (isEditMode) "Edit Vehicle" else "Add Vehicle",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Form Canvas Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    shadowElevation = 2.dp,
                    border = BorderStroke(1.dp, Color(0xFFE8EDFF))
                ) {
                    Column(
                        modifier = Modifier.padding(spacing.md),
                        verticalArrangement = Arrangement.spacedBy(spacing.md)
                    ) {
                        // Vehicle Photos Section (Max 3 Photos)
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "VEHICLE PHOTOS (MAX 3)",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${photos.size}/3 uploaded",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (photos.size == 3) Color(0xFF006844) else primaryBlue,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Photos Row with Thumbnails & Add Slot
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                // Existing / Added Photos Thumbnails
                                itemsIndexed(photos) { index, photoUrl ->
                                    Box(
                                        modifier = Modifier
                                            .size(width = 110.dp, height = 90.dp)
                                    ) {
                                        Surface(
                                            modifier = Modifier.fillMaxSize(),
                                            shape = RoundedCornerShape(10.dp),
                                            color = Color(0xFFE0E8FF),
                                            border = BorderStroke(1.dp, primaryBlue.copy(alpha = 0.3f))
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                    Icon(
                                                        Icons.Outlined.DirectionsCar,
                                                        contentDescription = null,
                                                        tint = primaryBlue,
                                                        modifier = Modifier.size(32.dp)
                                                    )
                                                    Text(
                                                        text = "Photo ${index + 1}",
                                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                                        color = primaryBlue,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                        }

                                        // Delete Badge
                                        IconButton(
                                            onClick = {
                                                photos = photos.filterIndexed { i, _ -> i != index }
                                            },
                                            modifier = Modifier
                                                .size(24.dp)
                                                .align(Alignment.TopEnd)
                                                .offset(x = 4.dp, y = (-4).dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFFBA1A1A))
                                        ) {
                                            Icon(
                                                Icons.Outlined.Close,
                                                contentDescription = "Remove photo",
                                                tint = Color.White,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }

                                // Add Photo Slot (if less than 3)
                                if (photos.size < 3) {
                                    item {
                                        Box(
                                            modifier = Modifier
                                                .size(width = 110.dp, height = 90.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(Color(0xFFF1F3FF))
                                                .drawBehind {
                                                    val stroke = Stroke(
                                                        width = 2.dp.toPx(),
                                                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                                                    )
                                                    drawRoundRect(color = Color(0xFFC3C6D6), style = stroke)
                                                }
                                                .clickable {
                                                    photos = photos + "mock_car_photo_${photos.size + 1}"
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.spacedBy(2.dp)
                                            ) {
                                                Icon(
                                                    Icons.Outlined.AddAPhoto,
                                                    contentDescription = null,
                                                    tint = primaryBlue,
                                                    modifier = Modifier.size(24.dp)
                                                )
                                                Text(
                                                    text = "Add Photo",
                                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                                    fontWeight = FontWeight.Bold,
                                                    color = primaryBlue
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Brand Dropdown
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "BRAND",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            ExposedDropdownMenuBox(
                                expanded = brandExpanded,
                                onExpandedChange = { brandExpanded = !brandExpanded }
                            ) {
                                OutlinedTextField(
                                    value = selectedBrand,
                                    onValueChange = {},
                                    readOnly = true,
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = brandExpanded) },
                                    modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true).fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = Color.White,
                                        unfocusedContainerColor = Color.White,
                                        focusedBorderColor = primaryBlue,
                                        unfocusedBorderColor = Color(0xFFC3C6D6)
                                    )
                                )
                                ExposedDropdownMenu(
                                    expanded = brandExpanded,
                                    onDismissRequest = { brandExpanded = false }
                                ) {
                                    brands.forEach { brandOption ->
                                        DropdownMenuItem(
                                            text = { Text(brandOption) },
                                            onClick = {
                                                selectedBrand = brandOption
                                                brandExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // Model Input
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "MODEL",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            OutlinedTextField(
                                value = model,
                                onValueChange = { model = it },
                                placeholder = { Text("e.g. Camry / City / Dzire / Creta") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedBorderColor = primaryBlue,
                                    unfocusedBorderColor = Color(0xFFC3C6D6)
                                )
                            )
                        }

                        // Registration Plate
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "REGISTRATION PLATE",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            OutlinedTextField(
                                value = plateNumber,
                                onValueChange = { plateNumber = it.uppercase() },
                                placeholder = { Text("e.g. DL-01-AB-1234") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.Characters
                                ),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedBorderColor = primaryBlue,
                                    unfocusedBorderColor = Color(0xFFC3C6D6)
                                )
                            )
                        }

                        // Seating Capacity (Fixed 5-Seater vs 7-Seater Selector)
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "SEATING CAPACITY",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(spacing.sm)
                            ) {
                                // 5-Seater Option Pill
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .clickable { seatingCapacity = 5 },
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (seatingCapacity == 5) primaryBlue else Color(0xFFF1F3FF),
                                    border = BorderStroke(
                                        1.dp,
                                        if (seatingCapacity == 5) primaryBlue else Color(0xFFC3C6D6)
                                    )
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Icon(
                                                Icons.Outlined.DirectionsCar,
                                                contentDescription = null,
                                                tint = if (seatingCapacity == 5) Color.White else MaterialTheme.colorScheme.onSurface,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Text(
                                                text = "5-Seater",
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = if (seatingCapacity == 5) Color.White else MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }

                                // 7-Seater Option Pill
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .clickable { seatingCapacity = 7 },
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (seatingCapacity == 7) primaryBlue else Color(0xFFF1F3FF),
                                    border = BorderStroke(
                                        1.dp,
                                        if (seatingCapacity == 7) primaryBlue else Color(0xFFC3C6D6)
                                    )
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Icon(
                                                Icons.Outlined.AirportShuttle,
                                                contentDescription = null,
                                                tint = if (seatingCapacity == 7) Color.White else MaterialTheme.colorScheme.onSurface,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Text(
                                                text = "7-Seater",
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = if (seatingCapacity == 7) Color.White else MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }
                            }
                            Text(
                                text = if (seatingCapacity == 5) "Supports up to 4 passenger seats" else "Supports up to 6 passenger seats",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                color = Color(0xFF737685)
                            )
                        }

                        // Roof Rack Toggle
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "ROOF RACK (OPTIONAL)",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Surface(
                                modifier = Modifier.fillMaxWidth().height(52.dp),
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFF1F3FF),
                                border = BorderStroke(1.dp, Color(0xFFC3C6D6))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = spacing.md),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Has luggage roof carrier",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Switch(
                                        checked = hasRoofRack,
                                        onCheckedChange = { hasRoofRack = it }
                                    )
                                }
                            }
                        }

                        // Validation Error if any
                        if (validationError != null) {
                            Text(
                                text = validationError ?: "",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }

                // Save / Update Vehicle Button
                JukoButton(
                    text = if (isEditMode) "Update Vehicle" else "Save Car",
                    onClick = {
                        if (model.isBlank()) {
                            validationError = "Please enter the vehicle model"
                            return@JukoButton
                        }
                        if (plateNumber.isBlank()) {
                            validationError = "Please enter the registration plate number"
                            return@JukoButton
                        }

                        // Concatenate Brand + Model
                        val finalModelName = "${selectedBrand.trim()} ${model.trim()}"
                        val finalPlate = plateNumber.trim().uppercase()

                        val vehicleItem = VehicleItem(
                            id = existingVehicle?.id ?: "veh_${finalModelName.hashCode()}_${finalPlate.hashCode()}",
                            brand = selectedBrand,
                            model = finalModelName,
                            plateNumber = finalPlate,
                            seatingCapacity = seatingCapacity,
                            photos = photos,
                            imageUrl = photos.firstOrNull() ?: existingVehicle?.imageUrl
                        )

                        onVehicleSaved(vehicleItem)
                        navigator.pop()
                    },
                    modifier = Modifier.fillMaxWidth().padding(top = spacing.sm)
                )
            }
        }
    }
}
