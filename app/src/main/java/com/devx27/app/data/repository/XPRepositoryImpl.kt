package com.devx27.app.data.repository

import com.devx27.app.domain.model.LeaderboardEntry
import com.devx27.app.domain.model.UserProfile
import com.devx27.app.domain.repository.XPRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class XPRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
) : XPRepository {

    override fun getUserProfile(userId: String): Flow<Result<UserProfile>> = callbackFlow {
        val docRef = firestore.collection("users").document(userId)
        val listener = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(Result.failure(error))
                return@addSnapshotListener
            }
            val profile = snapshot?.let { doc ->
                UserProfile(
                    uid               = doc.id,
                    displayName       = doc.getString("displayName") ?: "",
                    email             = doc.getString("email") ?: "",
                    totalXp           = doc.getLong("totalXp")?.toInt() ?: 0,
                    level             = doc.getLong("level")?.toInt() ?: 1,
                    nextLevelXp       = doc.getLong("nextLevelXp")?.toInt() ?: 1000,
                    challengesSolved  = doc.getLong("challengesSolved")?.toInt() ?: 0,
                    streak            = doc.getLong("streak")?.toInt() ?: 0,
                    globalRank        = doc.getLong("globalRank")?.toInt() ?: 0,
                )
            } ?: UserProfile()
            trySend(Result.success(profile))
        }
        awaitClose { listener.remove() }
    }

    override fun getLeaderboard(limit: Int): Flow<Result<List<LeaderboardEntry>>> = callbackFlow {
        val query = firestore.collection("users")
            .orderBy("totalXp", Query.Direction.DESCENDING)
            .limit(limit.toLong())

        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(Result.failure(error))
                return@addSnapshotListener
            }
            val entries = snapshot?.documents?.map { doc ->
                LeaderboardEntry(
                    userId          = doc.id,
                    displayName     = doc.getString("displayName") ?: "Anonymous",
                    totalXp         = doc.getLong("totalXp")?.toInt() ?: 0,
                    level           = doc.getLong("level")?.toInt() ?: 1,
                    challengesSolved = doc.getLong("challengesSolved")?.toInt() ?: 0,
                )
            } ?: emptyList()
            trySend(Result.success(entries))
        }
        awaitClose { listener.remove() }
    }

    override suspend fun awardXP(userId: String, xpToAdd: Int, challengeId: String): Result<Unit> = runCatching {
        val userRef = firestore.collection("users").document(userId)
        firestore.runTransaction { tx ->
            val snap    = tx.get(userRef)
            val current = snap.getLong("totalXp")?.toInt() ?: 0
            val newXp   = current + xpToAdd

            // Level thresholds: each level requires level * 500 XP
            val newLevel    = calculateLevel(newXp)
            val nextLevelXp = newLevel * 500

            tx.update(userRef, mapOf(
                "totalXp"    to newXp,
                "level"      to newLevel,
                "nextLevelXp" to nextLevelXp,
                "challengesSolved" to com.google.firebase.firestore.FieldValue.increment(1),
            ))

            // Log XP event
            firestore.collection("xp_events").add(mapOf(
                "userId"      to userId,
                "challengeId" to challengeId,
                "xpGained"   to xpToAdd,
                "timestamp"  to com.google.firebase.firestore.FieldValue.serverTimestamp(),
            ))
        }.await()
    }

    private fun calculateLevel(totalXp: Int): Int {
        var level = 1
        var threshold = 500
        var accumulated = 0
        while (accumulated + threshold <= totalXp) {
            accumulated += threshold
            level++
            threshold = level * 500
        }
        return level
    }
}
