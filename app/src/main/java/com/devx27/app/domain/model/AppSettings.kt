package com.devx27.app.domain.model

enum class AppTheme {
    SYSTEM, LIGHT, DARK
}

data class AppSettings(
    val theme: AppTheme = AppTheme.SYSTEM,
    val notificationsEnabled: Boolean = true,
    val preferredLanguage: String = "Python",
    val hapticsEnabled: Boolean = true
)
