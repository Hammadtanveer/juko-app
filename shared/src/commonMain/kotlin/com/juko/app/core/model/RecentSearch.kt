package com.juko.app.core.model

/**
 * Model representing a user's recent ride search stored in local memory/storage.
 */
data class RecentSearch(
    val from: String,
    val to: String,
    val passengers: Int = 1,
    val date: String = "Today",
    val timestamp: Long = 0L
)
