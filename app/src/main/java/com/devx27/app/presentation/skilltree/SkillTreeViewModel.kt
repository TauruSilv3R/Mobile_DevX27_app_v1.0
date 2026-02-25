package com.devx27.app.presentation.skilltree

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devx27.app.domain.model.SkillGraph
import com.devx27.app.domain.model.SkillNode
import com.devx27.app.domain.repository.SkillTreeRepository
import com.devx27.app.domain.usecase.GetUserXPUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SkillTreeUiState(
    val isLoading: Boolean              = true,
    val graph: SkillGraph?              = null,
    val selectedNode: SkillNode?        = null,
    val userXp: Int                     = 0,
    val error: String?                  = null,
)

@HiltViewModel
class SkillTreeViewModel @Inject constructor(
    private val getUserXPUseCase: GetUserXPUseCase,
    private val skillTreeRepository: SkillTreeRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SkillTreeUiState())
    val uiState: StateFlow<SkillTreeUiState> = _uiState.asStateFlow()

    init { observeXpAndBuildGraph() }

    private fun observeXpAndBuildGraph() {
        viewModelScope.launch {
            getUserXPUseCase().collect { result ->
                result.fold(
                    onSuccess = { profile ->
                        val graph = skillTreeRepository.getSkillGraph(profile.totalXp)
                        _uiState.value = SkillTreeUiState(
                            isLoading = false,
                            graph     = graph,
                            userXp    = profile.totalXp,
                        )
                    },
                    onFailure = { err ->
                        _uiState.value = SkillTreeUiState(isLoading = false, error = err.message)
                    }
                )
            }
        }
    }

    fun onNodeSelected(node: SkillNode?) {
        _uiState.value = _uiState.value.copy(selectedNode = node)
    }
}
