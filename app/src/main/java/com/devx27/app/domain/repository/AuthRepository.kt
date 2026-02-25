package com.devx27.app.domain.repository

import com.devx27.app.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUserId: String?
    val isAuthenticated: Boolean

    suspend fun signIn(email: String, password: String): Result<UserProfile>
    suspend fun signUp(email: String, password: String, displayName: String): Result<UserProfile>
    suspend fun updateProfile(profile: UserProfile)
    suspend fun getCurrentProfile(): UserProfile?
    suspend fun signOut()
    fun observeAuthState(): Flow<Boolean>
}
