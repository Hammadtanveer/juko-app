package com.juko.app.core.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.juko.app.core.location.PlaceSuggestion
import com.juko.app.core.presentation.theme.LocalSpacing

/**
 * Reusable location input with autocomplete dropdown, Google Places API ready.
 */
@Composable
fun LocationAutocompleteRow(
    label: String,
    city: String,
    onQueryChange: (String) -> Unit,
    suggestions: List<PlaceSuggestion>,
    onSelectSuggestion: (PlaceSuggestion) -> Unit,
    isConfirmed: Boolean,
    icon: ImageVector,
    iconColor: Color,
    iconSize: Dp = 20.dp,
    showTrack: Boolean = false,
    onRemove: (() -> Unit)? = null,
    placeholder: String = "Search location..."
) {
    val spacing = LocalSpacing.current
    val focusManager = LocalFocusManager.current
    var isFocused by remember { mutableStateOf(false) }

    Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
        // Track / Icon Column
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(40.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .padding(top = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(iconSize))
            }
            if (showTrack) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .width(2.dp)
                        .background(Color(0xFFC3C6D6))
                )
            }
        }

        // Input & Suggestions Column
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = if (showTrack) spacing.md else 0.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(label, style = MaterialTheme.typography.labelSmall, color = Color(0xFF737685))

                    BasicTextField(
                        value = city,
                        onValueChange = onQueryChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { isFocused = it.isFocused },
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                        decorationBox = { innerTextField ->
                            Box {
                                if (city.isEmpty()) {
                                    Text(
                                        text = placeholder,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = Color(0xFFC3C6D6)
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (city.isNotBlank()) {
                        IconButton(
                            onClick = { onQueryChange("") },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                Icons.Outlined.Close,
                                contentDescription = "Clear",
                                tint = Color(0xFF9E9E9E),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    if (onRemove != null) {
                        IconButton(
                            onClick = onRemove,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                Icons.Outlined.DeleteOutline,
                                contentDescription = "Remove",
                                tint = Color(0xFFBA1A1A),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // Suggestions List (Google Places Autocomplete ready)
            AnimatedVisibility(visible = isFocused && suggestions.isNotEmpty()) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    shadowElevation = 6.dp,
                    border = BorderStroke(1.dp, Color(0xFFE0E8FF))
                ) {
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        suggestions.take(4).forEach { suggestion ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onSelectSuggestion(suggestion)
                                        focusManager.clearFocus()
                                    }
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFF1F5FE)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Outlined.LocationOn,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = suggestion.primaryText,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    if (suggestion.secondaryText.isNotBlank()) {
                                        Text(
                                            text = suggestion.secondaryText,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFF737685)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
