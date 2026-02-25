package com.devx27.app.domain.model

data class LeaderboardEntry(
    val userId: String      = "",
    val displayName: String = "",
    val totalXp: Int        = 0,
    val level: Int          = 1,
    val challengesSolved: Int = 0,
)

data class XPEvent(
    val userId: String      = "",
    val challengeId: String = "",
    val xpGained: Int       = 0,
    val timestamp: Long     = 0L,
)
