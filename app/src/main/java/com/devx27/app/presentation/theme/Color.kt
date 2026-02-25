package com.devx27.app.presentation.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.geometry.Offset

// ─────────────────────────────────────────────────────────────────────────────
// DevX27 — 60 / 20 / 20 Colour System (Logo: Black + Grey + White)
//
//  LIGHT THEME:  60% White  |  20% Grey  |  20% Black
//  DARK  THEME:  60% Black  |  20% Grey  |  20% White
//
// All backgrounds, cards → dominant colour (60%)
// All secondary text, borders, muted elements → grey (20%)
// All primary text, active elements, CTAs → opposite (20%)
// ─────────────────────────────────────────────────────────────────────────────

// ── Pure Brand Anchors ────────────────────────────────────────────────────────
val BrandBlack  = Color(0xFF0A0A0A)   // Near-pure black (logo black)
val BrandGrey   = Color(0xFF888888)   // Logo silver grey
val BrandWhite  = Color(0xFFFFFFFF)   // Pure white

// ── Surface Stack (for layering, all within 60% zone) ────────────────────────
val Black       = Color(0xFF000000)
val Surface08   = Color(0xFF080808)   // darkest card
val Surface12   = Color(0xFF121212)   // card base
val Surface1C   = Color(0xFF1C1C1C)   // elevated card
val Surface26   = Color(0xFF262626)   // input / pressed

// ── Grey Scale (the 20% grey zone) ───────────────────────────────────────────
val White       = Color(0xFFFFFFFF)
val Grey96      = Color(0xFFF5F5F5)   // lightest card in light mode
val Grey92      = Color(0xFFECECEC)   // surface in light mode
val Grey85      = Color(0xFFD9D9D9)   // border in light mode
val Grey70      = Color(0xFFB3B3B3)   // muted in light mode
val Grey50      = Color(0xFF808080)   // mid grey (logo silver)
val Grey40      = Color(0xFF666666)   // secondary text dark
val Grey30      = Color(0xFF4D4D4D)   // tertiary text dark

// ── Status / Semantic (kept tasteful, not vivid) ─────────────────────────────
val StatusGreen  = Color(0xFF1A7A1A)  // Dark green — solve / success
val StatusRed    = Color(0xFFB22222)  // Dark red — error
val StatusAmber  = Color(0xFF9A6B00)  // Dark amber — warning
val StatusGold   = Color(0xFF7A6000)  // Muted gold — leaderboard

// Keep these as aliases for backward-compat with existing code
val XpSuccess       = BrandBlack
val XpSuccessLight  = Grey30
val XpError         = StatusRed
val XpWarning       = StatusAmber
val XpGold          = StatusGold
val XpSuccessAlpha20 = Color(0x15000000)

// Difficulty — kept colour-coded for clarity (universal UX convention)
val DifficultyEasy   = Color(0xFF1A7A1A)  // Dark green
val DifficultyMedium = Color(0xFF9A6B00)  // Dark amber
val DifficultyHard   = Color(0xFFB22222)  // Dark red
val DifficultyElite  = Color(0xFF1A1A1A)  // Black

// ── Transparent ───────────────────────────────────────────────────────────────
val BlackAlpha10  = Color(0x1A000000)
val BlackAlpha20  = Color(0x33000000)
val BlackAlpha60  = Color(0x99000000)
val BlackAlpha80  = Color(0xCC000000)
val WhiteAlpha10  = Color(0x1AFFFFFF)
val WhiteAlpha5   = Color(0x0DFFFFFF)

// Gradients removed to strictly enforce solid color palette

// ── Light Theme Palette (60% White / 20% Grey / 20% Black) ───────────────────
val LightBackground      = Grey96                // 60% — primary bg
val LightSurface         = BrandWhite            // 60% — cards
val LightSurfaceElevated = BrandWhite            // 60% zone — elevated card
val LightSurfaceInput    = Grey92                // 60% zone — input bg
val LightBottomBar       = BrandWhite            // 60% — nav bar
val LightTextPrimary     = BrandBlack            // 20% Black — headlines
val LightTextSecondary   = Grey40                // 20% Grey — body
val LightTextTertiary    = Grey50                // 20% Grey — captions
val LightDivider         = Grey85                // 20% Grey — borders
val LightActionColor     = BrandBlack            // 20% Black — CTAs, active tabs
