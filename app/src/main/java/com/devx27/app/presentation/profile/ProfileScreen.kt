package com.devx27.app.presentation.profile

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.devx27.app.domain.model.Difficulty
import com.devx27.app.domain.model.RecentActivity
import com.devx27.app.domain.model.UserProfile
import com.devx27.app.domain.model.UserStats
import com.devx27.app.presentation.theme.*
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(DevX27Theme.colors.background)) {
        LazyColumn(
            modifier            = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            contentPadding      = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
        // Settings + Sign out
        item {
            uiState.stats?.let {
                ProfileHeader(
                    profile = it.profile,
                    onSignOut = viewModel::signOut,
                    onSettings = { navController.navigate(com.devx27.app.presentation.navigation.Screen.Settings.route) }
                )
            }
        }

        // ── Stats Grid ────────────────────────────────────────────────────────
        item {
            uiState.stats?.let { StatsGrid(it.profile) }
        }

        // ── XP Level Bar ─────────────────────────────────────────────────────
        item {
            uiState.stats?.let { XPLevelBar(it.profile) }
        }

        // ── Profile Details ───────────────────────────────────────────────────
        item {
            uiState.stats?.let { ProfileDetails(it.profile) }
        }

        // ── Recent Activity ───────────────────────────────────────────────────
        item { SectionHeader("Recent Activity") }

        uiState.stats?.recentActivity?.let { activities ->
            items(activities, key = { "${it.challengeId}_${it.completedAt}" }) { activity ->
                ActivityRow(activity)
            }
        }

        item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Profile Header — Avatar initials + name + email + sign-out
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun ProfileHeader(profile: UserProfile, onSignOut: () -> Unit, onSettings: () -> Unit) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Avatar circle with initials or photo
            Box(
                modifier         = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(DevX27Theme.colors.xpSuccessBg),
                contentAlignment = Alignment.Center,
            ) {
                if (profile.photoUrl != null) {
                    AsyncImage(
                        model = profile.photoUrl,
                        contentDescription = "Profile Picture",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text       = profile.displayName.split(" ")
                            .mapNotNull { it.firstOrNull()?.toString() }
                            .take(2)
                            .joinToString("")
                            .uppercase(),
                        fontSize   = 20.sp,
                        fontWeight = FontWeight.Black,
                        color      = DevX27Theme.colors.xpSuccess,
                    )
                }
            }
            Spacer(Modifier.width(14.dp))
            Column {
                Text(profile.displayName, fontWeight = FontWeight.Black, fontSize = 20.sp, color = DevX27Theme.colors.onBackground)
                Text(profile.email, fontSize = 13.sp, color = DevX27Theme.colors.onSurfaceSubtle)
                if (profile.bio != null) {
                    Text(profile.bio, fontSize = 12.sp, color = DevX27Theme.colors.onSurfaceMuted, modifier = Modifier.padding(top = 4.dp))
                }
                if (profile.location != null) {
                    Text("📍 ${profile.location}", fontSize = 11.sp, color = DevX27Theme.colors.onSurfaceSubtle, modifier = Modifier.padding(top = 2.dp))
                }
            }
        }
        Row {
            IconButton(onClick = onSettings) {
                Icon(Icons.Default.Settings, "Settings", tint = DevX27Theme.colors.onSurfaceMuted)
            }
            IconButton(onClick = onSignOut) {
                Icon(Icons.Default.Logout, "Sign out", tint = DevX27Theme.colors.onSurfaceMuted)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Stats Grid — 3 high-contrast stat cards
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun StatsGrid(profile: UserProfile) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        StatCard("Global Rank",  "#${profile.globalRank}",        Modifier.weight(1f))
        StatCard("Streak",       "${profile.streak}d 🔥",         Modifier.weight(1f))
        StatCard("Solved",       "${profile.challengesSolved}",   Modifier.weight(1f))
    }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors   = CardDefaults.cardColors(containerColor = DevX27Theme.colors.surface),
        shape    = RoundedCornerShape(14.dp),
    ) {
        Column(
            modifier            = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text       = value,
                fontWeight = FontWeight.Black,
                fontSize   = 22.sp,
                color      = DevX27Theme.colors.onBackground,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text     = label,
                fontSize = 11.sp,
                color    = DevX27Theme.colors.onSurfaceSubtle,
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Profile Details
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun ProfileDetails(profile: UserProfile) {
    val roleStr = listOfNotNull(profile.role, profile.company).joinToString(" at ")
    val hasDetails = profile.bio != null || profile.education != null || profile.skills.isNotEmpty() || profile.programmingLanguages.isNotEmpty() || profile.website != null || profile.githubUrl != null || profile.linkedInUrl != null || roleStr.isNotBlank()

    if (!hasDetails) return

    Card(
        colors = CardDefaults.cardColors(containerColor = DevX27Theme.colors.surface),
        shape  = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            if (roleStr.isNotBlank()) {
                ProfileDetailItem("Current Role", roleStr)
            }
            if (profile.education != null) {
                ProfileDetailItem("Education", profile.education)
            }
            if (profile.skills.isNotEmpty()) {
                ProfileDetailItem("Skills", profile.skills.joinToString(", "))
            }
            if (profile.programmingLanguages.isNotEmpty()) {
                ProfileDetailItem("Languages", profile.programmingLanguages.joinToString(", "))
            }
            
            // Links
            val links = mutableListOf<@Composable () -> Unit>()
            if (profile.website != null) {
                links.add { DetailLink(Icons.Default.Language, "Website", profile.website) }
            }
            if (profile.githubUrl != null) {
                links.add { DetailLink(Icons.Default.Code, "GitHub", profile.githubUrl) }
            }
            if (profile.linkedInUrl != null) {
                links.add { DetailLink(Icons.Default.Link, "LinkedIn", profile.linkedInUrl) }
            }

            if (links.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Links", fontSize = 11.sp, color = DevX27Theme.colors.onSurfaceSubtle, fontWeight = FontWeight.SemiBold)
                    links.forEach { it() }
                }
            }
        }
    }
}

@Composable
private fun ProfileDetailItem(label: String, value: String) {
    Column {
        Text(label, fontSize = 11.sp, color = DevX27Theme.colors.onSurfaceSubtle, fontWeight = FontWeight.SemiBold)
        Text(value, fontSize = 14.sp, color = DevX27Theme.colors.onBackground, modifier = Modifier.padding(top = 2.dp))
    }
}

@Composable
private fun DetailLink(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, url: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = DevX27Theme.colors.onSurfaceMuted, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(8.dp))
        Column {
            Text(label, fontSize = 12.sp, color = DevX27Theme.colors.onBackground, fontWeight = FontWeight.Medium)
            Text(url, fontSize = 11.sp, color = DevX27Theme.colors.onSurfaceMuted)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// XP Level Bar
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun XPLevelBar(profile: UserProfile) {
    val progress = profile.totalXp.toFloat() / profile.nextLevelXp.toFloat()
    Card(
        colors = CardDefaults.cardColors(containerColor = DevX27Theme.colors.surface),
        shape  = RoundedCornerShape(14.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.Bolt, null, tint = DevX27Theme.colors.xpSuccess, modifier = Modifier.size(16.dp))
                    Text("${profile.totalXp} XP", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = DevX27Theme.colors.xpSuccess)
                }
                Text("Level ${profile.level}", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = DevX27Theme.colors.onBackground)
            }
            Spacer(Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(DevX27Theme.colors.surfaceInput)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress.coerceIn(0f, 1f))
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(DevX27Theme.colors.xpSuccess)
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                "${profile.nextLevelXp - profile.totalXp} XP to Level ${profile.level + 1}",
                fontSize = 11.sp,
                color    = DevX27Theme.colors.onSurfaceSubtle,
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Activity Row — one completed challenge entry
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun ActivityRow(activity: RecentActivity) {
    val difficultyColor = when (activity.difficulty) {
        Difficulty.EASY   -> DevX27Theme.colors.difficultyEasy
        Difficulty.MEDIUM -> DevX27Theme.colors.difficultyMedium
        Difficulty.HARD   -> DevX27Theme.colors.difficultyHard
        Difficulty.ELITE  -> DevX27Theme.colors.difficultyElite
    }
    val timeAgo = formatRelativeTime(activity.completedAt)

    Card(
        colors = CardDefaults.cardColors(containerColor = DevX27Theme.colors.surface),
        shape  = RoundedCornerShape(12.dp),
    ) {
        Row(
            modifier              = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment     = Alignment.CenterVertically,
        ) {
            // Pass / Fail icon
            Icon(
                imageVector        = if (activity.passed) Icons.Default.CheckCircle else Icons.Default.Error,
                contentDescription = null,
                tint               = if (activity.passed) DevX27Theme.colors.xpSuccess else DevX27Theme.colors.xpError,
                modifier           = Modifier.size(22.dp),
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(activity.title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = DevX27Theme.colors.onBackground)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment     = Alignment.CenterVertically,
                ) {
                    // Difficulty badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(difficultyColor.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(activity.difficulty.label, fontSize = 10.sp, color = difficultyColor, fontWeight = FontWeight.Bold)
                    }
                    Text("•", fontSize = 10.sp, color = DevX27Theme.colors.onSurfaceSubtle)
                    Text(activity.language, fontSize = 11.sp, color = DevX27Theme.colors.onSurfaceSubtle)
                    Text("•", fontSize = 10.sp, color = DevX27Theme.colors.onSurfaceSubtle)
                    Text(timeAgo, fontSize = 11.sp, color = DevX27Theme.colors.onSurfaceSubtle)
                }
            }
            if (activity.passed && activity.xpGained > 0) {
                Column(horizontalAlignment = Alignment.End) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        Icon(Icons.Default.Bolt, null, tint = DevX27Theme.colors.xpSuccess, modifier = Modifier.size(12.dp))
                        Text("+${activity.xpGained}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DevX27Theme.colors.xpSuccess)
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DevX27Theme.colors.onBackground)
}

private fun formatRelativeTime(epochMs: Long): String {
    val diff    = System.currentTimeMillis() - epochMs
    val minutes = diff / 60_000
    val hours   = minutes / 60
    val days    = hours / 24
    return when {
        minutes < 60   -> "${minutes}m ago"
        hours   < 24   -> "${hours}h ago"
        else           -> "${days}d ago"
    }
}
