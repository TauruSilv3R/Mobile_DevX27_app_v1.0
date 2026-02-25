package com.devx27.app.presentation.editor

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CodeTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    language: SyntaxHighlighter.Language,
    verticalScrollState: ScrollState,
    modifier: Modifier = Modifier,
) {
    val horizontalScroll = rememberScrollState()
    val syntaxColors     = SyntaxTheme.colors()

    val highlighted: AnnotatedString by remember(value.text, language, syntaxColors) {
        derivedStateOf { buildHighlighted(value.text, language, syntaxColors) }
    }

    BasicTextField(
        value          = value.copy(annotatedString = highlighted),
        onValueChange  = { new ->
            onValueChange(new.copy(annotatedString = buildHighlighted(new.text, language, syntaxColors)))
        },
        textStyle      = TextStyle(
            fontFamily    = FontFamily.Monospace,
            fontSize      = 14.sp,
            lineHeight    = 22.sp,
            color         = syntaxColors.plain,
            letterSpacing = 0.sp,
        ),
        cursorBrush     = SolidColor(syntaxColors.cursor),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Ascii,
            imeAction    = ImeAction.None,
            autoCorrect  = false,
        ),
        modifier = modifier
            .background(syntaxColors.background)
            .horizontalScroll(horizontalScroll)
            .verticalScroll(verticalScrollState),
    )
}

private fun buildHighlighted(
    source: String,
    language: SyntaxHighlighter.Language,
    colors: SyntaxColors
): AnnotatedString {
    val tokens = SyntaxHighlighter.tokenize(source, language)
    return buildAnnotatedString {
        append(source)
        tokens.forEach { token ->
            val color = when (token.type) {
                TokenType.KEYWORD    -> colors.keyword
                TokenType.STRING     -> colors.string
                TokenType.COMMENT    -> colors.comment
                TokenType.NUMBER     -> colors.number
                TokenType.FUNCTION   -> colors.function_
                TokenType.TYPE       -> colors.type
                TokenType.OPERATOR   -> colors.operator_
                TokenType.ANNOTATION -> colors.annotation
                TokenType.PLAIN      -> colors.plain
            }
            addStyle(
                style = SpanStyle(color = color),
                start = token.start.coerceIn(0, source.length),
                end   = token.end.coerceIn(0, source.length),
            )
        }
    }
}
