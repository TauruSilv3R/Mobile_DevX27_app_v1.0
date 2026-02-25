package com.devx27.app.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.material3.*
import coil.compose.AsyncImage
import androidx.compose.foundation.clickable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.devx27.app.domain.model.UserProfile
import com.devx27.app.presentation.theme.DevX27Theme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val profile = uiState.stats?.profile

    // Local editable state — initialised from current profile
    var displayName       by remember(profile) { mutableStateOf(profile?.displayName ?: "") }
    var headline          by remember(profile) { mutableStateOf(profile?.headline ?: "") }
    var bio               by remember(profile) { mutableStateOf(profile?.bio ?: "") }
    var location          by remember(profile) { mutableStateOf(profile?.location ?: "") }
    var website           by remember(profile) { mutableStateOf(profile?.website ?: "") }
    var githubUrl         by remember(profile) { mutableStateOf(profile?.githubUrl ?: "") }
    var linkedInUrl       by remember(profile) { mutableStateOf(profile?.linkedInUrl ?: "") }
    var company           by remember(profile) { mutableStateOf(profile?.company ?: "") }
    var role              by remember(profile) { mutableStateOf(profile?.role ?: "") }
    var education         by remember(profile) { mutableStateOf(profile?.education ?: "") }
    var skillsText        by remember(profile) { mutableStateOf(profile?.skills?.joinToString(", ") ?: "") }
    var langsText         by remember(profile) { mutableStateOf(profile?.programmingLanguages?.joinToString(", ") ?: "") }
    var photoUrl          by remember(profile) { mutableStateOf(profile?.photoUrl) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { photoUrl = it.toString() }
    }

    val snackbarHostState = remember { SnackbarHostState() }
    var saved by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = DevX27Theme.colors.onBackground)
                    }
                },
                actions = {
                    TextButton(onClick = {
                        // Save — update ViewModel
                        viewModel.updateProfile(
                            displayName = displayName,
                            headline = headline.ifBlank { null },
                            bio = bio.ifBlank { null },
                            location = location.ifBlank { null },
                            website = website.ifBlank { null },
                            githubUrl = githubUrl.ifBlank { null },
                            linkedInUrl = linkedInUrl.ifBlank { null },
                            company = company.ifBlank { null },
                            role = role.ifBlank { null },
                            education = education.ifBlank { null },
                            skills = skillsText.split(",").map { it.trim() }.filter { it.isNotBlank() },
                            programmingLanguages = langsText.split(",").map { it.trim() }.filter { it.isNotBlank() },
                            photoUrl = photoUrl,
                        )
                        saved = true
                        navController.popBackStack()
                    }) {
                        Text("Save", color = DevX27Theme.colors.xpSuccess, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DevX27Theme.colors.background)
            )
        },
        containerColor = DevX27Theme.colors.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).imePadding(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Avatar initials preview or image
            item {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(DevX27Theme.colors.xpSuccess.copy(alpha = 0.2f))
                            .clickable { photoPickerLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        if (photoUrl != null) {
                            AsyncImage(
                                model = photoUrl,
                                contentDescription = "Profile Picture",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                            )
                        } else {
                            Text(
                                displayName.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("").uppercase(),
                                fontSize = 28.sp, fontWeight = FontWeight.Black, color = DevX27Theme.colors.xpSuccess
                            )
                        }
                    }
                }
                Text(
                    "Tap to change photo", 
                    fontSize = 12.sp, 
                    color = DevX27Theme.colors.onSurfaceSubtle, 
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(Modifier.height(8.dp))
            }

            item { SectionLabel("Basic Info") }
            item { ProfileField("Full Name", displayName, Icons.Default.Person) { displayName = it } }
            item { ProfileField("Headline", headline, Icons.Default.Work, placeholder = "e.g. Software Engineer @ Google") { headline = it } }
            item { ProfileField("Location", location, Icons.Default.LocationOn, placeholder = "e.g. San Francisco, CA") { location = it } }

            item { Spacer(Modifier.height(8.dp)); SectionLabel("About") }
            item { ProfileFieldMultiline("Bio / Summary", bio, Icons.Default.Info, placeholder = "Tell the world about yourself…") { bio = it } }

            item { Spacer(Modifier.height(8.dp)); SectionLabel("Work") }
            item { ProfileField("Current Company", company, Icons.Default.Business, placeholder = "Company or organisation") { company = it } }
            item { ProfileField("Job Title / Role", role, Icons.Default.Badge, placeholder = "e.g. Senior Developer") { role = it } }
            item { ProfileField("Education", education, Icons.Default.School, placeholder = "University or course") { education = it } }

            item { Spacer(Modifier.height(8.dp)); SectionLabel("Links") }
            item { ProfileField("Website", website, Icons.Default.Language, placeholder = "https://yoursite.com", keyboardType = KeyboardType.Uri) { website = it } }
            item { ProfileField("GitHub", githubUrl, Icons.Default.Code, placeholder = "https://github.com/username", keyboardType = KeyboardType.Uri) { githubUrl = it } }
            item { ProfileField("LinkedIn", linkedInUrl, Icons.Default.Link, placeholder = "https://linkedin.com/in/username", keyboardType = KeyboardType.Uri) { linkedInUrl = it } }

            item { Spacer(Modifier.height(8.dp)); SectionLabel("Skills") }
            item { ProfileField("Skills", skillsText, Icons.Default.Star, placeholder = "Python, Kotlin, AWS (comma-separated)") { skillsText = it } }
            item { ProfileField("Programming Languages", langsText, Icons.Default.Terminal, placeholder = "Python, Kotlin, JavaScript…") { langsText = it } }

            item { Spacer(Modifier.height(32.dp)) }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text.uppercase(),
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = DevX27Theme.colors.xpSuccess,
        modifier = Modifier.padding(start = 4.dp, top = 8.dp, bottom = 4.dp)
    )
}

@Composable
private fun ProfileField(
    label: String,
    value: String,
    icon: ImageVector,
    placeholder: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 13.sp) },
        placeholder = { Text(placeholder, fontSize = 13.sp, color = DevX27Theme.colors.onSurfaceSubtle) },
        leadingIcon = { Icon(icon, null, tint = DevX27Theme.colors.onSurfaceMuted, modifier = Modifier.size(20.dp)) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = DevX27Theme.colors.onBackground,
            unfocusedTextColor = DevX27Theme.colors.onBackground,
            focusedBorderColor = DevX27Theme.colors.xpSuccess,
            unfocusedBorderColor = DevX27Theme.colors.divider,
            focusedContainerColor = DevX27Theme.colors.surface,
            unfocusedContainerColor = DevX27Theme.colors.surface,
        ),
        singleLine = true,
    )
}

@Composable
private fun ProfileFieldMultiline(
    label: String,
    value: String,
    icon: ImageVector,
    placeholder: String = "",
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 13.sp) },
        placeholder = { Text(placeholder, fontSize = 13.sp, color = DevX27Theme.colors.onSurfaceSubtle) },
        leadingIcon = { Icon(icon, null, tint = DevX27Theme.colors.onSurfaceMuted, modifier = Modifier.size(20.dp)) },
        modifier = Modifier.fillMaxWidth().heightIn(min = 100.dp),
        shape = RoundedCornerShape(12.dp),
        maxLines = 5,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = DevX27Theme.colors.onBackground,
            unfocusedTextColor = DevX27Theme.colors.onBackground,
            focusedBorderColor = DevX27Theme.colors.xpSuccess,
            unfocusedBorderColor = DevX27Theme.colors.divider,
            focusedContainerColor = DevX27Theme.colors.surface,
            unfocusedContainerColor = DevX27Theme.colors.surface,
        ),
    )
}
