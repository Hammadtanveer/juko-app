package com.juko.app.feature.auth.domain.usecase

import com.juko.app.feature.auth.domain.model.User
import com.juko.app.feature.auth.domain.repository.AuthRepository

/**
 * Verify OTP during signup or password recovery.
 */
class VerifyOtpUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(userId: String, otp: String): Result<User> {
        return repository.verifyOtp(userId = userId, otp = otp)
    }
}
