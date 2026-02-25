package com.devx27.app.domain.model

// ─────────────────────────────────────────────────────────────────────────────
// Battle domain models — shared across lobby, arena, and repository layer
// ─────────────────────────────────────────────────────────────────────────────

data class BattleOpponent(
    val userId: String,
    val displayName: String,
    val level: Int,
    val totalXp: Int,
    val avatarInitials: String,
)

sealed class BattleState {
    data object Idle                              : BattleState()
    data object Searching                         : BattleState()
    data class  MatchFound(
        val matchId:  String,
        val opponent: BattleOpponent,
    )                                             : BattleState()
    data class  BattleActive(
        val matchId:  String,
        val opponent: BattleOpponent,
        val challengeId: String,
        val challengeTitle: String,
        val durationSeconds: Int,
    )                                             : BattleState()
    data class  BattleEnded(
        val won: Boolean,
        val xpAwarded: Int,
    )                                             : BattleState()
    data class  Error(val message: String)        : BattleState()
}
