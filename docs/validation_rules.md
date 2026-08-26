# 🚗 JUKO — Input Validation Rules

> **Purpose:** This document defines the strict local validation rules that must be applied to user inputs *before* any API call is made. AI agents should implement these rules in a centralized `Validators.kt` object as pure functions.

---

## 1. Authentication Validations

| Field | Rule | Regex / Logic | Error Message |
|---|---|---|---|
| **Full Name** | 2 to 50 characters, letters and spaces only. | `^[A-Za-z\s]{2,50}$` | "Please enter a valid name" |
| **Email** | Standard email format. | `^[A-Za-z(0-9)]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$` | "Please enter a valid email address" |
| **Phone Number**| Indian mobile number format (10 digits starting with 6,7,8, or 9). | `^[6-9]\d{9}$` | "Please enter a valid 10-digit mobile number" |
| **Password** | Min 8 chars, at least 1 uppercase letter, at least 1 number. | `^(?=.*[A-Z])(?=.*\d).{8,}$` | "Password must be at least 8 characters with 1 number and 1 uppercase letter" |
| **Confirm Password**| Must exactly match the Password field. | `password == confirmPassword` | "Passwords do not match" |
| **OTP** | Exactly 6 numeric digits. | `^\d{6}$` | "Please enter the 6-digit OTP" |

---

## 2. Vehicle Validations

| Field | Rule | Regex / Logic | Error Message |
|---|---|---|---|
| **License Plate** | Indian RTO format (e.g., DL 01 AB 1234, UP32 XY 9999). | `^[A-Z]{2}[ -]?[0-9]{1,2}(?:[ -]?[A-Z]{1,3})?[ -]?[0-9]{4}$` | "Invalid license plate format (e.g., DL 01 AB 1234)" |
| **Make & Model** | Minimum 2 characters. | `length >= 2` | "Required field" |
| **Year** | Between 2010 and current year. | `year in 2010..currentYear` | "Vehicle must be 2010 or newer" |
| **Seats** | Numeric, between 2 and 8. | `seats in 2..8` | "Seats must be between 2 and 8" |
| **Photos** | At least 1 photo selected. | `photoUrls.isNotEmpty()` | "Please upload at least 1 photo" |

---

## 3. Ride Posting Validations

| Field | Rule | Logic | Error Message |
|---|---|---|---|
| **Date** | Must be today or in the future, up to 30 days max. | `date >= today && date <= today.plusDays(30)` | "Date must be within the next 30 days" |
| **Time** | If Date is today, Time must be at least 1 hour from now. | `if (date == today) time > now.plusHours(1)` | "Departure must be at least 1 hour from now" |
| **Origin & Dest**| Cannot be the exact same city/location. | `origin != destination` | "Origin and destination cannot be the same" |
| **Available Seats**| Minimum 1, maximum `(vehicle.totalSeats - 1)`. | `seats >= 1 && seats < vehicleSeats` | "Invalid seat count" |
| **Price per Seat**| Minimum ₹100, Maximum ₹10,000. | `price in 100..10000` | "Price must be between ₹100 and ₹10,000" |

---

## 4. Standard Implementation Pattern for AI

AI agents must use a unified `ValidationResult` pattern for pure validation functions.

```kotlin
// 1. Return type
sealed class ValidationResult {
    data object Valid : ValidationResult()
    data class Invalid(val errorMessage: String) : ValidationResult()
}

// 2. Pure functions in Validators.kt
object Validators {
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

// 3. Usage inside ViewModel
fun onEvent(event: SignupEvent) {
    when (event) {
        is SignupEvent.Submit -> {
            val phoneResult = Validators.validatePhone(state.value.phone)
            if (phoneResult is ValidationResult.Invalid) {
                _state.update { it.copy(phoneError = phoneResult.errorMessage) }
                return
            }
            // Proceed to API call...
        }
    }
}
```
