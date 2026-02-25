package com.devx27.app.presentation.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devx27.app.presentation.theme.DevX27Theme

@Composable
fun KeyboardAccessoryBar(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
) {
    var ctrlPressed by remember { mutableStateOf(false) }
    var altPressed  by remember { mutableStateOf(false) }
    
    val haptic      = LocalHapticFeedback.current
    val scrollState = rememberScrollState()

    val handleKey: (String) -> Unit = { key ->
        // Subtle haptic like a real keyboard key press
        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)

        when (key) {
            "CTRL"  -> ctrlPressed = !ctrlPressed
            "ALT"   -> altPressed  = !altPressed
            "ESC"   -> { /* E.g. dismiss focus or perform action */ }
            "TAB"   -> insertText("    ", value, onValueChange)
            "LEFT"  -> moveCursor(value, onValueChange, -1, 0)
            "RIGHT" -> moveCursor(value, onValueChange,  1, 0)
            "UP"    -> moveCursor(value, onValueChange,  0, -1)
            "DOWN"  -> moveCursor(value, onValueChange,  0,  1)
            "HOME"  -> moveCursorHome(value, onValueChange)
            else    -> insertText(key, value, onValueChange)
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(DevX27Theme.colors.surfaceElevated)
            .padding(vertical = 6.dp, horizontal = 8.dp)
            .horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment     = Alignment.CenterVertically
    ) {
        val keys = listOf("ESC", "CTRL", "ALT", "TAB", "|", "/", "-", "HOME", "UP", "DOWN", "LEFT", "RIGHT")
        keys.forEach { key ->
            val isActive = (key == "CTRL" && ctrlPressed) || (key == "ALT" && altPressed)
            AccessoryKey(
                text     = key,
                isActive = isActive,
                onClick  = { handleKey(key) }
            )
        }
    }
}

private fun insertText(text: String, value: TextFieldValue, onValueChange: (TextFieldValue) -> Unit) {
    val before = value.text.substring(0, value.selection.min)
    val after  = value.text.substring(value.selection.max)
    val newText = before + text + after
    val newCursor = before.length + text.length
    onValueChange(TextFieldValue(newText, TextRange(newCursor)))
}

private fun moveCursor(value: TextFieldValue, onValueChange: (TextFieldValue) -> Unit, dx: Int, dy: Int) {
    if (dx != 0) {
        val newCursor = (value.selection.start + dx).coerceIn(0, value.text.length)
        onValueChange(value.copy(selection = TextRange(newCursor)))
    } else if (dy != 0) {
        val text   = value.text
        val cursor = value.selection.start
        val lines  = text.split('\n')
        
        var currentLineIndex = 0
        var charCount = 0
        
        for (i in lines.indices) {
            val endOfLine = charCount + lines[i].length + (if (i == lines.lastIndex) 0 else 1)
            if (cursor <= endOfLine) {
                currentLineIndex = i
                break
            }
            charCount = endOfLine
        }
        
        val col = cursor - charCount
        val targetLineIndex = (currentLineIndex + dy).coerceIn(0, lines.lastIndex)
        
        if (targetLineIndex == currentLineIndex) return
        
        var targetLineStart = 0
        for (i in 0 until targetLineIndex) {
            targetLineStart += lines[i].length + 1
        }
        
        val targetCol = col.coerceAtMost(lines[targetLineIndex].length)
        val newCursorPos = targetLineStart + targetCol
        
        onValueChange(value.copy(selection = TextRange(newCursorPos)))
    }
}

private fun moveCursorHome(value: TextFieldValue, onValueChange: (TextFieldValue) -> Unit) {
    val text = value.text
    var startOfLine = value.selection.start
    while (startOfLine > 0 && text[startOfLine - 1] != '\n') {
        startOfLine--
    }
    onValueChange(value.copy(selection = TextRange(startOfLine)))
}

@Composable
private fun AccessoryKey(text: String, isActive: Boolean, onClick: () -> Unit) {
    val displayIcon = when(text) {
        "UP"    -> "↑"
        "DOWN"  -> "↓"
        "LEFT"  -> "←"
        "RIGHT" -> "→"
        "HOME"  -> "⇤"
        else    -> text
    }
    val bgColor   = if (isActive) DevX27Theme.colors.xpSuccessBg else DevX27Theme.colors.surfaceInput
    val textColor = if (isActive) DevX27Theme.colors.xpSuccess   else DevX27Theme.colors.onBackground
    
    Box(
        modifier = Modifier
            .height(36.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text       = displayIcon,
            color      = textColor,
            fontSize   = 13.sp,
            fontWeight = if (isActive) FontWeight.Black else FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
    }
}
