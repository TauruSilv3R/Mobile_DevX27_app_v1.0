package com.devx27.app.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devx27.app.domain.model.UserProfile
import com.devx27.app.domain.usecase.GetUserXPUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val isLoading: Boolean     = true,
    val userProfile: UserProfile? = null,
    val recentXpGain: Int      = 0,
    val weeklyStreak: Int      = 0,
    val error: String?         = null,
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getUserXPUseCase: GetUserXPUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init { loadDashboard() }

    private fun loadDashboard() {
        viewModelScope.launch {
            getUserXPUseCase()
                .collect { result ->
                    result.fold(
                        onSuccess = { profile ->
                            _uiState.value = DashboardUiState(
                                isLoading    = false,
                                userProfile  = profile,
                                weeklyStreak = profile.streak,
                            )
                        },
                        onFailure = { err ->
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                error     = err.message,
                            )
                        }
                    )
                }
        }
    }

    fun refresh() { loadDashboard() }
}
