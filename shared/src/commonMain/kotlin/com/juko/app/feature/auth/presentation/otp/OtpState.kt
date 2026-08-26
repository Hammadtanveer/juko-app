package com.juko.app.feature.auth.presentation.otp

data class OtpState(
    val email: String = "",
    val otp: String = "",
    val isLoading: Boolean = false,
    val isSendingOtp: Boolean = false,
    val resendTimer: Int = 28,
    val canResend: Boolean = false,
    val error: String? = null
)

sealed interface OtpEvent {
    data class EmailChanged(val value: String) : OtpEvent
    data object SendOtpClicked : OtpEvent
    data class OtpChanged(val value: String) : OtpEvent
    data object Submit : OtpEvent
    data object ResendClicked : OtpEvent
    data object BackClicked : OtpEvent
}

sealed interface OtpSideEffect {
    data object NavigateToNext : OtpSideEffect
    data object NavigateBack : OtpSideEffect
    data class ShowError(val message: String) : OtpSideEffect
}
