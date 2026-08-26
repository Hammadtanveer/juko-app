# 🚗 JUKO — Screen to API Mapping

> **Purpose:** This document maps every UI screen and user action to its corresponding API endpoint, and defines the required state changes (Loading/Success/Error). AI agents must follow this mapping to connect the Presentation layer to the Data layer.

---

## 1. Authentication Flow

| Screen | User Action | API Endpoint | Success Behavior | Error Behavior |
|---|---|---|---|---|
| **Login** | Click "Login" | `POST /auth/login` | Save tokens; Navigate to Home | Show error below fields |
| **Signup** | Click "Create Account" | `POST /auth/signup` | Navigate to Login (with success toast) | Show error below fields |
| **Forgot Password** | Click "Send OTP" | `POST /auth/forgot-password` | Navigate to OTP Verify | Show error toast |
| **Verify OTP** | Submit 6-digit code | `POST /auth/verify-otp` | Save tokens; Navigate to Reset Password | Clear OTP boxes; show error |

---

## 2. Main Navigation Tabs

| Screen | User Action | API Endpoint | Success Behavior | Error Behavior |
|---|---|---|---|---|
| **Home (Search)** | Screen loads / Pull-to-refresh | `GET /rides/search` | Populate ride cards | Show `JukoErrorState` with retry |
| **My Rides** | Screen loads / Tab switch | `GET /rides/mine?status={tab}` | Populate upcoming/active lists | Show `JukoEmptyState` or error |
| **Alerts (Notifications)** | Screen loads | `GET /notifications` | Populate notifications list | Show error toast |
| **Profile** | Screen loads | `GET /users/me` | Populate profile & verification badge | Show skeleton loading failure |

---

## 3. Ride Management

| Screen | User Action | API Endpoint | Success Behavior | Error Behavior |
|---|---|---|---|---|
| **Post Ride (Step 2)** | Click "Publish Ride" | `POST /rides` | Navigate to Home; Show success toast | Show error toast; keep data |
| **Booking Requests** | Screen loads | `GET /bookings?ride_id={id}`| Populate passenger requests | Show error |
| **Booking Requests** | Click "Accept" | `PATCH /bookings/{id}/accept`| Decrement seats; Replace card with "Accepted" state | Show toast: "Action failed" |
| **Booking Requests** | Click "Decline" | `PATCH /bookings/{id}/decline`| Remove card from list | Show toast: "Action failed" |
| **Ride History** | Scroll to bottom | `GET /rides/history?page={n}`| Append to list (pagination) | Show inline retry at bottom |

---

## 4. Chat & Messaging

| Screen | User Action | API Endpoint / Protocol | Success Behavior | Error Behavior |
|---|---|---|---|---|
| **Inbox** | Screen loads | `GET /conversations` | Populate chat list | Show `JukoEmptyState` |
| **Chat Room** | Screen loads | `GET /conversations/{id}/messages` | Populate message history | Show error |
| **Chat Room** | Connection init | `WebSocket /ws/chat` | Establish persistent connection | Auto-reconnect with backoff |
| **Chat Room** | Type & click Send | `WebSocket (emit text)` | Append message locally immediately | Show red "!" next to message |

---

## 5. Profile & Vehicles

| Screen | User Action | API Endpoint | Success Behavior | Error Behavior |
|---|---|---|---|---|
| **Add Vehicle** | Click "Save Vehicle" | `POST /vehicles` | Navigate back; Show success toast | Show error below fields |
| **Profile** | Click "Upload DL" | `POST /users/me/documents` | Update verification status to "Pending" | Show error toast |

---

## Global State Rules for AI

Whenever connecting an Action to an API call inside a `ViewModel`, you **MUST** follow this exact sequence:

1.  **Start Loading:** `_state.update { it.copy(isLoading = true, error = null) }`
2.  **Execute Call:** `useCase.execute()`
3.  **On Success:**
    *   Update State: `_state.update { it.copy(isLoading = false, data = result) }`
    *   Trigger SideEffect (if navigating): `_effect.send(SideEffect.NavigateToX)`
4.  **On Error:**
    *   Update State: `_state.update { it.copy(isLoading = false, error = e.message) }`
    *   Trigger SideEffect (if toast): `_effect.send(SideEffect.ShowToast(e.message))`
