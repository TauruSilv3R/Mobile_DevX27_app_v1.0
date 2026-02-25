package com.devx27.app.presentation.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.devx27.app.domain.repository.AuthRepository
import com.devx27.app.presentation.auth.AuthViewModel
import com.devx27.app.presentation.auth.LoginScreen
import com.devx27.app.presentation.auth.RegisterScreen
import com.devx27.app.presentation.battle.BattleArenaScreen
import com.devx27.app.presentation.battle.BattleLobbyScreen
import com.devx27.app.presentation.compete.CompeteScreen
import com.devx27.app.presentation.dashboard.DashboardScreen
import com.devx27.app.presentation.editor.CodeEditorScreen
import com.devx27.app.presentation.help.HelpScreen
import com.devx27.app.presentation.leaderboard.LeaderboardScreen
import com.devx27.app.presentation.careerhub.CareerHubScreen
import com.devx27.app.presentation.careerhub.CareerJobDetailScreen
import com.devx27.app.presentation.legal.PrivacyPolicyScreen
import com.devx27.app.presentation.settings.CodeEditorSettingsScreen
import com.devx27.app.presentation.practice.PracticeScreen
import com.devx27.app.presentation.profile.EditProfileScreen
import com.devx27.app.presentation.profile.ProfileScreen
import com.devx27.app.presentation.settings.SettingsScreen
import com.devx27.app.presentation.skilltree.SkillTreeScreen
import com.devx27.app.presentation.theme.DevX27Theme
import com.devx27.app.presentation.forum.ForumScreen

private const val NAV_ANIM_DURATION = 300

@Composable
fun DevX27NavGraph(
    authRepository: AuthRepository
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Check Authentication — go straight to Dashboard or Login, no permission gate
    val startDest = remember(authRepository.isAuthenticated) {
        if (authRepository.isAuthenticated) Screen.Dashboard.route
        else Screen.Login.route
    }

    val showBottomBar = currentRoute in listOf(
        Screen.Dashboard.route,
        Screen.Practice.route,
        Screen.Leaderboard.route,
        Screen.Forum.route,
        Screen.CareerHub.route,
        Screen.Profile.route
    )

    Scaffold(
        containerColor = DevX27Theme.colors.background,
        bottomBar = {
            if (showBottomBar) {
                DevX27BottomBar(navController = navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController    = navController,
            startDestination = startDest,
            modifier         = Modifier.padding(innerPadding),
            enterTransition  = {
                fadeIn(tween(NAV_ANIM_DURATION)) +
                slideInHorizontally(tween(NAV_ANIM_DURATION)) { it / 8 }
            },
            exitTransition   = {
                fadeOut(tween(NAV_ANIM_DURATION)) +
                slideOutHorizontally(tween(NAV_ANIM_DURATION)) { -it / 8 }
            },
            popEnterTransition = {
                fadeIn(tween(NAV_ANIM_DURATION)) +
                slideInHorizontally(tween(NAV_ANIM_DURATION)) { -it / 8 }
            },
            popExitTransition = {
                fadeOut(tween(NAV_ANIM_DURATION)) +
                slideOutHorizontally(tween(NAV_ANIM_DURATION)) { it / 8 }
            },
        ) {
            // ── System / Auth ─────────────────────────────────────────────────

            composable(Screen.Login.route)    { LoginScreen(navController) }
            composable(Screen.Register.route) { RegisterScreen(navController) }

            // ── Tab destinations ──────────────────────────────────────────────
            composable(Screen.Dashboard.route)   { DashboardScreen(navController) }
            composable(Screen.Practice.route)    { PracticeScreen(navController) }
            composable(Screen.Compete.route)     { CompeteScreen(navController) }
            composable(Screen.Leaderboard.route) { LeaderboardScreen(navController) }
            composable(Screen.Forum.route)       { ForumScreen(navController) }
            composable(Screen.CareerHub.route)   { CareerHubScreen(navController) }
            composable(Screen.Profile.route)     { ProfileScreen(navController) }

            // ── Nested destinations ───────────────────────────────────────────
            composable(
                route     = Screen.ChallengeDetail.route,
                arguments = listOf(navArgument("challengeId") { type = NavType.StringType }),
            ) { backStack ->
                val challengeId = backStack.arguments?.getString("challengeId") ?: return@composable
            }
            composable(
                route     = Screen.CodeEditor.route,
                arguments = listOf(navArgument("challengeId") { type = NavType.StringType }),
            ) { backStack ->
                val challengeId = backStack.arguments?.getString("challengeId") ?: return@composable
                CodeEditorScreen(navController = navController, challengeId = challengeId)
            }
            composable(Screen.SkillTree.route)   { SkillTreeScreen(navController) }
            composable(Screen.BattleLobby.route) { BattleLobbyScreen(navController) }
            composable(Screen.BattleArena.route) { BattleArenaScreen(navController) }
            composable(Screen.Help.route)        { HelpScreen(navController) }
            composable(Screen.Settings.route)    { SettingsScreen(navController) }
            composable(Screen.EditProfile.route) { EditProfileScreen(navController) }
            composable(Screen.CodeEditorSettings.route) { CodeEditorSettingsScreen(navController) }
            composable(Screen.PrivacyPolicy.route) { PrivacyPolicyScreen(navController) }
            composable(
                route = Screen.CareerJobDetail.route,
                arguments = listOf(navArgument("jobId") { type = NavType.StringType })
            ) { backStack ->
                val jobId = backStack.arguments?.getString("jobId") ?: return@composable
                CareerJobDetailScreen(navController, jobId)
            }
        }
    }
}
