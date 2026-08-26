package com.juko.app.feature.auth.domain.model

data class User(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val avatarUrl: String? = null,
    val rating: Float = 0f,
    val totalRides: Int = 0,
    val isVerified: Boolean
)
