package com.juko.app.feature.auth.domain.usecase

import com.juko.app.feature.auth.domain.repository.AuthRepository

/**
 * Trigger OTP for password recovery.
 */
class SendOtpUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String): Result<Unit> {
        return repository.forgotPassword(email = email)
    }
}
