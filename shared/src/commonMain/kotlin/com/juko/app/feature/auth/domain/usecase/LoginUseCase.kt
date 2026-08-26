package com.juko.app.feature.auth.domain.usecase

import com.juko.app.feature.auth.domain.model.User
import com.juko.app.feature.auth.domain.repository.AuthRepository

class LoginUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        return repository.login(email = email, password = password)
    }
}
