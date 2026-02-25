package com.devx27.app.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devx27.app.domain.model.AppTheme
import com.devx27.app.domain.model.AppSettings
import com.devx27.app.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val authRepository: com.devx27.app.domain.repository.AuthRepository
) : ViewModel() {

    val settings: StateFlow<AppSettings> = settingsRepository.getSettings()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppSettings()
        )

    fun setTheme(theme: AppTheme) {
        viewModelScope.launch {
            settingsRepository.updateTheme(theme)
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateNotifications(enabled)
        }
    }

    fun setPreferredLanguage(language: String) {
        viewModelScope.launch {
            settingsRepository.updatePreferredLanguage(language)
        }
    }

    fun setCodeAutoSave(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateCodeAutoSave(enabled)
        }
    }

    fun setCodeLineNumbers(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateCodeLineNumbers(enabled)
        }
    }

    fun setCodeVimMode(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateCodeVimMode(enabled)
        }
    }

    fun setCrashReporting(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateCrashReporting(enabled)
        }
    }

    fun signOut(navController: androidx.navigation.NavController) {
        viewModelScope.launch {
            authRepository.signOut()
            navController.navigate(com.devx27.app.presentation.navigation.Screen.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }
}
