package com.devx27.app.data.repository

import android.util.Base64
import com.devx27.app.domain.repository.CodeExecutionRepository
import com.devx27.app.domain.repository.CodeExecutionResult
import com.devx27.app.domain.repository.ExecutionLanguage
import com.devx27.app.presentation.editor.SyntaxHighlighter
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.post
import io.ktor.client.request.parameter
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Judge0RepositoryImpl @Inject constructor() : CodeExecutionRepository {

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            io.ktor.serialization.kotlinx.json.json(
                Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                }
            )
        }
        install(Logging) {
            level = LogLevel.NONE
            logger = Logger.DEFAULT
        }
    }

    private val baseUrl = "https://ce.judge0.com"

    override suspend fun runCode(
        code: String,
        languageId: Int,
        stdin: String
    ): CodeExecutionResult {
        val req = Judge0SubmissionRequest(
            sourceCode = code.encodeBase64(),
            languageId = languageId,
            stdin = if (stdin.isBlank()) null else stdin.encodeBase64()
        )

        val response: Judge0SubmissionResponse = client.post("$baseUrl/submissions") {
            parameter("base64_encoded", true)
            parameter("wait", true)
            contentType(ContentType.Application.Json)
            setBody(req)
        }.body()

        return CodeExecutionResult(
            stdout = response.stdout.decodeBase64OrNull(),
            stderr = response.stderr.decodeBase64OrNull(),
            compileOutput = response.compileOutput.decodeBase64OrNull(),
            status = response.status?.description ?: "unknown"
        )
    }

    private fun String.encodeBase64(): String =
        Base64.encodeToString(toByteArray(), Base64.NO_WRAP)

    private fun String?.decodeBase64OrNull(): String? =
        this?.let { String(Base64.decode(it, Base64.NO_WRAP)) }?.ifBlank { null }

    override suspend fun getLanguages(): List<ExecutionLanguage> {
        return client.get("$baseUrl/languages").body<List<Judge0Language>>()
            .map { ExecutionLanguage(it.id, it.name) }
    }
}

@Serializable
private data class Judge0Language(
    val id: Int,
    val name: String
)

@Serializable
private data class Judge0SubmissionRequest(
    @SerialName("source_code") val sourceCode: String,
    @SerialName("language_id") val languageId: Int,
    val stdin: String? = null
)

@Serializable
private data class Judge0SubmissionResponse(
    val stdout: String? = null,
    val stderr: String? = null,
    @SerialName("compile_output") val compileOutput: String? = null,
    val status: Judge0Status? = null
)

@Serializable
private data class Judge0Status(
    val id: Int,
    val description: String
)
