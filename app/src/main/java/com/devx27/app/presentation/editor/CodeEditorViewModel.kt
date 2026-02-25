package com.devx27.app.presentation.editor

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devx27.app.domain.repository.SettingsRepository
import com.devx27.app.domain.repository.SubmissionResult
import com.devx27.app.domain.repository.ChallengeRepository
import com.devx27.app.domain.repository.CodeExecutionRepository
import com.devx27.app.domain.usecase.SubmitSolutionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CodeEditorUiState(
    val challengeTitle: String               = "Challenge",
    val challengeDescription: String         = "",
    val challengeId: String                  = "",
    val nextChallengeId: String?             = null,
    val code: TextFieldValue                 = TextFieldValue(""),
    val language: SyntaxHighlighter.Language = SyntaxHighlighter.Language.PYTHON,
    val isRunning: Boolean                   = false,
    val isSubmitting: Boolean                = false,
    val isSubmitted: Boolean                 = false,
    val runOutput: String?                   = null,
    val result: SubmissionResult?            = null,
    val error: String?                       = null,
)

@HiltViewModel
class CodeEditorViewModel @Inject constructor(
    private val submitSolutionUseCase: SubmitSolutionUseCase,
    private val challengeRepository: ChallengeRepository,
    private val settingsRepository: SettingsRepository,
    private val codeExecutionRepository: CodeExecutionRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CodeEditorUiState())
    val uiState: StateFlow<CodeEditorUiState> = _uiState.asStateFlow()

    init {
        val challengeId = savedStateHandle.get<String>("challengeId") ?: ""
        viewModelScope.launch {
            val preferred = settingsRepository.getSettings().first().preferredLanguage
            val preferredLang = preferred.toLanguage()
            _uiState.value = _uiState.value.copy(
                language = preferredLang,
                code = TextFieldValue(getStarterCode(preferredLang))
            )
            loadChallenge(challengeId)
        }
    }

    private fun loadChallenge(id: String) {
        viewModelScope.launch {
            challengeRepository.getChallengeById(id).collect { result ->
                result.onSuccess { challenge ->
                    // Find the next challenge id from the full list
                    var nextId: String? = null
                    challengeRepository.getChallenges().collect { listResult ->
                        nextId = listResult.getOrNull()
                            ?.let { list ->
                                val idx = list.indexOfFirst { it.id == id }
                                if (idx >= 0 && idx + 1 < list.size) list[idx + 1].id else null
                            }
                    }
                    _uiState.value = _uiState.value.copy(
                        challengeId = id,
                        challengeTitle = challenge.title,
                        challengeDescription = challenge.description.ifBlank { "" },
                        nextChallengeId = nextId,
                        code = TextFieldValue(challenge.starterCode.ifBlank { getStarterCode(_uiState.value.language) })
                    )
                }
            }
        }
    }

    fun onCodeChanged(value: TextFieldValue) {
        _uiState.value = _uiState.value.copy(code = value)
    }

    fun onLanguageChanged(lang: SyntaxHighlighter.Language) {
        _uiState.value = _uiState.value.copy(
            language = lang,
            code     = TextFieldValue(getStarterCode(lang)),
        )
    }

    fun onRun() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRunning = true, runOutput = null, error = null)
            try {
                val result = codeExecutionRepository.runCode(
                    code = _uiState.value.code.text,
                    language = _uiState.value.language
                )
                val output = listOfNotNull(
                    result.stdout?.let { "Output:\n$it" },
                    result.stderr?.let { "Stderr:\n$it" },
                    result.compileOutput?.let { "Compile:\n$it" }
                ).joinToString("\n\n").ifBlank { "Status: ${result.status}" }

                _uiState.value = _uiState.value.copy(
                    isRunning = false,
                    runOutput = output
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isRunning = false,
                    runOutput = "Execution failed: ${e.message}"
                )
            }
        }
    }

    fun onSubmit() {
        if (_uiState.value.isSubmitted) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSubmitting = true, result = null)
            val uiState = _uiState.value
            val result  = submitSolutionUseCase(
                challengeId = uiState.challengeId,
                code        = uiState.code.text,
                language    = uiState.language.name.lowercase(),
            )
            result.fold(
                onSuccess = { submission ->
                    _uiState.value = _uiState.value.copy(
                        isSubmitting = false,
                        isSubmitted  = submission.passed,
                        result       = submission,
                    )
                },
                onFailure = { err ->
                    _uiState.value = _uiState.value.copy(
                        isSubmitting = false,
                        error        = err.message,
                    )
                }
            )
        }
    }

    fun onDismissResult() {
        _uiState.value = _uiState.value.copy(result = null)
    }

    private fun getStarterCode(lang: SyntaxHighlighter.Language): String = when (lang) {
        SyntaxHighlighter.Language.PYTHON -> """
            |def solution(nums: list[int]) -> int:
            |    # Write your solution here
            |    pass
            |
            |# --- Test ---
            |print(solution([1, 2, 3]))
        """.trimMargin()

        SyntaxHighlighter.Language.KOTLIN -> """
            |fun solution(nums: List<Int>): Int {
            |    // Write your solution here
            |    return 0
            |}
            |
            |// --- Test ---
            |fun main() {
            |    println(solution(listOf(1, 2, 3)))
            |}
        """.trimMargin()

        SyntaxHighlighter.Language.CPP -> """
            |#include <iostream>
            |#include <vector>
            |
            |int solution(std::vector<int>& nums) {
            |    // Write your solution here
            |    return 0;
            |}
            |
            |int main() {
            |    std::vector<int> nums = {1, 2, 3};
            |    std::cout << solution(nums) << std::endl;
            |    return 0;
            |}
        """.trimMargin()

        SyntaxHighlighter.Language.JAVA -> """
            |import java.util.*;
            |
            |public class Solution {
            |    public int solution(List<Integer> nums) {
            |        // Write your solution here
            |        return 0;
            |    }
            |
            |    public static void main(String[] args) {
            |        Solution sol = new Solution();
            |        System.out.println(sol.solution(Arrays.asList(1, 2, 3)));
            |    }
            |}
        """.trimMargin()

        SyntaxHighlighter.Language.JAVASCRIPT -> """
            |/**
            | * @param {number[]} nums
            | * @return {number}
            | */
            |function solution(nums) {
            |    // Write your solution here
            |    return 0;
            |}
            |
            |// --- Test ---
            |console.log(solution([1, 2, 3]));
        """.trimMargin()
    }

    private fun String.toLanguage(): SyntaxHighlighter.Language = when (lowercase()) {
        "python" -> SyntaxHighlighter.Language.PYTHON
        "kotlin" -> SyntaxHighlighter.Language.KOTLIN
        "cpp", "c++" -> SyntaxHighlighter.Language.CPP
        "java" -> SyntaxHighlighter.Language.JAVA
        "javascript", "js" -> SyntaxHighlighter.Language.JAVASCRIPT
        else -> SyntaxHighlighter.Language.PYTHON
    }
}
