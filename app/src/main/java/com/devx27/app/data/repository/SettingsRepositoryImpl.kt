package com.devx27.app.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.devx27.app.domain.model.AppSettings
import com.devx27.app.domain.model.AppTheme
import com.devx27.app.domain.repository.SettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : SettingsRepository {

    private object PreferencesKeys {
        val THEME = stringPreferencesKey("theme")
        val NOTIFICATIONS = booleanPreferencesKey("notifications")
        val PREFERRED_LANGUAGE = stringPreferencesKey("preferred_language")
        val CODE_AUTO_SAVE = booleanPreferencesKey("code_auto_save")
        val CODE_LINE_NUMBERS = booleanPreferencesKey("code_line_numbers")
        val CODE_VIM_MODE = booleanPreferencesKey("code_vim_mode")
        val CRASH_REPORTING = booleanPreferencesKey("crash_reporting")
    }

    override fun getSettings(): Flow<AppSettings> {
        return context.dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                val themeName = preferences[PreferencesKeys.THEME] ?: AppTheme.SYSTEM.name
                val theme = try { AppTheme.valueOf(themeName) } catch (e: Exception) { AppTheme.SYSTEM }
                
                AppSettings(
                    theme = theme,
                    notificationsEnabled = preferences[PreferencesKeys.NOTIFICATIONS] ?: true,
                    preferredLanguage = preferences[PreferencesKeys.PREFERRED_LANGUAGE] ?: "Python",
                    codeAutoSave = preferences[PreferencesKeys.CODE_AUTO_SAVE] ?: true,
                    codeLineNumbers = preferences[PreferencesKeys.CODE_LINE_NUMBERS] ?: true,
                    codeVimMode = preferences[PreferencesKeys.CODE_VIM_MODE] ?: false,
                    crashReporting = preferences[PreferencesKeys.CRASH_REPORTING] ?: true
                )
            }
    }

    override suspend fun updateTheme(theme: AppTheme) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME] = theme.name
        }
    }

    override suspend fun updateNotifications(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.NOTIFICATIONS] = enabled
        }
    }

    override suspend fun updatePreferredLanguage(language: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.PREFERRED_LANGUAGE] = language
        }
    }

    override suspend fun updateCodeAutoSave(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.CODE_AUTO_SAVE] = enabled
        }
    }

    override suspend fun updateCodeLineNumbers(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.CODE_LINE_NUMBERS] = enabled
        }
    }

    override suspend fun updateCodeVimMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.CODE_VIM_MODE] = enabled
        }
    }

    override suspend fun updateCrashReporting(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.CRASH_REPORTING] = enabled
        }
    }
}
