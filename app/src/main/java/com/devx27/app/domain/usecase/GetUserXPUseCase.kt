package com.devx27.app.domain.usecase

import com.devx27.app.domain.model.UserProfile
import com.devx27.app.domain.repository.AuthRepository
import com.devx27.app.domain.repository.XPRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserXPUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val xpRepository: XPRepository,
) {
    operator fun invoke(): Flow<Result<UserProfile>> {
        val userId = authRepository.currentUserId
            ?: return kotlinx.coroutines.flow.flow {
                emit(Result.failure(IllegalStateException("User not authenticated")))
            }
        return xpRepository.getUserProfile(userId)
    }
}
