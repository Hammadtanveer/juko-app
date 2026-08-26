package com.juko.app.feature.auth.presentation.auth

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.AddAPhoto
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.juko.app.core.presentation.components.JukoButton
import com.juko.app.core.presentation.components.JukoSegmentedControl
import com.juko.app.core.presentation.components.JukoTextField
import com.juko.app.core.presentation.theme.LocalSpacing
import com.juko.app.feature.auth.presentation.login.LoginEvent
import com.juko.app.feature.auth.presentation.login.LoginSideEffect
import com.juko.app.feature.auth.presentation.login.LoginState
import com.juko.app.feature.auth.presentation.login.LoginViewModel
import com.juko.app.feature.auth.presentation.otp.OtpScreen
import com.juko.app.feature.auth.presentation.signup.SignupEvent
import com.juko.app.feature.auth.presentation.signup.SignupSideEffect
import com.juko.app.feature.auth.presentation.signup.SignupState
import com.juko.app.feature.auth.presentation.signup.SignupViewModel
import com.juko.app.feature.home.presentation.HomeScreen
import cafe.adriel.voyager.koin.getScreenModel

class AuthScreen : Screen {
    @Composable
    override fun Content() {
        val loginViewModel = getScreenModel<LoginViewModel>()
        val signupViewModel = getScreenModel<SignupViewModel>()
        
        val loginState by loginViewModel.state.collectAsState()
        val signupState by signupViewModel.state.collectAsState()
        
        val navigator = LocalNavigator.currentOrThrow
        var selectedTab by remember { mutableStateOf(0) }

        LaunchedEffect(Unit) {
            loginViewModel.effect.collect { effect ->
                when (effect) {
                    is LoginSideEffect.NavigateToHome -> navigator.replaceAll(HomeScreen())
                    is LoginSideEffect.NavigateToSignup -> selectedTab = 1
                    is LoginSideEffect.NavigateToForgotPassword -> navigator.push(OtpScreen())
                    is LoginSideEffect.ShowError -> { /* Show error */ }
                }
            }
        }

        LaunchedEffect(Unit) {
            signupViewModel.effect.collect { effect ->
                when (effect) {
                    SignupSideEffect.NavigateToLogin -> selectedTab = 0
                    is SignupSideEffect.ShowError -> { /* Show error */ }
                }
            }
        }

        AuthContent(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it },
            loginState = loginState,
            onLoginEvent = loginViewModel::onEvent,
            signupState = signupState,
            onSignupEvent = signupViewModel::onEvent
        )
    }
}

@Composable
private fun AuthContent(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    loginState: LoginState,
    onLoginEvent: (LoginEvent) -> Unit,
    signupState: SignupState,
    onSignupEvent: (SignupEvent) -> Unit
) {
    val spacing = LocalSpacing.current
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Atmospheric Background Decoration
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
                .padding(horizontal = spacing.edgeMargin, vertical = spacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
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
                    text = if (selectedTab == 0) "Move forward together with Juko" else "Create your account to start",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(spacing.xl))

            // Segmented Control
            JukoSegmentedControl(
                options = listOf("Login", "Sign Up"),
                selectedIndex = selectedTab,
                onOptionSelected = onTabSelected,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(spacing.lg))

            if (selectedTab == 0) {
                LoginForm(state = loginState, onEvent = onLoginEvent)
            } else {
                SignupForm(state = signupState, onEvent = onSignupEvent)
            }

            Spacer(modifier = Modifier.height(spacing.xl))

            // Footer Section
            Row(
                modifier = Modifier
                    .clickable { onTabSelected(if (selectedTab == 0) 1 else 0) },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (selectedTab == 0) "Don't have an account? " else "Already have an account? ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = if (selectedTab == 0) "Sign up" else "Login",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun LoginForm(
    state: LoginState,
    onEvent: (LoginEvent) -> Unit
) {
    val spacing = LocalSpacing.current
    var passwordVisible by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(spacing.md),
            verticalArrangement = Arrangement.spacedBy(spacing.lg)
        ) {
            JukoTextField(
                value = state.email,
                onValueChange = { onEvent(LoginEvent.EmailChanged(it)) },
                label = "EMAIL ADDRESS",
                placeholder = "name@example.com",
                errorText = state.emailError,
                modifier = Modifier.fillMaxWidth()
            )

            Column(verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "PASSWORD",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "FORGOT?",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onEvent(LoginEvent.ForgotPasswordClicked) }
                    )
                }

                OutlinedTextField(
                    value = state.password,
                    onValueChange = { onEvent(LoginEvent.PasswordChanged(it)) },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    placeholder = {
                        Text(
                            text = "••••••••",
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                    },
                    isError = state.passwordError != null,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, contentDescription = null)
                        }
                    },
                    shape = MaterialTheme.shapes.small,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                        errorBorderColor = MaterialTheme.colorScheme.error
                    )
                )
                
                if (state.passwordError != null) {
                    Text(
                        text = state.passwordError,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            if (state.generalError != null) {
                Text(
                    text = state.generalError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            JukoButton(
                text = "Login",
                onClick = { onEvent(LoginEvent.Submit) },
                isLoading = state.isLoading,
                modifier = Modifier.fillMaxWidth().height(56.dp)
            )
        }
    }
}

@Composable
private fun SignupForm(
    state: SignupState,
    onEvent: (SignupEvent) -> Unit
) {
    val spacing = LocalSpacing.current
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(spacing.md),
            verticalArrangement = Arrangement.spacedBy(spacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Photo Picker
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(spacing.xs)
            ) {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clickable { /* TODO: Pick Photo */ }
                ) {
                    val primaryColor = MaterialTheme.colorScheme.primary
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawCircle(
                            color = primaryColor.copy(alpha = 0.2f),
                            style = Stroke(
                                width = 2.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                            )
                        )
                    }
                    
                    Icon(
                        imageVector = Icons.Outlined.AddAPhoto,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp).align(Alignment.Center),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(28.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        shadowElevation = 2.dp
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = null,
                            modifier = Modifier.padding(6.dp),
                            tint = Color.White
                        )
                    }
                }
                Text(
                    text = "UPLOAD PROFILE PHOTO",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            JukoTextField(
                value = state.fullName,
                onValueChange = { onEvent(SignupEvent.NameChanged(it)) },
                label = "FULL NAME",
                placeholder = "John Doe",
                errorText = state.nameError,
                modifier = Modifier.fillMaxWidth()
            )

            JukoTextField(
                value = state.email,
                onValueChange = { onEvent(SignupEvent.EmailChanged(it)) },
                label = "EMAIL ADDRESS",
                placeholder = "john.doe@email.com",
                errorText = state.emailError,
                modifier = Modifier.fillMaxWidth()
            )

            Column(verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
                Text(
                    text = "PHONE NUMBER",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing.sm)
                ) {
                    Surface(
                        modifier = Modifier.width(80.dp).height(56.dp),
                        shape = MaterialTheme.shapes.small,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        color = MaterialTheme.colorScheme.surfaceContainer
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "+91",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    OutlinedTextField(
                        value = state.phone,
                        onValueChange = { onEvent(SignupEvent.PhoneChanged(it)) },
                        modifier = Modifier.weight(1f).height(56.dp),
                        placeholder = {
                            Text(
                                text = "1234567890",
                                color = MaterialTheme.colorScheme.outlineVariant
                            )
                        },
                        isError = state.phoneError != null,
                        shape = MaterialTheme.shapes.small,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        )
                    )
                }
                if (state.phoneError != null) {
                    Text(
                        text = state.phoneError,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            JukoTextField(
                value = state.password,
                onValueChange = { onEvent(SignupEvent.PasswordChanged(it)) },
                label = "PASSWORD",
                placeholder = "••••••••",
                errorText = state.passwordError,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = null)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            JukoTextField(
                value = state.confirmPassword,
                onValueChange = { onEvent(SignupEvent.ConfirmPasswordChanged(it)) },
                label = "CONFIRM PASSWORD",
                placeholder = "••••••••",
                errorText = state.confirmPasswordError,
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(imageVector = image, contentDescription = null)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            if (state.generalError != null) {
                Text(
                    text = state.generalError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "By signing up, you agree to our",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Terms of Service",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "and",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Privacy Policy.",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            JukoButton(
                text = "Create Account",
                onClick = { onEvent(SignupEvent.Submit) },
                isLoading = state.isLoading,
                modifier = Modifier.fillMaxWidth().height(48.dp)
            )
        }
    }
}
