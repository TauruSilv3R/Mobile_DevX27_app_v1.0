package com.devx27.app.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devx27.app.domain.repository.SubmissionResult
import com.devx27.app.presentation.theme.DevX27Theme

// ─────────────────────────────────────────────────────────────────────────────
// SubmissionResultOverlay — bottom sheet card shown after code submission.
//
// On success:
//   • Triggers HapticFeedbackType.LongPress for physical confirmation
//   • Plays XP bar fill animation with spring physics + neon green glow
//   • Shows XP count-up (visual, no additional animation lib needed)
//
// On failure:
//   • Shows error card with feedback text
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun SubmissionResultOverlay(
    result: SubmissionResult,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val haptic      = LocalHapticFeedback.current
    val scaleAnim   = remember { Animatable(0.8f) }
    val xpBarFill   = remember { Animatable(0f) }
    val glowAlpha   = remember { Animatable(0f) }

    LaunchedEffect(result) {
        if (result.passed) {
            // ① Haptic — physical confirmation of success
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)

            // ② Spring pop-in for the card
            scaleAnim.animateTo(
                targetValue   = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness    = Spring.StiffnessMedium,
                ),
            )

            // ③ XP bar fills with spring physics (overshoots, then settles)
            xpBarFill.animateTo(
                targetValue   = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,   // subtle overshoot
                    stiffness    = Spring.StiffnessLow,
                ),
            )

            // ④ Neon glow pulse
            glowAlpha.animateTo(0.6f, animationSpec = tween(300))
            glowAlpha.animateTo(0f,   animationSpec = tween(600))
        } else {
            scaleAnim.animateTo(1f, animationSpec = spring(Spring.DampingRatioMediumBouncy))
        }
    }

    Box(
        modifier = modifier.scale(scaleAnim.value),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(DevX27Theme.colors.surfaceElevated)
                .padding(20.dp),
        ) {
            if (result.passed) {
                SuccessContent(result, xpBarFill.value, glowAlpha.value)
            } else {
                FailureContent(result)
            }

            Spacer(Modifier.height(8.dp))
            TextButton(
                onClick  = onDismiss,
                modifier = Modifier.align(Alignment.End),
            ) {
                Text("Continue", color = DevX27Theme.colors.xpSuccess, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun SuccessContent(
    result: SubmissionResult,
    barFill: Float,
    glowAlpha: Float,
) {
    Row(
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(
            imageVector        = Icons.Default.CheckCircle,
            contentDescription = null,
            tint               = DevX27Theme.colors.xpSuccess,
            modifier           = Modifier.size(28.dp),
        )
        Column {
            Text(
                text       = "All Tests Passed!",
                fontWeight = FontWeight.Black,
                fontSize   = 18.sp,
                color      = DevX27Theme.colors.onBackground,
            )
            Text(
                text     = "Solved in ${result.executionMs}ms",
                fontSize = 13.sp,
                color    = DevX27Theme.colors.onSurfaceMuted,
            )
        }
    }

    Spacer(Modifier.height(16.dp))

    // XP gain label
    Row(
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Icon(Icons.Default.Bolt, null, tint = DevX27Theme.colors.xpSuccess, modifier = Modifier.size(18.dp))
        Text(
            text       = "+${result.xpAwarded} XP",
            fontWeight = FontWeight.Black,
            fontSize   = 22.sp,
            color      = DevX27Theme.colors.xpSuccess,
        )
    }

    Spacer(Modifier.height(10.dp))

    // ── XP Progress bar (spring-physics fill + neon glow) ─────────────────
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(12.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(DevX27Theme.colors.surfaceInput)
    ) {
        // Glow layer (behind the bar, pulsing alpha)
        Box(
            modifier = Modifier
                .fillMaxWidth(barFill.coerceIn(0f, 1f))
                .height(12.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(DevX27Theme.colors.xpSuccessGlow.copy(alpha = glowAlpha))
        )
        // Filled bar
        Box(
            modifier = Modifier
                .fillMaxWidth(barFill.coerceIn(0f, 1f))
                .height(12.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(DevX27Theme.colors.xpSuccess)
        )
    }

    Spacer(Modifier.height(6.dp))
    Text(
        text     = "Leveling up…",
        fontSize = 12.sp,
        color    = DevX27Theme.colors.onSurfaceSubtle,
    )
}

@Composable
private fun FailureContent(result: SubmissionResult) {
    Row(
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(
            imageVector        = Icons.Default.Error,
            contentDescription = null,
            tint               = DevX27Theme.colors.xpError,
            modifier           = Modifier.size(28.dp),
        )
        Column {
            Text(
                text       = "Not Quite Right",
                fontWeight = FontWeight.Black,
                fontSize   = 18.sp,
                color      = DevX27Theme.colors.onBackground,
            )
            Text(
                text     = result.feedback,
                fontSize = 13.sp,
                color    = DevX27Theme.colors.onSurfaceMuted,
            )
        }
    }
}
