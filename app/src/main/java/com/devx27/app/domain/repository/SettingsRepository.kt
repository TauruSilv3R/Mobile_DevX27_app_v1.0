package com.devx27.app.domain.repository

import com.devx27.app.domain.model.AppTheme
import com.devx27.app.domain.model.AppSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getSettings(): Flow<AppSettings>
    suspend fun updateTheme(theme: AppTheme)
    suspend fun updateNotifications(enabled: Boolean)
    suspend fun updatePreferredLanguage(language: String)
    suspend fun updateCodeAutoSave(enabled: Boolean)
    suspend fun updateCodeLineNumbers(enabled: Boolean)
    suspend fun updateCodeVimMode(enabled: Boolean)
    suspend fun updateCrashReporting(enabled: Boolean)
}
