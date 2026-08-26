package com.juko.app.feature.auth.presentation.signup

data class SignupState(
    val profilePhotoUri: String? = null,
    val fullName: String = "",
    val nameError: String? = null,
    val email: String = "",
    val emailError: String? = null,
    val phone: String = "",
    val phoneError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val confirmPassword: String = "",
    val confirmPasswordError: String? = null,
    val isLoading: Boolean = false,
    val generalError: String? = null
)

sealed interface SignupEvent {
    data class ProfilePhotoSelected(val uri: String) : SignupEvent
    data class NameChanged(val value: String) : SignupEvent
    data class EmailChanged(val value: String) : SignupEvent
    data class PhoneChanged(val value: String) : SignupEvent
    data class PasswordChanged(val value: String) : SignupEvent
    data class ConfirmPasswordChanged(val value: String) : SignupEvent
    data object Submit : SignupEvent
    data object LoginClicked : SignupEvent
    data object ClearErrors : SignupEvent
}

sealed interface SignupSideEffect {
    data object NavigateToLogin : SignupSideEffect
    data class ShowError(val message: String) : SignupSideEffect
}
