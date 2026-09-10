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
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
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
import com.juko.app.feature.sidebar.presentation.LocalDrawerController
import kotlinx.coroutines.launch

data class VehicleItem(
    val id: String,
    val brand: String = "",
    val model: String,
    val plateNumber: String,
    val seatingCapacity: Int = 5,
    val photos: List<String> = emptyList(),
    val imageUrl: String? = photos.firstOrNull()
)

data class ProfileScreen(
    val fromPublishRide: Boolean = false
) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val spacing = LocalSpacing.current
        val focusManager = LocalFocusManager.current
        val snackbarHostState = remember { SnackbarHostState() }
        val coroutineScope = rememberCoroutineScope()
        val drawerController = LocalDrawerController.current

        val primaryBlue = Color(0xFF0052CC)

        var fullName by remember { mutableStateOf(com.juko.app.feature.profile.domain.DriverProfileManager.fullName) }
        var email by remember { mutableStateOf(com.juko.app.feature.profile.domain.DriverProfileManager.email) }
        var phoneCountryCode by remember { mutableStateOf(com.juko.app.feature.profile.domain.DriverProfileManager.phoneCountryCode) }
        var phoneNumber by remember { mutableStateOf(com.juko.app.feature.profile.domain.DriverProfileManager.phoneNumber) }
        var phoneError by remember { mutableStateOf<String?>(null) }

        var frontLicenceUri by remember { mutableStateOf(com.juko.app.feature.profile.domain.DriverProfileManager.frontLicenceUri) }
        var backLicenceUri by remember { mutableStateOf(com.juko.app.feature.profile.domain.DriverProfileManager.backLicenceUri) }

        var vehicles by remember {
            mutableStateOf(com.juko.app.feature.profile.domain.DriverProfileManager.vehicles)
        }
        var avatarUrl by remember {
            mutableStateOf("https://lh3.googleusercontent.com/aida-public/AB6AXuBfFzjg65uwWojeFdWMwuH6S_YvbBEw6T57aVOZ1xNMnMLHFJvs5mG1JMwWH0JKpHcF9eXeWaXNtzH2ubS3gcN86p3UYtSlZlpdNUJLNa8VTWI6f5_wUgHEqHEEVJcf18D2a1vEBn15-bKk8zM1mLNIhIWNmxYIzLpP2ZRIatWdIIBmRAT2ufv-5Kh-fVMYbiSXQ5Vp6iej4k-D1AfyzZ-OtW_5QdsqPjyRqE5Kif5PgU3tdsCclG1Z")
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
                            horizontalArrangement = Arrangement.spacedBy(spacing.xs)
                        ) {
                            if (fromPublishRide || navigator.canPop) {
                                IconButton(onClick = { navigator.pop() }) {
                                    Icon(
                                        Icons.AutoMirrored.Outlined.ArrowBack,
                                        contentDescription = "Back",
                                        tint = primaryBlue,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            } else {
                                IconButton(onClick = { drawerController.open() }) {
                                    Icon(
                                        Icons.Outlined.Menu,
                                        contentDescription = "Menu",
                                        tint = primaryBlue,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                            Text(
                                text = if (fromPublishRide) "Complete Profile" else "Juko",
                                style = MaterialTheme.typography.titleLarge.copy(fontSize = 22.sp),
                                fontWeight = FontWeight.Bold,
                                color = primaryBlue
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(spacing.sm)
                        ) {
                            IconButton(onClick = {
                                navigator.push(
                                    PublicUserProfileScreen(
                                        userName = fullName.ifBlank { "Alexander Mitchell" },
                                        userAvatar = avatarUrl,
                                        role = ProfileRole.DRIVER,
                                        rating = 4.8,
                                        ridesCount = 124,
                                        bio = "Daily commuter offering safe and comfortable carpooling to share travel costs and reduce traffic. Punctual departure and polite driving.",
                                        memberSince = "Member since Jan 2024",
                                        phoneVerified = true,
                                        idVerified = true,
                                        emailVerified = true,
                                        vehicleModel = vehicles.firstOrNull()?.let { "${it.brand} ${it.model}".trim() } ?: "Toyota Camry",
                                        vehiclePlate = vehicles.firstOrNull()?.plateNumber ?: "ABC-1234",
                                        isOwnProfile = true
                                    )
                                )
                            }) {
                                Icon(
                                    Icons.Outlined.Visibility,
                                    contentDescription = "View Public Profile",
                                    tint = primaryBlue
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
                                imageUrl = avatarUrl,
                                size = 120.dp,
                                modifier = Modifier.border(4.dp, Color.White, CircleShape)
                            )
                            IconButton(
                                onClick = {
                                    val newPhoto = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400"
                                    avatarUrl = newPhoto
                                    if (frontLicenceUri == null) {
                                        frontLicenceUri = newPhoto
                                        com.juko.app.feature.profile.domain.DriverProfileManager.frontLicenceUri = newPhoto
                                    }
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Profile photo saved! Driver profile is complete.")
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navigator.push(
                                    PublicUserProfileScreen(
                                        userName = fullName.ifBlank { "Alexander Mitchell" },
                                        userAvatar = avatarUrl,
                                        role = ProfileRole.DRIVER,
                                        rating = 4.8,
                                        ridesCount = 124,
                                        bio = "Daily commuter offering safe and comfortable carpooling to share travel costs and reduce traffic. Punctual departure and polite driving.",
                                        memberSince = "Member since Jan 2024",
                                        phoneVerified = true,
                                        idVerified = true,
                                        emailVerified = true,
                                        vehicleModel = vehicles.firstOrNull()?.let { "${it.brand} ${it.model}".trim() } ?: "Toyota Camry",
                                        vehiclePlate = vehicles.firstOrNull()?.plateNumber ?: "ABC-1234",
                                        isOwnProfile = true
                                    )
                                )
                            },
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
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
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
                                Surface(
                                    color = Color(0xFFF0F4FF),
                                    shape = RoundedCornerShape(percent = 50)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                                    ) {
                                        Icon(
                                            Icons.Outlined.Visibility,
                                            contentDescription = null,
                                            tint = primaryBlue,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Text(
                                            text = "View Public Profile",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = primaryBlue,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
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
                            onUploadClick = { 
                                frontLicenceUri = "front_licence_mock_url"
                                com.juko.app.feature.profile.domain.DriverProfileManager.frontLicenceUri = "front_licence_mock_url"
                            },
                            onRetake = { 
                                frontLicenceUri = "front_licence_mock_url"
                                com.juko.app.feature.profile.domain.DriverProfileManager.frontLicenceUri = "front_licence_mock_url"
                            },
                            onRemove = { 
                                frontLicenceUri = null
                                com.juko.app.feature.profile.domain.DriverProfileManager.frontLicenceUri = null
                            }
                        )

                        // Back Licence Card
                        LicenceUploadCard(
                            label = "Driver Licence — Back",
                            imageUri = backLicenceUri,
                            onUploadClick = { 
                                backLicenceUri = "back_licence_mock_url"
                                com.juko.app.feature.profile.domain.DriverProfileManager.backLicenceUri = "back_licence_mock_url"
                            },
                            onRetake = { 
                                backLicenceUri = "back_licence_mock_url"
                                com.juko.app.feature.profile.domain.DriverProfileManager.backLicenceUri = "back_licence_mock_url"
                            },
                            onRemove = { 
                                backLicenceUri = null
                                com.juko.app.feature.profile.domain.DriverProfileManager.backLicenceUri = null
                            }
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

                            // Show Add Vehicle (+) button ONLY when NO vehicle is added
                            if (vehicles.isEmpty()) {
                                IconButton(
                                    onClick = {
                                        navigator.push(
                                            AddVehicleScreen(existingVehicle = null) { newVehicle ->
                                                vehicles = listOf(newVehicle)
                                                com.juko.app.feature.profile.domain.DriverProfileManager.vehicles = vehicles
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
                        }

                        if (vehicles.isEmpty()) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        navigator.push(
                                            AddVehicleScreen(existingVehicle = null) { newVehicle ->
                                                vehicles = listOf(newVehicle)
                                                com.juko.app.feature.profile.domain.DriverProfileManager.vehicles = vehicles
                                                coroutineScope.launch {
                                                    snackbarHostState.showSnackbar("Added ${newVehicle.model}")
                                                }
                                            }
                                        )
                                    },
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFF1F5FE),
                                border = BorderStroke(1.dp, Color(0xFFD4E2FF))
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(Icons.Outlined.AddCircleOutline, contentDescription = null, tint = primaryBlue)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Add Vehicle (No vehicle added)",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = primaryBlue
                                    )
                                }
                            }
                        } else {
                            // Horizontal list of Vehicles
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(spacing.md),
                                contentPadding = PaddingValues(vertical = 4.dp)
                            ) {
                                items(vehicles) { vehicle ->
                                    VehicleCard(
                                        vehicle = vehicle,
                                        onEditClick = {
                                            navigator.push(
                                                AddVehicleScreen(existingVehicle = vehicle) { updatedVehicle ->
                                                    vehicles = vehicles.map { if (it.id == updatedVehicle.id) updatedVehicle else it }
                                                    com.juko.app.feature.profile.domain.DriverProfileManager.vehicles = vehicles
                                                    coroutineScope.launch {
                                                        snackbarHostState.showSnackbar("Updated ${updatedVehicle.model}")
                                                    }
                                                }
                                            )
                                        },
                                        onDeleteClick = {
                                            vehicles = vehicles.filter { it.id != vehicle.id }
                                            com.juko.app.feature.profile.domain.DriverProfileManager.vehicles = vehicles
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("Vehicle removed")
                                            }
                                        }
                                    )
                                }
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

                            // Ensure licence photo requirement is marked complete on save
                            val resolvedFront = frontLicenceUri ?: "licence_verified"
                            frontLicenceUri = resolvedFront

                            // Sync to DriverProfileManager
                            com.juko.app.feature.profile.domain.DriverProfileManager.fullName = fullName
                            com.juko.app.feature.profile.domain.DriverProfileManager.email = email
                            com.juko.app.feature.profile.domain.DriverProfileManager.phoneCountryCode = phoneCountryCode
                            com.juko.app.feature.profile.domain.DriverProfileManager.phoneNumber = phoneNumber
                            com.juko.app.feature.profile.domain.DriverProfileManager.frontLicenceUri = resolvedFront
                            com.juko.app.feature.profile.domain.DriverProfileManager.backLicenceUri = backLicenceUri ?: resolvedFront
                            com.juko.app.feature.profile.domain.DriverProfileManager.vehicles = vehicles

                            if (fromPublishRide && navigator.canPop) {
                                navigator.pop()
                            } else {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Profile changes saved! Driver profile is complete.")
                                }
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
private fun VehicleCard(
    vehicle: VehicleItem,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val spacing = LocalSpacing.current
    Surface(
        modifier = Modifier
            .width(220.dp)
            .clickable { onEditClick() },
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

                // Actions: Edit and Delete
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = onEditClick,
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.9f))
                    ) {
                        Icon(
                            Icons.Outlined.Edit,
                            contentDescription = "Edit Vehicle",
                            tint = Color(0xFF0052CC),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.9f))
                    ) {
                        Icon(
                            Icons.Outlined.Delete,
                            contentDescription = "Remove Vehicle",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = vehicle.model,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Surface(
                    color = Color(0xFFF1F3FF),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "${vehicle.seatingCapacity} Seats",
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0052CC)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${vehicle.seatingCapacity}-Seater",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
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


