package com.juko.app.feature.home.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import com.juko.app.core.presentation.components.JukoButton
import com.juko.app.core.presentation.theme.LocalSpacing

class HomeScreen : Screen {
    @Composable
    override fun Content() {
        val spacing = LocalSpacing.current
        
        Scaffold(
            topBar = {
                HomeHeader()
            },
            containerColor = MaterialTheme.colorScheme.background,
            contentWindowInsets = WindowInsets(0, 0, 0, 0)
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = spacing.edgeMargin)
            ) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    HeroSection()
                }
                
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    SearchCard()
                }
                
                item {
                    Spacer(modifier = Modifier.height(spacing.lg))
                    RecentSearchesHeader()
                }
                
                items(recentSearchList) { search ->
                    RecentSearchCard(search)
                    Spacer(modifier = Modifier.height(spacing.md))
                }
                
                item {
                    Spacer(modifier = Modifier.height(spacing.xl))
                }
            }
        }
    }
}

@Composable
private fun HomeHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .height(56.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { /* TODO */ }) {
                Icon(Icons.Outlined.Menu, contentDescription = "Menu")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Juko",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        
        IconButton(
            onClick = { /* TODO */ },
            modifier = Modifier.clip(CircleShape).background(MaterialTheme.colorScheme.surface)
        ) {
            Icon(Icons.Outlined.Notifications, contentDescription = "Notifications")
        }
    }
}

@Composable
private fun HeroSection() {
    val spacing = LocalSpacing.current
    Column(verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
        Text(
            text = "Hello, Alex! 👋",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.secondary
        )
        Text(
            text = "Travel anywhere together. Spend smarter.",
            style = MaterialTheme.typography.displayLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 40.sp
        )
    }
}

@Composable
private fun SearchCard() {
    val spacing = LocalSpacing.current
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(spacing.md)) {
            SearchInputRow(
                label = "FROM",
                icon = Icons.Outlined.TripOrigin,
                value = "City, station, place"
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = spacing.md), color = MaterialTheme.colorScheme.outlineVariant)
            SearchInputRow(
                label = "TO",
                icon = Icons.Outlined.LocationOn,
                value = "City, station, place"
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = spacing.md), color = MaterialTheme.colorScheme.outlineVariant)
            
            Row(modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.weight(1f)) {
                    SearchInputRow(
                        label = "DATE",
                        icon = Icons.Outlined.CalendarToday,
                        value = "Today"
                    )
                }
                Box(modifier = Modifier.width(1.dp).height(40.dp).background(MaterialTheme.colorScheme.outlineVariant).align(Alignment.CenterVertically))
                Box(modifier = Modifier.weight(1f)) {
                    SearchInputRow(
                        label = "PASSENGERS",
                        icon = Icons.Outlined.Person,
                        value = "1 passenger"
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(spacing.lg))
            
            JukoButton(
                text = "Search",
                onClick = { /* TODO */ },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(8.dp),
                leadingIcon = {
                    Icon(Icons.Outlined.Search, contentDescription = null)
                }
            )
        }
    }
}

@Composable
private fun SearchInputRow(
    label: String,
    icon: ImageVector,
    value: String
) {
    val spacing = LocalSpacing.current
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.secondary,
            fontWeight = FontWeight.Bold
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(spacing.sm))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = if (value.contains("City")) MaterialTheme.colorScheme.outlineVariant else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun RecentSearchesHeader() {
    val spacing = LocalSpacing.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Recent searches",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
        TextButton(onClick = { /* TODO */ }) {
            Text(
                text = "CLEAR ALL",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun RecentSearchCard(search: RecentSearch) {
    val spacing = LocalSpacing.current
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .padding(spacing.md)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.Schedule,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.width(spacing.md))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${search.from} → ${search.to}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${search.passengers} passengers",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            Icon(
                Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outlineVariant
            )
        }
    }
}

data class RecentSearch(
    val from: String,
    val to: String,
    val passengers: Int
)

private val recentSearchList = listOf(
    RecentSearch("Seohara", "Delhi", 2),
    RecentSearch("Seohara", "Pune", 3)
)
