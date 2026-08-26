package com.juko.app.core.util

sealed class ValidationResult {
    data object Valid : ValidationResult()
    data class Invalid(val errorMessage: String) : ValidationResult()
}

object Validators {
    fun validateEmail(email: String): ValidationResult {
        if (email.isBlank()) return ValidationResult.Invalid("Email is required")
        val regex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        return if (regex.matches(email)) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid("Please enter a valid email address")
        }
    }

    fun validatePassword(password: String): ValidationResult {
        if (password.isBlank()) return ValidationResult.Invalid("Password is required")
        // Min 8 chars, at least 1 uppercase letter, at least 1 number
        val regex = Regex("^(?=.*[A-Z])(?=.*\\d).{8,}$")
        return if (regex.matches(password)) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid("Password must be at least 8 characters with 1 number and 1 uppercase letter")
        }
    }

    fun validateName(name: String): ValidationResult {
        if (name.isBlank()) return ValidationResult.Invalid("Full name is required")
        val regex = Regex("^[A-Za-z\\s]{2,50}$")
        return if (regex.matches(name)) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid("Please enter a valid name")
        }
    }

    fun validatePhone(phone: String): ValidationResult {
        if (phone.isBlank()) return ValidationResult.Invalid("Phone number is required")
        val regex = Regex("^[6-9]\\d{9}$")
        return if (regex.matches(phone)) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid("Please enter a valid 10-digit mobile number")
        }
    }
}
