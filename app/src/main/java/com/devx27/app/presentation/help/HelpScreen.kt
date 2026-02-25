package com.devx27.app.presentation.help

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.devx27.app.presentation.theme.DevX27Theme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    val sections = listOf(
        HelpSection(
            title = "Getting Started",
            icon = Icons.Default.RocketLaunch,
            items = listOf(
                HelpItem("How to gain XP?", "You gain XP by solving challenges in Practice mode or winning Battles. Harder challenges award more XP."),
                HelpItem("What are Levels?", "Levels represent your overall progress. As you reach XP milestones, you'll level up and unlock new skills.")
            )
        ),
        HelpSection(
            title = "Code Editor",
            icon = Icons.Default.Code,
            items = listOf(
                HelpItem("Supported Languages", "We currently support Python, Kotlin, C++, Java, and JavaScript."),
                HelpItem("How to run code?", "Tap the 'Play' icon in the editor to run your code against mock test cases."),
                HelpItem("Keyboard Bar", "The bar above your keyboard provides quick access to common coding symbols like brackets, pipes, and cursor keys.")
            )
        ),
        HelpSection(
            title = "Battle Arena",
            icon = Icons.Default.SportsMartialArts,
            items = listOf(
                HelpItem("Finding a Match", "Tap 'Search' in Compete mode. We'll pair you with an opponent of similar skill level."),
                HelpItem("Winning a Battle", "Passed all test cases before your opponent to win. Speed is key!")
            )
        ),
        HelpSection(
            title = "Account & Privacy",
            icon = Icons.Default.Security,
            items = listOf(
                HelpItem("Is my data local?", "Currently, your data is stored locally on this device. Cloud sync is coming in Phase 5."),
                HelpItem("Contact Support", "For technical issues, reach out to support@devx27.com")
            )
        )
    )

    Scaffold(
        containerColor = DevX27Theme.colors.background,
        topBar = {
            TopAppBar(
                title = { Text("Help & Documentation", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DevX27Theme.colors.background,
                    titleContentColor = DevX27Theme.colors.onBackground,
                    navigationIconContentColor = DevX27Theme.colors.onBackground
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search help topics...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = DevX27Theme.colors.xpSuccess,
                    unfocusedBorderColor = DevX27Theme.colors.divider
                )
            )

            sections.forEach { section ->
                HelpSectionView(section)
                Spacer(Modifier.height(16.dp))
            }
            
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun HelpSectionView(section: HelpSection) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = section.icon,
                contentDescription = null,
                tint = DevX27Theme.colors.xpSuccess,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = section.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = DevX27Theme.colors.onBackground
            )
        }
        
        Spacer(Modifier.height(12.dp))
        
        section.items.forEach { item ->
            HelpCard(item)
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun HelpCard(item: HelpItem) {
    var expanded by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(containerColor = DevX27Theme.colors.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.question,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = DevX27Theme.colors.onBackground,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = DevX27Theme.colors.onSurfaceMuted
                )
            }
            
            if (expanded) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = item.answer,
                    fontSize = 14.sp,
                    color = DevX27Theme.colors.onSurfaceMuted,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

data class HelpSection(
    val title: String,
    val icon: ImageVector,
    val items: List<HelpItem>
)

data class HelpItem(
    val question: String,
    val answer: String
)
