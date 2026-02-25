package com.devx27.app.domain.usecase

import com.devx27.app.domain.model.Challenge
import com.devx27.app.domain.repository.ChallengeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetChallengesUseCase @Inject constructor(
    private val repository: ChallengeRepository,
) {
    operator fun invoke(): Flow<Result<List<Challenge>>> = repository.getChallenges()
}
