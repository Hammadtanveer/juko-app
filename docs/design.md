---
name: Velocity Drive
colors:
  surface: '#f9f9ff'
  surface-dim: '#cadaff'
  surface-bright: '#f9f9ff'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f1f3ff'
  surface-container: '#e8edff'
  surface-container-high: '#e0e8ff'
  surface-container-highest: '#d7e2ff'
  on-surface: '#041b3c'
  on-surface-variant: '#434654'
  inverse-surface: '#1d3052'
  inverse-on-surface: '#edf0ff'
  outline: '#737685'
  outline-variant: '#c3c6d6'
  surface-tint: '#0c56d0'
  primary: '#003d9b'
  on-primary: '#ffffff'
  primary-container: '#0052cc'
  on-primary-container: '#c4d2ff'
  inverse-primary: '#b2c5ff'
  secondary: '#5d5f5f'
  on-secondary: '#ffffff'
  secondary-container: '#dfe0e0'
  on-secondary-container: '#616363'
  tertiary: '#004e32'
  on-tertiary: '#ffffff'
  tertiary-container: '#006844'
  on-tertiary-container: '#72e9af'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#dae2ff'
  primary-fixed-dim: '#b2c5ff'
  on-primary-fixed: '#001848'
  on-primary-fixed-variant: '#0040a2'
  secondary-fixed: '#e2e2e2'
  secondary-fixed-dim: '#c6c6c7'
  on-secondary-fixed: '#1a1c1c'
  on-secondary-fixed-variant: '#454747'
  tertiary-fixed: '#82f9be'
  tertiary-fixed-dim: '#65dca4'
  on-tertiary-fixed: '#002113'
  on-tertiary-fixed-variant: '#005235'
  background: '#f9f9ff'
  on-background: '#041b3c'
  surface-variant: '#d7e2ff'
typography:
  display-lg:
    fontFamily: Inter
    fontSize: 32px
    fontWeight: '700'
    lineHeight: 40px
    letterSpacing: -0.02em
  headline-md:
    fontFamily: Inter
    fontSize: 24px
    fontWeight: '600'
    lineHeight: 32px
    letterSpacing: -0.01em
  headline-md-mobile:
    fontFamily: Inter
    fontSize: 20px
    fontWeight: '600'
    lineHeight: 28px
  title-sm:
    fontFamily: Inter
    fontSize: 18px
    fontWeight: '600'
    lineHeight: 24px
  body-lg:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  body-md:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
  label-caps:
    fontFamily: Inter
    fontSize: 12px
    fontWeight: '700'
    lineHeight: 16px
    letterSpacing: 0.05em
  numeric-data:
    fontFamily: Inter
    fontSize: 28px
    fontWeight: '700'
    lineHeight: 32px
    letterSpacing: -0.02em
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  base: 4px
  xs: 8px
  sm: 12px
  md: 16px
  lg: 24px
  xl: 32px
  edge-margin: 16px
  stack-gap: 12px
---

## Brand & Style

The design system is engineered for the high-context, fast-paced environment of a professional driver. The brand personality is **reliable, efficient, and clarifying**, prioritizing utility and safety over decorative elements. 

The aesthetic follows a **Modern Corporate** direction with a focus on high-legibility "Card-based" architecture. This ensures that information units—like trip requests, earnings, and navigation cues—are physically distinct and easily tappable in a vehicle mount scenario. The visual language uses generous whitespace and a "Safety-First" philosophy, where critical data points are elevated through scale rather than complex ornamentation.

## Colors

The palette is anchored by **Primary Blue (#0052CC)**, used for primary actions, active states, and brand reinforcement. **White (#FFFFFF)** serves as the primary structural background color to maintain a high-contrast, clean environment that performs well under varying daylight conditions.

- **Success/Secondary**: A vibrant green (#36B37E) is utilized for "Accept" actions and positive earning indicators.
- **Neutral/Text**: A deep Navy (#172B4D) is used for primary text to ensure maximum contrast against white backgrounds, reducing eye strain for drivers.
- **Surface**: Light grey washes (#F4F5F7) are used to distinguish the background from the white cards.

## Typography

This design system utilizes **Inter** exclusively to leverage its exceptional legibility and neutral, systematic tone. 

The type scale emphasizes **Numeric-Data** and **Display** roles to ensure earnings and ETAs are readable at a glance from a distance. **Label-caps** are used for secondary metadata (e.g., "DISTANCE", "PICKUP") to create a clear visual hierarchy between labels and dynamic user data. For mobile devices, headlines scale down slightly to prevent awkward text wrapping in narrow card components.

## Layout & Spacing

The layout utilizes a **fluid grid** with a focused mobile-first approach. Content is housed within a central container with **16px side margins**.

- **Vertical Rhythm**: A 4px baseline grid governs all spacing. Vertical stacks of cards use a **12px gap** to maintain a tight but distinct relationship.
- **Touch Targets**: All interactive elements (buttons, segmented controls) must maintain a minimum height of **48px** to accommodate rapid interaction while driving.
- **Card Padding**: Internal card padding is set to **16px (md)** to allow for clear separation between data points.

## Elevation & Depth

Visual hierarchy is achieved through **Tonal Layers** and **Ambient Shadows**. 

The base application background is a subtle light grey (#F4F5F7). Interactive cards are pure white (#FFFFFF) and sit at a low elevation, using a soft, highly-diffused shadow (0px 2px 8px rgba(0, 0, 0, 0.05)) to suggest "tapability."

Critical floating elements, such as the persistent bottom navigation or "New Request" overlays, use a higher elevation shadow (0px 8px 24px rgba(0, 0, 0, 0.12)) to appear closer to the driver and demand immediate attention. Outlines are avoided in favor of shadow-based depth to keep the interface feeling soft and modern.

## Shapes

The design system adopts a **Rounded** (Level 2) shape language. 

Standard components like input fields and small cards use a **0.5rem (8px)** corner radius. Large containers and primary action buttons utilize **rounded-lg (1rem/16px)** to feel more approachable and modern. Segmented controls and the bottom navigation bar use **pill-shaped** containers for high-frequency touch areas, providing a distinct ergonomic feel compared to static information cards.

## Components

### Brand Header
The header features the "RideShare" brand mark left-aligned in Primary Blue. The right side is reserved for a "Go Offline/Online" status indicator or driver profile shortcut.

### Segmented Control
Used for toggling between views (e.g., "Daily" vs "Weekly" earnings). It features a light grey track with a white, elevated "pill" that slides to indicate the active selection.

### Buttons
- **Primary**: Solid Primary Blue with white text. High-contrast, 16px rounded corners.
- **Success/Action**: Solid Green (#36B37E) specifically for "Accept Trip" or "Complete Ride."
- **Ghost**: Used for secondary actions (e.g., "Details"), featuring Primary Blue text with no background.

### Cards
Cards are the primary container. They must always have a white background, 8px corner radius, and a subtle shadow. Content inside cards should be separated by thin 1px light grey dividers when listing multiple items (like a trip history).

### Bottom Navigation
A persistent 4-tab bar (Search, Bookings, Alerts, Profile). It uses a white background with a top border blur. Icons use Primary Blue for the active state and a medium grey for inactive states.

### Input Fields
Clean, outlined boxes with a 1px grey border that thickens and turns Primary Blue on focus. Labels sit above the field in **label-caps** typography.
