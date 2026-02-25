package com.devx27.app.domain.model

enum class AppTheme {
    SYSTEM, LIGHT, DARK
}

data class AppSettings(
    val theme: AppTheme = AppTheme.SYSTEM,
    val notificationsEnabled: Boolean = true,
    val preferredLanguage: String = "Python",
    // Code editor preferences
    val codeAutoSave: Boolean = true,
    val codeLineNumbers: Boolean = true,
    val codeVimMode: Boolean = false,
    // App-level preferences
    val crashReporting: Boolean = true
)
