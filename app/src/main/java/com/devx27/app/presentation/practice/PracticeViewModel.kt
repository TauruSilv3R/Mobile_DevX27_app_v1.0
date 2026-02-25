package com.devx27.app.presentation.practice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devx27.app.domain.model.Challenge
import com.devx27.app.domain.model.Difficulty
import com.devx27.app.domain.usecase.GetChallengesUseCase
import com.devx27.app.domain.usecase.GetUserXPUseCase
import kotlinx.coroutines.flow.combine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PracticeUiState(
    val isLoading: Boolean        = true,
    val challenges: List<Challenge> = emptyList(),
    val filteredChallenges: List<Challenge> = emptyList(),
    val solvedChallengeIds: List<String> = emptyList(),
    val selectedDifficulty: Difficulty? = null,
    val searchQuery: String       = "",
    val error: String?            = null,
)

@HiltViewModel
class PracticeViewModel @Inject constructor(
    private val getChallengesUseCase: GetChallengesUseCase,
    private val getUserXPUseCase: GetUserXPUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PracticeUiState())
    val uiState: StateFlow<PracticeUiState> = _uiState.asStateFlow()

    init { loadData() }

    private fun loadData() {
        viewModelScope.launch {
            combine(
                getChallengesUseCase(),
                getUserXPUseCase()
            ) { challengesRes, profileRes ->
                val challenges = challengesRes.getOrNull() ?: emptyList()
                val solvedIds = profileRes.getOrNull()?.solvedChallengeIds ?: emptyList()
                Pair(challenges, solvedIds)
            }.collect { (challenges, solvedIds) ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    challenges = challenges,
                    solvedChallengeIds = solvedIds
                )
                applyFilters()
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        applyFilters()
    }

    fun onDifficultySelected(difficulty: Difficulty?) {
        _uiState.value = _uiState.value.copy(selectedDifficulty = difficulty)
        applyFilters()
    }

    private fun applyFilters() {
        val state = _uiState.value
        _uiState.value = state.copy(
            filteredChallenges = state.challenges.filter { challenge ->
                val matchesQuery = state.searchQuery.isBlank() ||
                    challenge.title.contains(state.searchQuery, ignoreCase = true)
                val matchesDiff  = state.selectedDifficulty == null ||
                    challenge.difficulty == state.selectedDifficulty
                matchesQuery && matchesDiff
            }
        )
    }
}
