package com.devx27.app.data.repository

import android.content.Context
import com.devx27.app.data.local.dao.UserDao
import com.devx27.app.data.local.entity.UserEntity
import com.devx27.app.domain.model.UserProfile
import com.devx27.app.domain.repository.AuthRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockAuthRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userDao: UserDao
) : AuthRepository {

    private val prefs = context.getSharedPreferences("mock_auth_prefs", Context.MODE_PRIVATE)
    
    private val _authState = MutableStateFlow(prefs.getString("current_uid", null) != null)
    
    override val currentUserId: String?
        get() = prefs.getString("current_uid", null)

    override val isAuthenticated: Boolean
        get() = currentUserId != null

    override suspend fun signIn(email: String, password: String): Result<UserProfile> {
        val userEntity = userDao.getUserByEmail(email)
        
        if (userEntity != null && userEntity.password == password) {
            val profile = userEntity.toProfile()
            loginSuccess(profile)
            return Result.success(profile)
        }
        
        return Result.failure(Exception("Invalid credentials"))
    }

    override suspend fun signUp(email: String, password: String, displayName: String): Result<UserProfile> {
        if (userDao.getUserByEmail(email) != null) {
            return Result.failure(Exception("User already exists"))
        }
        
        val newUid = "mock-uid-${System.currentTimeMillis()}"
        val userEntity = UserEntity(
            email = email,
            password = password,
            uid = newUid,
            displayName = displayName
        )
        userDao.insertUser(userEntity)
        
        val userProfile = UserProfile(uid = newUid, displayName = displayName, email = email)
        loginSuccess(userProfile)
        return Result.success(userProfile)
    }

    override suspend fun updateProfile(profile: UserProfile) {
        val uid = currentUserId ?: return
        val existing = userDao.getUserByUid(uid) ?: return
        val updated = existing.copy(
            displayName         = profile.displayName,
            headline            = profile.headline,
            bio                 = profile.bio,
            location            = profile.location,
            website             = profile.website,
            githubUrl           = profile.githubUrl,
            linkedInUrl         = profile.linkedInUrl,
            company             = profile.company,
            role                = profile.role,
            education           = profile.education,
            skills              = profile.skills.joinToString(","),
            programmingLanguages = profile.programmingLanguages.joinToString(","),
        )
        userDao.updateUser(updated)
    }

    override suspend fun getCurrentProfile(): UserProfile? {
        val uid = currentUserId ?: return null
        return userDao.getUserByUid(uid)?.toProfile()
    }

    override suspend fun signOut() {
        prefs.edit().remove("current_uid").apply()
        _authState.value = false
    }

    override fun observeAuthState(): Flow<Boolean> = _authState.asStateFlow()

    private fun loginSuccess(user: UserProfile) {
        prefs.edit().putString("current_uid", user.uid).apply()
        _authState.value = true
    }

    private fun UserEntity.toProfile() = UserProfile(
        uid                  = uid,
        displayName          = displayName,
        email                = email,
        photoUrl             = photoUrl,
        totalXp              = totalXp,
        level                = level,
        challengesSolved     = challengesSolved,
        streak               = streak,
        headline             = headline,
        bio                  = bio,
        location             = location,
        website              = website,
        githubUrl            = githubUrl,
        linkedInUrl          = linkedInUrl,
        company              = company,
        role                 = role,
        education            = education,
        skills               = skills?.split(",")?.filter { it.isNotBlank() } ?: emptyList(),
        programmingLanguages = programmingLanguages?.split(",")?.filter { it.isNotBlank() } ?: emptyList(),
    )
}
