package com.devx27.app.domain.repository

import com.devx27.app.domain.model.UserStats
import kotlinx.coroutines.flow.Flow

// ─────────────────────────────────────────────────────────────────────────────
// ProfileRepository — provides full user stats including XP history and
// recent activity feed. Backed by Firestore in production, mock in Phase 4.
// ─────────────────────────────────────────────────────────────────────────────
interface ProfileRepository {
    fun getUserStats(userId: String): Flow<Result<UserStats>>
}
