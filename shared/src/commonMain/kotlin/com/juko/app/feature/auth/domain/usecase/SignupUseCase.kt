package com.juko.app.feature.auth.domain.usecase

import com.juko.app.feature.auth.domain.model.User
import com.juko.app.feature.auth.domain.repository.AuthRepository

class SignupUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(
        name: String,
        email: String,
        phone: String,
        password: String
    ): Result<User> {
        return repository.signup(
            name = name,
            email = email,
            phone = phone,
            password = password
        )
    }
}
