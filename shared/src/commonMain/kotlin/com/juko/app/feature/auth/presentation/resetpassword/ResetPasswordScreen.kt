package com.juko.app.feature.auth.presentation.resetpassword

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.juko.app.core.presentation.theme.LocalSpacing
import com.juko.app.feature.auth.domain.usecase.ResetPasswordUseCase
import com.juko.app.feature.auth.presentation.auth.AuthScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

class ResetPasswordScreen(
    val email: String = "",
    val otp: String = ""
) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val resetPasswordUseCase = koinInject<ResetPasswordUseCase>()
        val spacing = LocalSpacing.current
        val coroutineScope = rememberCoroutineScope()
        val focusManager = LocalFocusManager.current
        val scrollState = rememberScrollState()

        var newPassword by remember { mutableStateOf("") }
        var confirmPassword by remember { mutableStateOf("") }
        var isPasswordVisible by remember { mutableStateOf(false) }
        var isConfirmPasswordVisible by remember { mutableStateOf(false) }

        var isLoading by remember { mutableStateOf(false) }
        var errorMessage by remember { mutableStateOf<String?>(null) }
        var isSuccess by remember { mutableStateOf(false) }

        val hasMinLength = newPassword.length >= 6
        val passwordsMatch = newPassword.isNotEmpty() && newPassword == confirmPassword

        fun navigateToLogin() {
            val hasAuthScreen = navigator.items.any { it is AuthScreen }
            if (hasAuthScreen) {
                navigator.popUntil { it is AuthScreen }
            } else {
                navigator.replaceAll(AuthScreen(initialTab = 0))
            }
        }

        fun performReset() {
            focusManager.clearFocus()
            errorMessage = null

            if (newPassword.isBlank()) {
                errorMessage = "Please enter a new password"
                return
            }
            if (newPassword.length < 6) {
                errorMessage = "Password must be at least 6 characters"
                return
            }
            if (newPassword != confirmPassword) {
                errorMessage = "Passwords do not match"
                return
            }

            coroutineScope.launch {
                isLoading = true
                val targetEmail = email.ifBlank { "user@juko.app" }
                val targetOtp = otp.ifBlank { "1234" }

                val result = resetPasswordUseCase(
                    email = targetEmail,
                    otp = targetOtp,
                    newPassword = newPassword
                )

                isLoading = false
                result.onSuccess {
                    isSuccess = true
                    delay(1200)
                    navigateToLogin()
                }.onFailure { error ->
                    errorMessage = error.message ?: "Failed to reset password. Please try again."
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Atmospheric Background Glow
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 100.dp, y = (-100).dp)
                    .size(256.dp)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f), CircleShape)
                    .blur(80.dp)
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = (-150).dp, y = 150.dp)
                    .size(384.dp)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.10f), CircleShape)
                    .blur(100.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = spacing.edgeMargin)
                    .statusBarsPadding()
                    .navigationBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Navigation Bar with Back Button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = spacing.md),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { navigator.pop() },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(spacing.sm))

                // Header Section
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(spacing.xs)
                ) {
                    Text(
                        text = "JUKO",
                        style = MaterialTheme.typography.displayLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Set new password for your account",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (email.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Text(
                                text = email,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(spacing.xl))

                // Form Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier.padding(spacing.md),
                        verticalArrangement = Arrangement.spacedBy(spacing.md)
                    ) {
                        Text(
                            text = "CREATE NEW PASSWORD",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )

                        // New Password Field
                        OutlinedTextField(
                            value = newPassword,
                            onValueChange = {
                                newPassword = it
                                errorMessage = null
                            },
                            label = { Text("New Password") },
                            placeholder = { Text("Enter at least 6 characters") },
                            leadingIcon = {
                                Icon(Icons.Outlined.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            },
                            trailingIcon = {
                                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                    Icon(
                                        imageVector = if (isPasswordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                                        contentDescription = if (isPasswordVisible) "Hide password" else "Show password"
                                    )
                                }
                            },
                            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Next
                            ),
                            singleLine = true,
                            shape = MaterialTheme.shapes.small,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                            )
                        )

                        // Confirm Password Field
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = {
                                confirmPassword = it
                                errorMessage = null
                            },
                            label = { Text("Confirm New Password") },
                            placeholder = { Text("Re-enter new password") },
                            leadingIcon = {
                                Icon(Icons.Outlined.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            },
                            trailingIcon = {
                                IconButton(onClick = { isConfirmPasswordVisible = !isConfirmPasswordVisible }) {
                                    Icon(
                                        imageVector = if (isConfirmPasswordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                                        contentDescription = if (isConfirmPasswordVisible) "Hide password" else "Show password"
                                    )
                                }
                            },
                            visualTransformation = if (isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(onDone = { performReset() }),
                            singleLine = true,
                            shape = MaterialTheme.shapes.small,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                            )
                        )

                        // Password Match & Strength Indicators
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            PasswordRequirementItem(
                                satisfied = hasMinLength,
                                text = "At least 6 characters long"
                            )
                            PasswordRequirementItem(
                                satisfied = passwordsMatch,
                                text = "Passwords match"
                            )
                        }

                        // Error Banner
                        if (errorMessage != null) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.6f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        Icons.Outlined.ErrorOutline,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = errorMessage.orEmpty(),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.error,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        // Success Banner
                        if (isSuccess) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFE8F5E9),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        Icons.Outlined.CheckCircle,
                                        contentDescription = null,
                                        tint = Color(0xFF2E7D32),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = "Password reset successfully! Redirecting to login...",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF2E7D32),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }

                        // Submit Button
                        Button(
                            onClick = { performReset() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(8.dp),
                            enabled = !isLoading && !isSuccess,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(spacing.xs)
                                ) {
                                    Icon(Icons.Outlined.CheckCircle, contentDescription = null)
                                    Text(
                                        text = "Reset Password",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(spacing.xl))

                // Back to Login Footer
                Text(
                    text = "Back to Login",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { navigateToLogin() }
                )

                Spacer(modifier = Modifier.height(spacing.xl))
            }
        }
    }
}

@Composable
private fun PasswordRequirementItem(satisfied: Boolean, text: String) {
    val iconTint = if (satisfied) Color(0xFF2E7D32) else MaterialTheme.colorScheme.outline
    val textColor = if (satisfied) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = Icons.Outlined.Check,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
            color = textColor,
            fontWeight = if (satisfied) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}
