# 🚗 JUKO — Iterative Development Phases

> **Purpose:** This document breaks down the entire KMP frontend development into bite-sized, sequential phases. **AI Agents:** You must complete these phases in order. Do not jump ahead. Check off `[x]` as you complete each step.

---

## Phase 0: Project Scaffolding & Core Architecture
*Goal: Set up the foundation, DI, networking, and theme before any screens are built.*

- [ ] **0.1 Theme Setup:** Implement `JukoTheme`, `Color`, `Typography`, `Shape`, and `Spacing` from `design.md`.
- [ ] **0.2 Network Client:** Setup Ktor `ApiClient` and `WebSocketManager` with dummy endpoints.
- [ ] **0.3 Storage & Settings:** Implement `TokenManager` using Multiplatform Settings / Expect-Actual Secure Storage.
- [ ] **0.4 Dependency Injection:** Setup Koin `appModule` (Core dependencies only).
- [ ] **0.5 Root Navigation:** Setup Voyager `AppNavigator` switching between Auth and Main flows.

---

## Phase 1: Component Library
*Goal: Build the reusable UI blocks so they don't have to be re-invented on every screen.*

- [ ] **1.1 Core Components:** Build `JukoButton`, `JukoGhostButton`, and `JukoTextField`.
- [ ] **1.2 Cards & Layouts:** Build `JukoCard`, `JukoTopBar`, and `JukoSegmentedControl`.
- [ ] **1.3 States & Indicators:** Build `JukoEmptyState`, `JukoLoadingScreen`, and `JukoStatusChip`.

---

## Phase 2: Authentication Flow
*Goal: Allow the user to log in, sign up, and recover passwords.*

- [ ] **2.1 Auth Domain/Data:** Create `AuthRepository` (mocked), DTOs, and UseCases.
- [ ] **2.2 Login Screen:** UI (`LoginScreen`), State, and `LoginViewModel`.
- [ ] **2.3 Signup Screen:** UI (`SignupScreen`), State, and `SignupViewModel`.
- [ ] **2.4 Forgot Password & OTP:** UI for `ForgotPasswordScreen` and `VerifyOtpScreen` + ViewModels.
- [ ] **2.5 Auth Navigation:** Wire up `AuthNavigator` and test transitions to Main Flow on success.

---

## Phase 3: Main App Scaffold
*Goal: Set up the bottom navigation that holds the rest of the app together.*

- [ ] **3.1 Bottom Tabs:** Create `SearchTab`, `BookingsTab`, `AlertsTab`, and `ProfileTab`.
- [ ] **3.2 Main Navigator:** Implement `TabNavigator` with the custom Juko Bottom Bar.

---

## Phase 4: Driver Profile & Vehicle Setup
*Goal: Build the Profile tab so drivers can get verified and add their car.*

- [ ] **4.1 Profile Domain/Data:** Create Repositories, DTOs, and UseCases for Profile and Vehicles.
- [ ] **4.2 Profile Screen:** UI (`DriverProfileScreen`), State, ViewModel (Show verification status).
- [ ] **4.3 Add Vehicle Screen:** UI (`AddVehicleScreen`), State, ViewModel (Form validation for car details).

---

## Phase 5: Post Ride Wizard
*Goal: The 2-step flow for a driver to publish a new trip.*

- [ ] **5.1 Post Ride Domain/Data:** Create Repositories, DTOs, and UseCases.
- [ ] **5.2 Step 1 (Details):** UI (`PostRideDetailsScreen`), State, ViewModel (Date, Time, Vehicle, Seats).
- [ ] **5.3 Step 2 (Route & Price):** UI (`PostRideRoutingScreen`), State, ViewModel (Origin, Dest, Price).
- [ ] **5.4 Wizard Navigation:** Push from Home -> Step 1 -> Step 2 -> Pop to Home on Success.

---

## Phase 6: Home Dashboard (Search)
*Goal: The main landing screen displaying quick stats and ride feed.*

- [ ] **6.1 Home Domain/Data:** Create Repositories, DTOs, and UseCases.
- [ ] **6.2 Home Screen:** UI (`HomeScreen`), State, ViewModel. Include "Post Ride" CTA and ride feed.

---

## Phase 7: Ride Management & Bookings
*Goal: Allow drivers to see their upcoming rides and accept/decline passengers.*

- [ ] **7.1 Bookings Domain/Data:** Create Repositories, DTOs, and UseCases.
- [ ] **7.2 My Rides Screen:** UI (`MyRidesScreen`) with segmented control (Upcoming/Active/Completed).
- [ ] **7.3 Booking Requests:** UI (`BookingRequestsScreen`), State, ViewModel (Accept/Decline logic).
- [ ] **7.4 History Screen:** UI (`HistoryScreen`), State, ViewModel (Pagination).

---

## Phase 8: Inbox & Chat
*Goal: Real-time communication between driver and passenger.*

- [ ] **8.1 Chat Domain/Data:** Create Repositories, DTOs, and WebSocket logic.
- [ ] **8.2 Inbox Screen:** UI (`InboxScreen`), State, ViewModel (List of conversations).
- [ ] **8.3 Chat Room Screen:** UI (`ChatScreen`), State, ViewModel (Message bubbles, text input).

---

## Phase 9: Notifications & Polish
*Goal: Final alerts tab and application cleanup.*

- [ ] **9.1 Alerts Domain/Data:** Create Repositories, DTOs.
- [ ] **9.2 Notifications Screen:** UI (`NotificationsScreen`), State, ViewModel.
- [ ] **9.3 Global Polish:** Final QA on error states, loading states, and form validations.
