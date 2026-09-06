package com.juko.app.feature.sidebar.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.juko.app.core.presentation.theme.LocalSpacing

class DataProtectionScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val spacing = LocalSpacing.current
        val primaryBlue = Color(0xFF0052CC)

        Scaffold(
            topBar = {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 1.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .height(56.dp)
                            .padding(horizontal = spacing.edgeMargin),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(
                                Icons.AutoMirrored.Outlined.ArrowBack,
                                contentDescription = "Back",
                                tint = primaryBlue
                            )
                        }
                        Spacer(modifier = Modifier.width(spacing.xs))
                        Text(
                            text = "Data Protection & Privacy",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
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
                    .verticalScroll(rememberScrollState())
                    .padding(spacing.edgeMargin),
                verticalArrangement = Arrangement.spacedBy(spacing.lg)
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFE8EDFF)
                ) {
                    Text(
                        text = "Your privacy and data security are our top priorities at Juko.",
                        style = MaterialTheme.typography.bodySmall,
                        color = primaryBlue,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(spacing.md)
                    )
                }

                PrivacyBlock(
                    title = "1. Information We Collect",
                    content = "We collect necessary identity verification information (full name, verified phone number, email address), driver licence credentials, vehicle specifications (5/7 Seater capacity, registration number), and transaction logs for safety and trust."
                )

                PrivacyBlock(
                    title = "2. How We Use Location Data",
                    content = "Location data is utilized exclusively to calculate ride routes, discover nearby pickup stops, compute accurate distance fares, and verify boarding points along the forward route."
                )

                PrivacyBlock(
                    title = "3. Peer Data Visibility",
                    content = "Only confirmed passengers and drivers on the same ride can view mutual contact details, chosen boarding stop, and vehicle registration numbers during active ride windows."
                )

                PrivacyBlock(
                    title = "4. Encryption & Security",
                    content = "All sensitive user credentials, licence images, and communications are encrypted in transit (TLS 1.3) and stored in accordance with modern security standards."
                )

                PrivacyBlock(
                    title = "5. Right to Erasure & Account Closure",
                    content = "You have full control over your data. You may request account deletion at any time via 'Close My Account' in the main menu, which permanently purges your personal profile records."
                )

                Spacer(modifier = Modifier.height(spacing.xl))
            }
        }
    }
}

@Composable
private fun PrivacyBlock(title: String, content: String) {
    val spacing = LocalSpacing.current
    Column(verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF5D5F5F),
            lineHeight = 22.sp
        )
    }
}
