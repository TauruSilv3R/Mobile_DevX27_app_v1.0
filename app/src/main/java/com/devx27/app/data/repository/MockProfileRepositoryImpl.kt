package com.devx27.app.data.repository

import android.content.Context
import com.devx27.app.data.local.dao.UserDao
import com.devx27.app.domain.model.RecentActivity
import com.devx27.app.domain.model.UserProfile
import com.devx27.app.domain.model.UserStats
import com.devx27.app.domain.model.XPHistoryEntry
import com.devx27.app.domain.repository.ProfileRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Local-only ProfileRepository — reads everything from Room.
 * No network calls; all data lives on the device.
 */
@Singleton
class MockProfileRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userDao: UserDao,
) : ProfileRepository {

    override fun getUserStats(userId: String): Flow<Result<UserStats>> = flow {
        // Load user from Room DB
        val entity = userDao.getUserByUid(userId)

        val profile = if (entity != null) {
            UserProfile(
                uid                  = entity.uid,
                displayName          = entity.displayName,
                email                = entity.email,
                photoUrl             = entity.photoUrl,
                totalXp              = entity.totalXp,
                level                = entity.level,
                challengesSolved     = entity.challengesSolved,
                streak               = entity.streak,
                globalRank           = 0,
                nextLevelXp          = entity.level * 500,
                headline             = entity.headline,
                bio                  = entity.bio,
                location             = entity.location,
                website              = entity.website,
                githubUrl            = entity.githubUrl,
                linkedInUrl          = entity.linkedInUrl,
                company              = entity.company,
                role                 = entity.role,
                education            = entity.education,
                skills               = entity.skills
                    ?.split(",")?.map { it.trim() }?.filter { it.isNotBlank() } ?: emptyList(),
                programmingLanguages = entity.programmingLanguages
                    ?.split(",")?.map { it.trim() }?.filter { it.isNotBlank() } ?: emptyList(),
            )
        } else {
            // Guest / not logged in — show empty state
            UserProfile(
                uid         = userId,
                displayName = "Guest",
                email       = "",
            )
        }

        // XP history — 7 days of zeros (no mock data)
        val calendar  = Calendar.getInstance()
        val dayLabels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        var cumulative = 0
        val history = dayLabels.mapIndexed { i, label ->
            XPHistoryEntry(
                dayLabel     = label,
                dateEpoch    = calendar.apply { add(Calendar.DAY_OF_YEAR, -6 + i) }.timeInMillis,
                xpGained     = 0,
                cumulativeXp = cumulative,
            )
        }

        emit(Result.success(
            UserStats(
                profile        = profile,
                xpHistory      = history,
                recentActivity = emptyList<RecentActivity>(),
            )
        ))
    }
}
