package com.juko.app.feature.auth.presentation.login

data class LoginState(
    val email: String = "",
    val emailError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val isLoading: Boolean = false,
    val generalError: String? = null
)

sealed interface LoginEvent {
    data class EmailChanged(val value: String) : LoginEvent
    data class PasswordChanged(val value: String) : LoginEvent
    data object Submit : LoginEvent
    data object SignupClicked : LoginEvent
    data object ForgotPasswordClicked : LoginEvent
    data object ClearErrors : LoginEvent
}

sealed interface LoginSideEffect {
    data object NavigateToHome : LoginSideEffect
    data object NavigateToSignup : LoginSideEffect
    data object NavigateToForgotPassword : LoginSideEffect
    data class ShowError(val message: String) : LoginSideEffect
}
