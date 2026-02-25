package com.devx27.app.domain.model

import androidx.compose.ui.graphics.Color
import com.devx27.app.presentation.theme.DifficultyEasy
import com.devx27.app.presentation.theme.DifficultyElite
import com.devx27.app.presentation.theme.DifficultyHard
import com.devx27.app.presentation.theme.DifficultyMedium

data class Challenge(
    val id: String          = "",
    val title: String       = "",
    val description: String = "",
    val difficulty: Difficulty = Difficulty.EASY,
    val category: String    = "",
    val xpReward: Int       = 0,
    val timeLimit: Int      = 0,          // seconds; 0 = untimed
    val starterCode: String = "",
    val testCases: List<TestCase> = emptyList(),
    val tags: List<String>  = emptyList(),
    val solveCount: Int     = 0,
    val successRate: Float  = 0f,
)

data class TestCase(
    val input: String       = "",
    val expectedOutput: String = "",
    val isHidden: Boolean   = false,
)

enum class Difficulty(val label: String, val color: Color) {
    EASY("Easy", DifficultyEasy),
    MEDIUM("Medium", DifficultyMedium),
    HARD("Hard", DifficultyHard),
    ELITE("Elite", DifficultyElite),
}
