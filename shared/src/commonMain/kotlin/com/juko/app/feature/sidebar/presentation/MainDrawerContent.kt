package com.juko.app.feature.sidebar.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForwardIos
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.Navigator
import com.juko.app.core.presentation.components.JukoAvatar
import com.juko.app.core.presentation.theme.LocalSpacing
import com.juko.app.feature.auth.presentation.auth.AuthScreen
import com.juko.app.feature.profile.domain.DriverProfileManager
import com.juko.app.feature.profile.presentation.ProfileRole
import com.juko.app.feature.profile.presentation.PublicUserProfileScreen

@Composable
fun MainDrawerContent(
    navigator: Navigator,
    onCloseDrawer: () -> Unit
) {
    val spacing = LocalSpacing.current
    val primaryBlue = Color(0xFF0052CC)

    val isLoggedIn by com.juko.app.core.data.AuthStateManager.isLoggedIn.collectAsState()
    var showAuthPromptDialog by remember { mutableStateOf<String?>(null) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showCloseAccountDialog by remember { mutableStateOf(false) }

    ModalDrawerSheet(
        modifier = Modifier.width(310.dp),
        drawerContainerColor = Color.White,
        drawerShape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = spacing.md),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                // Profile Header Card
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = spacing.md, vertical = spacing.sm),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF1F5FE)
                ) {
                    if (isLoggedIn) {
                        Row(
                            modifier = Modifier
                                .clickable {
                                    onCloseDrawer()
                                    navigator.push(
                                        PublicUserProfileScreen(
                                            userName = DriverProfileManager.fullName.ifBlank { "Alexander Mitchell" },
                                            userAvatar = "https://lh3.googleusercontent.com/aida-public/AB6AXuBfFzjg65uwWojeFdWMwuH6S_YvbBEw6T57aVOZ1xNMnMLHFJvs5mG1JMwWH0JKpHcF9eXeWaXNtzH2ubS3gcN86p3UYtSlZlpdNUJLNa8VTWI6f5_wUgHEqHEEVJcf18D2a1vEBn15-bKk8zM1mLNIhIWNmxYIzLpP2ZRIatWdIIBmRAT2ufv-5Kh-fVMYbiSXQ5Vp6iej4k-D1AfyzZ-OtW_5QdsqPjyRqE5Kif5PgU3tdsCclG1Z",
                                            role = ProfileRole.DRIVER,
                                            rating = 4.8,
                                            ridesCount = 124,
                                            bio = "Daily commuter offering safe and comfortable carpooling to share travel costs and reduce traffic. Punctual departure and polite driving.",
                                            memberSince = "Member since Jan 2024",
                                            phoneVerified = true,
                                            idVerified = true,
                                            emailVerified = true,
                                            vehicleModel = DriverProfileManager.vehicles.firstOrNull()?.let { "${it.brand} ${it.model}".trim() } ?: "Toyota Camry",
                                            vehiclePlate = DriverProfileManager.vehicles.firstOrNull()?.plateNumber ?: "ABC-1234",
                                            isOwnProfile = true
                                        )
                                    )
                                }
                                .padding(spacing.md),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(spacing.sm)
                        ) {
                            JukoAvatar(
                                imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuBfFzjg65uwWojeFdWMwuH6S_YvbBEw6T57aVOZ1xNMnMLHFJvs5mG1JMwWH0JKpHcF9eXeWaXNtzH2ubS3gcN86p3UYtSlZlpdNUJLNa8VTWI6f5_wUgHEqHEEVJcf18D2a1vEBn15-bKk8zM1mLNIhIWNmxYIzLpP2ZRIatWdIIBmRAT2ufv-5Kh-fVMYbiSXQ5Vp6iej4k-D1AfyzZ-OtW_5QdsqPjyRqE5Kif5PgU3tdsCclG1Z",
                                size = 50.dp
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = DriverProfileManager.fullName.ifBlank { "Alexander Mitchell" },
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = DriverProfileManager.email.ifBlank { "alex.mitchell@driver.rideshare.com" },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF737685)
                                )
                                Row(
                                    modifier = Modifier.padding(top = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        color = Color(0xFFE8EDFF),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                                        ) {
                                            Icon(
                                                Icons.Outlined.Verified,
                                                contentDescription = null,
                                                tint = primaryBlue,
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Text(
                                                text = "Verified Member",
                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                                color = primaryBlue,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                            Icon(
                                Icons.AutoMirrored.Outlined.ArrowForwardIos,
                                contentDescription = "View Profile",
                                tint = Color(0xFF737685),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    } else {
                        Column(
                            modifier = Modifier.padding(spacing.md),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(spacing.sm)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFE8EDFF)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Outlined.Person,
                                        contentDescription = null,
                                        tint = primaryBlue,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = "Guest Traveler",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Sign in to manage rides & profile",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF737685)
                                    )
                                }
                            }

                            Button(
                                onClick = {
                                    onCloseDrawer()
                                    navigator.push(AuthScreen())
                                },
                                modifier = Modifier.fillMaxWidth().height(38.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = primaryBlue),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Icon(Icons.Outlined.Login, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Log In / Sign Up", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                }

                HorizontalDivider(
                    color = Color(0xFFE8EDFF),
                    modifier = Modifier.padding(vertical = spacing.xs, horizontal = spacing.md)
                )

                // Menu Items List matching specification
                DrawerNavigationItem(
                    icon = Icons.Outlined.StarRate,
                    label = "Ratings",
                    badge = if (isLoggedIn) "4.8 ★" else null,
                    onClick = {
                        if (!isLoggedIn) {
                            showAuthPromptDialog = "Ratings"
                        } else {
                            onCloseDrawer()
                            navigator.push(RatingsScreen())
                        }
                    }
                )

                DrawerNavigationItem(
                    icon = Icons.Outlined.HelpOutline,
                    label = "Help",
                    onClick = {
                        onCloseDrawer()
                        navigator.push(HelpScreen())
                    }
                )

                DrawerNavigationItem(
                    icon = Icons.Outlined.Lock,
                    label = "Password",
                    onClick = {
                        if (!isLoggedIn) {
                            showAuthPromptDialog = "Password Management"
                        } else {
                            onCloseDrawer()
                            navigator.push(ChangePasswordScreen())
                        }
                    }
                )

                DrawerNavigationItem(
                    icon = Icons.Outlined.Description,
                    label = "Terms and Conditions",
                    onClick = {
                        onCloseDrawer()
                        navigator.push(TermsAndConditionsScreen())
                    }
                )

                DrawerNavigationItem(
                    icon = Icons.Outlined.Security,
                    label = "Data Protection",
                    onClick = {
                        onCloseDrawer()
                        navigator.push(DataProtectionScreen())
                    }
                )

                HorizontalDivider(
                    color = Color(0xFFE8EDFF),
                    modifier = Modifier.padding(vertical = spacing.xs, horizontal = spacing.md)
                )

                if (isLoggedIn) {
                    DrawerNavigationItem(
                        icon = Icons.Outlined.Logout,
                        label = "Log Out",
                        tint = Color(0xFFBA1A1A),
                        onClick = {
                            showLogoutDialog = true
                        }
                    )

                    DrawerNavigationItem(
                        icon = Icons.Outlined.PersonOff,
                        label = "Close My Account",
                        tint = Color(0xFFBA1A1A),
                        onClick = {
                            showCloseAccountDialog = true
                        }
                    )
                } else {
                    DrawerNavigationItem(
                        icon = Icons.Outlined.Login,
                        label = "Log In / Sign Up",
                        tint = primaryBlue,
                        onClick = {
                            onCloseDrawer()
                            navigator.push(AuthScreen())
                        }
                    )
                }
            }

            // Footer Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.md),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "JUKO App v1.0.0",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF9E9E9E),
                    letterSpacing = 1.sp
                )
            }
        }
    }

    // Logout Confirmation Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            icon = {
                Icon(Icons.Outlined.Logout, contentDescription = null, tint = primaryBlue, modifier = Modifier.size(28.dp))
            },
            title = {
                Text("Log Out of Juko?", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            },
            text = {
                Text("Are you sure you want to log out? You will need to enter your credentials to sign in again.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        onCloseDrawer()
                        com.juko.app.core.data.AuthStateManager.logout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = primaryBlue)
                ) {
                    Text("Log Out")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Close Account Warning Dialog
    if (showCloseAccountDialog) {
        AlertDialog(
            onDismissRequest = { showCloseAccountDialog = false },
            icon = {
                Icon(Icons.Outlined.WarningAmber, contentDescription = null, tint = Color(0xFFBA1A1A), modifier = Modifier.size(32.dp))
            },
            title = {
                Text("Close My Account?", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = Color(0xFFBA1A1A))
            },
            text = {
                Text("Closing your account is permanent. All your published rides, booking history, ratings, and vehicle records will be deleted immediately.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showCloseAccountDialog = false
                        onCloseDrawer()
                        com.juko.app.core.data.AuthStateManager.logout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBA1A1A))
                ) {
                    Text("Permanently Close Account")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCloseAccountDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Auth Prompt Dialog for Protected Menu Items
    if (showAuthPromptDialog != null) {
        AlertDialog(
            onDismissRequest = { showAuthPromptDialog = null },
            icon = {
                Icon(Icons.Outlined.Lock, contentDescription = null, tint = primaryBlue, modifier = Modifier.size(28.dp))
            },
            title = {
                Text("Log In Required", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            },
            text = {
                Text("Please log in or sign up to access ${showAuthPromptDialog}. You can search and view rides freely without an account.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        val target = showAuthPromptDialog
                        showAuthPromptDialog = null
                        onCloseDrawer()
                        navigator.push(AuthScreen())
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = primaryBlue)
                ) {
                    Text("Log In / Sign Up")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAuthPromptDialog = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun DrawerNavigationItem(
    icon: ImageVector,
    label: String,
    badge: String? = null,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {
    val spacing = LocalSpacing.current
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.sm, vertical = 2.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() },
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.md, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.md)
            ) {
                Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(22.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = tint
                )
            }

            if (badge != null) {
                Surface(
                    color = Color(0xFFFEF3C7),
                    shape = RoundedCornerShape(percent = 50)
                ) {
                    Text(
                        text = badge,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        color = Color(0xFFD97706),
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Icon(
                    Icons.Outlined.ChevronRight,
                    contentDescription = null,
                    tint = Color(0xFFC3C6D6),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
