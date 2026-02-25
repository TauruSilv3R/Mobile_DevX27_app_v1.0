package com.devx27.app.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.KeyboardAlt
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.devx27.app.domain.model.AppTheme
import com.devx27.app.presentation.theme.DevX27Theme

@Composable
fun CodeEditorSettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val settings by viewModel.settings.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DevX27Theme.colors.background)
    ) {
        TopAppBar(
            title = { Text("Code Editor Settings", fontWeight = FontWeight.Bold, color = DevX27Theme.colors.onBackground) },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = DevX27Theme.colors.onBackground)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = DevX27Theme.colors.background)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(PaddingValues(16.dp)),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SettingSectionHeader("Defaults")
            LanguageSettingRow(
                currentLanguage = settings.preferredLanguage,
                onLanguageSelected = viewModel::setPreferredLanguage
            )

            SettingSectionHeader("Editor")
            ToggleSettingItem(
                title = "Auto-save code",
                subtitle = "Save code drafts automatically",
                icon = Icons.Default.Save,
                isEnabled = settings.codeAutoSave,
                onCheckedChange = viewModel::setCodeAutoSave
            )
            ToggleSettingItem(
                title = "Show line numbers",
                subtitle = "Display line numbers in editor",
                icon = Icons.Default.FormatListNumbered,
                isEnabled = settings.codeLineNumbers,
                onCheckedChange = viewModel::setCodeLineNumbers
            )
            ToggleSettingItem(
                title = "Vim keybindings",
                subtitle = "Enable Vim-style shortcuts",
                icon = Icons.Default.KeyboardAlt,
                isEnabled = settings.codeVimMode,
                onCheckedChange = viewModel::setCodeVimMode
            )
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
        modifier = Modifier.padding(bottom = 4.dp, top = 12.dp)
    )
}

@Composable
private fun LanguageSettingRow(
    currentLanguage: String,
    onLanguageSelected: (String) -> Unit
) {
    val languages = listOf("Python", "Kotlin", "JavaScript", "Java", "C++")
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DevX27Theme.colors.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable { /* selection handled in chips below */ },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Language, null, tint = DevX27Theme.colors.onSurface)
            Spacer(Modifier.size(12.dp))
            Column(Modifier.weight(1f)) {
                Text("Preferred language", color = DevX27Theme.colors.onSurface, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.size(6.dp))
                FlowChips(
                    items = languages,
                    selected = currentLanguage,
                    onSelect = onLanguageSelected
                )
            }
        }
    }
}

@Composable
private fun FlowChips(
    items: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items.forEach { item ->
            val isSelected = item.equals(selected, ignoreCase = true)
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) DevX27Theme.colors.xpSuccess.copy(alpha = 0.15f) else DevX27Theme.colors.surfaceInput
                ),
                modifier = Modifier.clickable { onSelect(item) }
            ) {
                Text(
                    item,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    color = if (isSelected) DevX27Theme.colors.xpSuccess else DevX27Theme.colors.onSurfaceMuted,
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                )
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
            Icon(icon, null, tint = DevX27Theme.colors.onSurface, modifier = Modifier.size(22.dp))
            Spacer(Modifier.size(12.dp))
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
