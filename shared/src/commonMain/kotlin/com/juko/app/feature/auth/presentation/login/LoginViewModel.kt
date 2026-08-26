package com.juko.app.feature.auth.presentation.login

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.juko.app.core.util.ValidationResult
import com.juko.app.core.util.Validators
import com.juko.app.feature.auth.domain.usecase.LoginUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginUseCase: LoginUseCase
) : ScreenModel {

    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    private val _effect = Channel<LoginSideEffect>()
    val effect = _effect.receiveAsFlow()

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.EmailChanged -> {
                _state.update { it.copy(email = event.value, emailError = null) }
            }
            is LoginEvent.PasswordChanged -> {
                _state.update { it.copy(password = event.value, passwordError = null) }
            }
            is LoginEvent.Submit -> performLogin()
            is LoginEvent.SignupClicked -> {
                screenModelScope.launch { _effect.send(LoginSideEffect.NavigateToSignup) }
            }
            is LoginEvent.ForgotPasswordClicked -> {
                screenModelScope.launch { _effect.send(LoginSideEffect.NavigateToForgotPassword) }
            }
            is LoginEvent.ClearErrors -> {
                _state.update { it.copy(emailError = null, passwordError = null, generalError = null) }
            }
        }
    }

    private fun performLogin() {
        val emailValidation = Validators.validateEmail(_state.value.email)
        val passwordValidation = Validators.validatePassword(_state.value.password)

        if (emailValidation is ValidationResult.Invalid || passwordValidation is ValidationResult.Invalid) {
            _state.update {
                it.copy(
                    emailError = (emailValidation as? ValidationResult.Invalid)?.errorMessage,
                    passwordError = (passwordValidation as? ValidationResult.Invalid)?.errorMessage
                )
            }
            return
        }

        screenModelScope.launch {
            _state.update { it.copy(isLoading = true, generalError = null) }

            loginUseCase(_state.value.email, _state.value.password)
                .onSuccess {
                    _state.update { it.copy(isLoading = false) }
                    _effect.send(LoginSideEffect.NavigateToHome)
                }
                .onFailure { error ->
                    _state.update { 
                        it.copy(
                            isLoading = false, 
                            generalError = error.message ?: "An unknown error occurred"
                        ) 
                    }
                    _effect.send(LoginSideEffect.ShowError(error.message ?: "Login failed"))
                }
        }
    }
}
