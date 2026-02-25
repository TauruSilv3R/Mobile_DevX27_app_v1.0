package com.devx27.app.domain.repository

import com.devx27.app.presentation.editor.SyntaxHighlighter

data class CodeExecutionResult(
    val stdout: String?,
    val stderr: String?,
    val compileOutput: String?,
    val status: String
)

data class ExecutionLanguage(
    val id: Int,
    val name: String
)

interface CodeExecutionRepository {
    suspend fun runCode(
        code: String,
        languageId: Int,
        stdin: String = ""
    ): CodeExecutionResult

    suspend fun getLanguages(): List<ExecutionLanguage>
}
