package com.devx27.app.presentation.permissions

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.devx27.app.presentation.navigation.Screen
import com.devx27.app.presentation.theme.DevX27Theme

@Composable
fun PermissionGateScreen(
    navController: NavController,
) {
    val context = LocalContext.current
    
    val needsNotification = Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
            
    var notificationGranted   by remember { mutableStateOf(!needsNotification) }
    var notificationDenyCount by remember { mutableIntStateOf(0) }
    
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            notificationGranted = isGranted
            if (!isGranted) {
                notificationDenyCount++
            }
        }
    )

    LaunchedEffect(notificationGranted) {
        if (notificationGranted) {
            navController.navigate(Screen.Dashboard.route) {
                popUpTo(Screen.PermissionGate.route) { inclusive = true }
            }
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DevX27Theme.colors.background)
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier            = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text       = "System Setup",
                fontSize   = 28.sp,
                fontWeight = FontWeight.Black,
                color      = DevX27Theme.colors.onBackground,
            )
            
            Text(
                text      = "DevX27 requires a few permissions to function as a pro live-coding environment.",
                fontSize  = 15.sp,
                color     = DevX27Theme.colors.onSurfaceMuted,
                textAlign = TextAlign.Center,
            )
            
            Spacer(Modifier.height(16.dp))
            
            PermissionRow(
                icon      = Icons.Default.Wifi,
                title     = "Internet Access",
                desc      = "Required for Firebase Sync",
                isGranted = true, // Normal permission, always granted
                onRequest = {}
            )
            
            PermissionRow(
                icon      = Icons.Default.Vibration,
                title     = "Haptic Engine",
                desc      = "Level-up and match vibrations",
                isGranted = true, // Normal permission
                onRequest = {}
            )
            
            PermissionRow(
                icon                = Icons.Default.Notifications,
                title               = "Push Notifications",
                desc                = "Battle alerts & XP reminders",
                isGranted           = notificationGranted,
                isPermanentlyDenied = notificationDenyCount >= 2,
                onRequest = {
                    if (notificationDenyCount >= 2) {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                        context.startActivity(intent)
                    } else if (Build.VERSION.SDK_INT >= 33) {
                        launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        notificationGranted = true
                    }
                }
            )
            
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun PermissionRow(
    icon: ImageVector,
    title: String,
    desc: String,
    isGranted: Boolean,
    isPermanentlyDenied: Boolean = false,
    onRequest: () -> Unit,
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DevX27Theme.colors.surfaceElevated),
        shape  = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier              = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector        = icon,
                contentDescription = null,
                tint               = if (isGranted) DevX27Theme.colors.xpSuccess else DevX27Theme.colors.onSurfaceMuted,
                modifier           = Modifier.size(28.dp)
            )
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = title,
                    fontSize   = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color      = DevX27Theme.colors.onBackground
                )
                Text(
                    text     = desc,
                    fontSize = 13.sp,
                    color    = DevX27Theme.colors.onSurfaceSubtle
                )
            }
            
            if (isGranted) {
                Text(
                    text       = "Granted",
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color      = DevX27Theme.colors.xpSuccess
                )
            } else {
                Button(
                    onClick        = onRequest,
                    colors         = ButtonDefaults.buttonColors(
                        containerColor = DevX27Theme.colors.xpSuccess,
                        contentColor   = Color.Black
                    ),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text       = if (isPermanentlyDenied) "Settings" else "Grant",
                        fontWeight = FontWeight.Bold,
                        fontSize   = 13.sp
                    )
                }
            }
        }
    }
}
