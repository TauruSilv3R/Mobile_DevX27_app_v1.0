package com.devx27.app.data.repository

import com.devx27.app.domain.model.UserProfile
import com.devx27.app.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
) : AuthRepository {

    override val currentUserId: String?
        get() = firebaseAuth.currentUser?.uid

    override val isAuthenticated: Boolean
        get() = firebaseAuth.currentUser != null

    override suspend fun signIn(email: String, password: String): Result<UserProfile> = runCatching {
        val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
        val user   = result.user ?: error("Firebase returned null user")
        UserProfile(uid = user.uid, displayName = user.displayName ?: "", email = user.email ?: "")
    }

    override suspend fun signUp(email: String, password: String, displayName: String): Result<UserProfile> = runCatching {
        val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        val user   = result.user ?: error("Firebase returned null user")
        user.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(displayName).build()).await()
        UserProfile(uid = user.uid, displayName = displayName, email = email)
    }

    override suspend fun updateProfile(profile: UserProfile) {
        // Firebase profile update — update displayName in Firebase Auth
        val user = firebaseAuth.currentUser ?: return
        val request = UserProfileChangeRequest.Builder()
            .setDisplayName(profile.displayName)
            .build()
        user.updateProfile(request).await()
    }

    override suspend fun getCurrentProfile(): UserProfile? {
        val user = firebaseAuth.currentUser ?: return null
        return UserProfile(
            uid = user.uid,
            displayName = user.displayName ?: "",
            email = user.email ?: ""
        )
    }

    override suspend fun signOut() {
        firebaseAuth.signOut()
    }

    override fun observeAuthState(): Flow<Boolean> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser != null)
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }
}
