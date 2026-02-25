package com.devx27.app.domain.usecase

import com.devx27.app.domain.repository.ChallengeRepository
import com.devx27.app.domain.repository.SubmissionResult
import com.devx27.app.domain.repository.XPRepository
import com.devx27.app.domain.repository.AuthRepository
import javax.inject.Inject

class SubmitSolutionUseCase @Inject constructor(
    private val challengeRepository: ChallengeRepository,
    private val xpRepository: XPRepository,
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(
        challengeId: String,
        code: String,
        language: String,
    ): Result<SubmissionResult> {
        val result = challengeRepository.submitSolution(challengeId, code, language)
        result.onSuccess { submission ->
            if (submission.passed) {
                val userId = authRepository.currentUserId ?: return result
                xpRepository.awardXP(userId, submission.xpAwarded, challengeId)
            }
        }
        return result
    }
}
