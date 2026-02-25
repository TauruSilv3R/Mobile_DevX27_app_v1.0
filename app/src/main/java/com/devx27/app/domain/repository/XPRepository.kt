package com.devx27.app.domain.repository

import com.devx27.app.domain.model.LeaderboardEntry
import com.devx27.app.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

/**
 * XPRepository is the source of truth for all XP-related data.
 * Backed by a real-time Firestore listener so UI state updates
 * automatically when XP changes server-side.
 */
interface XPRepository {
    /** Real-time stream of the current user's profile (XP, level, streak). */
    fun getUserProfile(userId: String): Flow<Result<UserProfile>>

    /** Real-time global leaderboard, ordered by totalXp desc. */
    fun getLeaderboard(limit: Int = 50): Flow<Result<List<LeaderboardEntry>>>

    /** Award XP after a successful submission — called after ChallengeRepository.submitSolution(). */
    suspend fun awardXP(userId: String, xpToAdd: Int, challengeId: String): Result<Unit>
}
