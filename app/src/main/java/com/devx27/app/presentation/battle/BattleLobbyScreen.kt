package com.devx27.app.presentation.battle

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.devx27.app.domain.model.BattleOpponent
import com.devx27.app.domain.model.BattleState
import com.devx27.app.presentation.navigation.Screen
import com.devx27.app.presentation.theme.DevX27Theme
import kotlinx.coroutines.delay

@Composable
fun BattleLobbyScreen(
    navController: NavController,
    viewModel: BattleViewModel = hiltViewModel(),
) {
    val uiState   by viewModel.uiState.collectAsState()
    val haptic    = LocalHapticFeedback.current

    // ── Double-pulse haptic on match found ────────────────────────────────────
    LaunchedEffect(uiState.matchFound) {
        if (uiState.matchFound) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            delay(150)
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            viewModel.onMatchFoundAcknowledged()
        }
    }

    // ── Navigate to arena when battle becomes active ──────────────────────────
    LaunchedEffect(uiState.battleState) {
        if (uiState.battleState is BattleState.BattleActive) {
            navController.navigate(Screen.BattleArena.route) {
                popUpTo(Screen.BattleLobby.route) { inclusive = false }
            }
        }
    }

    Box(
        modifier         = Modifier
            .fillMaxSize()
            .background(DevX27Theme.colors.background)
            .statusBarsPadding(),
        contentAlignment = Alignment.Center,
    ) {
        AnimatedContent(
            targetState    = uiState.battleState,
            transitionSpec = { fadeIn(tween(400)) togetherWith fadeOut(tween(400)) },
            label          = "lobby_state",
        ) { state ->
            when (state) {
                is BattleState.Idle       -> IdleContent(onFind = viewModel::startSearch)
                is BattleState.Searching  -> SearchingContent(onCancel = viewModel::cancelSearch)
                is BattleState.MatchFound -> MatchFoundContent(opponent = state.opponent)
                else                      -> Box(Modifier.fillMaxSize())
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// State 1 — Idle: "Find Match" CTA
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun IdleContent(onFind: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier            = Modifier.padding(40.dp),
    ) {
        Text("⚔️", fontSize = 64.sp)
        Text(
            text       = "BattleX27",
            fontSize   = 32.sp,
            fontWeight = FontWeight.Black,
            color      = DevX27Theme.colors.onBackground,
            textAlign  = TextAlign.Center,
        )
        Text(
            text      = "1v1 live coding duels. First to pass all test cases wins.",
            fontSize  = 15.sp,
            color     = DevX27Theme.colors.onSurfaceMuted,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = onFind,
            colors  = ButtonDefaults.buttonColors(
                containerColor = DevX27Theme.colors.xpSuccess,
                contentColor   = Color.Black,
            ),
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape    = RoundedCornerShape(14.dp),
        ) {
            Text("Find Opponent", fontWeight = FontWeight.Black, fontSize = 16.sp)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// State 2 — Searching: infiniteTransition pulse animation
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun SearchingContent(onCancel: () -> Unit) {
    val infinite = rememberInfiniteTransition(label = "search_pulse")

    // Outer pulse ring — scale + alpha
    val outerScale by infinite.animateFloat(
        initialValue  = 0.6f,
        targetValue   = 1.4f,
        animationSpec = infiniteRepeatable(
            animation  = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "outer_scale",
    )
    val outerAlpha by infinite.animateFloat(
        initialValue  = 0.6f,
        targetValue   = 0f,
        animationSpec = infiniteRepeatable(
            animation  = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "outer_alpha",
    )

    // Middle ring — offset phase
    val midScale by infinite.animateFloat(
        initialValue  = 0.6f,
        targetValue   = 1.2f,
        animationSpec = infiniteRepeatable(
            animation  = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "mid_scale",
    )
    val midAlpha by infinite.animateFloat(
        initialValue  = 0.45f,
        targetValue   = 0f,
        animationSpec = infiniteRepeatable(
            animation  = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "mid_alpha",
    )

    // Inner core — subtle breathe
    val innerScale by infinite.animateFloat(
        initialValue  = 0.94f,
        targetValue   = 1.06f,
        animationSpec = infiniteRepeatable(
            animation  = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "inner_scale",
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier            = Modifier.padding(40.dp),
    ) {
        // Pulse stack
        Box(
            modifier         = Modifier.size(120.dp),
            contentAlignment = Alignment.Center,
        ) {
            // Outer fading ring
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .scale(outerScale)
                    .clip(CircleShape)
                    .background(DevX27Theme.colors.xpSuccess.copy(alpha = outerAlpha * 0.3f))
            )
            // Middle ring
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .scale(midScale)
                    .clip(CircleShape)
                    .background(DevX27Theme.colors.xpSuccess.copy(alpha = midAlpha * 0.4f))
            )
            // Inner core (solid)
            Box(
                modifier         = Modifier
                    .size(56.dp)
                    .scale(innerScale)
                    .clip(CircleShape)
                    .background(DevX27Theme.colors.xpSuccessBg)
                    .border(2.dp, DevX27Theme.colors.xpSuccess, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text("⚔️", fontSize = 22.sp)
            }
        }

        Text(
            text       = "Searching for opponent…",
            fontSize   = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color      = DevX27Theme.colors.onBackground,
            textAlign  = TextAlign.Center,
        )
        Text(
            text      = "Matching by skill level and XP",
            fontSize  = 13.sp,
            color     = DevX27Theme.colors.onSurfaceMuted,
            textAlign = TextAlign.Center,
        )

        OutlinedButton(
            onClick  = onCancel,
            shape    = RoundedCornerShape(12.dp),
            colors   = ButtonDefaults.outlinedButtonColors(contentColor = DevX27Theme.colors.onSurfaceMuted),
        ) {
            Text("Cancel")
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// State 3 — Match Found: User vs Opponent cards
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun MatchFoundContent(opponent: BattleOpponent) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier            = Modifier.padding(32.dp),
    ) {
        Text(
            text       = "Match Found!",
            fontSize   = 28.sp,
            fontWeight = FontWeight.Black,
            color      = DevX27Theme.colors.xpSuccess,
        )

        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            PlayerCard(name = "You", level = "Lv.6", isYou = true)
            Text("VS", fontSize = 22.sp, fontWeight = FontWeight.Black, color = DevX27Theme.colors.onSurfaceMuted)
            PlayerCard(name = opponent.displayName.split(" ").first(), level = "Lv.${opponent.level}", isYou = false)
        }

        Text(
            text      = "Get ready…",
            fontSize  = 15.sp,
            color     = DevX27Theme.colors.onSurfaceMuted,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun PlayerCard(name: String, level: String, isYou: Boolean) {
    val accent = if (isYou) DevX27Theme.colors.xpSuccess else DevX27Theme.colors.onSurfaceMuted
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier         = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(accent.copy(alpha = 0.12f))
                .border(2.dp, accent, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(name.take(2).uppercase(), fontWeight = FontWeight.Black, fontSize = 20.sp, color = accent)
        }
        Text(name,  fontSize = 14.sp, fontWeight = FontWeight.Bold,  color = DevX27Theme.colors.onBackground)
        Text(level, fontSize = 12.sp, color = DevX27Theme.colors.onSurfaceSubtle)
    }
}
