# 🚗 JUKO — App Flow Documentation

> **Platform:** Mobile (Driver-side)  
> **Design System:** Velocity Drive  
> **Last Updated:** August 24, 2026

---

## Overview

JUKO is an intercity ride-sharing platform for **drivers**. The app enables professional drivers to post rides, manage bookings, communicate with passengers, and handle vehicle/profile verification — all from a safety-first, card-based mobile interface.

### Navigation Model

The app uses a **Hub-and-Spoke** architecture with a persistent **4-tab bottom navigation bar**:

```
┌──────────────────────────────────────────┐
│              BOTTOM NAV BAR              │
├──────────┬──────────┬────────┬───────────┤
│  Search  │ Bookings │ Alerts │  Profile  │
└──────────┴──────────┴────────┴───────────┘
```

---

## Phase 1 — Authentication & Onboarding

### Screen Flow

```
                    ┌───────────────────┐
                    │   DRIVER LOGIN    │
                    │  (Email/Password) │
                    └─────────┬─────────┘
                              │
              ┌───────────────┼───────────────┐
              │               │               │
              ▼               ▼               ▼
    ┌─────────────┐  ┌───────────────┐  ┌──────────────┐
    │   SIGNUP    │  │  FORGOT PASS  │  │  RIDE SEARCH │
    │ (New Acct)  │  │ (Verify OTP)  │  │    HOME      │
    └─────────────┘  └───────────────┘  └──────────────┘
```

### Screens

| Screen | Purpose | Key Actions |
|---|---|---|
| **Driver Login** | Entry point for returning drivers | Enter email + password → Login |
| **Driver Signup** | Registration for new drivers | Fill name, phone, email, password → Create Account |
| **Forgot Password (OTP)** | Password recovery | Enter email/phone → Receive OTP → Verify → Reset password |

### User Journey

1. **New Driver** → Opens app → Taps "Sign Up" → Fills registration form → Redirected to Login
2. **Returning Driver** → Opens app → Enters credentials → Lands on Ride Search Home
3. **Forgot Password** → Taps "Forgot Password?" on Login → Enters phone/email → Receives OTP → Verifies → Sets new password → Back to Login

---

## Phase 2 — Home & Ride Discovery

### Screen Flow

```
┌──────────────────────────────────────────────────┐
│                RIDE SEARCH HOME                  │
│                (Main Dashboard)                  │
│                                                  │
│  ┌─────────────┐  ┌──────────────────────────┐   │
│  │ Search Bar  │  │ Nearby/Available Rides   │   │
│  │ From → To   │  │ (Scrollable cards)       │   │
│  └─────────────┘  └──────────────────────────┘   │
│                                                  │
│  ┌──────────────────────────────────────────┐    │
│  │     Quick Actions / Recent Routes        │    │
│  └──────────────────────────────────────────┘    │
│                                                  │
│  ┌──────┬──────────┬─────────┬───────────┐       │
│  │Search│ Bookings │ Alerts  │ Profile   │       │
│  └──────┴──────────┴─────────┴───────────┘       │
└──────────────────────────────────────────────────┘
```

### Screen

| Screen | Purpose | Key Actions |
|---|---|---|
| **Ride Search Home** | Central hub — the main dashboard after login | Search rides, browse available trips, navigate to all sections |

### Connections

From Ride Search Home, the driver can navigate to:

```
Ride Search Home
    ├── [Tab: Search]    → Stay on Search Home
    ├── [Tab: Bookings]  → My Rides
    ├── [Tab: Alerts]    → Notifications
    ├── [Tab: Profile]   → Driver Profile
    ├── [Action: Post]   → Post a Ride
    └── [Action: Inbox]  → Inbox Conversations
```

---

## Phase 3 — Managing Rides

### Screen Flow

```
┌──────────────┐       ┌─────────────────────┐       ┌────────────────┐
│   MY RIDES   │──────→│  BOOKING REQUESTS   │       │    HISTORY     │
│  (Active &   │       │  (Detailed Info)     │       │  (Past Trips)  │
│  Upcoming)   │──────→│                     │       │                │
└──────────────┘       │  ┌───────────────┐  │       └────────────────┘
       │               │  │ Passenger Info│  │              ▲
       │               │  │ Pickup/Drop   │  │              │
       │               │  │ Fare Details  │  │              │
       │               │  │               │  │              │
       │               │  │ [Accept] [X]  │  │              │
       │               │  └───────────────┘  │              │
       │               └─────────────────────┘              │
       │                                                    │
       └────────────────────────────────────────────────────┘
                        (View Past Trips)
```

### Screens

| Screen | Purpose | Key Actions |
|---|---|---|
| **My Rides (Upgraded)** | View current & upcoming rides | See ride status (Scheduled, In Progress), tap into details |
| **Booking Requests (Detailed)** | Review incoming passenger bookings | View passenger info, pickup/drop, fare → Accept or Decline |
| **History (Upgraded)** | Complete trip history | Browse past rides, earnings, ratings, routes |

### User Journey

1. Driver taps **Bookings** tab → Sees **My Rides** list (upcoming rides as cards)
2. Taps a ride card → Opens **Booking Requests** with full details
3. Reviews passenger info, route, fare → Taps **Accept** (green) or **Decline**
4. Swipes to **History** tab → Views **completed trips** with earnings breakdown

---

## Phase 4 — Posting a Ride

### Screen Flow

```
┌─────────────────────────┐       ┌─────────────────────────────┐
│  POST A RIDE             │       │  POST A RIDE                 │
│  Step 1: Ride Details    │──────→│  Step 2: Route & Pricing     │
│                          │       │                              │
│  ┌─────────────────┐    │       │  ┌────────────────────────┐  │
│  │ Date & Time     │    │       │  │ Map Preview            │  │
│  │ Vehicle Select  │    │       │  │ Origin ──→ Stops ──→   │  │
│  │ Seats Available │    │       │  │ Destination            │  │
│  │ Preferences     │    │       │  ├────────────────────────┤  │
│  │ (Smoke/Pets/    │    │       │  │ Price per seat         │  │
│  │  Luggage)       │    │       │  │ Total fare estimate    │  │
│  └─────────────────┘    │       │  ├────────────────────────┤  │
│                          │       │  │ [Review & Publish]     │  │
│  [Next →]                │       │  └────────────────────────┘  │
└─────────────────────────┘       └─────────────────────────────┘
```

### Screens

| Screen | Purpose | Key Actions |
|---|---|---|
| **Post a Ride — Ride Details** | Step 1: Define ride basics | Select date/time, choose vehicle, set available seats, preferences |
| **Post a Ride — Route & Pricing** | Step 2: Set route and price | Define origin/stops/destination on map, set price per seat → Publish |

### User Journey

1. Driver taps **"Post a Ride"** button on Home
2. **Step 1** — Fills in ride details: when, which vehicle, how many seats, ride preferences
3. Taps **"Next"** →
4. **Step 2** — Sets route on map (origin → optional stops → destination), enters price per seat
5. Reviews summary → Taps **"Publish"**
6. Ride goes live for passengers to discover and book

---

## Phase 5 — Messaging

### Screen Flow

```
┌──────────────────────┐       ┌──────────────────────────────┐
│  INBOX                │       │  CHAT WITH SNEHA GUPTA       │
│  (Conversations)      │──────→│  (1-on-1 Messaging)          │
│                       │       │                              │
│  ┌──────────────────┐ │       │  ┌────────────────────────┐  │
│  │ 👩 Sneha Gupta   │ │       │  │ Message bubbles        │  │
│  │ "I'll be at..."  │ │       │  │ Timestamps             │  │
│  │ 2m ago       (1) │ │       │  │ Read receipts          │  │
│  ├──────────────────┤ │       │  ├────────────────────────┤  │
│  │ 👤 Ravi Kumar    │ │       │  │ [Type a message...]    │  │
│  │ "Thanks!"        │ │       │  │ [📎] [📷] [Send →]    │  │
│  │ 1h ago           │ │       │  └────────────────────────┘  │
│  └──────────────────┘ │       └──────────────────────────────┘
└──────────────────────┘

         │ No conversations?
         ▼
┌──────────────────────┐
│  INBOX (EMPTY STATE) │
│                      │
│    💬               │
│  "No messages yet"   │
│  Start a conversation│
│  after accepting a   │
│  booking.            │
└──────────────────────┘
```

### Screens

| Screen | Purpose | Key Actions |
|---|---|---|
| **Inbox — Conversations** | List of all active chats | View conversations, last message preview, unread count → Tap to open |
| **Chat with Sneha Gupta** | Real-time 1-on-1 messaging | Send/receive messages, share location, coordinate pickup |
| **Inbox — Empty State** | No conversations state | Informational — guides user to accept a booking first |

### User Journey

1. Driver taps **Inbox** icon → Sees list of **active conversations**
2. Each row shows: passenger avatar, name, last message preview, timestamp, unread badge
3. Taps a conversation → Opens **Chat** screen
4. Can send text messages, photos, location to coordinate with passenger
5. If no conversations exist → Shows **Empty State** with guidance

---

## Phase 6 — Notifications

### Screen Flow

```
┌──────────────────────────┐
│  NOTIFICATIONS           │
│                          │
│  ┌────────────────────┐  │
│  │ 🔔 New Booking     │  │
│  │ Sneha requested... │  │
│  │ 5 min ago          │  │
│  ├────────────────────┤  │
│  │ ✅ Ride Confirmed  │  │
│  │ Delhi → Jaipur     │  │
│  │ 2 hours ago        │  │
│  ├────────────────────┤  │
│  │ 💰 Payment Received│  │
│  │ ₹450 credited      │  │
│  │ Yesterday          │  │
│  └────────────────────┘  │
└──────────────────────────┘

         │ No notifications?
         ▼
┌──────────────────────────┐
│  NOTIFICATIONS           │
│  (EMPTY STATE)           │
│                          │
│       🔔                │
│  "No notifications yet"  │
│  We'll let you know when │
│  something happens.      │
└──────────────────────────┘
```

### Screens

| Screen | Purpose | Key Actions |
|---|---|---|
| **Notifications** | All system alerts and updates | View booking requests, confirmations, payments, ratings |
| **Notifications — Empty State** | No notifications yet | Informational display |

### Notification Types

| Type | Icon | Description |
|---|---|---|
| New Booking | 🔔 | A passenger has requested to join your ride |
| Ride Confirmed | ✅ | A booking has been confirmed |
| Payment Received | 💰 | Earnings credited to your wallet |
| Rating Received | ⭐ | A passenger rated your ride |
| Ride Reminder | ⏰ | Upcoming ride starting soon |
| System Alert | ℹ️ | App updates, policy changes |

---

## Phase 7 — Profile & Vehicle Management

### Screen Flow

```
┌──────────────────────────────────┐
│  DRIVER PROFILE                  │
│  (Licence Verification)         │
│                                  │
│  ┌────────────────────────────┐  │
│  │ 👤 Profile Photo           │  │
│  │ Name, Phone, Email         │  │
│  ├────────────────────────────┤  │
│  │ 📋 Driving Licence         │  │
│  │ ┌─────────┐ ┌───────────┐ │  │
│  │ │  Front  │ │   Back    │ │  │
│  │ │ [Upload]│ │  [Upload] │ │  │
│  │ └─────────┘ └───────────┘ │  │
│  ├────────────────────────────┤  │
│  │ Status: ⏳ Under Review    │  │
│  │         ✅ Verified        │  │
│  │         ❌ Rejected        │  │
│  └────────────────────────────┘  │
└──────────────────────────────────┘

┌──────────────────────────────────┐
│  ADD VEHICLE                     │
│                                  │
│  ┌────────────────────────────┐  │
│  │ Make & Model               │  │
│  │ Year of Manufacture        │  │
│  │ License Plate Number       │  │
│  │ Vehicle Color              │  │
│  │ Number of Seats            │  │
│  ├────────────────────────────┤  │
│  │ 📷 Vehicle Photos          │  │
│  │ [Front] [Side] [Interior]  │  │
│  ├────────────────────────────┤  │
│  │ [Save Vehicle]             │  │
│  └────────────────────────────┘  │
└──────────────────────────────────┘
```

### Screens

| Screen | Purpose | Key Actions |
|---|---|---|
| **Driver Profile — Licence Verification** | KYC and identity verification | Upload DL front/back, fill personal info, track verification status |
| **Add Vehicle** | Register a vehicle for rides | Enter make/model/year/plate/color, upload photos → Save |

### Verification States

| State | Display | Next Step |
|---|---|---|
| ⏳ **Pending** | "Under Review" | Wait for admin approval |
| ✅ **Verified** | Green badge | Can start posting rides |
| ❌ **Rejected** | Red warning | Re-upload correct documents |

---

## Complete App Flow Map

```
══════════════════════════════════════════════════════════════
                      JUKO APP FLOW
══════════════════════════════════════════════════════════════

    NEW USER                              EXISTING USER
        │                                       │
        ▼                                       ▼
  ┌──────────┐                           ┌──────────┐
  │  SIGNUP  │                           │  LOGIN   │
  └────┬─────┘                           └────┬─────┘
       │                                      │
       │    ┌──────────────┐                  │
       │    │ FORGOT PASS  │←─── Forgot? ─────┤
       │    │ (OTP Verify) │                  │
       │    └──────┬───────┘                  │
       │           │                          │
       └───────────┴──────────────────────────┘
                            │
                            ▼
               ┌────────────────────────┐
               │    RIDE SEARCH HOME    │ ◄── MAIN HUB
               │    (Dashboard)         │
               └────────────┬───────────┘
                            │
          ┌─────────┬───────┼────────┬──────────┐
          │         │       │        │          │
          ▼         ▼       ▼        ▼          ▼
     ┌─────────┐ ┌──────┐ ┌─────┐ ┌──────┐ ┌────────┐
     │MY RIDES │ │ POST │ │INBOX│ │NOTIF │ │PROFILE │
     │(Active) │ │ RIDE │ │     │ │      │ │& DOCS  │
     └────┬────┘ └──┬───┘ └──┬──┘ └──┬───┘ └───┬────┘
          │         │        │       │         │
          ▼         ▼        ▼       ▼         ▼
     ┌─────────┐ ┌──────┐ ┌─────┐ ┌──────┐ ┌────────┐
     │BOOKING  │ │ROUTE │ │CHAT │ │EMPTY │ │  ADD   │
     │REQUESTS │ │& PRICE│ │     │ │STATE │ │VEHICLE │
     └────┬────┘ └──────┘ └─────┘ └──────┘ └────────┘
          │
          ▼
     ┌─────────┐
     │ HISTORY │
     │(Past)   │
     └─────────┘

══════════════════════════════════════════════════════════════
  BOTTOM NAV:  Search  │  Bookings  │  Alerts  │  Profile
══════════════════════════════════════════════════════════════
```

---

## Screen Inventory Summary

| Category | Screens | Count |
|---|---|---|
| 🔐 Authentication | Login, Signup, Forgot Password (OTP) | 3 |
| 🏠 Home | Ride Search Home | 1 |
| 🚘 Ride Management | My Rides, Booking Requests, History | 3 |
| 📝 Post a Ride | Ride Details, Route & Pricing | 2 |
| 💬 Messaging | Inbox, Chat, Inbox Empty State | 3 |
| 🔔 Notifications | Notifications, Notifications Empty State | 2 |
| 👤 Profile & Vehicle | Driver Profile (Licence), Add Vehicle | 2 |
| | **Total Visible Screens** | **16** |
| | Hidden/Variant Instances | ~49 |
| | **Total on Canvas** | **~65** |

---

## Key UX Patterns

### ✅ What JUKO Does Well

| Pattern | Implementation |
|---|---|
| **Empty States** | Both Inbox & Notifications have dedicated empty screens with guidance text |
| **Progressive Disclosure** | Ride posting split into 2 focused steps instead of one long form |
| **Card-based UI** | Every data unit (ride, booking, message) is a distinct, tappable card |
| **Safety-first** | 48px min touch targets, high-contrast text, designed for vehicle-mount use |
| **Clear Hierarchy** | Numeric-Data typography (28px bold) for earnings/ETAs at a glance |

### 🔁 Core User Loop

```
Post Ride → Receive Booking → Accept → Chat with Passenger → Complete Ride → Get Paid → View History
    ▲                                                                                        │
    └────────────────────────────────────────────────────────────────────────────────────────┘
                                        (Repeat)
```

---

*Generated from Stitch project `projects/4074151794218231363` on August 24, 2026*
