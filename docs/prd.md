# 🚗 JUKO — Product Requirements Document (PRD)

> **Document Version:** 1.0  
> **Date:** August 24, 2026  
> **Author:** Product Team  
> **Status:** Draft  
> **Project ID:** `projects/4074151794218231363`

---

## Table of Contents

1. [Executive Summary](#1-executive-summary)
2. [Problem Statement](#2-problem-statement)
3. [Product Vision & Goals](#3-product-vision--goals)
4. [Target Users](#4-target-users)
5. [User Personas](#5-user-personas)
6. [User Stories & Requirements](#6-user-stories--requirements)
7. [Feature Specification](#7-feature-specification)
8. [Screen-by-Screen Specification](#8-screen-by-screen-specification)
9. [User Flows](#9-user-flows)
10. [Information Architecture](#10-information-architecture)
11. [Business Rules & Logic](#11-business-rules--logic)
12. [Notifications Strategy](#12-notifications-strategy)
13. [Non-Functional Requirements](#13-non-functional-requirements)
14. [Analytics & KPIs](#14-analytics--kpis)
15. [Monetization Model](#15-monetization-model)
16. [Competitive Analysis](#16-competitive-analysis)
17. [Assumptions & Constraints](#17-assumptions--constraints)
18. [Risks & Mitigations](#18-risks--mitigations)
19. [Release Plan & Milestones](#19-release-plan--milestones)
20. [Success Criteria](#20-success-criteria)
21. [Appendix](#21-appendix)

---

## 1. Executive Summary

**JUKO** is a mobile-first intercity ride-sharing platform built for the Indian market. It connects **drivers** traveling between cities with **passengers** looking for affordable, safe, and convenient long-distance rides.

Unlike traditional cab-hailing apps (Ola, Uber) that focus on intra-city trips, JUKO specifically targets the **intercity corridor** — trips between 100km to 800km — where buses are slow, trains are crowded, and flights are expensive. By enabling private car owners and professional drivers to share their journeys and earn from empty seats, JUKO creates a **win-win ecosystem** that reduces travel costs for passengers and generates income for drivers.

### Product Scope (This PRD)

This PRD covers the **Driver-side mobile application** — the app used by drivers to:
- Post available rides
- Manage incoming booking requests
- Communicate with passengers
- Handle vehicle and profile verification
- Track ride history and earnings

> **Note:** The Passenger-side app is a separate product and is not covered in this document.

### Platform

| Attribute | Detail |
|---|---|
| **Platforms** | Android & iOS (single codebase via KMP + Compose Multiplatform) |
| **Design System** | "Velocity Drive" — safety-first, card-based, high-legibility |
| **Primary Font** | Inter |
| **Primary Color** | Confident Blue (#0052CC) |
| **Backend** | External API (provided by backend team) |

---

## 2. Problem Statement

### For Drivers

| Problem | Impact |
|---|---|
| **Empty seats on long drives** | Drivers make intercity trips with 2-3 empty seats, wasting potential income |
| **No dedicated platform** | Existing ride-hailing apps don't support intercity ride publishing |
| **High fuel costs** | Fuel prices in India have risen 40%+ in the last 5 years; drivers need to offset costs |
| **Informal ride-sharing is risky** | WhatsApp groups and roadside pickups lack verification, trust, and payment safety |
| **Fragmented communication** | No unified platform for driver-passenger coordination (pickup timing, luggage, etc.) |

### For the Market

| Problem | Impact |
|---|---|
| **India's intercity gap** | 800M+ annual intercity trips, but limited affordable options between buses and flights |
| **Underserved routes** | Tier-2 and Tier-3 city corridors have minimal public transport |
| **Environmental cost** | Single-occupancy cars on highways create unnecessary emissions |
| **Safety concerns** | Unverified drivers on informal platforms create trust barriers |

---

## 3. Product Vision & Goals

### Vision

> *"Make intercity travel as easy as booking a cab — affordable for passengers, profitable for drivers, and safe for everyone."*

### Strategic Goals

| # | Goal | Metric | Target (Year 1) |
|---|---|---|---|
| G1 | **Driver Acquisition** | Registered & verified drivers | 50,000 |
| G2 | **Ride Supply** | Monthly posted rides | 200,000 |
| G3 | **Booking Conversion** | Booking requests → Accepted | > 60% |
| G4 | **Driver Retention** | Monthly active drivers (MAD) | > 40% of registered |
| G5 | **Trust & Safety** | Verified drivers ratio | > 80% |
| G6 | **Route Coverage** | Active city-pair corridors | 500+ |
| G7 | **User Satisfaction** | Driver NPS score | > 50 |

### Product Principles

1. **Safety First** — Every design decision prioritizes driver safety (large touch targets, minimal distraction, hands-free where possible)
2. **Clarity Over Decoration** — Information hierarchy is king; no unnecessary visual noise
3. **Earn With Ease** — Posting a ride should take < 2 minutes
4. **Trust Through Transparency** — Verified profiles, visible ratings, clear pricing
5. **Offline-Resilient** — Core features work on spotty highway connections

---

## 4. Target Users

### Primary User: The Intercity Driver

| Attribute | Detail |
|---|---|
| **Age** | 22–50 years |
| **Gender** | Predominantly male (90%+), growing female segment |
| **Location** | Tier-1 and Tier-2 Indian cities |
| **Vehicle** | Personal car (sedan/hatchback/SUV) |
| **Driving Frequency** | 2–8 intercity trips per month |
| **Smartphone** | Mid-range Android (70%), iPhone (30%) |
| **Language** | Hindi (primary), English (secondary), regional languages (future) |
| **Tech Literacy** | Moderate — comfortable with WhatsApp, UPI, Google Maps |

### Driver Segments

| Segment | Description | % of Drivers | Motivation |
|---|---|---|---|
| **Regular Commuters** | Travel the same route weekly (e.g., Delhi ↔ Jaipur for work) | 40% | Offset fuel costs |
| **Weekend Travelers** | Drive home or on trips on weekends | 25% | Earn extra income |
| **Professional Drivers** | Full-time drivers offering rides as a business | 20% | Primary income source |
| **Occasional Travelers** | One-off trips (festivals, events, family visits) | 15% | Cover trip expenses |

---

## 5. User Personas

### Persona 1: Rajesh — The Regular Commuter

```
┌──────────────────────────────────────────────────────┐
│  👤 RAJESH SHARMA                                    │
│  Age: 34 | Software Engineer | Delhi → Jaipur       │
│                                                      │
│  📱 Phone: Samsung Galaxy S23                        │
│  🚗 Vehicle: Maruti Suzuki Swift Dzire (2023)        │
│  🔄 Frequency: Every Friday evening, returns Sunday  │
│                                                      │
│  GOALS:                                              │
│  • Save ₹3,000/month on fuel for weekly commute     │
│  • Find verified passengers (no random strangers)    │
│  • Quick ride posting (< 2 min)                      │
│                                                      │
│  FRUSTRATIONS:                                       │
│  • Current WhatsApp group is unreliable              │
│  • Passengers cancel last minute                     │
│  • No payment guarantee                              │
│                                                      │
│  TECH COMFORT: ⭐⭐⭐⭐ (High)                      │
│  PRICE SENSITIVITY: ⭐⭐⭐ (Medium)                  │
└──────────────────────────────────────────────────────┘
```

### Persona 2: Priya — The Weekend Traveler

```
┌──────────────────────────────────────────────────────┐
│  👩 PRIYA PATEL                                      │
│  Age: 28 | Marketing Manager | Mumbai → Pune         │
│                                                      │
│  📱 Phone: iPhone 15                                 │
│  🚗 Vehicle: Hyundai Creta (2024)                    │
│  🔄 Frequency: 2-3 weekends per month                │
│                                                      │
│  GOALS:                                              │
│  • Earn ₹1,500–2,000 per trip                       │
│  • Meet interesting people on drives                 │
│  • Feel safe (prefer female passengers)              │
│                                                      │
│  FRUSTRATIONS:                                       │
│  • Safety concerns with unknown passengers           │
│  • No platform designed for women drivers            │
│  • Complicated registration on existing apps         │
│                                                      │
│  TECH COMFORT: ⭐⭐⭐⭐⭐ (Very High)                │
│  PRICE SENSITIVITY: ⭐⭐ (Low)                       │
└──────────────────────────────────────────────────────┘
```

### Persona 3: Amit — The Professional Driver

```
┌──────────────────────────────────────────────────────┐
│  👨 AMIT KUMAR                                       │
│  Age: 42 | Professional Driver | Lucknow ↔ Varanasi │
│                                                      │
│  📱 Phone: Redmi Note 13                             │
│  🚗 Vehicle: Toyota Innova Crysta (2022)             │
│  🔄 Frequency: 5-6 trips per week                    │
│                                                      │
│  GOALS:                                              │
│  • Maximize earnings (primary income source)         │
│  • Fill all seats on every trip                      │
│  • Build reputation through ratings                  │
│                                                      │
│  FRUSTRATIONS:                                       │
│  • Inconsistent passenger demand                     │
│  • No way to showcase vehicle quality                │
│  • Payment collection issues                         │
│                                                      │
│  TECH COMFORT: ⭐⭐⭐ (Medium)                       │
│  PRICE SENSITIVITY: ⭐⭐⭐⭐⭐ (Very High)            │
└──────────────────────────────────────────────────────┘
```

---

## 6. User Stories & Requirements

### Epic 1: Authentication & Onboarding

| ID | User Story | Priority | Acceptance Criteria |
|---|---|---|---|
| US-1.1 | As a new driver, I want to **sign up** with my name, email, phone, and password so I can create an account | P0 | Form validates all fields; password min 8 chars; duplicate email/phone blocked; OTP sent for phone verification |
| US-1.2 | As a returning driver, I want to **log in** with email and password so I can access my dashboard | P0 | Valid credentials → Home screen; invalid → error message; remember session |
| US-1.3 | As a driver, I want to **reset my password** via OTP so I can recover my account | P0 | OTP sent to registered phone/email; 6-digit code; 5-minute expiry; 3 retry limit |
| US-1.4 | As a driver, I want to **stay logged in** so I don't have to login every time | P1 | JWT refresh token persists session for 30 days |
| US-1.5 | As a driver, I want to **log out** from my account | P1 | Clears tokens; returns to login screen |

### Epic 2: Driver Profile & Verification

| ID | User Story | Priority | Acceptance Criteria |
|---|---|---|---|
| US-2.1 | As a driver, I want to **complete my profile** with photo and personal details so passengers can trust me | P0 | Upload profile photo (max 5MB), edit name, phone, email |
| US-2.2 | As a driver, I want to **upload my driving licence** for verification so I can post rides | P0 | Upload front + back photos of DL; accepted formats: JPG, PNG, PDF; max 5MB each |
| US-2.3 | As a driver, I want to **see my verification status** so I know when I'm approved | P0 | Status shown: Pending → Under Review → Verified / Rejected; push notification on status change |
| US-2.4 | As a driver, I want to **add my vehicle** details so passengers know what car I drive | P0 | Enter make, model, year, color, plate number, seats, upload 3 photos |
| US-2.5 | As a driver, I want to **manage multiple vehicles** so I can switch between cars | P2 | List of vehicles; set active vehicle; edit/delete vehicles |

### Epic 3: Posting a Ride

| ID | User Story | Priority | Acceptance Criteria |
|---|---|---|---|
| US-3.1 | As a driver, I want to **post a ride** with departure details so passengers can find me | P0 | Select date, time, vehicle, available seats; validation on all fields |
| US-3.2 | As a driver, I want to **set my route** with origin, destination, and optional stops | P0 | Map-based route selection; autocomplete for cities; add up to 3 intermediate stops |
| US-3.3 | As a driver, I want to **set my price** per seat so passengers know the cost | P0 | Enter price in ₹; show suggested price range based on distance; minimum ₹100 |
| US-3.4 | As a driver, I want to **set ride preferences** so I attract compatible passengers | P1 | Toggle: smoking allowed, pets allowed, luggage size (S/M/L), women-only option |
| US-3.5 | As a driver, I want to **review and publish** my ride before it goes live | P0 | Summary screen showing all details; confirm button; ride visible to passengers within 30 seconds |
| US-3.6 | As a driver, I want to **edit a posted ride** before departure | P1 | Change time, seats, price, or cancel; notify booked passengers of changes |
| US-3.7 | As a driver, I want to **cancel a ride** if my plans change | P0 | Confirm cancellation; auto-notify all booked passengers; update status |

### Epic 4: Managing Rides & Bookings

| ID | User Story | Priority | Acceptance Criteria |
|---|---|---|---|
| US-4.1 | As a driver, I want to **see my upcoming rides** so I can prepare | P0 | List of rides sorted by departure; show date, route, seats filled, status |
| US-4.2 | As a driver, I want to **view booking requests** with passenger details | P0 | Show passenger name, photo, rating, phone, pickup/drop, seats requested, fare |
| US-4.3 | As a driver, I want to **accept or decline** booking requests | P0 | Accept → passenger notified + seat count decremented; Decline → passenger notified with reason |
| US-4.4 | As a driver, I want to **see my ride history** so I can track my earnings | P0 | List of completed rides; show date, route, passengers, total earned, ratings received |
| US-4.5 | As a driver, I want to **start and complete a ride** to update status | P1 | "Start Ride" button at departure time; "Complete Ride" at destination; auto-trigger payment |
| US-4.6 | As a driver, I want to **see my total earnings** with daily/weekly/monthly views | P1 | Dashboard with segmented control (Daily/Weekly/Monthly); show total, average per ride, chart |

### Epic 5: Messaging

| ID | User Story | Priority | Acceptance Criteria |
|---|---|---|---|
| US-5.1 | As a driver, I want to **see my inbox** with all passenger conversations | P0 | List of conversations; last message preview; timestamp; unread badge |
| US-5.2 | As a driver, I want to **chat with a passenger** to coordinate pickup | P0 | Real-time text messaging; message delivery indicators; auto-scroll to latest |
| US-5.3 | As a driver, I want to **share my live location** with a passenger | P2 | Share current GPS location as a map pin in chat |
| US-5.4 | As a driver, I want to **send photos** in chat (e.g., vehicle photo, landmark) | P2 | Pick from gallery or camera; compress before sending; max 5MB |
| US-5.5 | As a driver, I want **chat to auto-create** when a booking is accepted | P0 | Conversation created automatically; both parties can message immediately |

### Epic 6: Notifications

| ID | User Story | Priority | Acceptance Criteria |
|---|---|---|---|
| US-6.1 | As a driver, I want to **receive push notifications** for new bookings | P0 | Push notification with passenger name, route, fare; tap → opens booking detail |
| US-6.2 | As a driver, I want to **see all notifications** in-app | P0 | Chronological list; types: booking, confirmation, payment, rating, system |
| US-6.3 | As a driver, I want to **mark notifications as read** | P1 | Tap to mark individual; "Mark all read" button |
| US-6.4 | As a driver, I want **real-time notifications** without pulling to refresh | P1 | WebSocket connection for instant updates; fallback to push |

### Epic 7: Search & Discovery (Home)

| ID | User Story | Priority | Acceptance Criteria |
|---|---|---|---|
| US-7.1 | As a driver, I want to **see available rides** on my dashboard | P0 | Cards showing nearby ride requests from passengers |
| US-7.2 | As a driver, I want to **search for rides** by route and date | P1 | Search bar with origin/destination autocomplete; date filter |
| US-7.3 | As a driver, I want to **see my quick stats** on the home screen | P1 | Total rides, earnings this week, rating badge |

---

## 7. Feature Specification

### Feature Priority Matrix

| Feature | Priority | MVP? | Effort | Impact |
|---|---|---|---|---|
| Driver Signup & Login | P0 | ✅ | Medium | Critical |
| Forgot Password (OTP) | P0 | ✅ | Low | Critical |
| Driver Profile | P0 | ✅ | Medium | High |
| Licence Verification | P0 | ✅ | Medium | Critical (Trust) |
| Add Vehicle | P0 | ✅ | Medium | Critical |
| Post a Ride (2 steps) | P0 | ✅ | High | Critical |
| My Rides List | P0 | ✅ | Medium | High |
| Booking Requests | P0 | ✅ | High | Critical |
| Accept/Decline Booking | P0 | ✅ | Medium | Critical |
| Ride History | P0 | ✅ | Medium | High |
| Inbox (Conversations) | P0 | ✅ | Medium | High |
| 1-on-1 Chat | P0 | ✅ | High | High |
| Push Notifications | P0 | ✅ | Medium | High |
| In-app Notifications | P0 | ✅ | Low | Medium |
| Home Dashboard | P0 | ✅ | Medium | High |
| Earnings Dashboard | P1 | ❌ | Medium | Medium |
| Ride Preferences | P1 | ❌ | Low | Medium |
| Location Sharing | P2 | ❌ | Medium | Low |
| Photo Messages | P2 | ❌ | Low | Low |
| Multi-language | P2 | ❌ | High | Medium |
| Dark Mode | P3 | ❌ | Medium | Low |
| In-app Payments | P1 | ❌ | High | High |
| Ratings & Reviews | P1 | ❌ | Medium | High |

---

## 8. Screen-by-Screen Specification

### 8.1 Driver Login

| Attribute | Detail |
|---|---|
| **Screen ID** | `062f148272fb4725a5f3b8393a64034c` |
| **Entry Points** | App launch (not logged in) |
| **Exit Points** | Home (success), Signup, Forgot Password |

**UI Elements:**
- JUKO logo + tagline at top
- Email input field (label-caps: "EMAIL")
- Password input field with show/hide toggle (label-caps: "PASSWORD")
- "Login" primary button (Confident Blue, full-width, 48px height)
- "Forgot Password?" ghost link
- "Don't have an account? Sign Up" link at bottom
- Social login buttons (Google, Apple) — P2

**Validation Rules:**
- Email: valid format, required
- Password: minimum 8 characters, required
- Show inline errors below fields
- Disable button while loading
- Show toast on API error

**API Call:** `POST /auth/login`

---

### 8.2 Driver Signup

| Attribute | Detail |
|---|---|
| **Screen ID** | `bd07c6db61fd4748a2606be45c7dc766` |
| **Entry Points** | Login screen → "Sign Up" link |
| **Exit Points** | Login (success), back to Login |

**UI Elements:**
- "Create Account" headline
- Full Name input
- Email input
- Phone number input (with +91 country code prefix)
- Password input (with strength indicator)
- Confirm Password input
- "I agree to Terms & Privacy Policy" checkbox
- "Create Account" primary button
- "Already have an account? Login" link

**Validation Rules:**
- Name: 2-50 characters, required
- Email: valid format, unique (server-side check)
- Phone: valid Indian mobile (10 digits), unique
- Password: min 8 chars, 1 uppercase, 1 number
- Confirm password: must match
- Terms checkbox: required

**API Call:** `POST /auth/signup`

---

### 8.3 Forgot Password — Verify OTP

| Attribute | Detail |
|---|---|
| **Screen ID** | `e3ffecb695594040a3ab9df8e0b6ef55` |
| **Entry Points** | Login → "Forgot Password?" |
| **Exit Points** | Reset Password (success), back to Login |

**UI Elements:**
- "Verify OTP" headline
- Subtitle: "Enter the 6-digit code sent to your phone/email"
- 6-box OTP input (auto-advance, auto-submit on complete)
- Timer: "Resend in 0:59" → "Resend OTP" link
- "Verify" primary button
- Back arrow to return to Login

**Behavior:**
- Auto-focus first OTP box
- Paste from clipboard support
- 3 wrong attempts → lock for 15 minutes
- OTP expires in 5 minutes
- Max 5 resend requests per hour

**API Calls:** `POST /auth/forgot-password` → `POST /auth/verify-otp`

---

### 8.4 Ride Search Home

| Attribute | Detail |
|---|---|
| **Screen ID** | `c842c1d166974be49aa9e05bc08408c5` |
| **Entry Points** | Login success, bottom nav "Search" tab |
| **Exit Points** | Post Ride, Booking Detail, Inbox, any bottom nav tab |

**UI Elements:**
- Top bar: JUKO logo + Online/Offline toggle + notification bell (with badge)
- Search bar: "Where are you going?" with origin/destination fields
- Quick stats row: Total rides | This week earnings | Rating
- "Post a Ride" prominent CTA button (Success Green)
- Available ride requests cards (scrollable list)
- Each card: passenger photo, route, date, fare, seats needed
- Bottom navigation: Search | Bookings | Alerts | Profile

**Behavior:**
- Pull-to-refresh updates ride feed
- Search autocomplete powered by Google Places API
- Bottom nav badge on Alerts tab when unread notifications exist
- "Post a Ride" button always visible (floating or sticky)

---

### 8.5 My Rides

| Attribute | Detail |
|---|---|
| **Screen ID** | `c2d013aa65234fbd8dc6fafc9e8d4dc9` |
| **Entry Points** | Bottom nav "Bookings" tab |
| **Exit Points** | Booking Requests, History |

**UI Elements:**
- Segmented control: "Upcoming" | "Active" | "Completed"
- Ride cards list (per tab):
  - Route (origin → destination)
  - Date & time
  - Seats: "3/4 filled"
  - Price per seat
  - Status chip (Scheduled / In Progress / Completed)
- Empty state per tab if no rides
- Pull-to-refresh

**Behavior:**
- Default tab: "Upcoming"
- Tap card → navigate to Booking Requests for that ride
- "Completed" tab tap → navigate to History

---

### 8.6 Booking Requests — Detailed Info

| Attribute | Detail |
|---|---|
| **Screen ID** | `9236be1ab8694ecd99a57834495f7c87` |
| **Entry Points** | My Rides → tap a ride card; Notification tap |
| **Exit Points** | Back to My Rides, Chat |

**UI Elements:**
- Ride summary header: route, date/time, vehicle
- Passenger request cards (one per request):
  - Passenger avatar + name + rating (⭐ 4.8)
  - Phone number (tap to call)
  - Pickup location
  - Drop location
  - Seats requested
  - Total fare
  - "Accept" button (Success Green, full-width)
  - "Decline" button (Ghost, red text)
- Accepted passengers section (collapsed by default)

**Behavior:**
- Accept → seat count decrements; passenger notified; chat auto-created
- Decline → passenger notified with "ride full" or custom reason
- Cannot accept if 0 seats remaining (button disabled)
- Swipe-to-decline gesture (P2)

---

### 8.7 History

| Attribute | Detail |
|---|---|
| **Screen ID** | `e377142e2e6e4a9fb2311bb2ffd56e69` |
| **Entry Points** | My Rides → "Completed" tab; Profile → "Ride History" |
| **Exit Points** | Back |

**UI Elements:**
- Monthly grouping headers ("August 2026", "July 2026")
- Ride history cards:
  - Route (origin → destination)
  - Date
  - Passengers count
  - Total earned (₹ in Numeric-Data typography, bold)
  - Rating received (⭐)
- Summary at top: Total trips | Total earned this month
- Cursor-based pagination (infinite scroll)

---

### 8.8 Post a Ride — Ride Details (Step 1)

| Attribute | Detail |
|---|---|
| **Screen ID** | `df4617371e5b472e819c82a17411c00a` |
| **Entry Points** | Home → "Post a Ride" CTA |
| **Exit Points** | Step 2 (Route & Pricing), Cancel (back to Home) |

**UI Elements:**
- "Post a Ride" headline + step indicator (1/2)
- Date picker (calendar modal)
- Time picker
- Vehicle selector (dropdown of my vehicles)
- Available seats selector (stepper: 1 to max vehicle seats - 1)
- Preferences section:
  - Smoking allowed toggle
  - Pets allowed toggle
  - Luggage size selector (Small / Medium / Large)
- "Next" primary button

**Validation:**
- Date: must be today or future; max 30 days ahead
- Time: must be at least 1 hour from now (if today)
- Vehicle: required (must have at least one verified vehicle)
- Seats: minimum 1

---

### 8.9 Post a Ride — Route & Pricing (Step 2)

| Attribute | Detail |
|---|---|
| **Screen ID** | `3acaeb0d0d5b42af8e78cd045bc1ead2` |
| **Entry Points** | Step 1 → "Next" |
| **Exit Points** | Publish (Home), Back (Step 1) |

**UI Elements:**
- Step indicator (2/2)
- Origin input with map pin (autocomplete)
- "Add Stop" button (max 3 stops)
- Destination input with map pin (autocomplete)
- Map preview showing route with pins
- Estimated distance & duration (auto-calculated)
- Price per seat input (₹)
- Suggested price range: "₹350 – ₹500 for this route"
- Total potential earnings display
- Ride summary card (review all details)
- "Publish Ride" primary button (Success Green)

**Validation:**
- Origin ≠ Destination
- Minimum distance: 50 km
- Price: minimum ₹100, maximum ₹10,000
- All required fields filled

**API Call:** `POST /rides`

---

### 8.10 Inbox — Conversations

| Attribute | Detail |
|---|---|
| **Screen ID** | `48d0b410d54c4875ba9a79ec40c1eb03` |
| **Entry Points** | Home → Inbox icon; Chat notification tap |
| **Exit Points** | Chat screen (tap conversation) |

**UI Elements:**
- "Messages" headline
- Conversation list:
  - Passenger avatar
  - Passenger name
  - Last message preview (truncated to 1 line)
  - Timestamp ("2m ago", "Yesterday")
  - Unread badge (count)
  - Route label below name ("Delhi → Jaipur, Aug 28")
- Empty state if no conversations

**Behavior:**
- Sorted by last message time (newest first)
- Real-time updates via WebSocket
- Unread conversations have bold text
- Pull-to-refresh

---

### 8.11 Chat

| Attribute | Detail |
|---|---|
| **Screen ID** | `622df6059d064067b54949f77a4ae602` |
| **Entry Points** | Inbox → tap conversation |
| **Exit Points** | Back to Inbox |

**UI Elements:**
- Top bar: Passenger name + avatar + "Call" icon + back arrow
- Message bubbles:
  - Sent (right, Confident Blue background, white text)
  - Received (left, Surface Container background, dark text)
  - Timestamp below each message
- "Typing..." indicator
- Message input field at bottom
- Send button (arrow icon, Confident Blue)
- Attachment button (📎) — P2

**Behavior:**
- Real-time via WebSocket; REST fallback
- Auto-scroll to latest message
- Load older messages on scroll up (pagination)
- Message states: Sent → Delivered → Read
- Soft keyboard pushes input field up

---

### 8.12 Notifications

| Attribute | Detail |
|---|---|
| **Screen ID** | `94420b5a18204910b3d5af0ca18c77c2` |
| **Entry Points** | Bottom nav "Alerts" tab; push notification tap |
| **Exit Points** | Related screen (tap notification) |

**UI Elements:**
- "Notifications" headline + "Mark all read" ghost button
- Notification cards:
  - Icon based on type (🔔 booking, ✅ confirmed, 💰 payment, ⭐ rating)
  - Title (bold)
  - Body text
  - Timestamp
  - Unread indicator (blue dot)
- Empty state with bell icon + "No notifications yet"

**Deep Links:**
- New Booking → Booking Requests screen
- Ride Confirmed → My Rides
- Payment Received → History / Earnings
- Rating Received → Profile

---

### 8.13 Add Vehicle

| Attribute | Detail |
|---|---|
| **Screen ID** | `9ee9633b9e9243cd90b23c1dc8a881e2` |
| **Entry Points** | Profile → "Add Vehicle"; Post Ride (if no vehicle) |
| **Exit Points** | Profile (success), back |

**UI Elements:**
- "Add Vehicle" headline
- Make input (autocomplete: Maruti, Hyundai, Tata, Toyota, etc.)
- Model input (filtered by make)
- Year of manufacture (dropdown: 2010–2026)
- Color input
- License plate number (formatted: XX 00 XX 0000)
- Total seats (stepper: 2–8)
- Vehicle photos section:
  - Front view (required)
  - Side view (optional)
  - Interior view (optional)
  - Camera / Gallery picker
- "Save Vehicle" primary button

**Validation:**
- Plate number: valid Indian format, unique
- At least 1 photo required
- Photo max size: 5MB each
- All text fields required

**API Call:** `POST /vehicles`

---

### 8.14 Driver Profile — Licence Verification

| Attribute | Detail |
|---|---|
| **Screen ID** | `06043777420d4a9ca57a07c5b92a90dd` |
| **Entry Points** | Bottom nav "Profile" tab |
| **Exit Points** | Add Vehicle, Licence Upload |

**UI Elements:**
- Profile header: avatar (editable), name, rating, total rides
- Verification status banner:
  - ⏳ Pending: "Your documents are being reviewed"
  - ✅ Verified: Green "Verified Driver" badge
  - ❌ Rejected: "Please re-upload your documents"
- Document upload section:
  - Driving Licence — Front (photo/PDF)
  - Driving Licence — Back (photo/PDF)
  - Upload progress indicator
- Personal info section: Name, Email, Phone (editable)
- My Vehicles link
- Settings: Notification preferences, language, help & support
- Logout button (red ghost)

---

### 8.15 & 8.16 — Empty States

**Inbox Empty State** (`b4c9d1274efc44389e521737d2dabf38`):
- 💬 Chat bubble illustration
- "No messages yet"
- "Conversations will appear here after you accept a booking"

**Notifications Empty State** (`97f680d954f64e6fbc4efd62e7cba15e`):
- 🔔 Bell illustration
- "No notifications yet"
- "We'll notify you about bookings, payments, and updates"

---

## 9. User Flows

### Flow 1: First-Time Driver (Onboarding → First Ride)

```
App Install
    → Signup (name, email, phone, password)
    → Verify phone (OTP)
    → Login
    → Upload profile photo
    → Upload driving licence (front + back)
    → Wait for verification (push notification when done)
    → Add vehicle (make, model, plate, photos)
    → Post first ride (details → route → pricing → publish)
    → Receive first booking request
    → Accept booking
    → Chat with passenger to coordinate
    → Complete ride
    → View earnings in History ✅
```

### Flow 2: Returning Driver (Post & Manage)

```
Open app (auto-login)
    → Home dashboard
    → Tap "Post a Ride"
    → Step 1: Select date, time, vehicle, seats
    → Step 2: Set route, add stops, set price
    → Publish ride
    → Receive push notification: "New booking from Sneha"
    → Open Booking Requests
    → Review passenger details
    → Accept booking
    → Chat opens automatically
    → Coordinate pickup details
    → On departure day: Start ride
    → At destination: Complete ride
    → View earnings updated ✅
```

### Flow 3: Handling a Booking Request

```
Push notification: "New booking request"
    → Tap notification
    → Opens Booking Requests screen
    → See passenger: name, rating, pickup, drop, fare
    → Decision:
        ├── Accept → Seat count -1 → Chat created → Passenger notified ✅
        └── Decline → Passenger notified → Seat count unchanged ❌
```

---

## 10. Information Architecture

```
JUKO Driver App
│
├── 🔐 Auth (unauthenticated)
│   ├── Login
│   ├── Signup
│   └── Forgot Password → OTP Verify → Reset
│
├── 🏠 Home (Tab 1: Search)
│   ├── Dashboard stats
│   ├── Ride feed
│   ├── Search rides
│   └── → Post a Ride (2-step wizard)
│
├── 🚘 Bookings (Tab 2)
│   ├── My Rides (Upcoming / Active / Completed)
│   │   ├── → Booking Requests (per ride)
│   │   └── → History
│   └── Earnings overview
│
├── 🔔 Alerts (Tab 3)
│   └── Notifications list
│
├── 👤 Profile (Tab 4)
│   ├── Driver info & photo
│   ├── Verification status & document upload
│   ├── My Vehicles
│   │   └── → Add/Edit Vehicle
│   ├── Settings
│   └── Logout
│
└── 💬 Inbox (global, accessible from any tab)
    ├── Conversation list
    └── → Chat (1-on-1)
```

---

## 11. Business Rules & Logic

### Ride Publishing

| Rule | Detail |
|---|---|
| Only verified drivers can post rides | Verification status must be "verified" |
| Must have at least 1 active vehicle | Cannot post without a registered vehicle |
| Maximum 3 intermediate stops | Origin + 3 stops + destination |
| Ride must be at least 1 hour in the future | If posting for today |
| Maximum 30 days in advance | No rides beyond 30 days |
| Minimum distance: 50 km | Filters out intra-city trips |
| Price range: ₹100 — ₹10,000 per seat | Backend suggests range based on distance |
| Minimum 1 seat, max = vehicle capacity - 1 | Driver seat excluded |
| Cannot post overlapping rides | Time conflict within 2-hour window blocked |

### Booking Management

| Rule | Detail |
|---|---|
| Booking auto-expires if not responded in 24 hours | Status → "Expired"; passenger notified |
| Cannot accept more passengers than available seats | Accept button disabled when 0 seats |
| Cancellation within 1 hour of departure → penalty flag | Driver profile shows "late cancellation" warning |
| Chat auto-creates on booking acceptance | Both parties can message immediately |
| Driver can cancel a ride anytime | All booked passengers notified; cancellation logged |

### Verification

| Rule | Detail |
|---|---|
| DL must be valid (not expired) | Backend team verifies; future: OCR auto-check |
| Maximum 3 re-upload attempts on rejection | After 3 rejections, manual support ticket required |
| Vehicle plate must be unique across platform | No two drivers can register the same plate |
| Profile photo must be a real face | Future: face detection AI |

### Ratings

| Rule | Detail |
|---|---|
| Passengers rate drivers after ride completion | 1–5 stars |
| Rating is a weighted average of last 50 rides | Older ratings have less weight |
| Drivers below 3.5 rating receive a warning | Below 3.0 for 3 consecutive months → account review |

---

## 12. Notifications Strategy

### Push Notification Types

| Trigger | Title | Body | Deep Link | Priority |
|---|---|---|---|---|
| New booking request | "New Booking Request" | "{Name} wants to join your {route} ride" | Booking Requests | High |
| Booking accepted (by driver) | "Booking Confirmed" | "You accepted {Name}'s booking for {route}" | Chat | Medium |
| Passenger cancelled | "Booking Cancelled" | "{Name} cancelled their booking for {route}" | My Rides | Medium |
| Ride reminder (1 hour before) | "Ride Starting Soon" | "Your {route} ride departs in 1 hour" | My Rides | High |
| Payment received | "Payment Received" | "₹{amount} credited for your {route} ride" | History | Medium |
| Rating received | "New Rating" | "You received a {stars}⭐ rating from {Name}" | Profile | Low |
| Verification approved | "You're Verified! ✅" | "Your driving licence has been verified. Start posting rides!" | Home | High |
| Verification rejected | "Verification Failed" | "Your documents could not be verified. Please re-upload." | Profile | High |
| New chat message | "{Name}" | "{message_preview}" | Chat | High |

### In-App Notification Settings

| Setting | Default | Options |
|---|---|---|
| Booking notifications | ON | ON / OFF |
| Chat message notifications | ON | ON / OFF |
| Ride reminders | ON | ON / OFF |
| Payment & earnings | ON | ON / OFF |
| Marketing & tips | ON | ON / OFF |

---

## 13. Non-Functional Requirements

### Performance

| Metric | Target |
|---|---|
| App cold start | < 2 seconds |
| Screen transition | < 300ms |
| API response rendering | < 500ms (including network) |
| Image load (cached) | < 100ms |
| Image load (network) | < 2 seconds |
| Chat message delivery | < 500ms |
| Search results | < 1 second |
| Smooth scrolling | 60 FPS consistently |

### Reliability

| Metric | Target |
|---|---|
| Crash-free rate | > 99.5% |
| API error rate | < 1% |
| WebSocket uptime | > 99% with auto-reconnect |
| Offline data availability | Core screens viewable offline |

### Compatibility

| Platform | Minimum | Recommended |
|---|---|---|
| Android | API 26 (Android 8.0) | API 33+ (Android 13+) |
| iOS | iOS 16.0 | iOS 17.0+ |
| Screen sizes | 5" to 6.9" | Optimized for 6.1"–6.7" |
| Orientation | Portrait only | — |
| Network | 2G (degraded), 3G (functional), 4G/5G (optimal) | 4G+ |

### App Size

| Platform | Target |
|---|---|
| Android APK | < 30 MB |
| Android AAB | < 20 MB (per-ABI splits) |
| iOS IPA | < 40 MB |

### Accessibility

| Requirement | Implementation |
|---|---|
| Minimum touch target | 48dp × 48dp |
| Color contrast | WCAG AA (4.5:1 for text, 3:1 for UI) |
| Screen reader | Content descriptions on all interactive elements |
| Font scaling | Support up to 200% system font scale |
| Reduce motion | Respect system "reduce motion" setting |

### Localization (Future)

| Language | Priority | Phase |
|---|---|---|
| English | P0 | v1.0 |
| Hindi | P1 | v1.1 |
| Tamil | P2 | v2.0 |
| Telugu | P2 | v2.0 |
| Marathi | P2 | v2.0 |
| Kannada | P2 | v2.0 |

---

## 14. Analytics & KPIs

### Key Events to Track

| Category | Event | Properties |
|---|---|---|
| **Auth** | `signup_completed` | method (email/google/apple) |
| **Auth** | `login_completed` | method |
| **Auth** | `login_failed` | error_type |
| **Profile** | `document_uploaded` | doc_type (dl_front, dl_back) |
| **Profile** | `verification_completed` | status (verified/rejected) |
| **Profile** | `vehicle_added` | vehicle_type, seats |
| **Ride** | `ride_posted` | origin_city, dest_city, distance_km, price_per_seat |
| **Ride** | `ride_cancelled` | reason, hours_before_departure |
| **Booking** | `booking_received` | ride_id, source |
| **Booking** | `booking_accepted` | response_time_seconds |
| **Booking** | `booking_declined` | reason |
| **Chat** | `message_sent` | conversation_id, message_type |
| **Chat** | `conversation_opened` | from (notification/inbox) |
| **Navigation** | `screen_viewed` | screen_name, duration_seconds |
| **Navigation** | `tab_switched` | from_tab, to_tab |

### KPI Dashboard

| KPI | Formula | Target |
|---|---|---|
| **DAU** | Daily active users | Track growth |
| **Rides per driver per month** | Total rides / Active drivers | > 4 |
| **Booking acceptance rate** | Accepted / Total requests | > 60% |
| **Avg response time** | Time from request to accept/decline | < 2 hours |
| **Avg earnings per ride** | Total payments / Completed rides | ₹400–800 |
| **Chat engagement** | Conversations with > 3 messages | > 70% |
| **Verification funnel** | Signup → Upload → Verified | > 50% |
| **Retention (D7)** | Users active on Day 7 | > 40% |
| **Retention (D30)** | Users active on Day 30 | > 25% |

---

## 15. Monetization Model

| Revenue Stream | Description | Phase |
|---|---|---|
| **Service Fee** | 10–15% commission on each booking fare | v1.0 |
| **Boost Listing** | Drivers pay to promote their ride to top of search | v2.0 |
| **Subscription (Pro Driver)** | ₹299/month for lower commission (8%), priority support, analytics | v2.0 |
| **Insurance Upsell** | Partner with insurance for per-ride travel insurance | v2.0 |
| **Fleet Management** | Dashboard for fleet operators (manage multiple drivers) | v3.0 |

---

## 16. Competitive Analysis

| Feature | JUKO | BlaBlaCar | Ola Outstation | InDriver |
|---|---|---|---|---|
| **Focus** | Intercity ride-sharing | Intercity ride-sharing | Intercity cab booking | City rides |
| **Market** | India | Europe (primary), India (limited) | India | Global |
| **Driver-first app** | ✅ Dedicated | ✅ Combined | ❌ Driver as employee | ✅ Combined |
| **Ride publishing** | ✅ 2-step wizard | ✅ Multi-step | ❌ Dispatch model | ❌ |
| **Booking control** | ✅ Accept/Decline | ✅ Auto-accept option | ❌ Auto-assign | ✅ Negotiation |
| **In-app chat** | ✅ Real-time | ✅ | ✅ | ✅ |
| **Price control** | ✅ Driver sets price | ✅ Driver sets | ❌ Fixed pricing | ✅ Bidding |
| **Verification** | ✅ DL + Vehicle | ✅ ID + DL | ✅ Full commercial | ⚠️ Basic |
| **Payment** | In-app (planned) | In-app | In-app | Cash + in-app |
| **Language** | EN + Hindi (planned) | Multi-language | EN + Hindi | Multi-language |

### JUKO's Differentiators

1. **India-first** — Built for Indian intercity corridors, UPI integration, regional routes
2. **Driver-centric** — Dedicated driver app with earnings focus, not a combined consumer app
3. **Safety-first UX** — 48px touch targets, high-contrast design for vehicle-mount use
4. **Affordable tech** — KMP reduces development cost by 40% vs native x2

---

## 17. Assumptions & Constraints

### Assumptions

| # | Assumption |
|---|---|
| A1 | Drivers have a valid Indian driving licence |
| A2 | Drivers own or have access to a registered vehicle |
| A3 | Drivers have a smartphone with internet access (at least 3G) |
| A4 | Drivers are willing to share their car with 1–4 strangers |
| A5 | Backend APIs will be available and documented before frontend development begins |
| A6 | Google Maps Platform API will be used for geocoding and routing |
| A7 | Firebase will be used for push notifications on both platforms |
| A8 | Payment integration will be handled in a later phase (v1.1+) |
| A9 | Driver verification will be manual (admin panel) in v1.0, automated later |

### Constraints

| # | Constraint |
|---|---|
| C1 | Single codebase required (KMP + Compose Multiplatform) — no separate native apps |
| C2 | Must support Android 8.0+ and iOS 16+ |
| C3 | Backend team provides APIs; frontend team does not build backend |
| C4 | v1.0 English only; Hindi in v1.1 |
| C5 | Portrait orientation only (no landscape support) |
| C6 | Maximum app size: 30MB APK, 40MB IPA |
| C7 | Must comply with Google Play and Apple App Store guidelines |

---

## 18. Risks & Mitigations

| # | Risk | Impact | Probability | Mitigation |
|---|---|---|---|---|
| R1 | Compose Multiplatform iOS performance issues | High | Low | Benchmark on low-end iPhones; fallback to SwiftUI for critical screens |
| R2 | Backend API delays | High | Medium | Define API contracts upfront (OpenAPI spec); use mock APIs during development |
| R3 | Low driver adoption in initial cities | High | Medium | Focus on 5 high-demand corridors; driver referral bonuses |
| R4 | Safety incidents | Critical | Low | DL verification; SOS button (v1.1); ride tracking; passenger ratings |
| R5 | App Store rejection | Medium | Low | Follow Apple Human Interface Guidelines; no private API usage |
| R6 | Payment fraud | High | Medium | Delay in-app payments to v1.1; use Razorpay fraud detection |
| R7 | Poor network on highways | Medium | High | Offline caching via SQLDelight; background sync when connected |
| R8 | Driver distraction while driving | Critical | Medium | No complex interactions during active ride; voice notifications (v2.0) |

---

## 19. Release Plan & Milestones

### Phase 1: MVP (v1.0) — 12 Weeks

| Week | Milestone | Deliverables |
|---|---|---|
| 1–2 | **Setup & Design** | KMP project setup, design system implementation, CI/CD pipeline |
| 3–4 | **Auth & Profile** | Login, Signup, Forgot Password, Profile, Document Upload |
| 5–6 | **Vehicle & Rides** | Add Vehicle, Post Ride (2 steps), My Rides |
| 7–8 | **Bookings & History** | Booking Requests, Accept/Decline, Ride History |
| 9–10 | **Chat & Notifications** | Inbox, 1-on-1 Chat (WebSocket), Push Notifications, In-app Notifications |
| 11 | **Testing & Polish** | Bug fixes, performance optimization, empty states, error handling |
| 12 | **Release** | Play Store + TestFlight beta → Production release |

### Phase 2: Growth (v1.1) — 6 Weeks

| Feature | Description |
|---|---|
| In-app Payments | Razorpay integration; auto-deduct commission |
| Ratings & Reviews | Post-ride rating system for drivers and passengers |
| Hindi Language | Full localization |
| Earnings Dashboard | Charts, weekly/monthly summaries |
| Ride Preferences | Smoking, pets, luggage, women-only |
| SOS Button | Emergency contact + location sharing |

### Phase 3: Scale (v2.0) — 8 Weeks

| Feature | Description |
|---|---|
| Dark Mode | Full dark theme variant |
| Location Sharing | Real-time GPS sharing in chat |
| Photo Messages | Image attachments in chat |
| Recurring Rides | "Repeat every Friday" automation |
| Driver Analytics | Trip patterns, peak hours, popular routes |
| Fleet Dashboard | Multi-driver management for fleet operators |
| Regional Languages | Tamil, Telugu, Marathi, Kannada |

---

## 20. Success Criteria

### MVP Launch (v1.0) — Month 1

| Criteria | Target |
|---|---|
| App live on Play Store + App Store | ✅ |
| Crash-free rate | > 99% |
| Core flow completion rate (signup → post first ride) | > 50% |
| App rating | > 4.0 on both stores |

### Month 3

| Criteria | Target |
|---|---|
| Registered drivers | 5,000 |
| Verified drivers | 3,000 (60%) |
| Monthly posted rides | 10,000 |
| Booking acceptance rate | > 50% |
| DAU | 1,000 |

### Month 6

| Criteria | Target |
|---|---|
| Registered drivers | 20,000 |
| Monthly posted rides | 50,000 |
| Active city-pair corridors | 100+ |
| Booking acceptance rate | > 60% |
| Driver NPS | > 40 |

### Year 1

| Criteria | Target |
|---|---|
| Registered drivers | 50,000 |
| Monthly posted rides | 200,000 |
| Revenue (monthly) | ₹50 Lakhs |
| Active corridors | 500+ |
| Driver NPS | > 50 |

---

## 21. Appendix

### A. Screen Inventory

| # | Screen Name | Screen ID | Category |
|---|---|---|---|
| 1 | Driver Login | `062f148272fb4725a5f3b8393a64034c` | Auth |
| 2 | Driver Signup | `bd07c6db61fd4748a2606be45c7dc766` | Auth |
| 3 | Forgot Password — Verify OTP | `e3ffecb695594040a3ab9df8e0b6ef55` | Auth |
| 4 | Ride Search Home | `c842c1d166974be49aa9e05bc08408c5` | Home |
| 5 | My Rides — Upgraded | `c2d013aa65234fbd8dc6fafc9e8d4dc9` | Rides |
| 6 | History — Upgraded | `e377142e2e6e4a9fb2311bb2ffd56e69` | Rides |
| 7 | Booking Requests — Detailed Info | `9236be1ab8694ecd99a57834495f7c87` | Rides |
| 8 | Post a Ride — Ride Details | `df4617371e5b472e819c82a17411c00a` | Post Ride |
| 9 | Post a Ride — Route & Pricing | `3acaeb0d0d5b42af8e78cd045bc1ead2` | Post Ride |
| 10 | Inbox — Conversations | `48d0b410d54c4875ba9a79ec40c1eb03` | Chat |
| 11 | Inbox — Empty State | `b4c9d1274efc44389e521737d2dabf38` | Chat |
| 12 | Chat with Sneha Gupta | `622df6059d064067b54949f77a4ae602` | Chat |
| 13 | Notifications | `94420b5a18204910b3d5af0ca18c77c2` | Notifications |
| 14 | Notifications — Empty State | `97f680d954f64e6fbc4efd62e7cba15e` | Notifications |
| 15 | Add Vehicle | `9ee9633b9e9243cd90b23c1dc8a881e2` | Profile |
| 16 | Driver Profile — Licence Verification | `06043777420d4a9ca57a07c5b92a90dd` | Profile |

### B. Design System Reference

- Design system name: **Velocity Drive**
- Design file: [`design.md`](file:///C:/Users/HAMMAD%20KHAN/.gemini/antigravity/scratch/design.md)
- Primary color: `#0052CC`
- Font: Inter
- Stitch project: `projects/4074151794218231363`

### C. Related Documents

| Document | Path |
|---|---|
| Project Details | [`JUKO_Project_Details.md`](file:///C:/Users/HAMMAD%20KHAN/.gemini/antigravity/scratch/JUKO_Project_Details.md) |
| Design System | [`design.md`](file:///C:/Users/HAMMAD%20KHAN/.gemini/antigravity/scratch/design.md) |
| App Flow | [`app_flow.md`](file:///C:/Users/HAMMAD%20KHAN/.gemini/antigravity/scratch/app_flow.md) |
| Tech Spec | [`tech_spec.md`](file:///C:/Users/HAMMAD%20KHAN/.gemini/antigravity/scratch/tech_spec.md) |

### D. Glossary

| Term | Definition |
|---|---|
| **Driver** | A vehicle owner who posts rides and carries passengers |
| **Passenger** | A traveler who books a seat on a driver's ride (separate app) |
| **Ride** | A published intercity trip with route, date, and pricing |
| **Booking** | A passenger's request to join a ride |
| **Corridor** | A frequently traveled city-pair route (e.g., Delhi ↔ Jaipur) |
| **Verification** | The process of validating a driver's identity and licence |
| **Seat** | One available spot in the driver's vehicle for a passenger |
| **KMP** | Kotlin Multiplatform — framework for shared code across Android & iOS |

---

*JUKO PRD v1.0 — August 24, 2026*
