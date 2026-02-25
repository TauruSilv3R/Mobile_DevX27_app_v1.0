package com.devx27.app.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devx27.app.domain.model.UserProfile
import com.devx27.app.presentation.theme.DevX27Theme

@Composable
fun ProfileCompletionBanner(
    userProfile: UserProfile?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (userProfile == null) return

    val isIncomplete = userProfile.bio.isNullOrBlank() || userProfile.photoUrl.isNullOrBlank()

    AnimatedVisibility(
        visible = isIncomplete,
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(16.dp))
                .clickable { onClick() },
            colors = CardDefaults.cardColors(
                containerColor = DevX27Theme.colors.xpSuccess.copy(alpha = 0.1f)
            ),
            border = androidx.compose.foundation.BorderStroke(1.dp, DevX27Theme.colors.xpSuccess.copy(alpha = 0.3f))
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(DevX27Theme.colors.xpSuccess.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ErrorOutline,
                        contentDescription = null,
                        tint = DevX27Theme.colors.xpSuccess,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Complete your profile",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = DevX27Theme.colors.onBackground
                    )
                    Text(
                        text = "Add a bio and photo to stand out!",
                        fontSize = 13.sp,
                        color = DevX27Theme.colors.onSurfaceMuted
                    )
                }

                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = DevX27Theme.colors.onSurfaceMuted
                )
            }
        }
    }
}
