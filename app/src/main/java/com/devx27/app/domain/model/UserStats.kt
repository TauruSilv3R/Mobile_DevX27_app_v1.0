package com.devx27.app.domain.model

data class XPHistoryEntry(
    val dayLabel: String,       // "Mon", "Tue", etc.
    val dateEpoch: Long,        // epoch millis
    val xpGained: Int,          // XP earned on that day
    val cumulativeXp: Int,      // Running total
)

data class RecentActivity(
    val challengeId: String,
    val title: String,
    val difficulty: Difficulty,
    val xpGained: Int,
    val passed: Boolean,
    val completedAt: Long,      // epoch millis
    val language: String,
)

data class UserStats(
    val profile: UserProfile,
    val xpHistory: List<XPHistoryEntry>,    // Last 7 days
    val recentActivity: List<RecentActivity>, // Last 5 completions
)
