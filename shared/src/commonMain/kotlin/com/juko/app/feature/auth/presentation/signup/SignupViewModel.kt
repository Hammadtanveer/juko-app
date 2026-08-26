package com.juko.app.feature.auth.presentation.signup

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.juko.app.core.util.ValidationResult
import com.juko.app.core.util.Validators
import com.juko.app.feature.auth.domain.usecase.SignupUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SignupViewModel(
    private val signupUseCase: SignupUseCase
) : ScreenModel {

    private val _state = MutableStateFlow(SignupState())
    val state = _state.asStateFlow()

    private val _effect = Channel<SignupSideEffect>()
    val effect = _effect.receiveAsFlow()

    fun onEvent(event: SignupEvent) {
        when (event) {
            is SignupEvent.ProfilePhotoSelected -> _state.update { it.copy(profilePhotoUri = event.uri) }
            is SignupEvent.NameChanged -> _state.update { it.copy(fullName = event.value, nameError = null) }
            is SignupEvent.EmailChanged -> _state.update { it.copy(email = event.value, emailError = null) }
            is SignupEvent.PhoneChanged -> _state.update { it.copy(phone = event.value, phoneError = null) }
            is SignupEvent.PasswordChanged -> _state.update { it.copy(password = event.value, passwordError = null) }
            is SignupEvent.ConfirmPasswordChanged -> _state.update { it.copy(confirmPassword = event.value, confirmPasswordError = null) }
            is SignupEvent.Submit -> performSignup()
            is SignupEvent.LoginClicked -> {
                screenModelScope.launch { _effect.send(SignupSideEffect.NavigateToLogin) }
            }
            is SignupEvent.ClearErrors -> {
                _state.update { 
                    it.copy(
                        nameError = null, 
                        emailError = null, 
                        phoneError = null, 
                        passwordError = null, 
                        confirmPasswordError = null,
                        generalError = null
                    ) 
                }
            }
        }
    }

    private fun performSignup() {
        val currentState = _state.value
        val nameValidation = Validators.validateName(currentState.fullName)
        val emailValidation = Validators.validateEmail(currentState.email)
        val phoneValidation = Validators.validatePhone(currentState.phone)
        val passwordValidation = Validators.validatePassword(currentState.password)
        
        val confirmPasswordError = if (currentState.password != currentState.confirmPassword) {
            "Passwords do not match"
        } else null

        if (nameValidation is ValidationResult.Invalid || 
            emailValidation is ValidationResult.Invalid || 
            phoneValidation is ValidationResult.Invalid || 
            passwordValidation is ValidationResult.Invalid ||
            confirmPasswordError != null) {
            
            _state.update {
                it.copy(
                    nameError = (nameValidation as? ValidationResult.Invalid)?.errorMessage,
                    emailError = (emailValidation as? ValidationResult.Invalid)?.errorMessage,
                    phoneError = (phoneValidation as? ValidationResult.Invalid)?.errorMessage,
                    passwordError = (passwordValidation as? ValidationResult.Invalid)?.errorMessage,
                    confirmPasswordError = confirmPasswordError
                )
            }
            return
        }

        screenModelScope.launch {
            _state.update { it.copy(isLoading = true, generalError = null) }

            signupUseCase(
                name = currentState.fullName,
                email = currentState.email,
                phone = currentState.phone,
                password = currentState.password
            ).onSuccess {
                _state.update { it.copy(isLoading = false) }
                _effect.send(SignupSideEffect.NavigateToLogin)
            }.onFailure { error ->
                _state.update { 
                    it.copy(
                        isLoading = false, 
                        generalError = error.message ?: "An unknown error occurred"
                    ) 
                }
                _effect.send(SignupSideEffect.ShowError(error.message ?: "Signup failed"))
            }
        }
    }
}
