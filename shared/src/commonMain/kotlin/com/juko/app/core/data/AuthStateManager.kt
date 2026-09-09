package com.juko.app.core.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Global reactive manager for user authentication state (Guest vs Authenticated).
 * Enables public browsing for Search and gates protected features.
 */
object AuthStateManager {

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _currentUserName = MutableStateFlow<String?>("Alex Rivera")
    val currentUserName: StateFlow<String?> = _currentUserName.asStateFlow()

    private val _currentUserEmail = MutableStateFlow<String?>("alex@example.com")
    val currentUserEmail: StateFlow<String?> = _currentUserEmail.asStateFlow()

    fun setLoggedIn(loggedIn: Boolean, name: String? = "Alex Rivera", email: String? = "alex@example.com") {
        _isLoggedIn.value = loggedIn
        if (loggedIn) {
            _currentUserName.value = name ?: "Alex Rivera"
            _currentUserEmail.value = email ?: "alex@example.com"
        } else {
            _currentUserName.value = null
            _currentUserEmail.value = null
        }
    }

    fun logout() {
        _isLoggedIn.value = false
        _currentUserName.value = null
        _currentUserEmail.value = null
    }
}
