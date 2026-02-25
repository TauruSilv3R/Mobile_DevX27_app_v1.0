package com.devx27.app.presentation.practice

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RectangleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.devx27.app.domain.model.Challenge
import com.devx27.app.domain.model.Difficulty
import com.devx27.app.presentation.navigation.Screen
import com.devx27.app.presentation.theme.DevX27Theme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PracticeScreen(
    navController: NavController,
    viewModel: PracticeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DevX27Theme.colors.background)
            .statusBarsPadding(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        item {
            Text(
                text       = "Practice",
                fontSize   = 28.sp,
                fontWeight = FontWeight.Black,
                color      = DevX27Theme.colors.onBackground,
                modifier   = Modifier.padding(vertical = 20.dp),
            )
        }
        item {
            OutlinedTextField(
                value         = uiState.searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                placeholder   = { Text("Search challenges...", color = DevX27Theme.colors.onSurfaceSubtle) },
                leadingIcon   = { Icon(Icons.Default.Search, null, tint = DevX27Theme.colors.onSurfaceMuted) },
                modifier      = Modifier
                    .fillMaxWidth(),
                shape  = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = DevX27Theme.colors.actionColor,
                    unfocusedBorderColor = DevX27Theme.colors.divider,
                    focusedTextColor     = DevX27Theme.colors.onBackground,
                    unfocusedTextColor   = DevX27Theme.colors.onBackground,
                    cursorColor          = DevX27Theme.colors.actionColor,
                ),
                singleLine = true,
            )
        }
        item { Spacer(Modifier.height(12.dp)) }
        item {
            LazyRow(
                contentPadding        = PaddingValues(horizontal = 0.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                item {
                    DifficultyChip(label = "All", isSelected = uiState.selectedDifficulty == null) {
                        viewModel.onDifficultySelected(null)
                    }
                }
                items(Difficulty.entries) { diff ->
                    DifficultyChip(
                        label      = diff.label,
                        isSelected = uiState.selectedDifficulty == diff,
                        color      = diff.color,
                    ) { viewModel.onDifficultySelected(diff) }
                }
            }
        }
        item { Spacer(Modifier.height(12.dp)) }

        items(uiState.filteredChallenges, key = { it.id }) { challenge ->
            val isSolved = uiState.solvedChallengeIds.contains(challenge.id)
            ChallengeCard(
                challenge = challenge,
                isSolved = isSolved,
                onClick = { navController.navigate(Screen.CodeEditor.createRoute(challenge.id)) }
            )
        }
    }
}

@Composable
private fun DifficultyChip(
    label: String,
    isSelected: Boolean,
    color: androidx.compose.ui.graphics.Color = DevX27Theme.colors.onBackground,
    onClick: () -> Unit,
) {
    val labelColor = if (DevX27Theme.colors.isDark) DevX27Theme.colors.onSurface else DevX27Theme.colors.onBackground
    FilterChip(
        selected = isSelected,
        onClick  = onClick,
        label    = { Text(label, fontSize = 13.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
        colors   = FilterChipDefaults.filterChipColors(
            selectedContainerColor   = color.copy(alpha = 0.2f),
            selectedLabelColor       = color,
            containerColor           = DevX27Theme.colors.surface,
            labelColor               = labelColor,
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled          = true,
            selected         = isSelected,
            selectedBorderColor = color,
            borderColor      = DevX27Theme.colors.divider,
            selectedBorderWidth = 1.dp,
        ),
    )
}

@Composable
private fun ChallengeCard(challenge: Challenge, isSolved: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors  = CardDefaults.cardColors(containerColor = DevX27Theme.colors.surface),
        shape   = RectangleShape,
    ) {
        Row(
            modifier              = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = challenge.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DevX27Theme.colors.onBackground,
                    )
                    Spacer(Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        DifficultyBadge(challenge.difficulty)
                        Text(
                            text = challenge.category,
                            fontSize = 12.sp,
                            color = DevX27Theme.colors.onSurfaceSubtle,
                        )
                    }
                }
            }

            // Status / XP pill
            if (isSolved) {
                Box(
                    modifier         = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(DevX27Theme.colors.xpSuccessBg)
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(Icons.Default.Check, null, tint = DevX27Theme.colors.xpSuccess, modifier = Modifier.size(14.dp))
                        Text("Solved", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DevX27Theme.colors.xpSuccess)
                    }
                }
            } else {
                Box(
                    modifier         = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(DevX27Theme.colors.actionColor)
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                    ) {
                        Icon(Icons.Default.Bolt, null, tint = Color.Black, modifier = Modifier.size(14.dp))
                        Text("+${challenge.xpReward}", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
                    }
                }
            }
        }
    }
}

@Composable
private fun DifficultyBadge(difficulty: Difficulty) {
    val color = when (difficulty) {
        Difficulty.EASY   -> DevX27Theme.colors.diffEasy
        Difficulty.MEDIUM -> DevX27Theme.colors.diffMedium
        Difficulty.HARD   -> DevX27Theme.colors.diffHard
        Difficulty.ELITE  -> DevX27Theme.colors.diffElite
    }
    Box(
        modifier         = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 6.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(difficulty.label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color)
    }
}
