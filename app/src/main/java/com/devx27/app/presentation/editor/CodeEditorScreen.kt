package com.devx27.app.presentation.editor

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import kotlinx.coroutines.launch
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.devx27.app.domain.repository.SubmissionResult
import com.devx27.app.presentation.navigation.Screen
import com.devx27.app.presentation.theme.DevX27Theme

// Ã¢â€â‚¬Ã¢â€â‚¬ Tab indices Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬
private const val TAB_DESCRIPTION = 0
private const val TAB_CODE        = 1
private const val TAB_CONSOLE     = 2

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class, androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun CodeEditorScreen(
    navController: NavController,
    challengeId: String,
    viewModel: CodeEditorViewModel = hiltViewModel(),
) {
    val uiState      by viewModel.uiState.collectAsState()
    val syntaxColors  = SyntaxTheme.colors()
    val pagerState = rememberPagerState(pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()

    // Auto-switch to Console when run starts
    LaunchedEffect(uiState.isRunning, uiState.runOutput) {
        if (uiState.isRunning || uiState.runOutput != null) {
            pagerState.animateScrollToPage(TAB_CONSOLE)
        }
    }

    Scaffold(
        containerColor = syntaxColors.background,
        topBar = {
            EditorTopBar(
                title        = uiState.challengeTitle,
                language     = uiState.language,
                isRunning    = uiState.isRunning,
                isSubmitting = uiState.isSubmitting,
                isSubmitted  = uiState.isSubmitted,
                onBack       = { navController.popBackStack() },
                onRun        = viewModel::onRun,
                onSubmit     = viewModel::onSubmit,
                onLangChange = viewModel::onLanguageChanged,
                syntaxColors = syntaxColors,
            )
        },
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = padding.calculateBottomPadding())
                .imePadding()
                .background(syntaxColors.background)
        ) {
            // Ã¢â€â‚¬Ã¢â€â‚¬ Tab Row Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬
            EditorTabRow(
                selectedTab = pagerState.currentPage, 
                onTabSelected = { 
                    coroutineScope.launch { pagerState.animateScrollToPage(it) } 
                }
            )

            // Ã¢â€â‚¬Ã¢â€â‚¬ Tab Content with slide animation Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬
            HorizontalPager(
                state = pagerState,
                userScrollEnabled = pagerState.currentPage != TAB_CODE,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                when (page) {
                    TAB_DESCRIPTION -> DescriptionTab(
                        description = uiState.challengeDescription,
                        title = uiState.challengeTitle,
                        modifier = Modifier.fillMaxSize()
                    )
                    TAB_CODE -> CodeTab(
                        uiState = uiState,
                        onCodeChanged = viewModel::onCodeChanged,
                        syntaxColors = syntaxColors,
                        modifier = Modifier.fillMaxSize()
                    )
                    TAB_CONSOLE -> ConsoleTab(
                        output = uiState.runOutput,
                        isRunning = uiState.isRunning,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        // Ã¢â€â‚¬Ã¢â€â‚¬ Submission result dialog (centred) Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬
        uiState.result?.let { result ->
            SubmissionDialog(
                result   = result,
                onDismiss = viewModel::onDismissResult,
                onContinue = {
                    viewModel.onDismissResult()
                    navController.popBackStack()
                    // Navigate to next challenge
                    val nextId = uiState.nextChallengeId
                    if (nextId != null) {
                        navController.navigate(Screen.CodeEditor.createRoute(nextId))
                    }
                }
            )
        }
    }
}

// Ã¢â€â‚¬Ã¢â€â‚¬ Tab Row Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬
@Composable
private fun EditorTabRow(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    val tabs = listOf("Description", "Code", "Console")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(DevX27Theme.colors.surface)
            .padding(horizontal = 16.dp)
    ) {
        tabs.forEachIndexed { index, label ->
            val isSelected = selectedTab == index
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onTabSelected(index) }
                    .padding(vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = label,
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) DevX27Theme.colors.xpSuccess else DevX27Theme.colors.onSurfaceMuted
                )
                Spacer(Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .height(2.dp)
                        .fillMaxWidth(if (isSelected) 0.7f else 0f)
                        .clip(RoundedCornerShape(1.dp))
                        .background(DevX27Theme.colors.xpSuccess)
                )
            }
        }
    }
    Divider(color = DevX27Theme.colors.divider, thickness = 0.5.dp)
}

// Ã¢â€â‚¬Ã¢â€â‚¬ Description Tab Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬
@Composable
private fun DescriptionTab(title: String, description: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(title, fontSize = 22.sp, fontWeight = FontWeight.Black, color = DevX27Theme.colors.onBackground)
        Text(
            description.ifBlank {
                "Solve the problem using the starter code provided.\n\n" +
                "Write a function that satisfies the given constraints and returns the correct result.\n\n" +
                "Example:\n  Input: [1, 2, 3]\n  Output: 6\n\nConstraints:\n  Ã¢â‚¬Â¢ 1 Ã¢â€°Â¤ n Ã¢â€°Â¤ 10Ã¢ÂÂµ\n  Ã¢â‚¬Â¢ Time limit: 1s"
            },
            fontSize = 14.sp,
            lineHeight = 22.sp,
            color = DevX27Theme.colors.onSurfaceMuted
        )
    }
}

// Ã¢â€â‚¬Ã¢â€â‚¬ Code Tab Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬
@Composable
private fun CodeTab(
    uiState: CodeEditorUiState,
    onCodeChanged: (androidx.compose.ui.text.input.TextFieldValue) -> Unit,
    syntaxColors: SyntaxColors,
    modifier: Modifier = Modifier,
) {
    val vertScroll = rememberScrollState()
    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 2.dp),
        ) {
            LineNumberGutter(
                lineCount = uiState.code.text.count { it == '\n' } + 1,
                modifier  = Modifier.fillMaxHeight(),
            )
            Box(
                modifier = Modifier.width(1.dp).fillMaxHeight()
                    .background(syntaxColors.lineNumber.copy(alpha = 0.25f))
            )
            CodeTextField(
                value               = uiState.code,
                onValueChange       = if (uiState.isSubmitted) ({ }) else onCodeChanged,
                language            = uiState.language,
                verticalScrollState = vertScroll,
                modifier            = Modifier.weight(1f).fillMaxSize().padding(start = 12.dp),
            )
        }
        // Lock overlay when submitted
        if (uiState.isSubmitted) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(DevX27Theme.colors.background.copy(alpha = 0.5f))
            ) {
                Card(
                    modifier = Modifier.align(Alignment.Center),
                    colors = CardDefaults.cardColors(containerColor = DevX27Theme.colors.surface),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "Ã¢Å“â€¦ Submitted Ã¢â‚¬â€ problem locked",
                        modifier = Modifier.padding(16.dp),
                        color = DevX27Theme.colors.xpSuccess,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        // Keyboard Accessory (only in code tab)
        if (!uiState.isSubmitted) {
            KeyboardAccessoryBar(
                value         = uiState.code,
                onValueChange = onCodeChanged,
                modifier      = Modifier.fillMaxWidth().align(Alignment.BottomCenter).imePadding()
            )
        }
    }
}

// Ã¢â€â‚¬Ã¢â€â‚¬ Console Tab Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬
@Composable
private fun ConsoleTab(output: String?, isRunning: Boolean, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(Color(0xFF0D1117))  // GitHub Dark Ã¢â‚¬â€ classic console feel
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(8.dp).clip(RoundedCornerShape(4.dp))
                    .background(if (isRunning) Color(0xFF00FF00) else Color(0xFF555555))
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = if (isRunning) "RunningÃ¢â‚¬Â¦" else "Console Output",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF8B949E)
            )
        }
        Spacer(Modifier.height(12.dp))
        if (isRunning) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth().height(2.dp).clip(RoundedCornerShape(1.dp)),
                color = DevX27Theme.colors.xpSuccess,
                trackColor = Color(0xFF21262D)
            )
        } else {
            Text(
                text = output ?: "Press Ã¢â€“Â¶ Run to execute your code.",
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                fontSize = 13.sp,
                lineHeight = 21.sp,
                color = if (output != null) Color(0xFFE6EDF3) else Color(0xFF555555)
            )
        }
    }
}

// Ã¢â€â‚¬Ã¢â€â‚¬ Top Bar with Run / Submit buttons Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditorTopBar(
    title: String,
    language: SyntaxHighlighter.Language,
    isRunning: Boolean,
    isSubmitting: Boolean,
    isSubmitted: Boolean,
    onBack: () -> Unit,
    onRun: () -> Unit,
    onSubmit: () -> Unit,
    onLangChange: (SyntaxHighlighter.Language) -> Unit,
    syntaxColors: SyntaxColors,
) {
    var showLangMenu by remember { mutableStateOf(false) }
    val busy = isRunning || isSubmitting || isSubmitted

    TopAppBar(
        title = {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = DevX27Theme.colors.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = DevX27Theme.colors.onBackground)
            }
        },
        actions = {
            // Language picker text
            Box {
                IconButton(onClick = { if (!isSubmitted) showLangMenu = true }) {
                    Text(
                        text = language.name.lowercase().replaceFirstChar { it.uppercase() },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DevX27Theme.colors.onBackground
                    )
                }
                DropdownMenu(expanded = showLangMenu, onDismissRequest = { showLangMenu = false }) {
                    SyntaxHighlighter.Language.entries.forEach { lang ->
                        DropdownMenuItem(
                            text = { Text(lang.name.lowercase().replaceFirstChar { it.uppercase() }) },
                            onClick = { onLangChange(lang); showLangMenu = false }
                        )
                    }
                }
            }

            // Run button
            IconButton(
                onClick = { if (!busy) onRun() },
                enabled = !busy
            ) {
                Icon(
                    Icons.Default.PlayArrow,
                    "Run",
                    tint = if (!busy) DevX27Theme.colors.xpSuccess else DevX27Theme.colors.onSurfaceSubtle,
                    modifier = Modifier.size(26.dp)
                )
            }

            // Submit button
            if (!isSubmitted) {
                Box(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .height(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (!busy) DevX27Theme.colors.actionColor else DevX27Theme.colors.surfaceInput)
                        .clickable(enabled = !busy) { onSubmit() },
                    contentAlignment = Alignment.Center
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp).padding(horizontal = 12.dp),
                            color = DevX27Theme.colors.background,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Upload,
                                "Submit",
                                tint = if (!busy) DevX27Theme.colors.background else DevX27Theme.colors.onSurfaceSubtle,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                "Submit",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (!busy) DevX27Theme.colors.background else DevX27Theme.colors.onSurfaceSubtle
                            )
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .height(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(DevX27Theme.colors.xpSuccess.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Ã¢Å“â€¦ Done", modifier = Modifier.padding(horizontal = 12.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DevX27Theme.colors.xpSuccess)
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = syntaxColors.background),
    )
}

// Ã¢â€â‚¬Ã¢â€â‚¬ Submission result dialog Ã¢â‚¬â€ centred Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬Ã¢â€â‚¬
@Composable
private fun SubmissionDialog(
    result: SubmissionResult,
    onDismiss: () -> Unit,
    onContinue: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .clip(RoundedCornerShape(18.dp))
                .background(DevX27Theme.colors.surfaceElevated)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header & Content
                Column(
                    modifier = Modifier.padding(top = 24.dp, bottom = 20.dp, start = 20.dp, end = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        if (result.passed) "All Tests Passed!" else "Not Quite Right",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = DevX27Theme.colors.onBackground,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    
                    if (result.passed) {
                        Text(
                            "+${result.xpAwarded} XP",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = DevX27Theme.colors.actionColor
                        )
                        Text(
                            "Solved in ${result.executionMs}ms", 
                            fontSize = 13.sp, 
                            color = DevX27Theme.colors.onSurfaceMuted,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    } else {
                        Text(
                            result.feedback, 
                            fontSize = 14.sp, 
                            color = DevX27Theme.colors.onSurfaceSubtle, 
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }

                HorizontalDivider(color = DevX27Theme.colors.divider)

                // iOS Style Action Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    // Close Button
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Close", 
                            fontSize = 16.sp, 
                            color = DevX27Theme.colors.onSurfaceMuted
                        )
                    }

                    // Vertical Separator
                    VerticalDivider(color = DevX27Theme.colors.divider)

                    // Primary Action
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable { onContinue() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            if (result.passed) "Next Ã¢â€ â€™" else "Retry", 
                            fontSize = 16.sp, 
                            fontWeight = FontWeight.Bold, 
                            color = DevX27Theme.colors.actionColor
                        )
                    }
                }
            }
        }
    }
}

