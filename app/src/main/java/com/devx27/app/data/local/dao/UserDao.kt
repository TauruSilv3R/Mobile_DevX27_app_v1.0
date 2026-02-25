package com.devx27.app.data.local.dao

import androidx.room.*
import com.devx27.app.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE uid = :uid LIMIT 1")
    suspend fun getUserByUid(uid: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("DELETE FROM users WHERE uid = :uid")
    suspend fun deleteUserByUid(uid: String)

    @Query("SELECT * FROM users WHERE uid = :uid LIMIT 1")
    fun getUserFlowByUid(uid: String): Flow<UserEntity?>

    @Query("SELECT * FROM users ORDER BY totalXp DESC LIMIT :limit")
    fun getTopUsersFlow(limit: Int): Flow<List<UserEntity>>
}
