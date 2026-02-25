package com.devx27.app.di

import android.content.Context
import androidx.room.Room
import com.devx27.app.data.local.DevX27Database
import com.devx27.app.data.local.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): DevX27Database {
        return Room.databaseBuilder(
            context,
            DevX27Database::class.java,
            "devx27_database"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideUserDao(database: DevX27Database): UserDao {
        return database.userDao()
    }
}
