package com.juko.app.feature.auth.presentation.otp

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.juko.app.feature.auth.domain.usecase.SendOtpUseCase
import com.juko.app.feature.auth.domain.usecase.VerifyOtpUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class OtpViewModel(
    private val sendOtpUseCase: SendOtpUseCase,
    private val verifyOtpUseCase: VerifyOtpUseCase,
    private val userId: String = "usr_001" // Placeholder for now
) : ScreenModel {

    private val _state = MutableStateFlow(OtpState())
    val state = _state.asStateFlow()

    private val _effect = Channel<OtpSideEffect>()
    val effect = _effect.receiveAsFlow()

    private var timerJob: Job? = null

    fun onEvent(event: OtpEvent) {
        when (event) {
            is OtpEvent.EmailChanged -> {
                _state.update { it.copy(email = event.value, error = null) }
            }
            is OtpEvent.SendOtpClicked -> performSendOtp()
            is OtpEvent.OtpChanged -> {
                if (event.value.length <= 4) {
                    _state.update { it.copy(otp = event.value, error = null) }
                    if (event.value.length == 4) performVerify()
                }
            }
            is OtpEvent.Submit -> performVerify()
            is OtpEvent.ResendClicked -> {
                if (_state.value.canResend) {
                    performSendOtp()
                }
            }
            is OtpEvent.BackClicked -> {
                screenModelScope.launch { _effect.send(OtpSideEffect.NavigateBack) }
            }
        }
    }

    private fun performSendOtp() {
        if (_state.value.email.isBlank()) {
            _state.update { it.copy(error = "Email is required") }
            return
        }

        screenModelScope.launch {
            _state.update { it.copy(isSendingOtp = true, error = null) }
            sendOtpUseCase(_state.value.email)
                .onSuccess {
                    _state.update { it.copy(isSendingOtp = false) }
                    startResendTimer()
                }
                .onFailure { error ->
                    _state.update { it.copy(isSendingOtp = false, error = error.message) }
                }
        }
    }

    private fun startResendTimer() {
        timerJob?.cancel()
        _state.update { it.copy(resendTimer = 28, canResend = false) }
        timerJob = screenModelScope.launch {
            while (_state.value.resendTimer > 0) {
                delay(1.seconds)
                _state.update { it.copy(resendTimer = it.resendTimer - 1) }
            }
            _state.update { it.copy(canResend = true) }
        }
    }

    private fun performVerify() {
        if (_state.value.otp.length < 4) {
            _state.update { it.copy(error = "Please enter the 4-digit OTP") }
            return
        }

        screenModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            verifyOtpUseCase(userId, _state.value.otp)
                .onSuccess {
                    _state.update { it.copy(isLoading = false) }
                    _effect.send(OtpSideEffect.NavigateToNext)
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, error = error.message) }
                    _effect.send(OtpSideEffect.ShowError(error.message ?: "Verification failed"))
                }
        }
    }
}
