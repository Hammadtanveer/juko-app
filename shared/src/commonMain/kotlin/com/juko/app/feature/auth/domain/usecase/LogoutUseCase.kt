package com.juko.app.feature.auth.domain.usecase

import com.juko.app.feature.auth.domain.repository.AuthRepository

/**
 * Logout the user and clear tokens.
 */
class LogoutUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return repository.logout()
    }
}
