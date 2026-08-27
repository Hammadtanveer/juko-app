package com.juko.app.core.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage

@Composable
fun JukoAvatar(
    imageUrl: String?,
    initials: String? = null,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp
) {
    val shapedModifier = modifier
        .size(size)
        .clip(CircleShape)
        .background(MaterialTheme.colorScheme.surfaceVariant)

    if (!imageUrl.isNullOrBlank()) {
        AsyncImage(
            model = imageUrl,
            contentDescription = "Avatar",
            modifier = shapedModifier,
            contentScale = ContentScale.Crop
        )
    } else if (!initials.isNullOrBlank()) {
        Box(modifier = shapedModifier, contentAlignment = Alignment.Center) {
            Text(
                text = initials.uppercase(),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = (size.value * 0.4).sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        Box(modifier = shapedModifier, contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Outlined.Person,
                contentDescription = "Avatar",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
