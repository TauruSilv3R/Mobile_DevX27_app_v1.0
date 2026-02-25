package com.devx27.app.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import com.devx27.app.R
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.devx27.app.presentation.navigation.Screen
import com.devx27.app.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            navController.navigate(Screen.Dashboard.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(DevX27Theme.colors.background).imePadding()
    ) {
        IconButton(
            onClick = { navController.navigate(Screen.Help.route) },
            modifier = Modifier.align(Alignment.TopEnd).padding(16.dp).statusBarsPadding()
        ) {
            Icon(
                imageVector = Icons.Default.HelpOutline,
                contentDescription = "Help",
                tint = DevX27Theme.colors.onSurfaceMuted
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth().padding(32.dp).align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Image(
                painter = painterResource(id = R.mipmap.devx27),
                contentDescription = "Logo",
                modifier = Modifier.size(100.dp).padding(bottom = 16.dp)
            )
            Text(
                "Welcome Back",
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = DevX27Theme.colors.onBackground
            )

            Text(
                "Sign in to continue your journey",
                fontSize = 15.sp,
                color = DevX27Theme.colors.onSurfaceMuted
            )

            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = DevX27Theme.colors.xpSuccess,
                    unfocusedBorderColor = DevX27Theme.colors.divider,
                    focusedLabelColor = DevX27Theme.colors.xpSuccess,
                    cursorColor = DevX27Theme.colors.xpSuccess
                )
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = DevX27Theme.colors.xpSuccess,
                    unfocusedBorderColor = DevX27Theme.colors.divider,
                    focusedLabelColor = DevX27Theme.colors.xpSuccess,
                    cursorColor = DevX27Theme.colors.xpSuccess
                )
            )

            if (uiState.error != null) {
                Text(uiState.error!!, color = DevX27Theme.colors.xpError, fontSize = 13.sp)
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = { viewModel.signIn(email, password) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = !uiState.isLoading,
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.Black
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(DevX27Theme.colors.actionColor),
                    contentAlignment = Alignment.Center
                ) {
                    if (uiState.isLoading) CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(24.dp))
                    else Text("Sign In", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                }
            }

            TextButton(onClick = { navController.navigate(Screen.Register.route) }) {
                Text("Don't have an account? Sign Up", color = DevX27Theme.colors.xpSuccess)
            }
        }
    }
}
