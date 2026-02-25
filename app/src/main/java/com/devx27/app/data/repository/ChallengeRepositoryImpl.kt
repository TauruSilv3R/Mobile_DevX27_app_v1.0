package com.devx27.app.data.repository

import com.devx27.app.domain.model.Challenge
import com.devx27.app.domain.model.Difficulty
import com.devx27.app.domain.model.TestCase
import com.devx27.app.domain.repository.ChallengeRepository
import com.devx27.app.domain.repository.SubmissionResult
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChallengeRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val functions: FirebaseFunctions,
) : ChallengeRepository {

    override fun getChallenges(): Flow<Result<List<Challenge>>> = callbackFlow {
        val listener = firestore.collection("challenges")
            .addSnapshotListener { snapshot, error ->
                if (error != null) { trySend(Result.failure(error)); return@addSnapshotListener }
                val challenges = snapshot?.documents?.map { doc ->
                    Challenge(
                        id          = doc.id,
                        title       = doc.getString("title") ?: "",
                        description = doc.getString("description") ?: "",
                        difficulty  = Difficulty.valueOf(doc.getString("difficulty") ?: "EASY"),
                        category    = doc.getString("category") ?: "",
                        xpReward    = doc.getLong("xpReward")?.toInt() ?: 0,
                        timeLimit   = doc.getLong("timeLimit")?.toInt() ?: 0,
                        starterCode = doc.getString("starterCode") ?: "",
                        solveCount  = doc.getLong("solveCount")?.toInt() ?: 0,
                        successRate = doc.getDouble("successRate")?.toFloat() ?: 0f,
                        tags        = (doc.get("tags") as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
                    )
                } ?: emptyList()
                trySend(Result.success(challenges))
            }
        awaitClose { listener.remove() }
    }

    override fun getChallengeById(id: String): Flow<Result<Challenge>> = callbackFlow {
        val listener = firestore.collection("challenges").document(id)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { trySend(Result.failure(error)); return@addSnapshotListener }
                val challenge = snapshot?.let { doc ->
                    Challenge(
                        id          = doc.id,
                        title       = doc.getString("title") ?: "",
                        description = doc.getString("description") ?: "",
                        difficulty  = Difficulty.valueOf(doc.getString("difficulty") ?: "EASY"),
                        category    = doc.getString("category") ?: "",
                        xpReward    = doc.getLong("xpReward")?.toInt() ?: 0,
                        starterCode = doc.getString("starterCode") ?: "",
                    )
                } ?: return@addSnapshotListener
                trySend(Result.success(challenge))
            }
        awaitClose { listener.remove() }
    }

    override suspend fun submitSolution(
        challengeId: String,
        code: String,
        language: String,
    ): Result<SubmissionResult> = runCatching {
        // Calls Firebase Cloud Function "evaluateSolution"
        val data = hashMapOf("challengeId" to challengeId, "code" to code, "language" to language)
        val result = functions.getHttpsCallable("evaluateSolution").call(data).await()
        val map = result.getData() as? Map<*, *> ?: error("Invalid response from function")
        SubmissionResult(
            passed      = map["passed"] as? Boolean ?: false,
            xpAwarded   = (map["xpAwarded"] as? Number)?.toInt() ?: 0,
            feedback    = map["feedback"] as? String ?: "",
            executionMs = (map["executionMs"] as? Number)?.toLong() ?: 0L,
        )
    }
}
