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

class TermsAndConditionsScreen : Screen {

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
                            text = "Terms and Conditions",
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
                        text = "Last updated: September 2026 • Please read these terms carefully before using Juko.",
                        style = MaterialTheme.typography.bodySmall,
                        color = primaryBlue,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(spacing.md)
                    )
                }

                SectionBlock(
                    title = "1. Introduction & Acceptance",
                    content = "Welcome to Juko. By downloading, accessing, or using our ride-sharing platform, you agree to comply with and be bound by these Terms and Conditions. If you do not agree to these terms, you must not use our services."
                )

                SectionBlock(
                    title = "2. User Accounts & Verification",
                    content = "All users must provide accurate, verified information during registration. Drivers offering rides must hold a valid driver's licence, maintain active vehicle registration, and upload verifiable proof before publishing rides."
                )

                SectionBlock(
                    title = "3. Ride Posting & Dynamic Stops",
                    content = "Rides posted on Juko operate on a forward-only journey structure. Stops added by drivers act exclusively as boarding/pickup points. All passengers in a ride share the same fixed final destination. Backward travel or using stops as destinations is strictly prohibited."
                )

                SectionBlock(
                    title = "4. Cost Sharing & Pricing Rules",
                    content = "Juko is a peer-to-peer cost-sharing network. Drivers enter a full-route price, and intermediate stop fares are calculated proportionally. Drivers must not charge commercial taxi rates exceeding statutory cost-sharing thresholds."
                )

                SectionBlock(
                    title = "5. Booking, Locks & Cancellations",
                    content = "Once one or more passengers have confirmed a booking, the driver's ride details (stops, timing, pricing) are locked and cannot be edited. Passengers can cancel free of charge up to 2 hours before the scheduled departure."
                )

                SectionBlock(
                    title = "6. Code of Conduct & Safety",
                    content = "Users must treat fellow community members with dignity, respect, and safety. Smoking, illegal substances, and hazardous items are strictly banned across all vehicles on the Juko platform."
                )

                Spacer(modifier = Modifier.height(spacing.xl))
            }
        }
    }
}

@Composable
private fun SectionBlock(title: String, content: String) {
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
