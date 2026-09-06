# Juko App - Frontend Architecture & Integration Guide

**Application Type:** Kotlin Multiplatform (Android & iOS) with Compose Multiplatform  
**Navigation:** Voyager Navigation  
**Dependency Injection:** Koin  
**Network Client:** Ktor Client  
**State Management:** StateFlow & Screen Models / ViewModels  
**Last Updated:** August 2026  

---

## 📱 1. Completed Frontend Screens & Feature Flows

### 🔐 1. Authentication (`feature/auth`)
* **`AuthScreen.kt`**: 
  - Login and Sign-Up tab switcher.
  - **Phone constraint:** Numbers only, exactly 10 digits with live validation.
  - **Password constraint:** 8–16 characters with uppercase and numeric validation.
  - **Keyboard IME actions:** `Enter` on Email focuses Password; `Enter/Done` on Password submits the form.
* **`OtpScreen.kt`**: 
  - 6-digit OTP code verification with auto-focus movement.

### 🔍 2. Search & Ride Discovery (`feature/search` & `feature/home`)
* **`HomeScreen.kt`**: 
  - Hero search card (Origin, Destination, Date Picker, Passenger Stepper).
  - Recent searches list that directly triggers pre-filled searches.
* **`SearchResultsScreen.kt`**: 
  - Filter chips for Sorting, Price range, Time, and Seats.
  - Multi-stop timeline cards with driver avatar, ratings, route stops, and seat badges.
* **`RideDetailsScreen.kt`**: 
  - Detailed passenger view with driver ratings, vehicle amenities (AC, Luggage, Smoke-free).
  - Interactive seat selector and add-on toggles (Front seat guarantee +₹50, Window seat +₹30).
  - Live total price calculator and booking confirmation dialog.

### 🚗 3. Publish Ride Flow (`feature/postride`)
* **`PostRideRouteScreen.kt` (Step 1)**: 
  - Interactive multi-stop route builder.
  - Departure & Arrival Date & Time pickers.
  - Segment pricing cards with **₹10 increment/decrement buttons** and direct numeric typing.
* **`PostRideDetailsScreen.kt` (Step 2)**: 
  - Seat capacity stepper (1 to 8 seats).
  - Front and Window seat price adjustments & Whole Car Booking toggle.
  - Ride summary card.
* **`RideSuccessScreen.kt`**: 
  - Published confirmation screen with navigation to Home or Your Rides.

### 📋 4. Your Rides Management (`feature/rides`)
* **`MyRidesScreen.kt`**:
  - **Published Tab:** Active & Draft listings with seat occupancy progress bars and passenger management.
  - **Requests Tab:** Incoming passenger booking requests with whole-car / seat preferences and **Accept / Reject** actions.
  - **History Tab:** Completed and cancelled rides history.

### 💬 5. Inbox & Real-Time Chat (`feature/inbox`)
* **`InboxScreen.kt`**: 
  - 5 active conversation rows with avatars, online indicators, route tags, and unread badges.
  - Bottom-right Floating Action Button (`FAB`) for composing new messages.
  - Empty state when no conversations exist.
* **`ChatScreen.kt`**: 
  - Route context banner (`Delhi → Seohara • Today 08:00 AM`).
  - Direct Call action button.
  - Sent vs. Received chat bubbles with timestamps and auto-scroll.
  - Gapless bottom input bar with send button.

### 👤 6. Driver Profile & Vehicle Management (`feature/profile`)
* **`ProfileScreen.kt`**: 
  - Driver rating score and stats (`4.8 · 124 rides completed`).
  - Personal info fields with 10-digit phone constraint and dynamic Verified status pill.
  - Driver's Licence Front & Back upload boxes with image preview, Retake, and Remove buttons.
  - Horizontal list of registered vehicles.
* **`AddVehicleScreen.kt`**: 
  - Photo upload area, Brand dropdown, Model, Color, and Registration Plate inputs.
  - Roof Rack toggle, AC toggle, and Seat capacity stepper.

### 🔔 7. App-Wide Notifications (`feature/notifications`)
* **`NotificationsScreen.kt`**: 
  - Grouped sections for **Today**, **Yesterday**, and **This Week**.
  - Contextual icon badges (Booking confirmed, Ride request, Driver arrival, Message).
  - **"Mark all as read"** interactive action button.
  - Connected notification bell across all top app bars.

---

## 🌐 2. Required Backend APIs (21 Endpoints)

### 1. Authentication & Onboarding
1. `POST /api/v1/auth/signup` - Register user (Name, Email, 10-digit Phone, Password).
2. `POST /api/v1/auth/login` - Authenticate user & return JWT tokens.
3. `POST /api/v1/auth/otp/send` - Send / Resend OTP to phone or email.
4. `POST /api/v1/auth/otp/verify` - Verify 6-digit OTP.

### 2. Search & Rides
5. `GET /api/v1/rides/search` - Search rides by `from`, `to`, `date`, `passengers`, `sort`, `price`.
6. `GET /api/v1/rides/{rideId}` - Get full details of a specific ride.
7. `GET /api/v1/rides/recent-searches` - Get user's recent search queries.

### 3. Publish & Manage Rides (Driver)
8. `POST /api/v1/rides/publish` - Create a new ride listing.
9. `PUT /api/v1/rides/{rideId}` - Update an existing ride.
10. `DELETE /api/v1/rides/{rideId}` - Cancel a published ride.

### 4. Bookings & Requests
11. `POST /api/v1/bookings` - Passenger books seat(s) with add-ons.
12. `GET /api/v1/rides/my-rides` - Get user's rides grouped by `published`, `requests`, `history`.
13. `POST /api/v1/bookings/{bookingId}/accept` - Driver accepts booking request.
14. `POST /api/v1/bookings/{bookingId}/reject` - Driver or passenger rejects/cancels booking.

### 5. Chat & Inbox
15. `GET /api/v1/chat/conversations` - Get all conversation threads.
16. `GET /api/v1/chat/{conversationId}/messages` - Get messages for a conversation.
17. `POST /api/v1/chat/{conversationId}/messages` - Send a text message *(or via WebSocket)*.

### 6. Profile & Vehicles
18. `GET /api/v1/users/me` - Get user profile, rating, and vehicle list.
19. `PUT /api/v1/users/me` - Update profile details and upload Driver's Licence images.
20. `POST /api/v1/users/me/vehicles` - Add a new vehicle.

### 7. Notifications
21. `GET /api/v1/notifications` / `PUT /api/v1/notifications/read-all` - Get notifications and mark all as read.

---

## 🔒 3. Android Device Permissions Checklist

When ready to enable native hardware features, add these to `androidApp/src/main/AndroidManifest.xml`:

```xml
<!-- Network & API communication -->
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

<!-- Camera & Gallery (Profile Photo, Licence, Car Photos) -->
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" /> <!-- Android 13+ -->
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" android:maxSdkVersion="32" />

<!-- Location (Nearby Rides & Tracking) -->
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />

<!-- Push Notifications (Android 13+) -->
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
```

---

## 🛠️ 4. Build & Run Commands

```powershell
# Compile shared Kotlin Multiplatform code
./gradlew.bat :shared:assemble

# Build Android Debug APK
./gradlew.bat :androidApp:assembleDebug

# Run unit tests
./gradlew.bat test
```
