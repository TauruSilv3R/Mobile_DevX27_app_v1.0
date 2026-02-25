package com.devx27.app.presentation.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.devx27.app.presentation.theme.DevX27Theme

@Composable
fun DevX27BottomBar(navController: NavController) {
    val navBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStack?.destination?.route

    AnimatedVisibility(
        visible = bottomNavItems.any { it.screen.route == currentRoute },
        enter   = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit    = slideOutVertically(targetOffsetY  = { it }) + fadeOut(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(DevX27Theme.colors.bottomBar)
        ) {
            // Hairline divider
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(0.5.dp)
                    .background(DevX27Theme.colors.divider)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 10.dp)
                    .navigationBarsPadding(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment     = Alignment.CenterVertically,
            ) {
                bottomNavItems.forEach { item ->
                    BottomBarTab(
                        item       = item,
                        isSelected = currentRoute == item.screen.route,
                        onClick    = {
                            if (currentRoute != item.screen.route) {
                                navController.navigate(item.screen.route) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState    = true
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun BottomBarTab(
    item: BottomNavItem,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0.92f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "tab_scale"
    )

    val iconColor   = if (isSelected) DevX27Theme.colors.xpSuccess else DevX27Theme.colors.onSurfaceSubtle
    val labelColor  = if (isSelected) DevX27Theme.colors.xpSuccess else DevX27Theme.colors.onSurfaceSubtle

    Column(
        modifier = Modifier
            .scale(scale)
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick    = onClick
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        // XP-green selection indicator dot
        Box(
            modifier = Modifier
                .size(3.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(
                    if (isSelected) DevX27Theme.colors.xpSuccess
                    else androidx.compose.ui.graphics.Color.Transparent
                )
        )

        Icon(
            imageVector        = if (isSelected) item.selectedIcon else item.icon,
            contentDescription = item.label,
            tint               = iconColor,
            modifier           = Modifier.size(22.dp),
        )

        Text(
            text       = item.label,
            fontSize   = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color      = labelColor,
        )
    }
}
