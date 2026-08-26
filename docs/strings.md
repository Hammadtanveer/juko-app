# 🚗 JUKO — UI Strings & Copy (UX Writing)

> **Purpose:** This document defines the exact UX copy (text) to be used across the JUKO driver app. AI agents must use these exact strings to ensure a professional, consistent, and localized-ready tone. 
> 
> **KMP Implementation Note:** AI agents should ideally place these strings in a centralized `object AppStrings { ... }` or use the Compose Multiplatform Resource library (`stringResource(...)`) rather than hardcoding them into UI components.

---

## 1. Common / Generic Actions

| Key / Purpose | String Value |
|---|---|
| button_continue | "Continue" |
| button_next | "Next" |
| button_save | "Save" |
| button_cancel | "Cancel" |
| button_retry | "Retry" |
| button_back | "Back" |
| state_loading | "Loading..." |
| state_no_results | "No results found" |

---

## 2. Authentication & Onboarding

| Key / Purpose | String Value |
|---|---|
| auth_login_title | "Welcome Back" |
| auth_signup_title | "Create Account" |
| auth_login_cta | "Login" |
| auth_signup_cta | "Create Account" |
| auth_forgot_password | "Forgot Password?" |
| auth_no_account | "Don't have an account? Sign Up" |
| auth_has_account | "Already have an account? Login" |
| auth_otp_title | "Verify OTP" |
| auth_otp_subtitle | "Enter the 6-digit code sent to your phone" |
| auth_otp_resend | "Resend OTP" |
| auth_terms_agree | "I agree to the Terms & Privacy Policy" |

---

## 3. Bottom Navigation Tabs

| Key / Purpose | String Value |
|---|---|
| tab_search | "Search" |
| tab_bookings | "Bookings" |
| tab_alerts | "Alerts" |
| tab_profile | "Profile" |

---

## 4. Ride Posting (Publish Flow)

| Key / Purpose | String Value |
|---|---|
| post_ride_title_step1 | "Post a Ride (1/2)" |
| post_ride_title_step2 | "Post a Ride (2/2)" |
| post_ride_date_time | "Departure Date & Time" |
| post_ride_vehicle_select | "Select Vehicle" |
| post_ride_seats | "Available Seats" |
| post_ride_preferences | "Ride Preferences" |
| post_ride_pref_smoking | "Smoking Allowed" |
| post_ride_pref_pets | "Pets Allowed" |
| post_ride_pref_women | "Women Only" |
| post_ride_origin | "Pickup City / Location" |
| post_ride_dest | "Drop-off City / Location" |
| post_ride_add_stop | "+ Add Intermediate Stop" |
| post_ride_price | "Price per Seat (₹)" |
| post_ride_publish_cta | "Publish Ride" |
| post_ride_success | "Ride published successfully!" |

---

## 5. Ride Management & Bookings

| Key / Purpose | String Value |
|---|---|
| rides_tab_upcoming | "Upcoming" |
| rides_tab_active | "Active" |
| rides_tab_completed | "Completed" |
| rides_empty_title | "No rides found" |
| rides_empty_subtitle | "You haven't posted any rides yet." |
| booking_req_title | "Booking Requests" |
| booking_action_accept | "Accept" |
| booking_action_decline | "Decline" |
| booking_seats_requested | "Seats requested:" |
| booking_total_fare | "Total fare:" |
| booking_accept_success| "Booking accepted. Chat opened." |

---

## 6. Profile & Verification

| Key / Purpose | String Value |
|---|---|
| profile_status_pending | "Verification Pending" |
| profile_status_verified| "Verified Driver" |
| profile_status_rejected| "Verification Rejected" |
| profile_dl_upload | "Upload Driving Licence" |
| profile_dl_front | "Licence Front Image" |
| profile_dl_back | "Licence Back Image" |
| vehicle_add_title | "Add a Vehicle" |
| vehicle_plate_hint | "e.g., DL 01 AB 1234" |
| vehicle_photo_req | "Please upload at least 1 photo" |

---

## 7. Global Error Messages

| Key / Purpose | String Value |
|---|---|
| err_network | "No internet connection. Please check your network and try again." |
| err_server | "The server is currently busy. Please try again later." |
| err_session_expired | "Your session has expired. Please log in again." |
| err_field_required | "This field is required" |
| err_invalid_email | "Please enter a valid email address" |
| err_invalid_phone | "Please enter a valid 10-digit mobile number" |
| err_password_weak | "Password must be at least 8 characters" |
| err_passwords_match | "Passwords do not match" |
