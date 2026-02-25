package com.devx27.app.presentation.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ─────────────────────────────────────────────────────────────────────────────
// DevX27Colors — typed colour slots
// All colour decisions live in Color.kt.  This class just names the slots.
// ─────────────────────────────────────────────────────────────────────────────
@Immutable
data class DevX27Colors(
    // ── 60% zone (dominant) ──────────────────────────────────────────────────
    val background: Color,      // page / screen bg
    val surface: Color,         // card bg
    val surfaceElevated: Color, // elevated card
    val surfaceInput: Color,    // text-field bg
    val bottomBar: Color,       // bottom nav bg

    // ── 20% zone (text / accents) ────────────────────────────────────────────
    val onBackground: Color,    // primary text on bg
    val onSurface: Color,       // primary text on card
    val onSurfaceMuted: Color,  // secondary text
    val onSurfaceSubtle: Color, // tertiary text / captions
    val divider: Color,         // borders & separators
    val actionColor: Color,     // buttons, active indicators (= 20% opposite)

    // ── Semantic colours (kept semantic, never vivid) ─────────────────────────
    val xpSuccess: Color,
    val xpSuccessGlow: Color,
    val xpSuccessBg: Color,
    val xpError: Color         = StatusRed,
    val xpWarning: Color       = StatusAmber,
    val xpGold: Color          = StatusGold,

    // ── Difficulty colours ────────────────────────────────────────────────────
    val diffEasy: Color        = DifficultyEasy,
    val diffMedium: Color      = DifficultyMedium,
    val diffHard: Color        = DifficultyHard,
    val diffElite: Color       = DifficultyElite,
    val difficultyEasy: Color  = DifficultyEasy,
    val difficultyMedium: Color = DifficultyMedium,
    val difficultyHard: Color  = DifficultyHard,
    val difficultyElite: Color = DifficultyElite,

    // ── Legacy compat ─────────────────────────────────────────────────────────
    val primary: Color         = actionColor,
    val onPrimary: Color       = background,
    val accent: Color          = actionColor,
    val ripple: Color,
    val isDark: Boolean
)

// ── LIGHT THEME — 60% White / 20% Grey / 20% Black ───────────────────────────
private val LightDevX27Colors = DevX27Colors(
    // 60% White
    background      = LightBackground,
    surface         = LightSurface,
    surfaceElevated = LightSurfaceElevated,
    surfaceInput    = LightSurfaceInput,
    bottomBar       = LightBottomBar,
    // 20% Black — text & action
    onBackground    = LightTextPrimary,
    onSurface       = LightTextPrimary,
    actionColor     = LightActionColor,
    // 20% Grey — secondary & borders
    onSurfaceMuted  = LightTextSecondary,
    onSurfaceSubtle = LightTextTertiary,
    divider         = LightDivider,
    xpSuccess       = LightActionColor,
    xpSuccessGlow   = LightActionColor,
    xpSuccessBg     = BlackAlpha10,
    // mechanics
    ripple          = BlackAlpha10,
    isDark          = false
)

// ── DARK THEME — 60% Black / 20% Grey / 20% White ────────────────────────────
private val DarkDevX27Colors = DevX27Colors(
    // 60% Black
    background      = Black,
    surface         = Surface12,
    surfaceElevated = Surface1C,
    surfaceInput    = Surface26,
    bottomBar       = Surface08,
    // 20% White — text & action
    onBackground    = White,
    onSurface       = White,
    actionColor     = White,
    // 20% Grey — secondary & borders
    onSurfaceMuted  = Grey50,
    onSurfaceSubtle = Grey40,
    divider         = WhiteAlpha10,
    xpSuccess       = White,
    xpSuccessGlow   = White,
    xpSuccessBg     = WhiteAlpha10,
    // mechanics
    ripple          = WhiteAlpha5,
    isDark          = true
)

// ── Material3 Colour Schemes ──────────────────────────────────────────────────
private val LightColorScheme = lightColorScheme(
    primary          = BrandBlack,
    onPrimary        = BrandWhite,
    background       = LightBackground,
    onBackground     = LightTextPrimary,
    surface          = LightSurface,
    onSurface        = LightTextPrimary,
    surfaceVariant   = LightSurfaceElevated,
    onSurfaceVariant = LightTextSecondary,
    error            = StatusRed,
    outline          = LightDivider,
    secondaryContainer = LightSurfaceInput,
    onSecondaryContainer = LightTextSecondary,
)

private val DarkColorScheme = darkColorScheme(
    primary          = White,
    onPrimary        = Black,
    background       = Black,
    onBackground     = White,
    surface          = Surface12,
    onSurface        = White,
    surfaceVariant   = Surface1C,
    onSurfaceVariant = Grey50,
    error            = StatusRed,
    outline          = Surface26,
)

val LocalDevX27Colors = staticCompositionLocalOf { LightDevX27Colors }

// ─────────────────────────────────────────────────────────────────────────────
// DevX27Theme
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun DevX27Theme(
    darkTheme: Boolean = false,   // Default = Light
    content: @Composable () -> Unit,
) {
    val customColors = if (darkTheme) DarkDevX27Colors else LightDevX27Colors
    val colorScheme  = if (darkTheme) DarkColorScheme  else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            @Suppress("DEPRECATION")
            window.statusBarColor = colorScheme.background.toArgb()
            @Suppress("DEPRECATION")
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars     = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    CompositionLocalProvider(LocalDevX27Colors provides customColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography  = DevX27Typography,
            shapes      = DevX27Shapes,
            content     = content,
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Accessor
// ─────────────────────────────────────────────────────────────────────────────
object DevX27Theme {
    val colors: DevX27Colors
        @Composable @ReadOnlyComposable
        get() = LocalDevX27Colors.current
}

// Surface elevation helper
fun Color.elevate(amount: Float): Color {
    val factor = if (amount > 0) 0.05f * amount else 0f
    return Color(
        red   = (red   + factor).coerceAtMost(1f),
        green = (green + factor).coerceAtMost(1f),
        blue  = (blue  + factor).coerceAtMost(1f),
        alpha = alpha
    )
}
