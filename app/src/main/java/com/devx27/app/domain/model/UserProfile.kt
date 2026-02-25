package com.devx27.app.domain.model

data class UserProfile(
    val uid: String               = "",
    val displayName: String       = "",
    val email: String             = "",
    val photoUrl: String?         = null,
    // LinkedIn-style fields
    val bio: String?              = null,      // About/summary
    val headline: String?         = null,      // "Software Engineer @ Google"
    val location: String?         = null,      // "San Francisco, CA"
    val website: String?          = null,      // personal website
    val githubUrl: String?        = null,
    val linkedInUrl: String?      = null,
    val company: String?          = null,
    val role: String?             = null,      // current job title
    val skills: List<String>      = emptyList(),
    val programmingLanguages: List<String> = emptyList(),
    val education: String?        = null,
    // Gamification stats
    val totalXp: Int              = 0,
    val level: Int                = 1,
    val nextLevelXp: Int          = 1000,
    val challengesSolved: Int     = 0,
    val solvedChallengeIds: List<String> = emptyList(),
    val streak: Int               = 0,
    val globalRank: Int           = 0,
    val joinedAt: Long            = 0L,
)
