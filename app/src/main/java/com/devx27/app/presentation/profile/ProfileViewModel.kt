package com.devx27.app.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devx27.app.domain.model.UserStats
import com.devx27.app.domain.repository.AuthRepository
import com.devx27.app.domain.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val isLoading: Boolean  = true,
    val stats: UserStats?   = null,
    val error: String?      = null,
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init { loadStats() }

    private fun loadStats() {
        viewModelScope.launch {
            val userId = authRepository.currentUserId ?: "mock-uid"
            profileRepository.getUserStats(userId).collect { result ->
                result.fold(
                    onSuccess = { stats -> _uiState.value = ProfileUiState(isLoading = false, stats = stats) },
                    onFailure = { err  -> _uiState.value = ProfileUiState(isLoading = false, error = err.message) },
                )
            }
        }
    }

    fun signOut() {
        viewModelScope.launch { authRepository.signOut() }
    }

    fun updateProfile(
        displayName: String,
        headline: String?,
        bio: String?,
        location: String?,
        website: String?,
        githubUrl: String?,
        linkedInUrl: String?,
        company: String?,
        role: String?,
        education: String?,
        skills: List<String>,
        programmingLanguages: List<String>,
        photoUrl: String?,
    ) {
        val current = _uiState.value.stats?.profile ?: return
        val updated = current.copy(
            displayName = displayName,
            headline = headline,
            bio = bio,
            location = location,
            website = website,
            githubUrl = githubUrl,
            linkedInUrl = linkedInUrl,
            company = company,
            role = role,
            education = education,
            skills = skills,
            programmingLanguages = programmingLanguages,
            photoUrl = photoUrl,
        )
        // Update local UI state immediately
        _uiState.value = _uiState.value.copy(
            stats = _uiState.value.stats?.copy(profile = updated)
        )
        // Persist to Room database
        viewModelScope.launch {
            authRepository.updateProfile(updated)
        }
    }
}
