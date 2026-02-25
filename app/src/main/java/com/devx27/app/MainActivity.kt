package com.devx27.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.devx27.app.domain.model.AppTheme
import com.devx27.app.domain.repository.AuthRepository
import com.devx27.app.domain.repository.SettingsRepository
import com.devx27.app.presentation.navigation.DevX27NavGraph
import com.devx27.app.presentation.theme.DevX27Theme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * MainActivity — the single Activity host for the entire app.
 * Compose navigation handles all destination switching from here.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var authRepository: AuthRepository
    @Inject lateinit var settingsRepository: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val settings by settingsRepository.getSettings().collectAsState(
                initial = com.devx27.app.domain.model.AppSettings()
            )
            val isSystemDark = isSystemInDarkTheme()
            val useDarkTheme = when (settings.theme) {
                AppTheme.DARK   -> true
                AppTheme.LIGHT  -> false
                AppTheme.SYSTEM -> false  // Default to Light as requested
            }

            DevX27Theme(darkTheme = useDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DevX27Theme.colors.background
                ) {
                    DevX27NavGraph(authRepository)
                }
            }
        }
    }
}
