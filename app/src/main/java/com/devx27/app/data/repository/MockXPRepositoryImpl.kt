package com.devx27.app.data.repository

import com.devx27.app.domain.model.LeaderboardEntry
import com.devx27.app.domain.model.UserProfile
import com.devx27.app.domain.repository.XPRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import com.devx27.app.data.local.dao.UserDao
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.devx27.app.data.local.entity.UserEntity

@Singleton
class MockXPRepositoryImpl @Inject constructor(
    private val userDao: UserDao
) : XPRepository {
    
    init {
        CoroutineScope(Dispatchers.IO).launch {
            if (userDao.getUserByUid("u1") == null) {
                val seedData = listOf(
                    UserEntity("u1", "Arjun Sharma", "arj@example.com", "pass", totalXp = 4200, level = 9, challengesSolved = 38),
                    UserEntity("u2", "Priya Mehta", "priya@example.com", "pass", totalXp = 3950, level = 8, challengesSolved = 35),
                    UserEntity("u3", "Wei Zhang", "wei@example.com", "pass", totalXp = 3800, level = 8, challengesSolved = 33),
                    UserEntity("u4", "Sofia Rossi", "sofia@example.com", "pass", totalXp = 3500, level = 7, challengesSolved = 30),
                    UserEntity("u5", "Marcus Johnson", "marcus@example.com", "pass", totalXp = 3200, level = 7, challengesSolved = 27),
                    UserEntity("u6", "Aya Nakamura", "aya@example.com", "pass", totalXp = 2900, level = 6, challengesSolved = 24),
                    UserEntity("u7", "Lena Müller", "lena@example.com", "pass", totalXp = 2400, level = 5, challengesSolved = 19),
                    UserEntity("u8", "Carlos Rivera", "carlos@example.com", "pass", totalXp = 2100, level = 5, challengesSolved = 17),
                    UserEntity("u9", "Yuna Kim", "yuna@example.com", "pass", totalXp = 1800, level = 4, challengesSolved = 14)
                )
                seedData.forEach { userDao.insertUser(it) }
            }
        }
    }

    override fun getUserProfile(userId: String): Flow<Result<UserProfile>> {
        return userDao.getUserFlowByUid(userId).map { user ->
            if (user != null) {
                Result.success(UserProfile(
                    uid              = user.uid,
                    displayName      = user.displayName,
                    email            = user.email,
                    photoUrl         = user.photoUrl,
                    totalXp          = user.totalXp,
                    level            = user.level,
                    nextLevelXp      = user.level * 500,
                    challengesSolved = user.challengesSolved,
                    solvedChallengeIds = user.solvedChallengeIds?.split(",")?.filter { it.isNotBlank() } ?: emptyList(),
                    streak           = user.streak,
                    globalRank       = 42,
                ))
            } else {
                Result.failure(Exception("User not found"))
            }
        }
    }

    override fun getLeaderboard(limit: Int): Flow<Result<List<LeaderboardEntry>>> {
        return userDao.getTopUsersFlow(limit).map { topUsers ->
            val entries = topUsers.map { user ->
                LeaderboardEntry(
                    userId = user.uid,
                    displayName = user.displayName,
                    totalXp = user.totalXp,
                    level = user.level,
                    challengesSolved = user.challengesSolved
                )
            }
            Result.success(entries)
        }
    }

    override suspend fun awardXP(userId: String, xpToAdd: Int, challengeId: String): Result<Unit> {
        val user = userDao.getUserByUid(userId)
        if (user != null) {
            val updatedXp = user.totalXp + xpToAdd
            val updatedLevel = calculateLevel(updatedXp)
            
            val currentSolved = user.solvedChallengeIds?.split(",")?.filter { it.isNotBlank() }?.toMutableList() ?: mutableListOf()
            if (!currentSolved.contains(challengeId)) {
                currentSolved.add(challengeId)
            }
            val newSolvedString = currentSolved.joinToString(",")

            val updatedUser = user.copy(
                totalXp = updatedXp,
                level = updatedLevel,
                challengesSolved = currentSolved.size,
                solvedChallengeIds = newSolvedString
            )
            userDao.updateUser(updatedUser)
        }
        return Result.success(Unit)
    }

    private fun calculateLevel(totalXp: Int): Int {
        var level = 1; var threshold = 500; var accumulated = 0
        while (accumulated + threshold <= totalXp) { accumulated += threshold; level++; threshold = level * 500 }
        return level
    }
}
