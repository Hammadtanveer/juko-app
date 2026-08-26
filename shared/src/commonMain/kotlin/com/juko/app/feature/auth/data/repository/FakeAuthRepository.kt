package com.juko.app.feature.auth.data.repository

import com.juko.app.core.data.TokenManager
import com.juko.app.feature.auth.domain.model.User
import com.juko.app.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.delay

class FakeAuthRepository(
    private val tokenManager: TokenManager
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<User> {
        delay(1_000)
        
        if (email == "error@juko.app") {
            return Result.failure(Exception("Invalid credentials"))
        }

        val user = User(
            id = "usr_login_001",
            name = "Juko Driver",
            email = email,
            phone = "+919876543210",
            avatarUrl = "https://i.pravatar.cc/300?u=usr_login_001",
            rating = 4.8f,
            totalRides = 156,
            isVerified = true
        )

        tokenManager.saveAccessToken("fake_access_token")
        tokenManager.saveRefreshToken("fake_refresh_token")

        return Result.success(user)
    }

    override suspend fun signup(
        name: String,
        email: String,
        phone: String,
        password: String
    ): Result<User> {
        delay(1_000)

        val user = User(
            id = "usr_signup_001",
            name = name,
            email = email,
            phone = phone,
            avatarUrl = null,
            rating = 0f,
            totalRides = 0,
            isVerified = false
        )

        tokenManager.saveAccessToken("fake_access_token")
        tokenManager.saveRefreshToken("fake_refresh_token")

        return Result.success(user)
    }

    override suspend fun verifyOtp(userId: String, otp: String): Result<User> {
        delay(1_000)

        if (otp == "000000") {
            return Result.failure(Exception("Invalid OTP"))
        }

        val user = User(
            id = userId,
            name = "Juko Driver",
            email = "driver@juko.app",
            phone = "+919876543210",
            avatarUrl = "https://i.pravatar.cc/300?u=$userId",
            rating = 4.5f,
            totalRides = 10,
            isVerified = true
        )

        tokenManager.saveAccessToken("verified_fake_access_token")

        return Result.success(user)
    }

    override suspend fun forgotPassword(email: String): Result<Unit> {
        delay(1_000)
        return if (email.contains("@")) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("User not found"))
        }
    }

    override suspend fun resetPassword(email: String, otp: String, newPassword: String): Result<Unit> {
        delay(1_000)
        return if (otp == "123456") {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Incorrect OTP"))
        }
    }

    override suspend fun logout(): Result<Unit> {
        delay(500)
        tokenManager.clear()
        return Result.success(Unit)
    }
}
