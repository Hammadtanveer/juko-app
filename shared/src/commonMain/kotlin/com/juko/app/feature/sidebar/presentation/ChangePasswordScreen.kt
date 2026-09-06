package com.juko.app.feature.sidebar.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.juko.app.core.presentation.components.JukoButton
import com.juko.app.core.presentation.theme.LocalSpacing
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ChangePasswordScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val spacing = LocalSpacing.current
        val focusManager = LocalFocusManager.current
        val snackbarHostState = remember { SnackbarHostState() }
        val coroutineScope = rememberCoroutineScope()
        val primaryBlue = Color(0xFF0052CC)

        var currentPassword by remember { mutableStateOf("") }
        var newPassword by remember { mutableStateOf("") }
        var confirmPassword by remember { mutableStateOf("") }

        var currentPasswordVisible by remember { mutableStateOf(false) }
        var newPasswordVisible by remember { mutableStateOf(false) }
        var confirmPasswordVisible by remember { mutableStateOf(false) }

        var currentPasswordError by remember { mutableStateOf<String?>(null) }
        var newPasswordError by remember { mutableStateOf<String?>(null) }
        var confirmPasswordError by remember { mutableStateOf<String?>(null) }
        var isLoading by remember { mutableStateOf(false) }

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
                            text = "Change Password",
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(spacing.edgeMargin),
                verticalArrangement = Arrangement.spacedBy(spacing.lg)
            ) {
                // Info Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFE8EDFF),
                    border = BorderStroke(1.dp, Color(0xFFDAE2FF))
                ) {
                    Row(
                        modifier = Modifier.padding(spacing.md),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(spacing.sm)
                    ) {
                        Icon(Icons.Outlined.Lock, contentDescription = null, tint = primaryBlue, modifier = Modifier.size(24.dp))
                        Text(
                            text = "Your new password must be at least 8 characters long and differ from previous passwords.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF041B3C)
                        )
                    }
                }

                // Password Form Fields Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    shadowElevation = 1.dp,
                    border = BorderStroke(1.dp, Color(0xFFE8EDFF))
                ) {
                    Column(
                        modifier = Modifier.padding(spacing.md),
                        verticalArrangement = Arrangement.spacedBy(spacing.md)
                    ) {
                        // Current Password
                        PasswordField(
                            label = "CURRENT PASSWORD",
                            value = currentPassword,
                            onValueChange = {
                                currentPassword = it
                                currentPasswordError = null
                            },
                            placeholder = "Enter your current password",
                            isVisible = currentPasswordVisible,
                            onToggleVisibility = { currentPasswordVisible = !currentPasswordVisible },
                            error = currentPasswordError,
                            imeAction = ImeAction.Next,
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        )

                        HorizontalDivider(color = Color(0xFFF1F3FF))

                        // New Password
                        PasswordField(
                            label = "NEW PASSWORD",
                            value = newPassword,
                            onValueChange = {
                                newPassword = it
                                newPasswordError = null
                            },
                            placeholder = "Enter at least 8 characters",
                            isVisible = newPasswordVisible,
                            onToggleVisibility = { newPasswordVisible = !newPasswordVisible },
                            error = newPasswordError,
                            imeAction = ImeAction.Next,
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        )

                        // Confirm New Password
                        PasswordField(
                            label = "CONFIRM NEW PASSWORD",
                            value = confirmPassword,
                            onValueChange = {
                                confirmPassword = it
                                confirmPasswordError = null
                            },
                            placeholder = "Re-enter your new password",
                            isVisible = confirmPasswordVisible,
                            onToggleVisibility = { confirmPasswordVisible = !confirmPasswordVisible },
                            error = confirmPasswordError,
                            imeAction = ImeAction.Done,
                            onNext = { focusManager.clearFocus() }
                        )
                    }
                }

                // Update Button
                JukoButton(
                    text = "Update Password",
                    isLoading = isLoading,
                    onClick = {
                        focusManager.clearFocus()
                        var hasError = false

                        if (currentPassword.isBlank()) {
                            currentPasswordError = "Please enter your current password"
                            hasError = true
                        }
                        if (newPassword.length < 8) {
                            newPasswordError = "Password must be at least 8 characters long"
                            hasError = true
                        }
                        if (newPassword != confirmPassword) {
                            confirmPasswordError = "Passwords do not match"
                            hasError = true
                        }

                        if (!hasError) {
                            coroutineScope.launch {
                                isLoading = true
                                delay(600)
                                isLoading = false
                                snackbarHostState.showSnackbar("Password updated successfully!")
                                delay(400)
                                navigator.pop()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    leadingIcon = {
                        Icon(Icons.Outlined.CheckCircle, contentDescription = null)
                    }
                )
            }
        }
    }
}

@Composable
private fun PasswordField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isVisible: Boolean,
    onToggleVisibility: () -> Unit,
    error: String?,
    imeAction: ImeAction,
    onNext: () -> Unit
) {
    val spacing = LocalSpacing.current
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF737685),
            fontWeight = FontWeight.Bold
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text(placeholder, color = Color(0xFFC3C6D6)) },
            isError = error != null,
            visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (isVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                IconButton(onClick = onToggleVisibility) {
                    Icon(imageVector = image, contentDescription = null, tint = Color(0xFF737685))
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = imeAction
            ),
            keyboardActions = KeyboardActions(
                onNext = { onNext() },
                onDone = { onNext() }
            ),
            shape = RoundedCornerShape(8.dp)
        )
        if (error != null) {
            Text(
                text = error,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 4.dp, top = 2.dp)
            )
        }
    }
}
