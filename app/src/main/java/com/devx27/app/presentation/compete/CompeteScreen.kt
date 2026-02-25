package com.devx27.app.presentation.compete

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import com.devx27.app.presentation.theme.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SportsMartialArts
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.devx27.app.presentation.navigation.Screen
import com.devx27.app.presentation.theme.DevX27Theme

@Composable
fun CompeteScreen(navController: NavController) {
    Box(modifier = Modifier.fillMaxSize().background(DevX27Theme.colors.background)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
        Spacer(Modifier.height(24.dp))
        Text(
            text       = "Compete",
            fontSize   = 28.sp,
            fontWeight = FontWeight.Black,
            color      = DevX27Theme.colors.onBackground,
            modifier   = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(32.dp))

        // Live match finder card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors   = CardDefaults.cardColors(containerColor = DevX27Theme.colors.surface),
            shape    = RoundedCornerShape(20.dp),
        ) {
            Column(
                modifier            = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Icon(
                    imageVector        = Icons.Default.SportsMartialArts,
                    contentDescription = null,
                    tint               = DevX27Theme.colors.xpSuccess,
                    modifier           = Modifier.size(56.dp),
                )
                Text(
                    text       = "1v1 Speed Challenge",
                    fontSize   = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color      = DevX27Theme.colors.onBackground,
                )
                Text(
                    text      = "Race against another developer. Solve the same problem fastest to win XP.",
                    fontSize  = 14.sp,
                    color     = DevX27Theme.colors.onSurfaceMuted,
                )
                Button(
                    onClick = { navController.navigate(Screen.BattleLobby.route) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape  = RoundedCornerShape(16.dp),
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor   = Color.Black,
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(DevX27Theme.colors.actionColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Find Match", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Tournament card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors   = CardDefaults.cardColors(containerColor = DevX27Theme.colors.surface),
            shape    = RoundedCornerShape(20.dp),
        ) {
            Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically,
                ) {
                    Text("Weekly Tournament", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = DevX27Theme.colors.onBackground)
                    Box(
                        Modifier
                            .background(DevX27Theme.colors.xpGold.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text("LIVE", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = DevX27Theme.colors.xpGold)
                    }
                }
                Text("Top 10 earn bonus XP multipliers. Ends in 3d 14h.", fontSize = 14.sp, color = DevX27Theme.colors.onSurfaceMuted)
                Button(
                    onClick  = { navController.navigate(Screen.BattleLobby.route) },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape    = RoundedCornerShape(12.dp),
                    colors   = ButtonDefaults.buttonColors(
                        containerColor = DevX27Theme.colors.xpGold.copy(alpha = 0.15f),
                        contentColor   = DevX27Theme.colors.xpGold,
                    ),
                ) {
                    Text("Join Tournament", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }
    }
}
}
