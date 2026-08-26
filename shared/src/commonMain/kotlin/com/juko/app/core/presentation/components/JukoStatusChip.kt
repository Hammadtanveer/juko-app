package com.juko.app.core.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.juko.app.core.presentation.theme.LocalSpacing

enum class RideStatus {
    Scheduled,
    Active,
    Completed
}

@Composable
fun JukoStatusChip(
    status: RideStatus,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current

    val (label, backgroundColor, contentColor) = when (status) {
        RideStatus.Scheduled -> Triple(
            "Scheduled",
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.onSurface
        )
        RideStatus.Active -> Triple(
            "Active",
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.primary
        )
        RideStatus.Completed -> Triple(
            "Completed",
            MaterialTheme.colorScheme.tertiaryContainer,
            MaterialTheme.colorScheme.tertiary
        )
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(percent = 50))
            .background(backgroundColor)
            .padding(horizontal = spacing.sm, vertical = spacing.xs)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = contentColor
        )
    }
}
