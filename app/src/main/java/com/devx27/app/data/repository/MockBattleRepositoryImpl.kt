package com.devx27.app.data.repository

import com.devx27.app.domain.model.BattleOpponent
import com.devx27.app.domain.model.BattleState
import com.devx27.app.domain.repository.BattleRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class MockBattleRepositoryImpl @Inject constructor() : BattleRepository {

    override fun findMatch(userId: String): Flow<BattleState> = flow {
        // ① Emit searching state
        emit(BattleState.Searching)

        // ② Simulate matchmaking delay (2–4 seconds)
        delay((2000..4000L).random())

        // ③ Opponent found
        val opponent = BattleOpponent(
            userId         = "opponent-${Random.nextInt(1000)}",
            displayName    = listOf("Wei Zhang", "Sofia Rossi", "Marcus Johnson", "Aya Nakamura").random(),
            level          = (3..8).random(),
            totalXp        = (1500..4000).random(),
            avatarInitials = "WZ",
        )
        emit(BattleState.MatchFound(matchId = "battle-001", opponent = opponent))

        // ④ Brief "Match Found!" display, then enter arena
        delay(2500)
        emit(BattleState.BattleActive(
            matchId        = "battle-001",
            opponent       = opponent,
            challengeId    = "c1",
            challengeTitle = "Two Sum",
            durationSeconds = 300,          // 5-minute battle
        ))
    }

    override fun getOpponentProgress(matchId: String): Flow<Float> = flow {
        // Simulate opponent typing — slow, irregular advancement
        var progress = 0f
        while (progress < 1f) {
            delay((800..2200L).random())
            progress = (progress + (0.03f..0.12f).random()).coerceAtMost(0.95f)
            emit(progress)
        }
    }

    override suspend fun cancelSearch(userId: String) {
        // No-op in mock
    }

    override suspend fun submitBattle(matchId: String, code: String, language: String): Result<Unit> {
        delay(1000)
        return Result.success(Unit)
    }
}

// Helper extensions for ranges
private fun ClosedRange<Long>.random(): Long =
    (endInclusive - start).let { (Math.random() * it).toLong() + start }

private fun ClosedRange<Float>.random(): Float =
    (endInclusive - start).let { (Math.random() * it).toFloat() + start }
