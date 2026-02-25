package com.devx27.app.presentation.battle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devx27.app.domain.model.BattleOpponent
import com.devx27.app.domain.model.BattleState
import com.devx27.app.domain.repository.AuthRepository
import com.devx27.app.domain.repository.BattleRepository
import com.devx27.app.presentation.editor.SyntaxHighlighter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BattleUiState(
    val battleState: BattleState            = BattleState.Idle,
    val opponentProgress: Float             = 0f,
    val myProgress: Float                   = 0f,
    val timeRemainingSeconds: Int           = 300,
    val code: androidx.compose.ui.text.input.TextFieldValue = androidx.compose.ui.text.input.TextFieldValue(""),
    val language: SyntaxHighlighter.Language = SyntaxHighlighter.Language.PYTHON,
    val isSubmitting: Boolean               = false,
    val matchFound: Boolean                 = false,   // triggers haptic
)

@HiltViewModel
class BattleViewModel @Inject constructor(
    private val battleRepository: BattleRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState  = MutableStateFlow(BattleUiState())
    val uiState: StateFlow<BattleUiState> = _uiState.asStateFlow()

    private var timerJob:    Job? = null
    private var opponentJob: Job? = null

    // ── Public actions ────────────────────────────────────────────────────────

    fun startSearch() {
        val userId = authRepository.currentUserId ?: "mock-uid"
        viewModelScope.launch {
            battleRepository.findMatch(userId).collect { state ->
                _uiState.value = _uiState.value.copy(
                    battleState = state,
                    matchFound  = state is BattleState.MatchFound,
                )
                if (state is BattleState.BattleActive) {
                    startCountdown(state.durationSeconds)
                    startOpponentTracking(state.matchId)
                }
            }
        }
    }

    fun cancelSearch() {
        viewModelScope.launch {
            battleRepository.cancelSearch(authRepository.currentUserId ?: "")
            _uiState.value = BattleUiState()
        }
    }

    fun onCodeChanged(value: androidx.compose.ui.text.input.TextFieldValue) {
        val progress = (value.text.length / 200f).coerceAtMost(0.95f)
        _uiState.value = _uiState.value.copy(code = value, myProgress = progress)
    }

    fun onSubmit() {
        val state = _uiState.value.battleState
        if (state !is BattleState.BattleActive) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSubmitting = true)
            battleRepository.submitBattle(state.matchId, _uiState.value.code.text, _uiState.value.language.name.lowercase())
            _uiState.value = _uiState.value.copy(
                isSubmitting = false,
                battleState  = BattleState.BattleEnded(won = true, xpAwarded = 150),
                myProgress   = 1f,
            )
            timerJob?.cancel()
            opponentJob?.cancel()
        }
    }

    fun onMatchFoundAcknowledged() {
        _uiState.value = _uiState.value.copy(matchFound = false)
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private fun startCountdown(seconds: Int) {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            for (remaining in seconds downTo 0) {
                _uiState.value = _uiState.value.copy(timeRemainingSeconds = remaining)
                kotlinx.coroutines.delay(1000)
            }
            _uiState.value = _uiState.value.copy(
                battleState = BattleState.BattleEnded(won = false, xpAwarded = 0)
            )
        }
    }

    private fun startOpponentTracking(matchId: String) {
        opponentJob?.cancel()
        opponentJob = viewModelScope.launch {
            battleRepository.getOpponentProgress(matchId).collect { progress ->
                _uiState.value = _uiState.value.copy(opponentProgress = progress)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        opponentJob?.cancel()
    }
}
