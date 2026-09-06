# Juko Rideshare — Product Requirements & Technical Architecture Blueprint

**Document Version:** 2.0  
**Target Platform:** Kotlin Multiplatform (Android & iOS) + Compose Multiplatform  
**Last Updated:** September 2026  
**Status:** Agreed & Finalized Architecture  

---

## 🧭 1. Core Mental Model: "Single Destination, Multiple Pickups"

A ride in Juko represents an intercity carpooling journey structured as:

$$\text{Source} \longrightarrow \text{Stop 1} \longrightarrow \text{Stop 2} \longrightarrow \dots \longrightarrow \text{Final Destination}$$

### 🔑 Golden Rules:
1. **One Fixed Final Destination:** Every ride has exactly **ONE Source** (e.g. *Seohara*) and **ONE Final Destination** (e.g. *Delhi*).
2. **Stops = Only Boarding Points (Not Drop-offs):** Stops are intermediate locations where passengers can be picked up. All passengers travel towards the same Final Destination.
3. **No Splitting of Rides:** A driver creates **ONE ride listing** (e.g. *Seohara $\rightarrow$ Delhi with stops at Noorpur & Chandpur*). The system does **not** create separate individual rides for each stop.
4. **Valid Search Routes:**
   - $\checkmark$ `Seohara -> Delhi` (Full route)
   - $\checkmark$ `Noorpur -> Delhi` (Boarding at Stop 1)
   - $\checkmark$ `Chandpur -> Delhi` (Boarding at Stop 2)
   - $\times$ `Noorpur -> Chandpur` (Invalid: Stops cannot be independent drop-off destinations)
5. **Auto-Proportional Pricing:** 
   - Driver enters **ONE full-route fare** (e.g. ₹500 for *Seohara $\rightarrow$ Delhi*).
   - System automatically calculates fares for passengers joining from stops based on remaining distance/route percentage (e.g. *Noorpur $\rightarrow$ Delhi* = ₹400, *Chandpur $\rightarrow$ Delhi* = ₹300).

---

## 👥 2. Role-Based Architecture (Customer vs. Driver)

* **Default Role:** Every user starts in the **`CUSTOMER` (Passenger)** role upon login.
* **Automatic Role Transition:** 
  - Tapping **"Publish"** (Tab 2) automatically switches context to **`DRIVER`**.
* **Driver Onboarding Verification Guardrail:**
  - Before a driver can publish a ride, the system enforces 3 mandatory requirements:
    1. 📞 **10-Digit Verified Phone Number**
    2. 🪪 **Driver's Licence Photos (Front & Back)**
    3. 🚗 **At Least 1 Registered Vehicle**
  - If any requirement is missing, the app displays a **"Complete Driver Profile First"** alert dialog and redirects directly to the Profile / Vehicle setup screen.

---

## 🚗 3. Vehicle Management Specification

### A. Data Schema & Constraints:
| Field | Type / Rule | Description |
| :--- | :--- | :--- |
| **`vehicle_name`** | String Concatenation | User enters `Brand` & `Model` separately in UI; frontend concatenates into `"${brand} ${model}"` (e.g. `"Toyota Camry"`). |
| **`color`** | String | Vehicle exterior color (e.g. `"Silver"`, `"White"`). |
| **`plate_number`** | String (Uppercase) | Registration plate (e.g. `"DL-01-AB-1234"`). |
| **`seating_capacity`**| Fixed Enum (`5` or `7`) | Segmented toggle for **`5-Seater`** or **`7-Seater`** (Open stepper removed). |
| **`photos`** | Array of URLs (Max 3) | Up to **3 vehicle photos**. Upload, delete (`✕`), and replace support. |
| **`has_ac`** | **REMOVED** | AC toggle is removed as it is not part of the backend schema. |

### B. Derived Passenger Seat Availability:
* **5-Seater Vehicle:** Driver can offer max **1 to 4 passenger seats**.
* **7-Seater Vehicle:** Driver can offer max **1 to 6 passenger seats**.

### C. Screen Architecture (Smart Dual-Mode Screen):
* `AddVehicleScreen.kt` serves both modes cleanly:
  - **Add Mode (`existingVehicle == null`):** Blank form, title *"Add Vehicle"*, button *"Save Car"*.
  - **Edit Mode (`existingVehicle != null`):** Pre-populated fields, existing photo thumbnails with delete badges, title *"Edit Vehicle"*, button *"Update Vehicle"*.

---

## 🔒 4. Ride Creation & Edit Restrictions

### A. Creation Flow:
$$\text{Source} \longrightarrow \text{Stops} \longrightarrow \text{Destination} \longrightarrow \text{Date/Time (Current+Future)} \longrightarrow \text{Seats (1-4 or 1-6)} \longrightarrow \text{Full Fare} \longrightarrow \text{Summary / Review} \longrightarrow \text{Publish}$$

### B. Strict Editing Rule:
* **0 Bookings:** Driver can freely edit departure date, time, route stops, seats, and fare.
* **$\ge 1$ Booking:** Ride details editing is **completely locked & disabled** to protect booked passengers.

---

## 📋 5. "Your Rides / My Rides" Tab Architecture

The "Your Rides" tab dynamically renders different views based on the active role:

### 👤 Customer View (Passenger):
1. **`Bookings` Tab:** Active and pending ride requests with *Driver Info*, *Pickup $\rightarrow$ Destination*, *Booked Seats*, *Fare*, and *Cancel / Chat* actions.
2. **`History` Tab:** Past completed and cancelled bookings with *Date Range & Status filters* and **"Review Driver"** action.

### 🚗 Driver View (Driver):
1. **`Published` Tab:** Active ride listings with real-time seat occupancy progress bars and edit controls (locked if bookings $> 0$).
2. **`Requests` Tab:** Incoming passenger booking requests with **"Accept"** / **"Reject"** actions.
3. **`History` Tab:** Past completed driver rides with *Date Range & Status filters* and **"Review Customer"** action.

---

## ⭐ 6. Two-Way Review System (History $\rightarrow$ Review)

Triggered directly from completed rides in the **History** tab:
* **Customer $\rightarrow$ Driver Review:**
  - Star Rating (1 to 5 Stars)
  - Review tags: *Punctuality*, *Car Cleanliness*, *Safe Driving*, *Communication*
  - Written comment
* **Driver $\rightarrow$ Customer Review:**
  - Star Rating (1 to 5 Stars)
  - Review tags: *Punctuality*, *Polite Behavior*, *Communication*
  - Written comment
* **Rule:** Duplicate reviews on the same completed ride are prevented.

---

## 📜 7. History Filters (Date Range + Status)

Filter bar for both Customer and Driver history:
* **Date Range Picker:** *Last 7 Days*, *This Month*, *Last 30 Days*, or *Custom Date Range*.
* **Status Filter Chips:** *All*, *Completed*, *Cancelled*.

---

## 🕒 8. Calendar & Clock Logic
* All date and time selectors across the app are restricted to **Current Date/Time and Future / Ahead Only** (past dates/times blocked).

---

## 🚀 9. 3-Phase Execution Roadmap

```
┌─────────────────────────────────────────────────────────────────────────┐
│ PHASE 1: Vehicle & Profile Refinements                                  │
│ - Remove AC toggle                                                      │
│ - Implement 5-Seater / 7-Seater toggle                                  │
│ - Max 3 Photos upload/delete/preview grid                               │
│ - Dual-mode Add/Edit Vehicle screen                                     │
│ - Concatenate Brand + Model                                             │
└────────────────────────────────────┬────────────────────────────────────┘
                                     │
                                     ▼
┌─────────────────────────────────────────────────────────────────────────┐
│ PHASE 2: Route, Pricing & Role-Based Rides Tab                          │
│ - Single full-route price input with auto-calculated stop pricing       │
│ - Customer Bookings tab vs Driver Published/Requests tabs in My Rides   │
│ - Strict Edit Lock enforcement for rides with >= 1 booking              │
└────────────────────────────────────┬────────────────────────────────────┘
                                     │
                                     ▼
┌─────────────────────────────────────────────────────────────────────────┐
│ PHASE 3: History, Filters & Two-Way Reviews                             │
│ - Date Range + Status filtering in History tab                          │
│ - Two-Way Driver <-> Customer Review Modal Dialog                       │
│ - Google Places API search integration (upon credential availability)   │
└─────────────────────────────────────────────────────────────────────────┘
```
