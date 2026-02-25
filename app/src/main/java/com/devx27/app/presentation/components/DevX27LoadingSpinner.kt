package com.devx27.app.presentation.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import com.devx27.app.presentation.theme.DevX27Theme

// ─────────────────────────────────────────────────────────────────────────────
// DevX27LoadingSpinner — custom rotating arc with XP-green glow
//
// Uses Canvas drawArc instead of CircularProgressIndicator to achieve
// the True Black aesthetic with a neon green trailing arc.
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun DevX27LoadingSpinner(modifier: Modifier = Modifier) {
    val infinite   = rememberInfiniteTransition(label = "spinner")
    val rotation   by infinite.animateFloat(
        initialValue  = 0f,
        targetValue   = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "rotation",
    )
    val arcSweep by infinite.animateFloat(
        initialValue  = 40f,
        targetValue   = 280f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "arc_sweep",
    )

    val green = DevX27Theme.colors.xpSuccess
    val dim   = DevX27Theme.colors.surfaceInput

    Box(
        modifier         = modifier
            .background(DevX27Theme.colors.background.copy(alpha = 0.85f)),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .drawBehind {
                    // Track (dim circle)
                    drawArc(
                        color      = dim,
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter  = false,
                        style      = Stroke(width = 4.dp.toPx()),
                    )
                    // Rotating arc
                    rotate(rotation) {
                        drawArc(
                            color      = green,
                            startAngle = -90f,
                            sweepAngle = arcSweep,
                            useCenter  = false,
                            style      = Stroke(
                                width = 4.dp.toPx(),
                                cap   = androidx.compose.ui.graphics.StrokeCap.Round,
                            ),
                        )
                    }
                }
        )
    }
}
