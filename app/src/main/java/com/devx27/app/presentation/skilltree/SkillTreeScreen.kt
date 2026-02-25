package com.devx27.app.presentation.skilltree

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.devx27.app.domain.model.SkillEdge
import com.devx27.app.domain.model.SkillGraph
import com.devx27.app.domain.model.SkillNode
import com.devx27.app.presentation.theme.DevX27Theme

// Pixel radius of each node circle
private const val NODE_RADIUS_DP = 36f

@Composable
fun SkillTreeScreen(
    navController: NavController,
    viewModel: SkillTreeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val density = LocalDensity.current

    // ── Pan & zoom state ──────────────────────────────────────────────────────
    var zoom      by remember { mutableFloatStateOf(0.85f) }
    var panOffset by remember { mutableStateOf(Offset(-180f, -20f)) }

    val textMeasurer = rememberTextMeasurer()

    // Colours captured from theme for use inside Canvas lambda
    val colorUnlocked  = DevX27Theme.colors.xpSuccess
    val colorLocked    = DevX27Theme.colors.surfaceInput
    val colorEdgeLock  = DevX27Theme.colors.divider
    val colorEdgeUnlock= DevX27Theme.colors.xpSuccess
    val colorBg        = DevX27Theme.colors.background
    val colorText      = DevX27Theme.colors.onBackground
    val colorSubtext   = DevX27Theme.colors.onSurfaceSubtle
    val colorGlow      = DevX27Theme.colors.xpSuccessGlow
    val colorSurface   = DevX27Theme.colors.surface

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DevX27Theme.colors.background)
    ) {
        // ── Header ────────────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Text("Skill Tree", fontSize = 28.sp, fontWeight = FontWeight.Black, color = colorText)
            Text(
                text     = "${uiState.userXp} XP • ${uiState.graph?.unlockedIds?.size ?: 0} / ${uiState.graph?.nodes?.size ?: 0} skills unlocked",
                fontSize = 13.sp,
                color    = colorSubtext,
            )
        }

        // ── Canvas — skill graph ───────────────────────────────────────────────
        uiState.graph?.let { graph ->
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 88.dp)
                    // Pinch-to-zoom + 2-finger pan
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoomChange, _ ->
                            zoom = (zoom * zoomChange).coerceIn(0.3f, 3.5f)
                            panOffset += pan
                        }
                    }
                    // Tap to select a node
                    .pointerInput(graph) {
                        val nodeRadiusPx = with(density) { NODE_RADIUS_DP.dp.toPx() }
                        detectTapGestures { tapOffset ->
                            val worldTap = Offset(
                                (tapOffset.x - panOffset.x) / zoom,
                                (tapOffset.y - panOffset.y) / zoom,
                            )
                            val tapped = graph.nodes.firstOrNull { node ->
                                val nodeCenter = Offset(
                                    with(density) { node.x.dp.toPx() },
                                    with(density) { node.y.dp.toPx() },
                                )
                                (worldTap - nodeCenter).getDistance() <= nodeRadiusPx
                            }
                            viewModel.onNodeSelected(tapped)
                        }
                    }
            ) {
                val nodeRadiusPx = NODE_RADIUS_DP.dp.toPx()

                translate(panOffset.x, panOffset.y) {
                    scale(zoom, Offset.Zero) {
                        // ① Draw edges first (behind nodes)
                        graph.edges.forEach { edge ->
                            drawSkillEdge(
                                edge         = edge,
                                graph        = graph,
                                density      = density,
                                unlockedColor = colorEdgeUnlock,
                                lockedColor  = colorEdgeLock,
                                nodeRadiusPx = nodeRadiusPx,
                            )
                        }

                        // ② Draw nodes on top
                        graph.nodes.forEach { node ->
                            val isUnlocked = node.id in graph.unlockedIds
                            val selected   = viewModel.uiState.value.selectedNode?.id == node.id
                            drawSkillNode(
                                node         = node,
                                isUnlocked   = isUnlocked,
                                isSelected   = selected,
                                density      = density,
                                nodeRadiusPx = nodeRadiusPx,
                                unlockedColor = colorUnlocked,
                                lockedColor  = colorLocked,
                                glowColor    = colorGlow,
                                textMeasurer = textMeasurer,
                                textColor    = colorText,
                            )
                        }
                    }
                }
            }
        }

        // ── Node detail panel (AnimatedVisibility slide-up) ───────────────────
        AnimatedVisibility(
            visible = uiState.selectedNode != null,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit  = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
        ) {
            uiState.selectedNode?.let { node ->
                NodeDetailPanel(
                    node       = node,
                    isUnlocked = node.id in (uiState.graph?.unlockedIds ?: emptySet()),
                    userXp     = uiState.userXp,
                    onDismiss  = { viewModel.onNodeSelected(null) },
                )
            }
        }

        // ── Zoom hint ─────────────────────────────────────────────────────────
        Text(
            text     = "Pinch to zoom  •  Drag to pan",
            fontSize = 11.sp,
            color    = DevX27Theme.colors.onSurfaceSubtle,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp),
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Canvas drawing functions
// ─────────────────────────────────────────────────────────────────────────────

private fun DrawScope.drawSkillEdge(
    edge: SkillEdge,
    graph: SkillGraph,
    density: androidx.compose.ui.unit.Density,
    unlockedColor: Color,
    lockedColor: Color,
    nodeRadiusPx: Float,
) {
    val fromNode = graph.nodes.firstOrNull { it.id == edge.fromId } ?: return
    val toNode   = graph.nodes.firstOrNull { it.id == edge.toId }   ?: return

    val fromCenter = Offset(
        with(density) { fromNode.x.dp.toPx() },
        with(density) { fromNode.y.dp.toPx() },
    )
    val toCenter = Offset(
        with(density) { toNode.x.dp.toPx() },
        with(density) { toNode.y.dp.toPx() },
    )

    // Shorten line to start/end at node circumference
    val direction   = (toCenter - fromCenter).normalize()
    val lineStart   = fromCenter + direction * nodeRadiusPx
    val lineEnd     = toCenter   - direction * nodeRadiusPx

    val bothUnlocked = fromNode.id in graph.unlockedIds && toNode.id in graph.unlockedIds
    val color       = if (bothUnlocked) unlockedColor else lockedColor
    val strokeWidth = if (bothUnlocked) 2.5f else 1.5f

    drawLine(
        color       = color.copy(alpha = if (bothUnlocked) 0.85f else 0.35f),
        start       = lineStart,
        end         = lineEnd,
        strokeWidth = strokeWidth.dp.toPx(),
        cap         = StrokeCap.Round,
    )
}

private fun DrawScope.drawSkillNode(
    node: SkillNode,
    isUnlocked: Boolean,
    isSelected: Boolean,
    density: androidx.compose.ui.unit.Density,
    nodeRadiusPx: Float,
    unlockedColor: Color,
    lockedColor: Color,
    glowColor: Color,
    textMeasurer: TextMeasurer,
    textColor: Color,
) {
    val center = Offset(
        with(density) { node.x.dp.toPx() },
        with(density) { node.y.dp.toPx() },
    )

    // Glow ring (unlocked only)
    if (isUnlocked) {
        drawCircle(
            color  = glowColor.copy(alpha = if (isSelected) 0.35f else 0.15f),
            radius = nodeRadiusPx * 1.5f,
            center = center,
        )
    }

    // Selection ring
    if (isSelected) {
        drawCircle(
            color       = unlockedColor,
            radius      = nodeRadiusPx + 4.dp.toPx(),
            center      = center,
            style       = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx()),
        )
    }

    // Main circle fill
    drawCircle(
        color  = if (isUnlocked) unlockedColor.copy(alpha = 0.15f) else lockedColor,
        radius = nodeRadiusPx,
        center = center,
    )

    // Circle border
    drawCircle(
        color       = if (isUnlocked) unlockedColor else lockedColor.copy(alpha = 0.5f),
        radius      = nodeRadiusPx,
        center      = center,
        style       = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx()),
    )

    // Icon text
    val iconStyle = TextStyle(
        fontSize   = 16.sp,
        textAlign  = TextAlign.Center,
        fontWeight = FontWeight.Bold,
        color      = if (isUnlocked) unlockedColor else textColor.copy(alpha = 0.4f),
    )
    val iconMeasured = textMeasurer.measure(node.icon, iconStyle)
    drawText(
        textLayoutResult = iconMeasured,
        topLeft = center - Offset(
            iconMeasured.size.width / 2f,
            iconMeasured.size.height / 2f + 6.dp.toPx(), // slight upward shift for label
        ),
    )

    // Label below node
    val labelStyle = TextStyle(
        fontSize   = 9.sp,
        textAlign  = TextAlign.Center,
        fontWeight = if (isUnlocked) FontWeight.Bold else FontWeight.Normal,
        color      = if (isUnlocked) textColor else textColor.copy(alpha = 0.4f),
        fontFamily = FontFamily.Default,
    )
    val labelMeasured = textMeasurer.measure(node.label, labelStyle)
    drawText(
        textLayoutResult = labelMeasured,
        topLeft = center + Offset(
            -labelMeasured.size.width / 2f,
            nodeRadiusPx + 4.dp.toPx(),
        ),
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Node Detail Panel — slide-up card shown on tap
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun NodeDetailPanel(
    node: SkillNode,
    isUnlocked: Boolean,
    userXp: Int,
    onDismiss: () -> Unit,
) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        colors    = CardDefaults.cardColors(containerColor = DevX27Theme.colors.surfaceElevated),
        shape     = RoundedCornerShape(20.dp),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                verticalAlignment     = Alignment.CenterVertically,
            ) {
                // Icon bubble
                Box(
                    modifier         = Modifier
                        .size(48.dp)
                        .background(
                            if (isUnlocked) DevX27Theme.colors.xpSuccessBg else DevX27Theme.colors.surfaceInput,
                            RoundedCornerShape(12.dp),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(text = node.icon, fontSize = 22.sp)
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(node.label, fontWeight = FontWeight.Black, fontSize = 18.sp, color = DevX27Theme.colors.onBackground)
                    Text(node.category.displayName, fontSize = 12.sp, color = DevX27Theme.colors.onSurfaceSubtle)
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, null, tint = DevX27Theme.colors.onSurfaceMuted)
                }
            }

            Spacer(Modifier.height(12.dp))
            Text(node.description, fontSize = 14.sp, color = DevX27Theme.colors.onSurfaceMuted)

            Spacer(Modifier.height(12.dp))

            // XP requirement row
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(6.dp)) {
                Icon(
                    imageVector = if (isUnlocked) Icons.Default.LockOpen else Icons.Default.Lock,
                    contentDescription = null,
                    tint     = if (isUnlocked) DevX27Theme.colors.xpSuccess else DevX27Theme.colors.onSurfaceSubtle,
                    modifier = Modifier.size(16.dp),
                )
                if (isUnlocked) {
                    Text("Unlocked ✔", fontSize = 13.sp, color = DevX27Theme.colors.xpSuccess, fontWeight = FontWeight.Bold)
                } else {
                    Text(
                        text     = "Requires ${node.xpThreshold} XP  •  ${(node.xpThreshold - userXp).coerceAtLeast(0)} XP to go",
                        fontSize = 13.sp,
                        color    = DevX27Theme.colors.onSurfaceSubtle,
                    )
                }
            }
        }
    }
}

// Extension: normalize an Offset vector
private fun Offset.normalize(): Offset {
    val len = getDistance()
    return if (len == 0f) Offset.Zero else Offset(x / len, y / len)
}
