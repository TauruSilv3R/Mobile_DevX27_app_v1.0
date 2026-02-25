package com.devx27.app.data.repository

import com.devx27.app.domain.model.Challenge
import com.devx27.app.domain.model.Difficulty
import com.devx27.app.domain.repository.ChallengeRepository
import com.devx27.app.domain.repository.SubmissionResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

// ─────────────────────────────────────────────────────────────────────────────
// MockChallengeRepositoryImpl — offline-first mock used until Firebase is live.
// Swap this binding in RepositoryModule for ChallengeRepositoryImpl once the
// google-services.json is real.
// ─────────────────────────────────────────────────────────────────────────────
@Singleton
class MockChallengeRepositoryImpl @Inject constructor() : ChallengeRepository {

    private val mockChallenges = listOf(
        Challenge(id = "c1", title = "Two Sum",               difficulty = Difficulty.EASY,   category = "Arrays",     xpReward = 50,  starterCode = twoSumPython),
        Challenge(id = "c2", title = "Valid Parentheses",     difficulty = Difficulty.EASY,   category = "Stack",      xpReward = 60,  starterCode = parenPython),
    ) + PythonChallenges.list.filter { it.id != "p1" } // Avoid duplicate p1/c1 "Two Sum" if needed, or just combine


    override fun getChallenges(): Flow<Result<List<Challenge>>> = flow {
        delay(400) // Simulate network
        emit(Result.success(mockChallenges))
    }

    override fun getChallengeById(id: String): Flow<Result<Challenge>> = flow {
        delay(200)
        val challenge = mockChallenges.find { it.id == id }
        if (challenge != null) emit(Result.success(challenge))
        else emit(Result.failure(NoSuchElementException("Challenge $id not found")))
    }

    override suspend fun submitSolution(
        challengeId: String,
        code: String,
        language: String,
    ): Result<SubmissionResult> {
        delay(1500) // Simulate evaluation time
        // Mock logic: if code contains "pass" or is very short → fail
        val passed = code.length > 30 && !code.contains("pass") && !code.contains("return 0")
        return if (passed) {
            val xp = mockChallenges.find { it.id == challengeId }?.xpReward ?: 50
            Result.success(SubmissionResult(passed = true, xpAwarded = xp, feedback = "All 5/5 test cases passed.", executionMs = (80..350L).random()))
        } else {
            Result.success(SubmissionResult(passed = false, xpAwarded = 0, feedback = "Failed test case 2: expected [0,1] but got [].", executionMs = 0))
        }
    }

    // ── Starter code templates ────────────────────────────────────────────────
    companion object {
        private val twoSumPython = """
            |def two_sum(nums: list[int], target: int) -> list[int]:
            |    seen = {}
            |    for i, num in enumerate(nums):
            |        complement = target - num
            |        if complement in seen:
            |            return [seen[complement], i]
            |        seen[num] = i
            |    return []
        """.trimMargin()

        private val parenPython = """
            |def is_valid(s: str) -> bool:
            |    stack = []
            |    pairs = {')': '(', '}': '{', ']': '['}
            |    for ch in s:
            |        if ch in '({[':
            |            stack.append(ch)
            |        elif not stack or stack[-1] != pairs[ch]:
            |            return False
            |        else:
            |            stack.pop()
            |    return not stack
        """.trimMargin()
    }
}

// Helper to generate a random Long in a range
private fun ClosedRange<Long>.random(): Long =
    (endInclusive - start).let { (Math.random() * it).toLong() + start }
