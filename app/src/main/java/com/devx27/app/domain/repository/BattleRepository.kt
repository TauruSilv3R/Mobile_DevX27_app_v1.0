package com.devx27.app.domain.repository

import com.devx27.app.domain.model.BattleState
import kotlinx.coroutines.flow.Flow

// ─────────────────────────────────────────────────────────────────────────────
// BattleRepository — manages the real-time battle lifecycle.
// In production, this uses a Firestore document at:
//   battles/{matchId} { status, player1, player2, challengeId, ... }
// ─────────────────────────────────────────────────────────────────────────────
interface BattleRepository {
    /**
     * Starts matchmaking and emits state transitions:
     * Searching → MatchFound → BattleActive → BattleEnded
     */
    fun findMatch(userId: String): Flow<BattleState>

    /**
     * Real-time opponent progress: emits a Float 0..1 representing how
     * close they are to completing the challenge (test cases passed / total).
     */
    fun getOpponentProgress(matchId: String): Flow<Float>

    /** Cancel matchmaking while in Searching state. */
    suspend fun cancelSearch(userId: String)

    /** Submit code for the active battle — ends the battle on success. */
    suspend fun submitBattle(matchId: String, code: String, language: String): Result<Unit>
}
