package com.devx27.app.presentation.legal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.devx27.app.presentation.theme.DevX27Theme

@Composable
fun PrivacyPolicyScreen(navController: NavController) {
    Scaffold(
        containerColor = DevX27Theme.colors.background,
        topBar = {
            TopAppBar(
                title = { Text("Privacy Policy", color = DevX27Theme.colors.onBackground, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = DevX27Theme.colors.onBackground)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DevX27Theme.colors.background)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState())
                .background(DevX27Theme.colors.background),
        ) {
            Text("Last updated: Feb 25, 2026", color = DevX27Theme.colors.onSurfaceSubtle, fontSize = 12.sp)
            Text(
                "Your Privacy",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = DevX27Theme.colors.onBackground,
                modifier = Modifier.padding(top = 8.dp, bottom = 6.dp)
            )
            Text(
                "We collect only the data needed to run DevX27: account credentials, profile details, challenge progress, and app settings. Code you write stays on your device unless you submit it to battles or shared features in the future. We do not sell personal data.",
                color = DevX27Theme.colors.onSurfaceMuted,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )

            Section(
                title = "Data Storage",
                body = "Data is stored locally on your device. Optional cloud sync features will be opt-in when released."
            )
            Section(
                title = "Crash Reporting",
                body = "If enabled in Settings, anonymized crash logs (no code content) help us diagnose issues. You can disable this anytime."
            )
            Section(
                title = "Third-Party Services",
                body = "Google sign-in (when enabled) uses Google's OAuth; we receive only your basic profile and email for authentication."
            )
            Section(
                title = "Your Choices",
                body = "You can edit or delete your profile data in Edit Profile. Turn off notifications, crash reporting, and other preferences in Settings."
            )
            Section(
                title = "Contact",
                body = "For questions, contact support@devx27.com"
            )
        }
    }
}

@Composable
private fun Section(title: String, body: String) {
    Text(
        title,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = DevX27Theme.colors.onBackground,
        modifier = Modifier.padding(top = 16.dp, bottom = 6.dp)
    )
    Text(
        body,
        color = DevX27Theme.colors.onSurfaceMuted,
        fontSize = 14.sp,
        lineHeight = 20.sp
    )
}
