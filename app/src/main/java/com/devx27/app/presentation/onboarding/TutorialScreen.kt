package com.devx27.app.presentation.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.devx27.app.R
import com.devx27.app.presentation.navigation.Screen
import com.devx27.app.presentation.theme.DevX27Theme
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TutorialScreen(navController: NavController) {
    val items = listOf(
        TutorialItem(
            title = "Master the Dashboard",
            description = "Track your XP, levels, and daily streaks. Watch your rank grow on the global leaderboard.",
            icon = R.mipmap.devx27
        ),
        TutorialItem(
            title = "OLED-Optimized Editor",
            description = "Write cleaner code with high-contrast themes and Monaco-style highlighting for 5+ languages.",
            icon = R.mipmap.devx27 // In a real app, use specific tutorial graphics
        ),
        TutorialItem(
            title = "Real-Time Battles",
            description = "Challenge other developers in high-speed coding duels. First to pass all test cases wins!",
            icon = R.mipmap.devx27
        ),
        TutorialItem(
            title = "Unlock Your Potential",
            description = "Progress through the skill tree to unlock advanced topics and special developer achievements.",
            icon = R.mipmap.devx27
        )
    )

    val pagerState = rememberPagerState(pageCount = { items.size })
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DevX27Theme.colors.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { page ->
                val item = items[page]
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = item.icon),
                        contentDescription = null,
                        modifier = Modifier
                            .size(160.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    
                    Spacer(Modifier.height(48.dp))
                    
                    Text(
                        text = item.title,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = DevX27Theme.colors.onBackground,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(Modifier.height(16.dp))
                    
                    Text(
                        text = item.description,
                        fontSize = 16.sp,
                        color = DevX27Theme.colors.onSurfaceMuted,
                        textAlign = TextAlign.Center,
                        lineHeight = 24.sp
                    )
                }
            }

            // Bottom controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Page Indicator
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    repeat(items.size) { iteration ->
                        val color = if (pagerState.currentPage == iteration) 
                            DevX27Theme.colors.xpSuccess 
                        else 
                            DevX27Theme.colors.divider
                        
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(color)
                        )
                    }
                }

                // Next Button
                Button(
                    onClick = {
                        if (pagerState.currentPage < items.size - 1) {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        } else {
                            navController.navigate(Screen.Dashboard.route) {
                                popUpTo(Screen.Tutorial.route) { inclusive = true }
                            }
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DevX27Theme.colors.xpSuccess,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier.height(56.dp)
                ) {
                    Text(
                        text = if (pagerState.currentPage == items.size - 1) "Get Started" else "Next",
                        fontWeight = FontWeight.Bold
                    )
                    if (pagerState.currentPage < items.size - 1) {
                        Spacer(Modifier.width(8.dp))
                        Icon(Icons.Default.ArrowForward, null, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

data class TutorialItem(
    val title: String,
    val description: String,
    val icon: Int
)
