package com.devx27.app.presentation.leaderboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devx27.app.domain.model.LeaderboardEntry
import com.devx27.app.domain.repository.AuthRepository
import com.devx27.app.domain.repository.XPRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LeaderboardUiState(
    val isLoading: Boolean              = true,
    val entries: List<LeaderboardEntry> = emptyList(),
    val currentUserId: String?          = null,
    val currentUserRank: Int?           = null,   // null = not in top 50
    val error: String?                  = null,
)

@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    private val xpRepository: XPRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LeaderboardUiState())
    val uiState: StateFlow<LeaderboardUiState> = _uiState.asStateFlow()

    init { loadLeaderboard() }

    private fun loadLeaderboard() {
        val currentUserId = authRepository.currentUserId

        viewModelScope.launch {
            // Fetch top 50 globally ordered by total_xp descending
            // (Firestore query: collection("users").orderBy("totalXp", DESCENDING).limit(50))
            xpRepository.getLeaderboard(limit = 50)
                .collect { result ->
                    result.fold(
                        onSuccess = { entries ->
                            val rank = entries.indexOfFirst { it.userId == currentUserId }
                                .takeIf { it >= 0 }
                                ?.let { it + 1 }  // convert 0-index to 1-based rank

                            _uiState.value = LeaderboardUiState(
                                isLoading       = false,
                                entries         = entries,
                                currentUserId   = currentUserId,
                                currentUserRank = rank,
                            )
                        },
                        onFailure = { err ->
                            _uiState.value = LeaderboardUiState(isLoading = false, error = err.message)
                        }
                    )
                }
        }
    }
}
