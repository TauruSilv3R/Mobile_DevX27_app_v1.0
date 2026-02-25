package com.devx27.app.di

import com.devx27.app.data.repository.AuthRepositoryImpl
import com.devx27.app.data.repository.MockBattleRepositoryImpl
import com.devx27.app.data.repository.MockChallengeRepositoryImpl
import com.devx27.app.data.repository.MockProfileRepositoryImpl
import com.devx27.app.data.repository.MockXPRepositoryImpl
import com.devx27.app.data.repository.SkillTreeRepositoryImpl
import com.devx27.app.domain.repository.AuthRepository
import com.devx27.app.domain.repository.BattleRepository
import com.devx27.app.domain.repository.CodeExecutionRepository
import com.devx27.app.domain.repository.ChallengeRepository
import com.devx27.app.domain.repository.ProfileRepository
import com.devx27.app.domain.repository.SkillTreeRepository
import com.devx27.app.domain.repository.XPRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * RepositoryModule — Phase 4 bindings.
 *
 * MOCK (Phase 2–4):  Challenge, XP, Profile, Battle → Mock impls
 * PRODUCTION (Phase 5): Swap Mocks for Firestore + Firebase Functions impls.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds @Singleton abstract fun bindAuth(impl: com.devx27.app.data.repository.MockAuthRepositoryImpl): AuthRepository
    @Binds @Singleton abstract fun bindChallenge(impl: MockChallengeRepositoryImpl): ChallengeRepository
    @Binds @Singleton abstract fun bindXP(impl: MockXPRepositoryImpl): XPRepository
    @Binds @Singleton abstract fun bindSkillTree(impl: SkillTreeRepositoryImpl): SkillTreeRepository
    @Binds @Singleton abstract fun bindProfile(impl: MockProfileRepositoryImpl): ProfileRepository
    @Binds @Singleton abstract fun bindBattle(impl: MockBattleRepositoryImpl): BattleRepository
    @Binds @Singleton abstract fun bindSettings(impl: com.devx27.app.data.repository.SettingsRepositoryImpl): com.devx27.app.domain.repository.SettingsRepository
    @Binds @Singleton abstract fun bindCodeExecution(impl: com.devx27.app.data.repository.Judge0RepositoryImpl): CodeExecutionRepository
}
