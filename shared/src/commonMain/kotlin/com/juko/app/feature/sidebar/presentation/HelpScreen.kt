package com.juko.app.feature.sidebar.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
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
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.juko.app.core.presentation.theme.LocalSpacing
import kotlinx.coroutines.launch

data class FaqItem(
    val question: String,
    val answer: String,
    val category: String
)

class HelpScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val spacing = LocalSpacing.current
        val snackbarHostState = remember { SnackbarHostState() }
        val coroutineScope = rememberCoroutineScope()
        val primaryBlue = Color(0xFF0052CC)

        val faqs = remember { getMockFaqs() }
        var selectedCategory by remember { mutableStateOf("All") }
        val categories = listOf("All", "Booking", "Publishing", "Stops & Pricing", "Safety")

        val filteredFaqs = if (selectedCategory == "All") faqs else faqs.filter { it.category == selectedCategory }

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
                            text = "Help & Support",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
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
                    .padding(padding)
                    .padding(horizontal = spacing.edgeMargin),
                contentPadding = PaddingValues(top = spacing.md, bottom = 48.dp),
                verticalArrangement = Arrangement.spacedBy(spacing.md)
            ) {
                // Quick Contact Card
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = primaryBlue,
                        shadowElevation = 2.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(spacing.md),
                            verticalArrangement = Arrangement.spacedBy(spacing.sm)
                        ) {
                            Text(
                                text = "Need immediate assistance?",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Our 24/7 dedicated support team is here to help with your rides, safety, and bookings.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(spacing.sm)
                            ) {
                                QuickSupportButton(
                                    icon = Icons.Outlined.Email,
                                    label = "Email Support",
                                    onClick = {
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("Support ticket email opened: support@juko.app")
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                                QuickSupportButton(
                                    icon = Icons.Outlined.Phone,
                                    label = "Call Support",
                                    onClick = {
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("Calling Juko Helpline: 1800-JUKO-HELP")
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                // FAQs Header & Filter Tabs
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                        Text(
                            text = "FREQUENTLY ASKED QUESTIONS",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF737685)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(spacing.xs)
                        ) {
                            categories.forEach { cat ->
                                val isSelected = selectedCategory == cat
                                Surface(
                                    color = if (isSelected) primaryBlue else Color.White,
                                    shape = RoundedCornerShape(percent = 50),
                                    border = BorderStroke(1.dp, if (isSelected) primaryBlue else Color(0xFFC3C6D6)),
                                    modifier = Modifier.clickable { selectedCategory = cat }
                                ) {
                                    Text(
                                        text = cat,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else Color(0xFF5D5F5F)
                                    )
                                }
                            }
                        }
                    }
                }

                // FAQ List
                items(filteredFaqs) { faq ->
                    ExpandableFaqCard(faq = faq)
                }
            }
        }
    }
}

@Composable
private fun QuickSupportButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.clickable { onClick() },
        color = Color.White.copy(alpha = 0.15f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(label, style = MaterialTheme.typography.labelSmall, color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ExpandableFaqCard(faq: FaqItem) {
    var isExpanded by remember { mutableStateOf(false) }
    val spacing = LocalSpacing.current
    val primaryBlue = Color(0xFF0052CC)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded },
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        shadowElevation = 1.dp,
        border = BorderStroke(1.dp, Color(0xFFE8EDFF))
    ) {
        Column(
            modifier = Modifier.padding(spacing.md),
            verticalArrangement = Arrangement.spacedBy(spacing.xs)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = faq.question,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,
                    contentDescription = null,
                    tint = primaryBlue,
                    modifier = Modifier.size(20.dp)
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = spacing.xs)) {
                    HorizontalDivider(color = Color(0xFFF1F3FF), modifier = Modifier.padding(bottom = spacing.xs))
                    Text(
                        text = faq.answer,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF5D5F5F),
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}

private fun getMockFaqs() = listOf(
    FaqItem(
        question = "How does multi-stop pickup boarding work?",
        answer = "Drivers can add intermediate stops along their main route (e.g. Seohara → Noorpur → Chandpur → Delhi). As a passenger, you can choose any stop as your boarding point. All passengers travel to the same final destination, and backward travel is not allowed.",
        category = "Stops & Pricing"
    ),
    FaqItem(
        question = "How is my fare calculated if I board from an intermediate stop?",
        answer = "The driver enters one full-route price. When you board at an intermediate stop, your fare is automatically and proportionally calculated for the remaining journey to the final destination.",
        category = "Stops & Pricing"
    ),
    FaqItem(
        question = "Can a driver edit a ride after passengers have booked?",
        answer = "No. Once 1 or more passengers have booked seats, the ride route, stops, timing, and pricing become locked to protect confirmed passengers.",
        category = "Publishing"
    ),
    FaqItem(
        question = "What are the requirements to publish a ride as a driver?",
        answer = "To offer a ride, you must complete your driver profile: verify your 10-digit phone number, upload your valid Driver Licence (front and back), and register a 5-Seater or 7-Seater vehicle with up to 3 photos.",
        category = "Publishing"
    ),
    FaqItem(
        question = "What is the cancellation policy for passengers?",
        answer = "Free cancellation is available up to 2 hours before the scheduled departure time. After that, nominal cancellation fees may apply.",
        category = "Booking"
    ),
    FaqItem(
        question = "How does the two-way review system work?",
        answer = "After a completed ride, you can rate and review your co-traveller under 'My Rides → History'. Both passenger and driver submit separate, independent reviews.",
        category = "Safety"
    )
)
