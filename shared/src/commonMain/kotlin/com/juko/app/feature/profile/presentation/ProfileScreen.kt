package com.juko.app.feature.profile.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.juko.app.core.presentation.components.JukoAvatar
import com.juko.app.core.presentation.components.JukoButton
import com.juko.app.core.presentation.components.JukoTextField
import com.juko.app.core.presentation.theme.LocalSpacing
import kotlinx.coroutines.launch

data class VehicleItem(
    val id: String,
    val model: String,
    val color: String,
    val plateNumber: String,
    val imageUrl: String? = null
)

class ProfileScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val spacing = LocalSpacing.current
        val focusManager = LocalFocusManager.current
        val snackbarHostState = remember { SnackbarHostState() }
        val coroutineScope = rememberCoroutineScope()

        val primaryBlue = Color(0xFF0052CC)
        val onlineGreen = Color(0xFF006844)
        val onlineGreenBg = Color(0xFF82F9BE)

        var fullName by remember { mutableStateOf("Alexander Mitchell") }
        var email by remember { mutableStateOf("alex.mitchell@driver.rideshare.com") }
        var phoneCountryCode by remember { mutableStateOf("+91") }
        var phoneNumber by remember { mutableStateOf("9876543210") }
        var phoneError by remember { mutableStateOf<String?>(null) }

        var frontLicenceUri by remember { mutableStateOf<String?>(null) }
        var backLicenceUri by remember { mutableStateOf<String?>(null) }

        var vehicles by remember {
            mutableStateOf(
                listOf(
                    VehicleItem(
                        id = "veh_1",
                        model = "Toyota Camry",
                        color = "Silver",
                        plateNumber = "ABC-1234",
                        imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCipvSxEU0VgIFtDTAudi-KdkzVxp7Oz24RaZwnz0ymk0LFyGSQst0DnmGAUhwlFc5N6htRJqYVbK8qIuCviGSmyhB2htUW9yalM7GAt4S8Zt7gR3yl-3ASXph0Ju-UqxykJ8ICX2RufyYlD4emhHndoPDhdTieHC4DuRWC7Xj0cJ74Dp3PGwrrFj10NEkRTEoVZx01w-nuUNezMpkfpoXhB7MMRENoHcOCkSIhb8EDxXfOR91SbnID"
                    ),
                    VehicleItem(
                        id = "veh_2",
                        model = "Swift Dzire",
                        color = "White",
                        plateNumber = "DL-01-AB-1234"
                    )
                )
            )
        }

        Scaffold(
            topBar = {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White,
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(spacing.sm)
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

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(spacing.sm)
                        ) {
                            // Online Status Pill
                            Surface(
                                color = onlineGreenBg,
                                shape = RoundedCornerShape(percent = 50)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(onlineGreen)
                                    )
                                    Text(
                                        text = "Online",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = onlineGreen
                                    )
                                }
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
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = MaterialTheme.colorScheme.background,
            contentWindowInsets = WindowInsets(0, 0, 0, 0)
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(
                    start = spacing.edgeMargin,
                    end = spacing.edgeMargin,
                    top = spacing.lg,
                    bottom = 120.dp
                ),
                verticalArrangement = Arrangement.spacedBy(spacing.lg)
            ) {
                // Profile Avatar with Edit Overlay
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(contentAlignment = Alignment.BottomEnd) {
                            JukoAvatar(
                                imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuBfFzjg65uwWojeFdWMwuH6S_YvbBEw6T57aVOZ1xNMnMLHFJvs5mG1JMwWH0JKpHcF9eXeWaXNtzH2ubS3gcN86p3UYtSlZlpdNUJLNa8VTWI6f5_wUgHEqHEEVJcf18D2a1vEBn15-bKk8zM1mLNIhIWNmxYIzLpP2ZRIatWdIIBmRAT2ufv-5Kh-fVMYbiSXQ5Vp6iej4k-D1AfyzZ-OtW_5QdsqPjyRqE5Kif5PgU3tdsCclG1Z",
                                size = 120.dp,
                                modifier = Modifier.border(4.dp, Color.White, CircleShape)
                            )
                            IconButton(
                                onClick = {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Profile photo update tapped")
                                    }
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(primaryBlue)
                            ) {
                                Icon(
                                    Icons.Outlined.Edit,
                                    contentDescription = "Edit Photo",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                // Ratings & Rides Card
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        shadowElevation = 1.dp,
                        border = BorderStroke(1.dp, Color(0xFFE8EDFF))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(spacing.md),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                repeat(5) {
                                    Icon(
                                        Icons.Filled.Star,
                                        contentDescription = null,
                                        tint = Color(0xFFF59E0B),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Text(
                                text = "4.8 · 124 rides completed",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Surface(
                                color = Color(0xFFE0E8FF),
                                shape = RoundedCornerShape(percent = 50)
                            ) {
                                Text(
                                    text = "Member since Jan 2024",
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                // Personal Details Form
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
                        // Full Name
                        JukoTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            label = "FULL NAME",
                            placeholder = "Enter full name",
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Email
                        JukoTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = "EMAIL ADDRESS",
                            placeholder = "name@example.com",
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Phone with Country Code & Verified Pill
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "PHONE NUMBER",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    modifier = Modifier.width(64.dp).height(56.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, Color(0xFFC3C6D6)),
                                    color = Color.White
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = phoneCountryCode,
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                OutlinedTextField(
                                    value = phoneNumber,
                                    onValueChange = { input ->
                                        val digits = input.filter { it.isDigit() }.take(10)
                                        phoneNumber = digits
                                        phoneError = if (digits.isNotEmpty() && digits.length < 10) "Phone number must be exactly 10 digits" else null
                                    },
                                    isError = phoneError != null,
                                    modifier = Modifier.weight(1f).height(56.dp),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Phone,
                                        imeAction = ImeAction.Done
                                    ),
                                    keyboardActions = KeyboardActions(
                                        onDone = { focusManager.clearFocus() }
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    trailingIcon = {
                                        Surface(
                                            color = if (phoneNumber.length == 10) Color(0xFFDAE2FF) else Color(0xFFF1F3FF),
                                            shape = RoundedCornerShape(percent = 50),
                                            modifier = Modifier.padding(end = 6.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                                            ) {
                                                Icon(
                                                    Icons.Outlined.Verified,
                                                    contentDescription = null,
                                                    tint = if (phoneNumber.length == 10) primaryBlue else Color(0xFF737685),
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Text(
                                                    text = if (phoneNumber.length == 10) "Verified" else "10 digits",
                                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                                    color = if (phoneNumber.length == 10) primaryBlue else Color(0xFF737685),
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                )
                            }
                            if (phoneError != null) {
                                Text(
                                    text = phoneError ?: "",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                                )
                            }
                        }
                    }
                }

                // Driver Licence Verification Section
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(spacing.sm)
                    ) {
                        Text(
                            text = "Driver Licence Verification",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        // Front Licence Card
                        LicenceUploadCard(
                            label = "Driver Licence — Front",
                            imageUri = frontLicenceUri,
                            onUploadClick = { frontLicenceUri = "front_licence_mock_url" },
                            onRetake = { frontLicenceUri = "front_licence_mock_url" },
                            onRemove = { frontLicenceUri = null }
                        )

                        // Back Licence Card
                        LicenceUploadCard(
                            label = "Driver Licence — Back",
                            imageUri = backLicenceUri,
                            onUploadClick = { backLicenceUri = "back_licence_mock_url" },
                            onRetake = { backLicenceUri = "back_licence_mock_url" },
                            onRemove = { backLicenceUri = null }
                        )
                    }
                }

                // My Vehicles Section
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(spacing.sm)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "My Vehicles",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            IconButton(
                                onClick = {
                                    navigator.push(
                                        AddVehicleScreen { newVehicle ->
                                            vehicles = vehicles + newVehicle
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("Added ${newVehicle.model}")
                                            }
                                        }
                                    )
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(primaryBlue)
                            ) {
                                Icon(
                                    Icons.Outlined.Add,
                                    contentDescription = "Add Vehicle",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        // Horizontal list of Vehicles
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(spacing.md),
                            contentPadding = PaddingValues(vertical = 4.dp)
                        ) {
                            items(vehicles) { vehicle ->
                                VehicleCard(vehicle = vehicle)
                            }
                        }
                    }
                }

                // Save Changes Button
                item {
                    JukoButton(
                        text = "Save Changes",
                        onClick = {
                            if (phoneNumber.length != 10) {
                                phoneError = "Phone number must be exactly 10 digits"
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Please enter a valid 10-digit phone number")
                                }
                                return@JukoButton
                            }
                            if (fullName.isBlank()) {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Full name cannot be blank")
                                }
                                return@JukoButton
                            }
                            if (email.isBlank() || !email.contains("@")) {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Please enter a valid email address")
                                }
                                return@JukoButton
                            }

                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Profile changes saved successfully!")
                            }
                        },
                        leadingIcon = { Icon(Icons.Outlined.CheckCircle, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth().padding(top = spacing.sm)
                    )
                }
            }
        }
    }
}

@Composable
private fun LicenceUploadCard(
    label: String,
    imageUri: String?,
    onUploadClick: () -> Unit,
    onRetake: () -> Unit,
    onRemove: () -> Unit
) {
    val spacing = LocalSpacing.current
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        shadowElevation = 1.dp,
        border = BorderStroke(1.dp, Color(0xFFE8EDFF))
    ) {
        Column(modifier = Modifier.padding(spacing.md), verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF737685),
                fontWeight = FontWeight.Bold
            )

            if (imageUri == null) {
                // Dashed Upload Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF1F3FF))
                        .drawBehind {
                            val stroke = Stroke(
                                width = 2.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 12f), 0f)
                            )
                            drawRoundRect(color = Color(0xFFC3C6D6), style = stroke)
                        }
                        .clickable { onUploadClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Outlined.AddAPhoto,
                            contentDescription = null,
                            tint = Color(0xFF737685),
                            modifier = Modifier.size(28.dp)
                        )
                        Text(
                            text = "Upload / take photo",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF737685),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            } else {
                // Uploaded Preview with Actions
                Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFE0E8FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Outlined.DirectionsCar,
                                contentDescription = null,
                                tint = Color(0xFF0052CC),
                                modifier = Modifier.size(36.dp)
                            )
                            Text(
                                text = "Licence Image Uploaded",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF0052CC),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(spacing.sm)
                    ) {
                        OutlinedButton(
                            onClick = onRetake,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color(0xFFC3C6D6))
                        ) {
                            Text("Retake", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface)
                        }
                        OutlinedButton(
                            onClick = onRemove,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
                        ) {
                            Text("Remove", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun VehicleCard(vehicle: VehicleItem) {
    val spacing = LocalSpacing.current
    Surface(
        modifier = Modifier
            .width(220.dp),
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        shadowElevation = 1.dp,
        border = BorderStroke(1.dp, Color(0xFFE8EDFF))
    ) {
        Column(
            modifier = Modifier.padding(spacing.md),
            verticalArrangement = Arrangement.spacedBy(spacing.xs)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF1F3FF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.DirectionsCar,
                    contentDescription = null,
                    tint = Color(0xFF0052CC),
                    modifier = Modifier.size(44.dp)
                )
            }

            Text(
                text = vehicle.model,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = vehicle.color,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF737685)
                )
                Surface(
                    color = Color(0xFFE0E8FF),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = vehicle.plateNumber,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}


