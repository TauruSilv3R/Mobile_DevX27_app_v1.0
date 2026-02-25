package com.devx27.app.presentation.dashboard

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.IconButton
import com.devx27.app.presentation.navigation.Screen
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.devx27.app.R
import com.devx27.app.presentation.components.ProfileCompletionBanner
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.devx27.app.presentation.theme.*

@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(DevX27Theme.colors.background)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
        ) {
        // ── Header ────────────────────────────────────────────────────────────
        DashboardHeader(
            username  = uiState.userProfile?.displayName ?: "Dev",
            totalXp   = uiState.userProfile?.totalXp ?: 0,
            onHelpClick = { navController.navigate(Screen.Help.route) }
        )

        ProfileCompletionBanner(
            userProfile = uiState.userProfile,
            onClick = { navController.navigate(Screen.Profile.route) }
        )

        Spacer(Modifier.height(12.dp))

        // ── XP Progress Card ─────────────────────────────────────────────────
        XpProgressCard(
            currentXp  = uiState.userProfile?.totalXp ?: 0,
            nextLevelXp = uiState.userProfile?.nextLevelXp ?: 1000,
            level      = uiState.userProfile?.level ?: 1,
        )

        Spacer(Modifier.height(16.dp))

        // ── Stats Row ─────────────────────────────────────────────────────────
        StatsRow(
            streak   = uiState.weeklyStreak,
            solved   = uiState.userProfile?.challengesSolved ?: 0,
            rank     = uiState.userProfile?.globalRank ?: 0,
        )

        Spacer(Modifier.height(24.dp))

        // ── Daily Missions ───────────────────────────────────────────────────
        DailyMissionsSection()

        Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun DashboardHeader(username: String, totalXp: Int, onHelpClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.mipmap.devx27),
                contentDescription = "Logo",
                modifier = Modifier.size(50.dp).padding(end = 12.dp).clip(CircleShape)
            )
            Column {
                Text(
                    text       = "Welcome back,",
                    fontSize   = 14.sp,
                    color      = DevX27Theme.colors.onSurfaceMuted,
                    fontWeight = FontWeight.Normal,
                )
                Text(
                    text       = username,
                    fontSize   = 26.sp,
                    fontWeight = FontWeight.Black,
                    color      = DevX27Theme.colors.onBackground,
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onHelpClick) {
                Icon(
                    imageVector = Icons.Default.HelpOutline,
                    contentDescription = "Help",
                    tint = DevX27Theme.colors.onSurfaceMuted,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(Modifier.width(8.dp))

            // XP badge with Gradient
            Box(
                modifier          = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(DevX27Theme.colors.actionColor)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                contentAlignment  = Alignment.Center,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Icon(
                        imageVector        = Icons.Default.Bolt,
                        contentDescription = null,
                        tint               = DevX27Theme.colors.background,
                        modifier           = Modifier.size(16.dp),
                    )
                    Text(
                        text       = "$totalXp XP",
                        fontSize   = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color      = DevX27Theme.colors.background,
                    )
                }
            }
        }
    }
}

@Composable
private fun XpProgressCard(currentXp: Int, nextLevelXp: Int, level: Int) {
    val progress = if (nextLevelXp > 0) currentXp.toFloat() / nextLevelXp.toFloat() else 0f
    var animatedProgress by remember { mutableFloatStateOf(0f) }
    val smoothProgress by animateFloatAsState(
        targetValue    = animatedProgress,
        animationSpec  = tween(1200),
        label          = "xp_progress"
    )

    LaunchedEffect(progress) { animatedProgress = progress.coerceIn(0f, 1f) }

    // Animated shimmer on progress bar
    val shimmer = rememberInfiniteTransition(label = "shimmer")
    val shimmerX by shimmer.animateFloat(
        initialValue  = -300f,
        targetValue   = 600f,
        animationSpec = infiniteRepeatable(tween(2000, easing = LinearEasing), RepeatMode.Restart),
        label         = "shimmer_x"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors   = CardDefaults.cardColors(containerColor = DevX27Theme.colors.surface),
        shape    = RoundedCornerShape(16.dp),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text       = "Level $level",
                    fontWeight = FontWeight.Bold,
                    fontSize   = 18.sp,
                    color      = DevX27Theme.colors.onBackground,
                )
                Text(
                    text     = "$currentXp / $nextLevelXp XP",
                    fontSize = 14.sp,
                    color    = DevX27Theme.colors.onSurfaceMuted,
                )
            }

            Spacer(Modifier.height(12.dp))

            // Progress bar track
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(DevX27Theme.colors.surfaceInput)
            ) {
                // Filled portion with shimmer
                Box(
                    modifier = Modifier
                        .fillMaxWidth(smoothProgress)
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(DevX27Theme.colors.xpSuccess)
                )
            }

            Spacer(Modifier.height(8.dp))
            Text(
                text     = "${(smoothProgress * 100).toInt()}% to Level ${level + 1}",
                fontSize = 12.sp,
                color    = DevX27Theme.colors.onSurfaceSubtle,
            )
        }
    }
}

@Composable
private fun StatsRow(streak: Int, solved: Int, rank: Int) {
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        StatCard(Modifier.weight(1f), "🔥 Streak", "$streak days", DevX27Theme.colors.xpWarning)
        StatCard(Modifier.weight(1f), "💎 Solved",  "$solved",      DevX27Theme.colors.xpSuccess)
        StatCard(Modifier.weight(1f), "🏆 Rank",   if (rank > 0) "#$rank" else "—", DevX27Theme.colors.xpGold)
    }
}

@Composable
private fun StatCard(modifier: Modifier, label: String, value: String, accent: Color) {
    Card(
        modifier = modifier,
        colors   = CardDefaults.cardColors(containerColor = DevX27Theme.colors.surface),
        shape    = RoundedCornerShape(12.dp),
    ) {
        Column(
            modifier            = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(text = label, fontSize = 11.sp, color = DevX27Theme.colors.onSurfaceSubtle)
            Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Black, color = accent)
        }
    }
}
@Composable
private fun DailyMissionsSection() {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text(
            "Daily Missions",
            fontWeight = FontWeight.Black,
            fontSize = 18.sp,
            color = DevX27Theme.colors.onBackground
        )
        Spacer(Modifier.height(12.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DevX27Theme.colors.surface),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("🎯", fontSize = 32.sp)
                Text(
                    "Solve problems to unlock daily missions!",
                    fontSize = 14.sp,
                    color = DevX27Theme.colors.onSurfaceMuted,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun MissionItem(title: String, progress: Float, reward: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DevX27Theme.colors.surface.elevate(1f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(DevX27Theme.colors.surfaceInput),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Star,
                    null,
                    tint = DevX27Theme.colors.xpGold,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = DevX27Theme.colors.onBackground)
                Spacer(Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape),
                    color = DevX27Theme.colors.xpSuccess,
                    trackColor = DevX27Theme.colors.surfaceInput
                )
            }
            Text(reward, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DevX27Theme.colors.xpSuccess)
        }
    }
}

