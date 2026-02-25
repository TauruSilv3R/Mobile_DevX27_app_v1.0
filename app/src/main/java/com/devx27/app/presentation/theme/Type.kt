package com.devx27.app.presentation.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle

// ─────────────────────────────────────────────────────────────────────────────
// DevX27 Typography System
//
// UI text    → System sans-serif (clean, readable at all sizes)
// Code text  → Monospace (JetBrains Mono equivalent via system font)
// ─────────────────────────────────────────────────────────────────────────────

// System monospace family — no custom font file needed;
// falls back to the device's best monospace (Droid Mono / Roboto Mono)
val MonoFontFamily = FontFamily.Monospace

// Derive a "code" text style to use in the editor and challenge descriptions
val CodeTextStyle = TextStyle(
    fontFamily = MonoFontFamily,
    fontSize    = 14.sp,
    lineHeight  = 22.sp,
    letterSpacing = 0.sp,
    color       = White,
    fontWeight  = FontWeight.Normal,
)

val CodeSmallStyle = TextStyle(
    fontFamily = MonoFontFamily,
    fontSize    = 12.sp,
    lineHeight  = 18.sp,
    letterSpacing = 0.sp,
    color       = Grey50,
)

// ─────────────────────────────────────────────────────────────────────────────
// Material 3 Typography overrides for DevX27
// ─────────────────────────────────────────────────────────────────────────────
val DevX27Typography = Typography(
    // Large headers — used for screen titles, XP display
    displayLarge  = TextStyle(fontSize = 57.sp, lineHeight = 64.sp, fontWeight = FontWeight.Black,  letterSpacing = (-0.25).sp, color = White),
    displayMedium = TextStyle(fontSize = 45.sp, lineHeight = 52.sp, fontWeight = FontWeight.Black,  letterSpacing = 0.sp,        color = White),
    displaySmall  = TextStyle(fontSize = 36.sp, lineHeight = 44.sp, fontWeight = FontWeight.Bold,   letterSpacing = 0.sp,        color = White),

    // Headlines — card titles, section titles
    headlineLarge  = TextStyle(fontSize = 32.sp, lineHeight = 40.sp, fontWeight = FontWeight.Bold,   letterSpacing = 0.sp, color = White),
    headlineMedium = TextStyle(fontSize = 28.sp, lineHeight = 36.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.sp, color = White),
    headlineSmall  = TextStyle(fontSize = 24.sp, lineHeight = 32.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.sp, color = White),

    // Titles — list items, challenge cards
    titleLarge  = TextStyle(fontSize = 22.sp, lineHeight = 28.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.sp,   color = White),
    titleMedium = TextStyle(fontSize = 16.sp, lineHeight = 24.sp, fontWeight = FontWeight.Medium,   letterSpacing = 0.15.sp, color = White),
    titleSmall  = TextStyle(fontSize = 14.sp, lineHeight = 20.sp, fontWeight = FontWeight.Medium,   letterSpacing = 0.1.sp,  color = Grey50),

    // Body — descriptions, general text
    bodyLarge   = TextStyle(fontSize = 16.sp, lineHeight = 24.sp, fontWeight = FontWeight.Normal,   letterSpacing = 0.5.sp,  color = White),
    bodyMedium  = TextStyle(fontSize = 14.sp, lineHeight = 20.sp, fontWeight = FontWeight.Normal,   letterSpacing = 0.25.sp, color = Grey50),
    bodySmall   = TextStyle(fontSize = 12.sp, lineHeight = 16.sp, fontWeight = FontWeight.Normal,   letterSpacing = 0.4.sp,  color = Grey40),

    // Labels — tags, badges, buttons
    labelLarge  = TextStyle(fontSize = 14.sp, lineHeight = 20.sp, fontWeight = FontWeight.Bold,     letterSpacing = 0.1.sp,  color = White),
    labelMedium = TextStyle(fontSize = 12.sp, lineHeight = 16.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.5.sp,  color = Grey50),
    labelSmall  = TextStyle(fontSize = 11.sp, lineHeight = 16.sp, fontWeight = FontWeight.Medium,   letterSpacing = 0.5.sp,  color = Grey40),
)
