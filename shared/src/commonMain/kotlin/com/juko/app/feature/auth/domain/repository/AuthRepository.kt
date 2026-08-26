package com.juko.app.feature.auth.domain.repository

import com.juko.app.feature.auth.domain.model.User

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>

    suspend fun signup(
        name: String,
        email: String,
        phone: String,
        password: String
    ): Result<User>

    suspend fun verifyOtp(userId: String, otp: String): Result<User>

    suspend fun forgotPassword(email: String): Result<Unit>

    suspend fun resetPassword(email: String, otp: String, newPassword: String): Result<Unit>

    suspend fun logout(): Result<Unit>
}
