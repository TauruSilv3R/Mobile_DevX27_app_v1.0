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
        val HAPTICS = booleanPreferencesKey("haptics")
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
                    hapticsEnabled = preferences[PreferencesKeys.HAPTICS] ?: true
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

    override suspend fun updateHaptics(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.HAPTICS] = enabled
        }
    }
}
