package com.devx27.app.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.filled.SportsMartialArts
import androidx.compose.ui.graphics.vector.ImageVector

// ─────────────────────────────────────────────────────────────────────────────
// Screen — sealed class representing every navigable destination in DevX27.
// Routes are string-based to support typed Navigation 2.x compatibility.
// ─────────────────────────────────────────────────────────────────────────────
sealed class Screen(val route: String) {

    // ── Auth flow ─────────────────────────────────────────────────────────────
    data object Login          : Screen("login")
    data object Register       : Screen("register")
    data object PermissionGate : Screen("permission_gate")

    // ── Main bottom-bar tabs ──────────────────────────────────────────────────
    data object Dashboard   : Screen("dashboard")
    data object Practice    : Screen("practice")
    data object Compete     : Screen("compete")
    data object Leaderboard : Screen("leaderboard")
    data object Forum       : Screen("forum")
    data object Profile     : Screen("profile")
    data object CareerHub   : Screen("career_hub")

    // ── Nested destinations ───────────────────────────────────────────────────
    data object ChallengeDetail : Screen("challenge_detail/{challengeId}") {
        fun createRoute(challengeId: String) = "challenge_detail/$challengeId"
    }
    data object CodeEditor : Screen("code_editor/{challengeId}") {
        fun createRoute(challengeId: String) = "code_editor/$challengeId"
    }
    data object SkillTree   : Screen("skill_tree")
    data object BattleLobby : Screen("battle_lobby")
    data object BattleArena : Screen("battle_arena")
    data object Tutorial    : Screen("tutorial")
    data object Help        : Screen("help")
    data object Settings    : Screen("settings")
    data object EditProfile : Screen("edit_profile")
    data object CodeEditorSettings : Screen("code_editor_settings")
    data object PrivacyPolicy : Screen("privacy_policy")
    data object CareerJobDetail : Screen("career_job/{jobId}") {
        fun createRoute(jobId: String) = "career_job/$jobId"
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// BottomNavItem — maps tabs to Screen, icon, and display label
// ─────────────────────────────────────────────────────────────────────────────
data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector = icon,
)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Dashboard,   "Home",        Icons.Default.Dashboard),
    BottomNavItem(Screen.Practice,    "Practice",    Icons.Default.Code),
    BottomNavItem(Screen.Leaderboard, "Leaderboard", Icons.Default.EmojiEvents),
    BottomNavItem(Screen.Forum,       "Forum",       Icons.Default.Forum),
    BottomNavItem(Screen.CareerHub,   "Career Hub",  Icons.Default.Work),
    BottomNavItem(Screen.Profile,     "Profile",     Icons.Default.Person),
)
