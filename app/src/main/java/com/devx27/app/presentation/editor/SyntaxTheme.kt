package com.devx27.app.presentation.editor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import com.devx27.app.presentation.theme.DevX27Theme

/**
 * SyntaxColors — immutable holder for a theme's editor colors
 */
data class SyntaxColors(
    val keyword: Color,
    val string: Color,
    val comment: Color,
    val number: Color,
    val function_: Color,
    val type: Color,
    val operator_: Color,
    val annotation: Color,
    val plain: Color,
    val background: Color,
    val lineNumber: Color,
    val lineHighlight: Color,
    val cursor: Color,
    val selection: Color
)

object SyntaxTheme {
    @Composable
    @ReadOnlyComposable
    fun colors(): SyntaxColors {
        val isDark = DevX27Theme.colors.isDark
        return if (isDark) {
            SyntaxColors(
                keyword    = Color(0xFF569CD6),
                string     = Color(0xFFCE9178),
                comment    = Color(0xFF6A9955),
                number     = Color(0xFFB5CEA8),
                function_  = Color(0xFFDCDCAA),
                type       = Color(0xFF4EC9B0),
                operator_  = Color(0xFFD4D4D4),
                annotation = Color(0xFFBBBBBB),
                plain      = Color(0xFFD4D4D4),
                background = Color(0xFF000000),
                lineNumber = Color(0xFF4A4A4A),
                lineHighlight = Color(0xFF0A0A0A),
                cursor     = Color(0xFF1BB661),
                selection  = Color(0xFF264F78)
            )
        } else {
            SyntaxColors(
                keyword    = Color(0xFF0033B3),
                string     = Color(0xFF067D17),
                comment    = Color(0xFF8C8C8C),
                number     = Color(0xFF1750EB),
                function_  = Color(0xFF00627A),
                type       = Color(0xFF000000),
                operator_  = Color(0xFF000000),
                annotation = Color(0xFF9E880D),
                plain      = Color(0xFF121212),
                background = Color(0xFFF5F5F5),
                lineNumber = Color(0xFFAAAAAA),
                lineHighlight = Color(0xFFE8E8E8),
                cursor     = Color(0xFF1BB661),
                selection  = Color(0xFFA6D2FF)
            )
        }
    }
}

enum class TokenType {
    KEYWORD, STRING, COMMENT, NUMBER, FUNCTION, TYPE,
    OPERATOR, ANNOTATION, PLAIN
}

data class SyntaxToken(val start: Int, val end: Int, val type: TokenType)
