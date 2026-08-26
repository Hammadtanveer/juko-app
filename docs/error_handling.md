# 🚗 JUKO — Global Error Handling Strategy

> **Purpose:** This document defines exactly how the app must handle failures, network issues, and validation errors. AI agents must apply these patterns consistently across all KMP modules to ensure a robust user experience.

---

## 1. Error Categories & UI Behavior

Never just show a generic "Something went wrong" alert. Handle errors based on their category:

| Error Type | HTTP Code | UI Response | Example Scenario |
|---|---|---|---|
| **Network / Timeout** | `0` / `RequestTimeout` | **Retryable Error State:** Show `JukoErrorState` if a page failed to load, or a Toast if an action failed. | User loses 4G while posting a ride. |
| **Validation / Bad Request**| `400`, `422` | **Inline Error:** Highlight the specific `JukoTextField` in red and show the error text below it. | Password too weak; invalid plate format. |
| **Unauthorized** | `401` | **Global Logout:** Ktor must catch this. Clear local tokens, navigate root to `LoginScreen`, show "Session expired" toast. | JWT token expired and refresh failed. |
| **Forbidden** | `403` | **Snackbar/Toast:** Show exact reason. | Trying to post a ride before DL is verified. |
| **Not Found** | `404` | **Empty State:** Show `JukoEmptyState`. | Searching for a ride ID that was deleted. |
| **Server Error** | `500`, `502`, `503` | **Snackbar/Toast:** "Server is currently busy. Please try again later." | Backend database is down. |

---

## 2. Global Error Classes (Kotlin)

AI agents must use these exact classes when mapping Ktor responses in the Repository layer.

```kotlin
// The standard Error DTO returned by the backend
@Serializable
data class ErrorResponse(
    val success: Boolean,
    val message: String,
    val error_code: String? = null
)

// The custom Exception thrown/returned to ViewModels
class ApiException(
    val statusCode: Int,
    override val message: String,
    val errorCode: String? = null
) : Exception(message)
```

---

## 3. UI Error Implementation Rules

### Rule 3.1: Form Validation Errors
Always validate locally **before** calling the API. If the API returns a 400, map it to the specific field state.
```kotlin
// CORRECT (In ViewModel State)
data class SignupState(
    val email: String = "",
    val emailError: String? = null // Passed to JukoTextField(errorText = state.emailError)
)
```

### Rule 3.2: Full-Screen Data Loading Errors
If a screen needs data to render (e.g., `MyRidesScreen`, `HomeScreen`) and the initial fetch fails, **do not show a blank screen or just a toast**.
```kotlin
// CORRECT (In Composable)
if (state.error != null && state.rides.isEmpty()) {
    JukoErrorState(
        message = state.error,
        onRetry = { onEvent(MyRidesEvent.LoadRides) }
    )
}
```

### Rule 3.3: Action Errors (Side Effects)
If a user performs an action (e.g., "Accept Booking") and it fails, **do not change the screen state**. Trigger a side effect to show a temporary message.
```kotlin
// CORRECT (In ViewModel)
useCase.acceptBooking(id).onFailure { error ->
    _effect.send(SideEffect.ShowToast(error.message))
}
```

---

## 4. Ktor Interceptor Rule (The 401 Catch-All)

To prevent writing 401-handling logic in every single ViewModel, the AI must configure Ktor's `Auth` plugin to handle token refreshing. 

If the refresh token also fails, the `TokenManager` must clear all secure storage and emit a global `SessionExpired` event that the root `AppNavigator` listens to.

```kotlin
// Ktor setup requirement for AI
install(Auth) {
    bearer {
        // ... load tokens ...
        refreshTokens {
            // Attempt refresh. If this throws, logout the user.
        }
    }
}
```
