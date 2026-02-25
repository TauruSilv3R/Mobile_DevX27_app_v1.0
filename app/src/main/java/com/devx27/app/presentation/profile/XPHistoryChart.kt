package com.devx27.app.presentation.profile

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devx27.app.domain.model.XPHistoryEntry
import com.devx27.app.presentation.theme.DevX27Theme

// ─────────────────────────────────────────────────────────────────────────────
// XPHistoryChart — custom Canvas line chart for 7-day XP history.
//
// Rendering pipeline:
//   1. Grid lines (subtle horizontal rules)
//   2. Gradient fill (below the line, green fade-to-transparent)
//   3. The line itself (spring-animated draw progress on enter)
//   4. Data point circles (filled white dot, green ring)
//   5. Day labels on X-axis
//   6. XP peak value label on the highest point
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun XPHistoryChart(
    entries: List<XPHistoryEntry>,
    modifier: Modifier = Modifier,
) {
    if (entries.isEmpty()) return

    val textMeasurer = rememberTextMeasurer()
    val drawProgress = remember { Animatable(0f) }

    // Spring-animate the chart drawing in on first composition
    LaunchedEffect(entries) {
        drawProgress.animateTo(
            targetValue   = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness    = Spring.StiffnessVeryLow,
            ),
        )
    }

    val lineColor   = DevX27Theme.colors.xpSuccess
    val glowColor   = DevX27Theme.colors.xpSuccessGlow
    val gridColor   = DevX27Theme.colors.divider
    val labelColor  = DevX27Theme.colors.onSurfaceSubtle
    val dotBg       = DevX27Theme.colors.background

    Canvas(modifier = modifier) {
        val xpValues  = entries.map { it.xpGained }
        val maxXp     = xpValues.max().coerceAtLeast(1).toFloat()
        val minXp     = 0f

        val paddingLeft   = 12.dp.toPx()
        val paddingRight  = 12.dp.toPx()
        val paddingTop    = 16.dp.toPx()
        val paddingBottom = 28.dp.toPx()   // space for day labels

        val chartWidth  = size.width  - paddingLeft - paddingRight
        val chartHeight = size.height - paddingTop  - paddingBottom

        val stepX = if (entries.size > 1) chartWidth / (entries.size - 1).toFloat() else chartWidth

        // Compute data point positions
        val points = entries.mapIndexed { i, e ->
            val xFraction = if (entries.size > 1) i / (entries.size - 1).toFloat() else 0.5f
            val yFraction = 1f - ((e.xpGained - minXp) / (maxXp - minXp))
            Offset(
                x = paddingLeft + xFraction * chartWidth,
                y = paddingTop  + yFraction * chartHeight,
            )
        }

        // ── 1. Grid lines ────────────────────────────────────────────────────
        val gridSteps = 3
        repeat(gridSteps + 1) { i ->
            val y = paddingTop + (i.toFloat() / gridSteps) * chartHeight
            drawLine(
                color       = gridColor.copy(alpha = 0.3f),
                start       = Offset(paddingLeft, y),
                end         = Offset(size.width - paddingRight, y),
                strokeWidth = 0.8f,
            )
        }

        // ── 2. Gradient fill under the line (clipped to drawProgress) ────────
        val fillPath = Path().apply {
            moveTo(points.first().x, size.height - paddingBottom)
            points.forEach { p -> lineTo(p.x, p.y) }
            lineTo(points.last().x, size.height - paddingBottom)
            close()
        }
        drawPath(
            path  = fillPath,
            brush = androidx.compose.ui.graphics.SolidColor(glowColor.copy(alpha = 0.15f)),
            alpha = drawProgress.value,
        )

        // ── 3. Line (drawn to drawProgress fraction of total length) ─────────
        if (points.size > 1) {
            val linePath = Path()
            val visibleCount = (points.size * drawProgress.value).toInt().coerceAtLeast(1)
            linePath.moveTo(points.first().x, points.first().y)
            for (i in 1 until visibleCount) {
                // Smooth bezier curve through points
                val prev = points[i - 1]
                val curr = points[i]
                val controlX = (prev.x + curr.x) / 2f
                linePath.cubicTo(controlX, prev.y, controlX, curr.y, curr.x, curr.y)
            }
            drawPath(
                path         = linePath,
                color        = lineColor,
                style        = Stroke(
                    width    = 2.5.dp.toPx(),
                    cap      = StrokeCap.Round,
                    join     = StrokeJoin.Round,
                ),
            )
        }

        // ── 4. Data point circles ─────────────────────────────────────────────
        val visibleCount = (points.size * drawProgress.value).toInt()
        points.take(visibleCount).forEachIndexed { i, p ->
            // Glow ring
            drawCircle(color = glowColor.copy(alpha = 0.3f), radius = 10.dp.toPx(), center = p)
            // White/dark fill
            drawCircle(color = dotBg,       radius = 5.dp.toPx(),  center = p)
            // Green outline
            drawCircle(
                color       = lineColor,
                radius      = 5.dp.toPx(),
                center      = p,
                style       = Stroke(width = 2.dp.toPx()),
            )
        }

        // ── 5. Day labels on X-axis ───────────────────────────────────────────
        entries.forEachIndexed { i, entry ->
            val point   = points[i]
            val measured = textMeasurer.measure(
                entry.dayLabel,
                TextStyle(fontSize = 10.sp, color = labelColor, fontFamily = FontFamily.Monospace),
            )
            drawText(
                textLayoutResult = measured,
                topLeft = Offset(
                    point.x - measured.size.width / 2f,
                    size.height - paddingBottom + 6.dp.toPx(),
                ),
            )
        }

        // ── 6. Peak XP label ─────────────────────────────────────────────────
        val maxIndex = xpValues.indexOf(xpValues.max())
        val maxPoint = points[maxIndex]
        val peakLabel = textMeasurer.measure(
            "+${xpValues[maxIndex]} XP",
            TextStyle(fontSize = 10.sp, color = lineColor, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace),
        )
        if (drawProgress.value > maxIndex.toFloat() / points.size) {
            drawText(
                textLayoutResult = peakLabel,
                topLeft = Offset(
                    (maxPoint.x - peakLabel.size.width / 2f).coerceIn(paddingLeft, size.width - paddingRight - peakLabel.size.width),
                    maxPoint.y - peakLabel.size.height - 6.dp.toPx(),
                ),
            )
        }
    }
}
