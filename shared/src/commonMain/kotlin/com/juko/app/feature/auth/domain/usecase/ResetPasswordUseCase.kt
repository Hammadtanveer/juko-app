package com.juko.app.feature.auth.domain.usecase

import com.juko.app.feature.auth.domain.repository.AuthRepository

/**
 * Reset password after successful OTP verification.
 */
class ResetPasswordUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, otp: String, newPassword: String): Result<Unit> {
        return repository.resetPassword(email = email, otp = otp, newPassword = newPassword)
    }
}
