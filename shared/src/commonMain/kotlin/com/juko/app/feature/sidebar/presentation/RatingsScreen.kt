package com.juko.app.feature.sidebar.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.juko.app.core.presentation.components.JukoAvatar
import com.juko.app.core.presentation.theme.LocalSpacing

data class UserReviewItem(
    val id: String,
    val reviewerName: String,
    val reviewerAvatar: String?,
    val role: String, // "Passenger" or "Driver"
    val route: String,
    val rating: Int,
    val date: String,
    val comment: String
)

class RatingsScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val spacing = LocalSpacing.current
        val primaryBlue = Color(0xFF0052CC)

        val reviews = remember { getMockReviews() }
        val averageRating = 4.8
        val totalReviews = reviews.size

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
                            text = "Ratings & Reviews",
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
            if (reviews.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(spacing.sm)
                    ) {
                        Icon(
                            Icons.Outlined.StarBorder,
                            contentDescription = null,
                            tint = Color(0xFFC3C6D6),
                            modifier = Modifier.size(64.dp)
                        )
                        Text(
                            text = "No reviews yet",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Complete rides with other members to receive ratings and feedback.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF737685),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 32.dp)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = spacing.edgeMargin),
                    contentPadding = PaddingValues(top = spacing.md, bottom = 48.dp),
                    verticalArrangement = Arrangement.spacedBy(spacing.md)
                ) {
                    // Rating Overview Card
                    item {
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
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                        Text(
                                            text = averageRating.toString(),
                                            style = MaterialTheme.typography.displayMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = primaryBlue
                                        )
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                                        ) {
                                            repeat(5) { index ->
                                                Icon(
                                                    Icons.Filled.Star,
                                                    contentDescription = null,
                                                    tint = if (index < 5) Color(0xFFF59E0B) else Color(0xFFC3C6D6),
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                        Text(
                                            text = "Based on $totalReviews reviews",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFF737685)
                                        )
                                    }

                                    // Rating Breakdown Bars
                                    Column(
                                        modifier = Modifier.width(160.dp),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        RatingBarRow(stars = 5, progress = 0.85f, count = "18")
                                        RatingBarRow(stars = 4, progress = 0.10f, count = "2")
                                        RatingBarRow(stars = 3, progress = 0.05f, count = "1")
                                        RatingBarRow(stars = 2, progress = 0.00f, count = "0")
                                        RatingBarRow(stars = 1, progress = 0.00f, count = "0")
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Text(
                            text = "RECENT REVIEWS",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF737685),
                            modifier = Modifier.padding(top = spacing.xs)
                        )
                    }

                    // Reviews List
                    items(reviews) { review ->
                        ReviewCard(review = review)
                    }
                }
            }
        }
    }
}

@Composable
private fun RatingBarRow(stars: Int, progress: Float, count: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = "${stars}★",
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF737685),
            modifier = Modifier.width(20.dp)
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(Color(0xFFE8EDFF))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(0xFFF59E0B))
            )
        }
        Text(
            text = count,
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF737685),
            modifier = Modifier.width(16.dp)
        )
    }
}

@Composable
private fun ReviewCard(review: UserReviewItem) {
    val spacing = LocalSpacing.current
    val primaryBlue = Color(0xFF0052CC)

    Surface(
        modifier = Modifier.fillMaxWidth(),
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(spacing.sm)
                ) {
                    JukoAvatar(imageUrl = review.reviewerAvatar, size = 40.dp)
                    Column {
                        Text(
                            text = review.reviewerName,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Surface(
                            color = Color(0xFFE8EDFF),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = review.role.uppercase(),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                color = primaryBlue,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Row(horizontalArrangement = Arrangement.spacedBy(1.dp)) {
                        repeat(5) { i ->
                            Icon(
                                Icons.Filled.Star,
                                contentDescription = null,
                                tint = if (i < review.rating) Color(0xFFF59E0B) else Color(0xFFC3C6D6),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                    Text(
                        text = review.date,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF737685)
                    )
                }
            }

            Text(
                text = "Route: ${review.route}",
                style = MaterialTheme.typography.bodySmall,
                color = primaryBlue,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = review.comment,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

private fun getMockReviews() = listOf(
    UserReviewItem(
        id = "rev_1",
        reviewerName = "Rahul Sharma",
        reviewerAvatar = "https://lh3.googleusercontent.com/aida-public/AB6AXuCKYEb47azE7KIsX7pIzB9mjz1RKlj_e8gPNrELvoNxr4a4ZbO81La7WXxWwGuBD-2oQWPHrwDuTRXv1G8uEuA-RFEFlIrMbuUqxPqEyND6lnxpPkr390ck8Lk66bqK1ziDQZwg5V9JSommvmFtM0wURjqHnMp9lErkm5-rTMsXV6xmevXkm-vngAc2TmsP1ntYMnk-QMM6UNewnh-dVrA9XA3G7Y1Td4TGZpheU9qWZsJ0O5IwK7ZU",
        role = "Passenger",
        route = "Seohara → Delhi",
        rating = 5,
        date = "2 days ago",
        comment = "Great co-passenger! Very polite, on time at the boarding stop, and respected ride rules."
    ),
    UserReviewItem(
        id = "rev_2",
        reviewerName = "Anita Kumari",
        reviewerAvatar = "https://lh3.googleusercontent.com/aida-public/AB6AXuDjCFi6hSeikXO26byFKauht4PmxZK204AR3XCqdXpOuM5L__mJ2cTmXDvEcl_G59mMY5F1ZCFx3mDLA5t_LhbfFRCcVN0OADih56H0naDNOo8O80lHswNiCLVi9_wrMvpla3t4r3yZ9nfpKnmLpJJPO7F1Xqg4V1JoPCrzXjc5--k7En9ONj0L9ibdyah-3MncNX0gjvGcHgaPoTqSHFKGhFOl92QEL9O7jfz4ixs01mRBBK9SaEN0",
        role = "Driver",
        route = "Chandpur → Delhi",
        rating = 5,
        date = "1 week ago",
        comment = "Punctual passenger, communicated clearly before boarding at Chandpur stop. Highly recommended!"
    ),
    UserReviewItem(
        id = "rev_3",
        reviewerName = "Vikram Malik",
        reviewerAvatar = "https://lh3.googleusercontent.com/aida-public/AB6AXuAzRflz8ZOhS_ubE7CLsRLWaTuwXxOwkKmx_r9WvvYDeKeKoSu81e41n0dn2HdioLA3eRHuc6VwnblnJi0CnpVfPZ5DZxOmz5jI4RpxKGlELzGrNG2u-G5Bxyuk0VJ4Yo16GPZ4SDli21_imo90sbVbK2XKuJt7TUQeW19Uibm3ugwJupwHgjVaeizjdwVpAxl4f8UXwCAAztao3hRZBLxFL16rd4xuUufYf6aSYTey65FLymTlVpUi",
        role = "Passenger",
        route = "Dhampur → Delhi",
        rating = 4,
        date = "2 weeks ago",
        comment = "Pleasant journey together, smooth ride and good conversation."
    )
)
