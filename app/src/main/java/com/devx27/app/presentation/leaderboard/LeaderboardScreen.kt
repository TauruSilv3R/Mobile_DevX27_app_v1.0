package com.devx27.app.presentation.leaderboard

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.devx27.app.domain.model.LeaderboardEntry
import com.devx27.app.presentation.theme.*

@Composable
fun LeaderboardScreen(
    navController: NavController,
    viewModel: LeaderboardViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(DevX27Theme.colors.background)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
        // ── Header ────────────────────────────────────────────────────────────
        Row(
            modifier              = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text       = "Leaderboard",
                    fontSize   = 28.sp,
                    fontWeight = FontWeight.Black,
                    color      = DevX27Theme.colors.onBackground,
                )
                Text(
                    text     = "Global • Real-time",
                    fontSize = 13.sp,
                    color    = DevX27Theme.colors.onSurfaceSubtle,
                )
            }
        }

        // ── Real-time update dot ───────────────────────────────────────────────
        Row(
            modifier              = Modifier.padding(horizontal = 20.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(DevX27Theme.colors.xpSuccess)
            )
            Text(
                text     = "Live — updates automatically",
                fontSize = 11.sp,
                color    = DevX27Theme.colors.onSurfaceSubtle,
            )
        }

        Spacer(Modifier.height(12.dp))

        // ── Current User Stats Container ──
        uiState.currentUserRank?.let { rank ->
            val myEntry = uiState.entries.find { it.userId == uiState.currentUserId }
            val myXp = myEntry?.totalXp ?: 0

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                colors = CardDefaults.cardColors(containerColor = DevX27Theme.colors.surfaceElevated),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("My Rank", fontSize = 12.sp, color = DevX27Theme.colors.onSurfaceMuted)
                        Text("#$rank", fontSize = 24.sp, fontWeight = FontWeight.Black, color = DevX27Theme.colors.actionColor)
                    }
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(30.dp)
                            .background(DevX27Theme.colors.divider)
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Total XP", fontSize = 12.sp, color = DevX27Theme.colors.onSurfaceMuted)
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.Bolt, contentDescription = null, tint = DevX27Theme.colors.xpSuccess, modifier = Modifier.size(18.dp))
                            Text("$myXp", fontSize = 24.sp, fontWeight = FontWeight.Black, color = DevX27Theme.colors.xpSuccess)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // ── LazyColumn with itemsIndexed for smooth real-time updates ─────────
        LazyColumn(
            contentPadding      = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            itemsIndexed(
                items = uiState.entries,
                key   = { _, entry -> entry.userId },      // Stable keys = no re-composition flicker
            ) { index, entry ->
                val rank       = index + 1
                val isCurrentUser = entry.userId == uiState.currentUserId

                LeaderboardRow(
                    rank          = rank,
                    entry         = entry,
                    isCurrentUser = isCurrentUser,
                )
            }

            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}
}

// ─────────────────────────────────────────────────────────────────────────────
// Podium — special display for ranks 1, 2, 3
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun PodiumRow(
    first: LeaderboardEntry,
    second: LeaderboardEntry,
    third: LeaderboardEntry,
) {
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment     = Alignment.Bottom,
    ) {
        PodiumCard(entry = second, rank = 2, height = 110, modifier = Modifier.weight(1f))
        PodiumCard(entry = first,  rank = 1, height = 140, modifier = Modifier.weight(1f))
        PodiumCard(entry = third,  rank = 3, height = 90,  modifier = Modifier.weight(1f))
    }
}

@Composable
private fun PodiumCard(
    entry: LeaderboardEntry,
    rank: Int,
    height: Int,
    modifier: Modifier = Modifier,
) {
    val medal = when (rank) { 1 -> "🥇"; 2 -> "🥈"; else -> "🥉" }
    val accent = when (rank) {
        1    -> DevX27Theme.colors.xpGold
        2    -> DevX27Theme.colors.onSurfaceMuted
        else -> DevX27Theme.colors.xpWarning
    }

    val scaleAnim = remember { Animatable(0.7f) }
    LaunchedEffect(Unit) {
        scaleAnim.animateTo(1f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium))
    }

    Card(
        modifier = modifier
            .height(height.dp)
            .scale(scaleAnim.value),
        colors   = CardDefaults.cardColors(containerColor = accent.copy(alpha = 0.08f)),
        shape    = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp, bottomStart = 4.dp, bottomEnd = 4.dp),
    ) {
        Column(
            modifier            = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(text = medal, fontSize = 24.sp)
            Text(
                text       = entry.displayName.split(" ").first(),
                fontSize   = 12.sp,
                fontWeight = FontWeight.Bold,
                color      = DevX27Theme.colors.onBackground,
            )
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Icon(Icons.Default.Bolt, null, tint = DevX27Theme.colors.xpSuccess, modifier = Modifier.size(11.dp))
                Text("${entry.totalXp}", fontSize = 11.sp, color = DevX27Theme.colors.xpSuccess, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// LeaderboardRow — ranks 4-50 in the LazyColumn
// Current user row has elevated surface + XP-green left accent bar
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun LeaderboardRow(
    rank: Int,
    entry: LeaderboardEntry,
    isCurrentUser: Boolean,
) {
    val containerColor = if (isCurrentUser)
        DevX27Theme.colors.xpSuccessBg
    else
        DevX27Theme.colors.surface

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors   = CardDefaults.cardColors(containerColor = containerColor),
        shape    = RoundedCornerShape(12.dp),
    ) {
        Row(
            modifier              = Modifier.padding(vertical = 12.dp, horizontal = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment     = Alignment.CenterVertically,
        ) {
            // Current user: accent left bar
            if (isCurrentUser) {
                Box(
                    modifier = Modifier
                        .width(3.dp)
                        .height(36.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(DevX27Theme.colors.xpSuccess)
                )
            }

            // Rank number
            Text(
                text       = "#$rank",
                fontWeight = FontWeight.Black,
                fontSize   = 16.sp,
                color      = if (isCurrentUser) DevX27Theme.colors.xpSuccess
                             else DevX27Theme.colors.onSurfaceSubtle,
                modifier   = Modifier.width(36.dp),
            )

            // User info
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text       = entry.displayName,
                        fontWeight = if (isCurrentUser) FontWeight.Bold else FontWeight.SemiBold,
                        fontSize   = 15.sp,
                        color      = DevX27Theme.colors.onBackground,
                    )
                    if (isCurrentUser) {
                        Spacer(Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(DevX27Theme.colors.xpSuccess.copy(alpha = 0.2f))
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        ) {
                            Text("YOU", fontSize = 9.sp, fontWeight = FontWeight.Black, color = DevX27Theme.colors.xpSuccess)
                        }
                    }
                }
                Text("Level ${entry.level}  •  ${entry.challengesSolved} solved", fontSize = 12.sp, color = DevX27Theme.colors.onSurfaceSubtle)
            }

            // XP
            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    Icon(Icons.Default.Bolt, null, tint = DevX27Theme.colors.xpSuccess, modifier = Modifier.size(14.dp))
                    Text(
                        text       = "${entry.totalXp}",
                        fontWeight = FontWeight.Bold,
                        fontSize   = 15.sp,
                        color      = DevX27Theme.colors.xpSuccess,
                    )
                }
                Text("XP", fontSize = 11.sp, color = DevX27Theme.colors.onSurfaceSubtle)
            }
        }
    }
}
