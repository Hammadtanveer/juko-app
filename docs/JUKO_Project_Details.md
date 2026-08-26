# 🚗 JUKO — Full Project Report

## Project Overview

| Property | Value |
|---|---|
| **Project ID** | `projects/4074151794218231363` |
| **Title** | JUKO |
| **Visibility** | 🌐 Public |
| **Project Type** | TEXT_TO_UI_PRO |
| **Device Target** | 📱 Mobile |
| **Origin** | Stitch |
| **Created** | July 23, 2026 (03:53 UTC) |
| **Last Updated** | August 24, 2026 (12:55 UTC) |
| **Your Role** | Owner |

---

## 🎨 Design Theme — "Velocity Drive"

### Core Settings

| Setting | Value |
|---|---|
| **Color Mode** | Light |
| **Primary Font** | Inter |
| **Headline Font** | Inter |
| **Body Font** | Inter |
| **Label Font** | Inter |
| **Roundness** | 8px (ROUND_EIGHT) |

### Color Palette

| Token | Hex | Usage |
|---|---|---|
| **Primary** | `#003d9b` | Core brand blue |
| **Primary Container** | `#0052cc` | Confident Blue — buttons, active states |
| **On Primary** | `#ffffff` | Text on primary |
| **Secondary** | `#5d5f5f` | Neutral accent |
| **Secondary Container** | `#dfe0e0` | Secondary backgrounds |
| **Tertiary** | `#004e32` | Success / green |
| **Tertiary Container** | `#006844` | Green accent area |
| **Error** | `#ba1a1a` | Error states |
| **Error Container** | `#ffdad6` | Error backgrounds |
| **Surface** | `#f9f9ff` | App background |
| **Surface Container** | `#e8edff` | Card containers |
| **Surface Container High** | `#e0e8ff` | Elevated surfaces |
| **Surface Container Highest** | `#d7e2ff` | Top-level surfaces |
| **Surface Container Low** | `#f1f3ff` | Low-level surfaces |
| **Surface Container Lowest** | `#ffffff` | White base |
| **Surface Dim** | `#cadaff` | Dimmed surface |
| **Surface Bright** | `#f9f9ff` | Bright surface |
| **On Surface** | `#041b3c` | Primary text |
| **On Surface Variant** | `#434654` | Secondary text |
| **Outline** | `#737685` | Borders |
| **Outline Variant** | `#c3c6d6` | Subtle borders |
| **Inverse Surface** | `#1d3052` | Dark surfaces |
| **Inverse On Surface** | `#edf0ff` | Text on dark surface |
| **Inverse Primary** | `#b2c5ff` | Primary on dark |
| **Background** | `#f9f9ff` | Page background |
| **On Background** | `#041b3c` | Text on background |
| **Surface Tint** | `#0c56d0` | Tint overlay |
| **Surface Variant** | `#d7e2ff` | Variant surface |

### Override Colors (Brand Anchors)

| Role | Color | Name |
|---|---|---|
| Primary Override | `#0052cc` | Confident Blue |
| Secondary Override | `#ffffff` | White |
| Tertiary Override | `#36b37e` | Success Green |
| Neutral Override | `#172b4d` | Navy |

### Fixed Colors

| Token | Hex |
|---|---|
| Primary Fixed | `#dae2ff` |
| Primary Fixed Dim | `#b2c5ff` |
| On Primary Fixed | `#001848` |
| On Primary Fixed Variant | `#0040a2` |
| Secondary Fixed | `#e2e2e2` |
| Secondary Fixed Dim | `#c6c6c7` |
| On Secondary Fixed | `#1a1c1c` |
| On Secondary Fixed Variant | `#454747` |
| Tertiary Fixed | `#82f9be` |
| Tertiary Fixed Dim | `#65dca4` |
| On Tertiary Fixed | `#002113` |
| On Tertiary Fixed Variant | `#005235` |

---

### Typography Scale

| Role | Font | Size | Weight | Line Height | Letter Spacing |
|---|---|---|---|---|---|
| **Display LG** | Inter | 32px | 700 (Bold) | 40px | -0.02em |
| **Headline MD** | Inter | 24px | 600 (Semi-Bold) | 32px | -0.01em |
| **Headline MD Mobile** | Inter | 20px | 600 | 28px | — |
| **Title SM** | Inter | 18px | 600 | 24px | — |
| **Body LG** | Inter | 16px | 400 (Regular) | 24px | — |
| **Body MD** | Inter | 14px | 400 | 20px | — |
| **Label Caps** | Inter | 12px | 700 | 16px | 0.05em |
| **Numeric Data** | Inter | 28px | 700 | 32px | -0.02em |

### Spacing System

| Token | Value |
|---|---|
| Base | 4px |
| XS | 8px |
| SM | 12px |
| MD | 16px |
| LG | 24px |
| XL | 32px |
| Edge Margin | 16px |
| Stack Gap | 12px |

### Border Radius

| Token | Value |
|---|---|
| SM | 0.25rem (4px) |
| Default | 0.5rem (8px) |
| MD | 0.75rem (12px) |
| LG | 1rem (16px) |
| XL | 1.5rem (24px) |
| Full | 9999px (Pill) |

---

## 🧭 Design Philosophy

### Brand & Style

The design system is engineered for the high-context, fast-paced environment of a professional driver. The brand personality is **reliable, efficient, and clarifying**, prioritizing utility and safety over decorative elements.

The aesthetic follows a **Modern Corporate** direction with a focus on high-legibility "Card-based" architecture. This ensures that information units — like trip requests, earnings, and navigation cues — are physically distinct and easily tappable in a vehicle mount scenario.

### Colors Philosophy

The palette is anchored by **Primary Blue (#0052CC)**, used for primary actions, active states, and brand reinforcement. **White (#FFFFFF)** serves as the primary structural background color to maintain a high-contrast, clean environment that performs well under varying daylight conditions.

- **Success/Secondary**: Vibrant green (#36B37E) for "Accept" actions and positive earning indicators
- **Neutral/Text**: Deep Navy (#172B4D) for maximum contrast against white backgrounds
- **Surface**: Light grey washes (#F4F5F7) to distinguish background from white cards

### Typography Philosophy

Inter is used exclusively to leverage its exceptional legibility and neutral, systematic tone. The type scale emphasizes **Numeric-Data** and **Display** roles to ensure earnings and ETAs are readable at a glance from a distance.

### Layout & Spacing

- **Fluid grid** with focused mobile-first approach
- Central container with **16px side margins**
- **4px baseline grid** governs all vertical spacing
- **12px gap** between stacked cards
- **48px minimum touch targets** for all interactive elements
- **16px internal card padding**

### Elevation & Depth

| Level | Surface | Shadow |
|---|---|---|
| **Background** | Light grey (#F4F5F7) | None |
| **Cards** | Pure white (#FFFFFF) | 0px 2px 8px rgba(0, 0, 0, 0.05) |
| **Floating elements** | White | 0px 8px 24px rgba(0, 0, 0, 0.12) |

> Outlines are avoided in favor of shadow-based depth for a soft, modern feel.

### Shapes

- Standard components: **0.5rem (8px)** corner radius
- Large containers / primary buttons: **1rem (16px)** corner radius
- Segmented controls / bottom nav: **Pill-shaped** for high-frequency touch areas

### Component Definitions

| Component | Specification |
|---|---|
| **Brand Header** | "RideShare" brand left-aligned in Primary Blue; Online/Offline toggle on right |
| **Segmented Control** | Light grey track with white elevated pill slider |
| **Primary Button** | Solid Primary Blue, white text, 16px rounded corners |
| **Success Button** | Solid Green (#36B37E) for "Accept Trip" / "Complete Ride" |
| **Ghost Button** | Primary Blue text, no background, for secondary actions |
| **Cards** | White BG, 8px radius, subtle shadow, 1px grey dividers for lists |
| **Bottom Navigation** | 4-tab bar (Search, Bookings, Alerts, Profile), white BG with top blur |
| **Input Fields** | 1px grey border → Primary Blue on focus, label-caps above field |

---

## 📱 All Screens — Visible (16)

### 🔐 Authentication & Onboarding

| # | Screen Title | Dimensions | Screen ID |
|---|---|---|---|
| 1 | **Driver Login - Email/Password (Juko)** | 780 × 1768 | `062f148272fb4725a5f3b8393a64034c` |
| 2 | **Driver Signup (Juko)** | 780 × 2078 | `bd07c6db61fd4748a2606be45c7dc766` |
| 3 | **Forgot Password - Verify OTP (Juko)** | 780 × 1768 | `e3ffecb695594040a3ab9df8e0b6ef55` |

### 🚘 Core Ride Flow

| # | Screen Title | Dimensions | Screen ID |
|---|---|---|---|
| 4 | **Ride Search Home (Juko)** | 780 × 2014 | `c842c1d166974be49aa9e05bc08408c5` |
| 5 | **My Rides - Upgraded (Juko)** | 780 × 1768 | `c2d013aa65234fbd8dc6fafc9e8d4dc9` |
| 6 | **History - Upgraded (Juko)** | 780 × 2212 | `e377142e2e6e4a9fb2311bb2ffd56e69` |
| 7 | **Booking Requests - Detailed Info (Juko)** | 780 × 3166 | `9236be1ab8694ecd99a57834495f7c87` |

### 📝 Posting a Ride

| # | Screen Title | Dimensions | Screen ID |
|---|---|---|---|
| 8 | **Post a Ride - Ride Details (Juko)** | 780 × 3188 | `df4617371e5b472e819c82a17411c00a` |
| 9 | **Post a Ride - Route & Pricing (Juko)** | 780 × 3312 | `3acaeb0d0d5b42af8e78cd045bc1ead2` |

### 💬 Messaging & Notifications

| # | Screen Title | Dimensions | Screen ID |
|---|---|---|---|
| 10 | **Inbox - Conversations (Juko)** | 780 × 2032 | `48d0b410d54c4875ba9a79ec40c1eb03` |
| 11 | **Inbox - Empty State (Juko)** | 780 × 1768 | `b4c9d1274efc44389e521737d2dabf38` |
| 12 | **Chat with Sneha Gupta (Juko)** | 780 × 1768 | `622df6059d064067b54949f77a4ae602` |
| 13 | **Notifications (Juko)** | 780 × 1912 | `94420b5a18204910b3d5af0ca18c77c2` |
| 14 | **Notifications - Empty State (Juko)** | 780 × 1768 | `97f680d954f64e6fbc4efd62e7cba15e` |

### 👤 Driver Profile & Vehicle

| # | Screen Title | Dimensions | Screen ID |
|---|---|---|---|
| 15 | **Add Vehicle (Juko)** | 780 × 2308 | `9ee9633b9e9243cd90b23c1dc8a881e2` |
| 16 | **Driver Profile - Licence Verification (Juko)** | 780 × 3284 | `06043777420d4a9ca57a07c5b92a90dd` |

---

## 🙈 Hidden / Variant Screen Instances (~49)

These are hidden screens on the canvas — older versions, variants, or work-in-progress iterations.

| # | Instance ID | Position (x, y) | Size (w × h) |
|---|---|---|---|
| 1 | `05a67aca0aac4881a4d3716e57c8ab11` | (5156, -123) | 390 × 1057 |
| 2 | `1bf5b7ee491a4abfa217326364cd7cf7` | (5572, -30) | 390 × 884 |
| 3 | `1ff78fa343664f738bd02d8ebd2a7267` | (2212, -148) | 390 × 885 |
| 4 | `23e36e0c31d04136b3c9b2030c100aaf` | (8252, 2483) | 390 × 1213 |
| 5 | `288a09bfea524c289e0a835bd1079d31` | (9633, 2070) | 390 × 1143 |
| 6 | `2c3f326b935143b98dfe28a1ac17fd54` | (3178, 2466) | 390 × 884 |
| 7 | `327b42a027b9443883b4ef3a234627f8` | (8271, 2484) | 390 × 1131 |
| 8 | `327b42a0...(variant)` | (8252, 3972) | 390 × 1131 |
| 9 | `34555d53dbf24456a87ea797d5943f22` | (12605, 2564) | 390 × 761 |
| 10 | `36fe5a70b88c4ee69d0be96963699054` | (3642, -132) | 390 × 859 |
| 11 | `3c1c939e8bd948ac867c3b0e926360bd` | (4143, -130) | 390 × 620 |
| 12 | `3f7a019182424d1bbf42fd726726afe2` | (16492, 2499) | 390 × 884 |
| 13 | `486b28b114324b67b887ca6c2b3ddaf0` | (6759, -111) | 390 × 956 |
| 14 | `4c041ab9d3aa4822aeb389d613deca66` | (3156, -130) | 390 × 664 |
| 15 | `513f71e4dfb0415890feeaa176b2e83b` | (5715, -116) | 390 × 1169 |
| 16 | `528afba8f32b48c2be39d1bae3f3597a` | (10986, 2483) | 390 × 1160 |
| 17 | `5492ed3859854f7d8992c01d85557f32` | (21106, 2527) | 390 × 884 |
| 18 | `5a8f792196b64543b83b3e4ad80f47ac` | (2679, -140) | 390 × 987 |
| 19 | `7c5afce0555a42ae98b12bb311e51047` | (8312, 3717) | 390 × 1135 |
| 20 | `8a92a33556d64056aa603aa1b6efcaae` | (454, 5201) | 390 × 864 |
| 21 | `8edf221996324ce885c1bf42bcca6f67` | (0, 2561) | 390 × 884 |
| 22 | `93fa961f4d5945ac839305063f5348b6` | (1258, -130) | 390 × 633 |
| 23 | `9b7c13ec857d4170a6ce2c8368074be9` | (14363, 2544) | 390 × 512 |
| 24 | `9bf421bf-db86-...` | (0, 2561) | 292 × 66 |
| 25 | `9d20eb907b814668a43a239ad8d1c556` | (11082, 2490) | 390 × 832 |
| 26 | `a0162d7a1ede4e14991323c237f7a4bd` | (7570, 3074) | 390 × 1168 |
| 27 | `a0c6b3c62e1b4383aac3e76441264411` | (7960, 2179) | 390 × 1343 |
| 28 | `a0c6b3c6...(variant)` | (7332, 3670) | 390 × 1274 |
| 29 | `a455cb849a8c4fa8b69d9788db4c8979` | (16979, 2100) | 390 × 884 |
| 30 | `b4f768c70ad14c9b8d415ac2fce9c60a` | (6246, -104) | 390 × 604 |
| 31 | `b5ec6cefc89b4f4ca580516fc6fcd558` | (15448, 2052) | 390 × 884 |
| 32 | `bebee233940d4cdba9e9929997c45f6e` | (18240, 2527) | 390 × 1169 |
| 33 | `bfae5d17bc1a49c9b5f10b6657e3451c` | (0, 5201) | 390 × 684 |
| 34 | `c08a041d4cc44b3b92cdb36350bb6ad5` | (2270, 2466) | 390 × 884 |
| 35 | `c7eb2fa9dc41432d9f13b2fc1a53643d` | (908, 5201) | 390 × 732 |
| 36 | `cd9296e710fd451ca9d21b80bc592383` | (7213, -111) | 390 × 884 |
| 37 | `d26157d00f964253be5f745bb5f242c8` | (4877, -46) | 390 × 884 |
| 38 | `d2b8f4df-d9de-...` | (0, 4306) | 255 × 38 |
| 39 | `d39ce3602ddf4bc091c9081c60619234` | (0, 1610) | 600 × 600 |
| 40 | `d7de8afcd6fe44708b8001fe14ebdf20` | (1362, 5201) | 390 × 700 |
| 41 | `defd26fc0a034a37931115e37ed02a79` | (4083, 2484) | 390 × 858 |
| 42 | `e8426755f06d45cb9b6c5deb3dc81c3a` | (4641, -123) | 390 × 1016 |
| 43 | `e852c1ffd37847469b8cf5fced4262cd` | (11697, 2564) | 390 × 732 |
| 44 | `ef635dd29c2b4f588b9ab699fec83c53` | (1816, 2466) | 390 × 884 |
| 45 | `f53159fa6ce54fab8716d386d7be9894` | (14890, 2070) | 390 × 884 |
| 46 | `f838c1d7f1e64c46a11384f88e45b832` | (1024, 0) | 390 × 884 |
| 47 | `fbbbc6a545f44bf3a1f852dd89624265` | (14179, 2490) | 390 × 833 |
| 48 | `fc4f5dd3bf944ab3842e0b0e75f516a2` | (1742, -140) | 390 × 1037 |

> **+1 Design System Instance** on canvas at (4760, 2100) — 960 × 540

---

## 📊 Summary Statistics

| Metric | Count |
|---|---|
| **Total Visible Screens** | 16 |
| **Hidden/Variant Instances** | ~49 |
| **Total Canvas Instances** | ~65 |
| **Design System Instances** | 1 |
| **Screen Categories** | Auth (3), Rides (4), Post Ride (2), Messaging (5), Profile (2) |
| **All Screens Mobile** | ✅ Yes (780px width @ 2x) |

---

## 🗺️ App Flow

```
Driver Login ─┬─→ Driver Signup
              ├─→ Forgot Password (OTP)
              └─→ Ride Search Home
                    ├─→ My Rides ─→ History
                    │              └─→ Booking Requests (Detailed)
                    ├─→ Post a Ride (Details) ─→ Post a Ride (Route & Pricing)
                    ├─→ Inbox (Conversations) ─→ Chat with Sneha Gupta
                    │   └─→ Inbox (Empty State)
                    ├─→ Notifications
                    │   └─→ Notifications (Empty State)
                    ├─→ Add Vehicle
                    └─→ Driver Profile (Licence Verification)
```

---

*Report generated on August 24, 2026 via Stitch MCP*
