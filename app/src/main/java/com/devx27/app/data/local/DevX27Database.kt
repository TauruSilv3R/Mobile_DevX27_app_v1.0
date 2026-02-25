package com.devx27.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.devx27.app.data.local.dao.UserDao
import com.devx27.app.data.local.entity.UserEntity

@Database(entities = [UserEntity::class], version = 4, exportSchema = false)
abstract class DevX27Database : RoomDatabase() {
    abstract fun userDao(): UserDao
}
