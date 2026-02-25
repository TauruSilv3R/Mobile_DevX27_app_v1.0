package com.devx27.app.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.devx27.app.domain.model.AppTheme
import com.devx27.app.presentation.theme.DevX27Theme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val settings by viewModel.settings.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DevX27Theme.colors.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, "Back", tint = DevX27Theme.colors.onBackground)
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    "Settings",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = DevX27Theme.colors.onBackground
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { SettingSectionHeader("Appearance") }
                item {
                    ThemeSettingItem(
                        currentTheme = settings.theme,
                        onThemeSelected = viewModel::setTheme
                    )
                }

                item { Spacer(Modifier.height(8.dp)) }
                item { SettingSectionHeader("Preferences") }
                item {
                    ToggleSettingItem(
                        title = "Notifications",
                        subtitle = "Enable push notifications",
                        icon = Icons.Default.Notifications,
                        isEnabled = settings.notificationsEnabled,
                        onCheckedChange = viewModel::setNotificationsEnabled
                    )
                }
                item {
                    LanguageSettingItem(
                        currentLanguage = settings.preferredLanguage,
                        onLanguageSelected = viewModel::setPreferredLanguage
                    )
                }
                item {
                    SettingSectionHeader("Code Editor")
                }
                item {
                    ActionSettingItem(
                        title = "Code Editor Settings",
                        icon = Icons.Default.Code,
                        onClick = { navController.navigate(com.devx27.app.presentation.navigation.Screen.CodeEditorSettings.route) }
                    )
                }

                item { Spacer(Modifier.height(8.dp)) }
                item { SettingSectionHeader("App") }
                item {
                    ActionSettingItem(
                        title = "Help & FAQs",
                        icon = Icons.Default.HelpOutline,
                        onClick = { navController.navigate(com.devx27.app.presentation.navigation.Screen.Help.route) }
                    )
                }
                item {
                    ActionSettingItem(
                        title = "Privacy Policy",
                        icon = Icons.Default.PrivacyTip,
                        onClick = { navController.navigate(com.devx27.app.presentation.navigation.Screen.PrivacyPolicy.route) }
                    )
                }
                item {
                    ToggleSettingItem(
                        title = "Crash reporting",
                        subtitle = "Share anonymized crash reports",
                        icon = Icons.Default.BugReport,
                        isEnabled = settings.crashReporting,
                        onCheckedChange = viewModel::setCrashReporting
                    )
                }
                item {
                    ActionSettingItem(
                        title = "Sign Out",
                        icon = Icons.Default.Logout,
                        onClick = { viewModel.signOut(navController) }
                    )
                }

                item { Spacer(Modifier.height(24.dp)) }
                item {
                    Text(
                        "App Version: 1.0.0 (Debug)",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        fontSize = 12.sp,
                        color = DevX27Theme.colors.onSurfaceSubtle
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingSectionHeader(title: String) {
    Text(
        text = title.uppercase(),
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = DevX27Theme.colors.xpSuccess,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun ThemeSettingItem(
    currentTheme: AppTheme,
    onThemeSelected: (AppTheme) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DevX27Theme.colors.surface)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Palette, null, tint = DevX27Theme.colors.onSurface)
                Spacer(Modifier.width(12.dp))
                Text("Theme", color = DevX27Theme.colors.onSurface, fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AppTheme.values().forEach { theme ->
                    val isSelected = currentTheme == theme
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .background(
                                color = if (isSelected) DevX27Theme.colors.xpSuccess.copy(alpha = 0.2f) 
                                        else Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { onThemeSelected(theme) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            theme.name.lowercase().capitalize(),
                            color = if (isSelected) DevX27Theme.colors.xpSuccess else DevX27Theme.colors.onSurfaceMuted,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ToggleSettingItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isEnabled: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DevX27Theme.colors.surface)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = DevX27Theme.colors.onSurface)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(title, color = DevX27Theme.colors.onSurface, fontWeight = FontWeight.SemiBold)
                Text(subtitle, color = DevX27Theme.colors.onSurfaceSubtle, fontSize = 12.sp)
            }
            Switch(
                checked = isEnabled,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = DevX27Theme.colors.xpSuccess,
                    checkedTrackColor = DevX27Theme.colors.xpSuccess.copy(alpha = 0.5f)
                )
            )
        }
    }
}

@Composable
private fun LanguageSettingItem(
    currentLanguage: String,
    onLanguageSelected: (String) -> Unit
) {
    val languages = listOf("Python", "Kotlin", "JavaScript", "Java")
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DevX27Theme.colors.surface)
    ) {
        Row(
            modifier = Modifier
                .clickable { expanded = true }
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Code, null, tint = DevX27Theme.colors.onSurface)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("Preferred Language", color = DevX27Theme.colors.onSurface, fontWeight = FontWeight.SemiBold)
                Text(currentLanguage, color = DevX27Theme.colors.xpSuccess, fontSize = 12.sp)
            }
            Icon(Icons.Default.KeyboardArrowRight, null, tint = DevX27Theme.colors.onSurfaceMuted)
            
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(DevX27Theme.colors.surfaceElevated)
            ) {
                languages.forEach { lang ->
                    DropdownMenuItem(
                        text = { Text(lang, color = DevX27Theme.colors.onSurface) },
                        onClick = {
                            onLanguageSelected(lang)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ActionSettingItem(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DevX27Theme.colors.surface)
    ) {
        Row(
            modifier = Modifier
                .clickable { onClick() }
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = DevX27Theme.colors.onSurface)
            Spacer(Modifier.width(12.dp))
            Text(title, modifier = Modifier.weight(1f), color = DevX27Theme.colors.onSurface, fontWeight = FontWeight.SemiBold)
            Icon(Icons.Default.KeyboardArrowRight, null, tint = DevX27Theme.colors.onSurfaceMuted)
        }
    }
}
