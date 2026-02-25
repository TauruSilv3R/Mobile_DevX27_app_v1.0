package com.devx27.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val email: String,
    val password: String,
    val uid: String,
    val displayName: String,
    val totalXp: Int = 0,
    val level: Int = 1,
    val challengesSolved: Int = 0,
    val streak: Int = 0,
    val photoUrl: String? = null,
    // LinkedIn-style profile fields
    val headline: String? = null,
    val bio: String? = null,
    val location: String? = null,
    val website: String? = null,
    val githubUrl: String? = null,
    val linkedInUrl: String? = null,
    val company: String? = null,
    val role: String? = null,
    val education: String? = null,
    val skills: String? = null,              // CSV: "Python,Kotlin,AWS"
    val programmingLanguages: String? = null, // CSV: "Python,Kotlin"
    val solvedChallengeIds: String? = null    // CSV: "c1,c2,c3"
)
