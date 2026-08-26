# JUKO Screen & Stitch UI Master Specification

This document maps all approved Stitch screens to their exact Compose Multiplatform specifications, component structures, and visual hierarchies.

---

## 1. Authentication Flow

### 1.1 Driver / User Login (`LoginScreen.kt`)
- **Stitch Title**: `Driver Login - Email/Password (Juko)`
- **Screen ID**: `062f148272fb4725a5f3b8393a64034c`
- **Path**: `com.juko.app.feature.auth.presentation.LoginScreen`
- **Visual Structure**:
  - Background: `#F9F9FF` with subtle primary atmospheric blue circular blur spots at top-right and bottom-left.
  - Header: Centered "JUKO" (Primary `#0052CC`, 32sp, Bold) + Subtitle "Move forward together with Juko" (`#434654`).
  - Segmented Control: `JukoSegmentedControl` (Login / Sign Up) with 12dp rounded background (`#DFE0E0`) and white active pill.
  - Form Card: Elevated white card (`#FFFFFF`, 16dp radius, 2dp elevation) containing:
    - `EMAIL ADDRESS` (label-caps, 12sp bold, `#434654`) -> `JukoTextField` (56dp height, 8dp radius).
    - `PASSWORD` (label-caps with right-aligned `FORGOT?` link in primary blue) -> `JukoTextField` with password mask + visibility toggle.
    - `Login` Button: `JukoButton` (56dp height, 12dp radius, `#0052CC` fill, white bold text).
  - Footer: "Don't have an account? Sign up" (clickable Sign Up in bold primary blue).

---

### 1.2 Driver / User Signup (`SignupScreen.kt`)
- **Stitch Title**: `Driver Signup (Juko)`
- **Screen ID**: `bd07c6db61fd4748a2606be45c7dc766`
- **Path**: `com.juko.app.feature.auth.presentation.SignupScreen`
- **Visual Structure**:
  - Background: `#F9F9FF` with atmospheric blue glow.
  - Header: Centered "JUKO" + Subtitle "Create your account to start".
  - Segmented Control: `JukoSegmentedControl` with "Sign Up" active.
  - Form Card: White card (16dp radius, 2dp elevation):
    - `FULL NAME` -> `JukoTextField` (placeholder: "Rajesh Sharma").
    - `EMAIL ADDRESS` -> `JukoTextField` (placeholder: "name@example.com").
    - `PHONE NUMBER` -> `JukoTextField` with `+91` prefix (placeholder: "98765 43210").
    - `PASSWORD` -> `JukoTextField` with password mask.
    - `Sign Up` Button: `JukoButton` (Primary Blue, 56dp height).
  - Footer: "Already have an account? Login" (clickable link).

---

### 1.3 Forgot Password / OTP Verification (`OtpScreen.kt`)
- **Stitch Title**: `Forgot Password - Verify OTP (Juko)`
- **Screen ID**: `e3ffecb695594040a3ab9df8e0b6ef55`
- **Path**: `com.juko.app.feature.auth.presentation.OtpScreen`
- **Visual Structure**:
  - TopBar: `JukoTopBar` with back arrow.
  - Header: Headline "Verification Code" + Subtitle "We sent a 6-digit code to your phone/email".
  - OTP Input: 6 individual square/rounded input boxes (48x56dp each) with auto-focus next on digit input. Active box gets 2dp Primary Blue border.
  - Resend Timer: "Resend code in 00:45" / "Resend Code" button.
  - Action Button: `JukoButton` ("Verify & Continue", 56dp height).

---

## 2. Home & Search Flow

### 2.1 Ride Search Home (`HomeScreen.kt`)
- **Stitch Title**: `Ride Search Home (Juko)`
- **Screen ID**: `c842c1d166974be49aa9e05bc08408c5`
- **Path**: `com.juko.app.feature.home.presentation.HomeScreen`
- **Visual Structure**:
  - Top Bar: JUKO brand mark left, Notification bell + User Avatar right.
  - Hero Search Card: White elevated card (16dp radius) with:
    - "Leaving from" input field with location icon.
    - "Going to" input field with destination pin icon.
    - Date picker row + Seat count selector (1-4 seats).
    - `Search Rides` Primary Button (`#0052CC`).
  - Quick Shortcuts: "Publish a Ride" card banner (for drivers) with green accent.
  - Recent / Popular Routes horizontal carousel.
  - Bottom Bar: 4-tab Persistent Navigation (`JukoBottomBar`: Search, Bookings, Inbox, Profile).

---

## 3. Driver & Ride Management Flow

### 3.1 Post a Ride - Route & Pricing (`PostRideRouteScreen.kt`)
- **Stitch Title**: `Post a Ride - Route & Pricing (Juko)`
- **Screen ID**: `3acaeb0d0d5b42af8e78cd045bc1ead2`
- **Path**: `com.juko.app.feature.postride.presentation.PostRideRouteScreen`
- **Visual Structure**:
  - TopBar: `JukoTopBar` with title "Post a Ride".
  - Step Progress Bar: Step 1 of 2.
  - Route Card: Pickup Location, Dropoff Location, Departure Date & Time.
  - Pricing Card: Price per seat slider / input with suggested pricing range (`₹450 - ₹650`).
  - Available Seats: Number counter `[- 3 +]`.
  - Action: `JukoButton` ("Next: Ride Details").

---

### 3.2 Post a Ride - Ride Details (`PostRideDetailsScreen.kt`)
- **Stitch Title**: `Post a Ride - Ride Details (Juko)`
- **Screen ID**: `df4617371e5b472e819c82a17411c00a`
- **Path**: `com.juko.app.feature.postride.presentation.PostRideDetailsScreen`
- **Visual Structure**:
  - TopBar: `JukoTopBar` with title "Ride Preferences".
  - Vehicle Selector: Dropdown / Card showing registered vehicle (e.g. "Honda City - DL 01 AB 1234").
  - Preferences Chips: Luggage allowed, AC available, Max 2 in back seat, Smoking/Pets toggle.
  - Notes to Passengers text area.
  - Action: `JukoButton` ("Publish Ride", Success Green `#36B37E`).

---

### 3.3 My Rides / Bookings List (`MyRidesScreen.kt`)
- **Stitch Title**: `My Rides - Upgraded (Juko)`
- **Screen ID**: `c2d013aa65234fbd8dc6fafc9e8d4dc9`
- **Path**: `com.juko.app.feature.rides.presentation.MyRidesScreen`
- **Visual Structure**:
  - TopBar: "My Rides" header.
  - Segmented Control: `Upcoming` | `Completed` | `Cancelled`.
  - Ride Cards: White cards (12dp radius) with Route summary (Origin -> Destination), Date/Time, Price, Driver Avatar + Rating star (`#FFAB00`), and `JukoStatusChip` (Scheduled / Completed).
  - Empty State fallback: `JukoEmptyState` ("No rides yet", car icon, "Book a ride" button).

---

### 3.4 Booking Requests (`BookingRequestsScreen.kt`)
- **Stitch Title**: `Booking Requests - Detailed Info (Juko)`
- **Screen ID**: `9236be1ab8694ecd99a57834495f7c87`
- **Path**: `com.juko.app.feature.bookings.presentation.BookingRequestsScreen`
- **Visual Structure**:
  - TopBar: "Ride Requests (3)".
  - Passenger Request Card:
    - Passenger avatar, Name, Rating, Number of seats requested.
    - Pickup/Drop sub-stop if along the route.
    - Action Buttons: Dual button row -> `JukoGhostButton` ("Decline", Red/Grey) + `JukoButton` ("Accept", Success Green `#36B37E`).

---

## 4. Communication & Profile Flow

### 4.1 Inbox & Conversations (`InboxScreen.kt` & `ChatScreen.kt`)
- **Stitch Title**: `Inbox - Conversations (Juko)` (`48d0b410d54c4875ba9a79ec40c1eb03`)
- **Stitch Title**: `Chat with Sneha Gupta (Juko)` (`622df6059d064067b54949f77a4ae602`)
- **Visual Structure**:
  - Inbox: List of conversation cards (Avatar, Name, Last message preview, Timestamp, Unread badge).
  - Chat: Sticky header (User name + ride badge), Scrollable message bubbles (Outbound: Primary Blue `#0052CC`, Inbound: Light Grey `#DFE0E0`), Bottom input bar with Send icon.

---

### 4.2 Driver Profile & License Verification (`DriverVerificationScreen.kt`)
- **Stitch Title**: `Driver Profile - Licence Verification (Juko)`
- **Screen ID**: `06043777420d4a9ca57a07c5b92a90dd`
- **Path**: `com.juko.app.feature.profile.presentation.DriverVerificationScreen`
- **Visual Structure**:
  - TopBar: "Driver Verification".
  - Driving License upload card (Front & Back camera capture box).
  - Vehicle Registration Certificate (RC) upload card.
  - Aadhaar / ID verification card with status pill ("Verified" / "Pending Review").
  - Action: `JukoButton` ("Submit for Verification").

---

### 4.3 Add Vehicle (`AddVehicleScreen.kt`)
- **Stitch Title**: `Add Vehicle (Juko)`
- **Screen ID**: `9ee9633b9e9243cd90b23c1dc8a881e2`
- **Path**: `com.juko.app.feature.vehicle.presentation.AddVehicleScreen`
- **Visual Structure**:
  - TopBar: "Add Vehicle".
  - Vehicle Type selector: Sedan, SUV, Hatchback (icon + label cards).
  - Make & Model text field (e.g. "Hyundai Creta").
  - License Plate input (e.g. "DL 01 AB 1234").
  - Color & Year selector.
  - Action: `JukoButton` ("Save Vehicle").
