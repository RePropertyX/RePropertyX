---
name: RePropertyX Design System
description: Modern, high-craft developer interface inspired by JetBrains and Kotlin design languages.
colors:
  primary: "#FF9800"
  primary-hover: "#F57C00"
  neutral-bg: "#121316"
  neutral-surface: "#1A1C20"
  neutral-surface-hover: "#22252B"
  neutral-border: "rgba(255, 255, 255, 0.08)"
  neutral-header: "rgba(0, 0, 0, 0.35)"
  text-main: "#F8FAFC"
  text-muted: "#94A3B8"
  text-dim: "#94A3B8"
  code-bg: "#16181D"
  accent-green: "#3DDC84"
  accent-green-text: "rgb(134, 239, 172)"
  accent-green-code: "rgb(167, 243, 208)"
  accent-purple: "#7F52FF"
  accent-cyan: "rgb(56, 189, 248)"
  accent-cyan-hex: "#38BDF8"
  accent-amber: "rgb(255, 183, 77)"
  accent-amber-hex: "#FBBF24"
  accent-blue: "#4285F4"
  accent-blue-soft: "rgba(66, 133, 244, 0.15)"
  accent-blue-text: "rgb(147, 197, 253)"
  accent-red: "#EF4444"
  accent-red-soft: "rgba(239, 68, 68, 0.15)"
  accent-red-text: "rgb(252, 165, 165)"
  accent-red-light: "rgb(248, 113, 113)"
  code-purple: "#A78BFA"
  dark-contrast: "rgb(15, 23, 42)"
  dark-contrast-green: "#064E3B"
  dark-contrast-amber: "#78350F"
  slate-muted: "#475569"
  scrollbar-hover: "#373B44"
rounded:
  xs: "4px"
  sm: "6px"
  md: "10px"
  lg: "14px"
  pill-sm: "16px"
  badge: "20px"
  pill: "24px"
  search: "30px"
spacing:
  xs: "4px"
  sm: "8px"
  md: "16px"
  lg: "24px"
  xl: "32px"
  xxl: "48px"
---

# Design System

## Overview

The RePropertyX visual identity is purposeful, disciplined, and rooted in the Kotlin ecosystem. It trades generic AI aesthetics (purple radial halos, gradient text, floating icon tiles) for high-contrast typography, authentic IDE charcoal surfaces, and the signature Kotlin warm amber accent.

## Colors

- **Neutral Canvas**: `#121316` (Deep Obsidian Slate) — grounds the reader without muddy or artificial purple gradient washes.
- **Surfaces**: `#1A1C20` with `rgba(255, 255, 255, 0.08)` borders for clean, subtle separation.
- **Brand Accent**: `#FF9800` (Warm Kotlin Amber) — applied deliberately to focal points, active states, and code emphasis.
- **Foreground**: `#F8FAFC` (pure readability) with `#94A3B8` secondary text maintaining WCAG AAA contrast ratios.

## Typography

- **Headings & Body**: `Outfit` — modern geometric sans-serif with natural balance, clarity at small sizes, and distinct personality.
- **Code & Primitives**: `JetBrains Mono` — the standard for Kotlin development with clear character distinctions and ligatures.
- **Rule**: Solid color text only. Weight and scale convey hierarchy, never text gradients.

## Layout

- Flat visual hierarchy without nested cards.
- Generous vertical rhythm (`gap: 32px` to `64px`).
- Side-by-side or flow layout for icons and titles rather than isolated tile boxes.

## Elevation & Depth

- Directional elevation with soft blur: `box-shadow: 0 4px 20px -2px rgba(0, 0, 0, 0.45)`.
- No zero-offset chromatic glows, neon halos, or faux drop-shadow borders.

## Shapes

- Consistent border radius: `6px` for tags and inputs, `10px` for buttons, `14px` for primary panels, `20px`-`30px` for pills and search inputs.

## Components

- **Code Cards**: Monolithic high-contrast code panels with clean titlebars and language badges.
- **Interactive Tabs**: Pill or underline-based state selectors with crisp active indicators.
- **Search Bar**: Fully accessible input with dedicated focus ring styling.

## Do's and Don'ts

- **DO** use solid high-contrast text and authentic Kotlin amber accents.
- **DO** place icons in flow or alongside headers.
- **DON'T** use purple-to-blue background gradients or glowing text.
- **DON'T** nest cards within cards.
