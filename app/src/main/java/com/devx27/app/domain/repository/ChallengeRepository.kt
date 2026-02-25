package com.devx27.app.domain.repository

import com.devx27.app.domain.model.Challenge
import kotlinx.coroutines.flow.Flow

interface ChallengeRepository {
    fun getChallenges(): Flow<Result<List<Challenge>>>
    fun getChallengeById(id: String): Flow<Result<Challenge>>
    suspend fun submitSolution(challengeId: String, code: String, language: String): Result<SubmissionResult>
}

data class SubmissionResult(
    val passed: Boolean,
    val xpAwarded: Int,
    val feedback: String,
    val executionMs: Long,
)
