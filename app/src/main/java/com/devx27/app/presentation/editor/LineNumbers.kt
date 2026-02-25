package com.devx27.app.presentation.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LineNumberGutter(
    lineCount: Int,
    modifier: Modifier = Modifier,
) {
    val syntaxColors = SyntaxTheme.colors()
    val gutterWidth  = (lineCount.toString().length * 10 + 24).dp

    Column(
        modifier = modifier
            .width(gutterWidth)
            .fillMaxHeight()
            .background(syntaxColors.background)
            .padding(end = 8.dp),
        horizontalAlignment = Alignment.End,
    ) {
        repeat(lineCount) { index ->
            Box(
                modifier          = Modifier
                    .widthIn(min = gutterWidth)
                    .padding(vertical = 0.dp),
                contentAlignment  = Alignment.CenterEnd,
            ) {
                Text(
                    text      = (index + 1).toString(),
                    style     = TextStyle(
                        fontFamily  = FontFamily.Monospace,
                        fontSize    = 14.sp,
                        lineHeight  = 22.sp,
                        color       = syntaxColors.lineNumber,
                        textAlign   = TextAlign.End,
                        letterSpacing = 0.sp,
                    ),
                )
            }
        }
    }
}
