package com.juko.app.feature.profile.presentation

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.juko.app.core.presentation.components.JukoButton
import com.juko.app.core.presentation.theme.LocalSpacing

data class AddVehicleScreen(
    val onVehicleAdded: (VehicleItem) -> Unit
) : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val spacing = LocalSpacing.current
        val primaryBlue = Color(0xFF0052CC)

        var photoUri by remember { mutableStateOf<String?>(null) }
        var selectedBrand by remember { mutableStateOf("Toyota") }
        var brandExpanded by remember { mutableStateOf(false) }
        val brands = listOf("Toyota", "Honda", "Maruti Suzuki", "Hyundai", "Tata", "Mahindra", "Tesla", "Ford", "Other")

        var model by remember { mutableStateOf("") }
        var color by remember { mutableStateOf("") }
        var plateNumber by remember { mutableStateOf("") }
        var hasRoofRack by remember { mutableStateOf(false) }
        var totalSeats by remember { mutableStateOf(4) }
        var hasAc by remember { mutableStateOf(true) }

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
                                Icon(
                                    Icons.Outlined.Menu,
                                    contentDescription = "Menu",
                                    tint = primaryBlue,
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    text = "Juko",
                                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 22.sp),
                                    fontWeight = FontWeight.Bold,
                                    color = primaryBlue
                                )
                            }

                            IconButton(onClick = { /* Notifications */ }) {
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
                        text = "Add Vehicle",
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
                        // Photo Upload
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "PHOTO",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFF1F3FF))
                                    .drawBehind {
                                        val stroke = Stroke(
                                            width = 2.dp.toPx(),
                                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 12f), 0f)
                                        )
                                        drawRoundRect(color = Color(0xFFC3C6D6), style = stroke)
                                    }
                                    .clickable { photoUri = "mock_vehicle_photo" },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        Icons.Outlined.AddAPhoto,
                                        contentDescription = null,
                                        tint = primaryBlue,
                                        modifier = Modifier.size(36.dp)
                                    )
                                    Text(
                                        text = if (photoUri == null) "Upload Photo" else "Photo Selected ✓",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = primaryBlue
                                    )
                                    Text(
                                        text = "JPG or PNG, max 5MB",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
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
                                    modifier = Modifier.menuAnchor().fillMaxWidth(),
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
                                placeholder = { Text("e.g. Camry / City / Dzire") },
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

                        // Color Input
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "COLOR",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            OutlinedTextField(
                                value = color,
                                onValueChange = { color = it },
                                placeholder = { Text("e.g. Silver / White / Blue") },
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
                                text = "REGISTRATION PLATE (OPTIONAL)",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            OutlinedTextField(
                                value = plateNumber,
                                onValueChange = { plateNumber = it.uppercase() },
                                placeholder = { Text("ABC-1234") },
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

                        // Roof Rack Toggle
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "ROOF RACK",
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
                                        text = "Roof Rack",
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

                        // Stepper & AC Grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(spacing.md)
                        ) {
                            // Total Seats Stepper
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "TOTAL SEATS",
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
                                        modifier = Modifier.padding(horizontal = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        IconButton(
                                            onClick = { if (totalSeats > 1) totalSeats-- },
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Icon(
                                                Icons.Outlined.Remove,
                                                contentDescription = "Decrease Seats",
                                                tint = primaryBlue
                                            )
                                        }
                                        Text(
                                            text = totalSeats.toString(),
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        IconButton(
                                            onClick = { if (totalSeats < 8) totalSeats++ },
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Icon(
                                                Icons.Outlined.Add,
                                                contentDescription = "Increase Seats",
                                                tint = primaryBlue
                                            )
                                        }
                                    }
                                }
                            }

                            // AC Toggle
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "AIR CONDITIONING",
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
                                            text = "AC",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Switch(
                                            checked = hasAc,
                                            onCheckedChange = { hasAc = it }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Save Car Button
                JukoButton(
                    text = "Save Car",
                    onClick = {
                        val finalModel = if (model.isNotBlank()) "$selectedBrand ${model.trim()}" else "$selectedBrand Vehicle"
                        val finalPlate = if (plateNumber.isNotBlank()) plateNumber.trim() else "NEW-0001"
                        val newCar = VehicleItem(
                            id = "veh_${finalModel.hashCode()}",
                            model = finalModel,
                            color = color.ifBlank { "Silver" }.trim(),
                            plateNumber = finalPlate
                        )
                        onVehicleAdded(newCar)
                        navigator.pop()
                    },
                    modifier = Modifier.fillMaxWidth().padding(top = spacing.sm)
                )
            }
        }
    }
}
