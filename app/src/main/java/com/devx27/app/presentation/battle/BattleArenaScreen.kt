package com.devx27.app.presentation.battle

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.devx27.app.domain.model.BattleState
import com.devx27.app.presentation.components.DevX27LoadingSpinner
import com.devx27.app.presentation.editor.CodeTextField
import com.devx27.app.presentation.editor.KeyboardAccessoryBar
import com.devx27.app.presentation.editor.LineNumberGutter
import com.devx27.app.presentation.editor.SyntaxHighlighter
import com.devx27.app.presentation.editor.SyntaxTheme
import com.devx27.app.presentation.theme.DevX27Theme

// ─────────────────────────────────────────────────────────────────────────────
// BattleArenaScreen — the live coding duel view.
//
// Layout:
//   ┌─────────────────────────────────────┐
//   │  ⏱ 04:23   Two Sum    [You 65%][Opp 40%]  │ ← Battle header
//   ├──────────────────────────────────────┤
//   │  1 │ def solution(nums):             │
//   │  2 │     …                           │ ← CodeEditor (reused)
//   ├──────────────────────────────────────┤
//   │                           [Submit ↑] │ ← FAB
//   └──────────────────────────────────────┘
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun BattleArenaScreen(
    navController: NavController,
    viewModel: BattleViewModel = hiltViewModel(),
) {
    val uiState   by viewModel.uiState.collectAsState()
    val vertScroll   = rememberScrollState()
    val syntaxColors = SyntaxTheme.colors()

    val battleActive = uiState.battleState as? BattleState.BattleActive
    val battleEnded  = uiState.battleState as? BattleState.BattleEnded

    Scaffold(
        containerColor = syntaxColors.background,
        topBar = {
            BattleTopBar(
                title             = battleActive?.challengeTitle ?: "Battle",
                timeRemaining     = uiState.timeRemainingSeconds,
                myProgress        = uiState.myProgress,
                opponentProgress  = uiState.opponentProgress,
                opponentName      = battleActive?.opponent?.displayName?.split(" ")?.first() ?: "Opp",
            )
        },
        floatingActionButton = {
            if (battleActive != null) {
                FloatingActionButton(
                    onClick        = viewModel::onSubmit,
                    containerColor = DevX27Theme.colors.xpSuccess,
                    contentColor   = Color.Black,
                    elevation      = FloatingActionButtonDefaults.elevation(0.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier          = Modifier.padding(horizontal = 16.dp),
                    ) {
                        Icon(Icons.Default.Upload, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Submit", fontWeight = FontWeight.Black, fontSize = 15.sp)
                    }
                }
            }
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(syntaxColors.background)
        ) {
            // ── Editor area ─────────────────────────────────────────────────
            if (battleActive != null) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 8.dp),
                ) {
                    LineNumberGutter(
                        lineCount = (uiState.code.text.count { it == '\n' } + 1),
                        modifier  = Modifier.fillMaxSize(),
                    )
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .fillMaxSize()
                            .background(syntaxColors.lineNumber.copy(alpha = 0.3f))
                    )
                    CodeTextField(
                        value              = uiState.code,
                        onValueChange      = viewModel::onCodeChanged,
                        language           = uiState.language,
                        verticalScrollState = vertScroll,
                        modifier           = Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .padding(start = 12.dp)
                            .verticalScrollFix(vertScroll),
                    )
                }
            }

            // ── Submit spinner ───────────────────────────────────────────────
            if (uiState.isSubmitting) {
                DevX27LoadingSpinner(modifier = Modifier.fillMaxSize())
            }

            // ── Battle Ended overlay ─────────────────────────────────────────
            if (battleEnded != null) {
                BattleEndedOverlay(
                    won       = battleEnded.won,
                    xpAwarded = battleEnded.xpAwarded,
                    onClose   = { navController.popBackStack() },
                    modifier  = Modifier.fillMaxSize(),
                )
            }
            // ── Keyboard Accessory Bar ────────────────────────────────────────
            KeyboardAccessoryBar(
                value         = uiState.code,
                onValueChange = viewModel::onCodeChanged,
                modifier      = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .imePadding()
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// BattleTopBar — countdown + dual progress bars
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun BattleTopBar(
    title: String,
    timeRemaining: Int,
    myProgress: Float,
    opponentProgress: Float,
    opponentName: String,
) {
    val minutes = timeRemaining / 60
    val seconds = timeRemaining % 60
    val timerColor by animateColorAsState(
        targetValue   = if (timeRemaining <= 30) DevX27Theme.colors.xpError
                        else DevX27Theme.colors.onBackground,
        animationSpec = tween(500),
        label         = "timer_color",
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors   = CardDefaults.cardColors(containerColor = DevX27Theme.colors.surface),
        shape    = RoundedCornerShape(0.dp),
    ) {
        Column(modifier = Modifier.statusBarsPadding().padding(horizontal = 16.dp, vertical = 12.dp)) {
            // ── Timer + Title ────────────────────────────────────────────────
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically,
            ) {
                Text(
                    text       = "%02d:%02d".format(minutes, seconds),
                    fontSize   = 20.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color      = timerColor,
                )
                Text(
                    text       = title,
                    fontSize   = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = DevX27Theme.colors.onBackground,
                )
                Icon(
                    Icons.Default.Bolt,
                    null,
                    tint     = DevX27Theme.colors.xpSuccess,
                    modifier = Modifier.size(20.dp),
                )
            }

            Spacer(Modifier.height(10.dp))

            // ── Progress bars: You vs Opponent ───────────────────────────────
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment     = Alignment.CenterVertically,
            ) {
                // YOU
                Column(modifier = Modifier.weight(1f)) {
                    Text("You", fontSize = 10.sp, color = DevX27Theme.colors.xpSuccess, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(3.dp))
                    ProgressBar(
                        progress = myProgress,
                        color    = DevX27Theme.colors.xpSuccess,
                    )
                }

                Text("VS", fontSize = 11.sp, color = DevX27Theme.colors.onSurfaceSubtle, fontWeight = FontWeight.Bold)

                // Opponent
                Column(modifier = Modifier.weight(1f)) {
                    Text(opponentName, fontSize = 10.sp, color = DevX27Theme.colors.xpWarning, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(3.dp))
                    ProgressBar(
                        progress = opponentProgress,
                        color    = DevX27Theme.colors.xpWarning,
                    )
                }
            }
        }
    }
}

@Composable
private fun ProgressBar(progress: Float, color: Color) {
    val animProgress by animateFloatAsState(
        targetValue   = progress,
        animationSpec = tween(400),
        label         = "battle_progress",
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(DevX27Theme.colors.surfaceInput)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(animProgress)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(color)
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// BattleEndedOverlay — win/lose result displayed over the editor
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun BattleEndedOverlay(
    won: Boolean,
    xpAwarded: Int,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier         = modifier.background(DevX27Theme.colors.background.copy(alpha = 0.92f)),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier            = Modifier.padding(40.dp),
        ) {
            Text(if (won) "🏆" else "💀", fontSize = 72.sp)
            Text(
                text       = if (won) "You Won!" else "Defeated",
                fontSize   = 32.sp,
                fontWeight = FontWeight.Black,
                color      = if (won) DevX27Theme.colors.xpSuccess else DevX27Theme.colors.xpError,
            )
            if (won && xpAwarded > 0) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Icon(Icons.Default.Bolt, null, tint = DevX27Theme.colors.xpSuccess, modifier = Modifier.size(20.dp))
                    Text("+$xpAwarded XP", fontSize = 22.sp, fontWeight = FontWeight.Black, color = DevX27Theme.colors.xpSuccess)
                }
            }
            Spacer(Modifier.height(8.dp))
            androidx.compose.material3.Button(
                onClick = onClose,
                colors  = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = if (won) DevX27Theme.colors.xpSuccess else DevX27Theme.colors.xpError,
                    contentColor   = Color.Black,
                ),
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape    = RoundedCornerShape(14.dp),
            ) {
                Text(if (won) "Claim Victory" else "Try Again", fontWeight = FontWeight.Black, fontSize = 16.sp)
            }
        }
    }
}

// ScrollFix modifier — allows BasicTextField to follow the scroll state
private fun Modifier.verticalScrollFix(scrollState: androidx.compose.foundation.ScrollState): Modifier =
    this.then(
        Modifier.padding(bottom = 120.dp)  // Ensure FAB doesn't overlap bottom line
    )
