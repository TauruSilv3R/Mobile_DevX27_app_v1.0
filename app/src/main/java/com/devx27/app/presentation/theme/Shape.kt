package com.devx27.app.presentation.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// ─────────────────────────────────────────────────────────────────────────────
// DevX27 Shape Scale — sharp-but-modern feel aligned with True Black aesthetic
// ─────────────────────────────────────────────────────────────────────────────
val DevX27Shapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),   // Tags, small chips
    small      = RoundedCornerShape(8.dp),   // Buttons, inputs
    medium     = RoundedCornerShape(12.dp),  // Cards, dialogs
    large      = RoundedCornerShape(16.dp),  // Bottom sheets
    extraLarge = RoundedCornerShape(24.dp),  // Full-screen modals
)
