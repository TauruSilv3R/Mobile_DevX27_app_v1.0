package com.devx27.app.presentation.careerhub

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.devx27.app.presentation.theme.DevX27Theme

@Composable
fun CareerJobDetailScreen(navController: NavController, jobId: String) {
    val job = remember(jobId) {
        // In a real app, fetch from repository; placeholder matches list
        sampleJobs().firstOrNull { it.id == jobId } ?: sampleJobs().first()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DevX27Theme.colors.background)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = DevX27Theme.colors.onBackground)
            }
            Spacer(Modifier.width(8.dp))
            Text("Job Details", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = DevX27Theme.colors.onBackground)
        }

        Spacer(Modifier.height(16.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = DevX27Theme.colors.surface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(job.title, fontSize = 20.sp, fontWeight = FontWeight.Black, color = DevX27Theme.colors.onBackground)
                Text(job.company, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = DevX27Theme.colors.onSurfaceMuted)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = DevX27Theme.colors.onSurfaceMuted, modifier = Modifier.height(16.dp))
                    Text(job.location, fontSize = 12.sp, color = DevX27Theme.colors.onSurfaceSubtle)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    DetailChip(job.type)
                    DetailChip(job.salary)
                }
                Text("About the role", fontWeight = FontWeight.Bold, color = DevX27Theme.colors.onBackground)
                Text(job.description, fontSize = 14.sp, color = DevX27Theme.colors.onSurfaceMuted, lineHeight = 20.sp)
                Button(
                    onClick = { /* TODO: apply link */ },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DevX27Theme.colors.actionColor, contentColor = DevX27Theme.colors.background)
                ) {
                    Text("Apply Now", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

private fun DetailChip(text: String) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = DevX27Theme.colors.surfaceInput)
    ) {
        Text(text, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), color = DevX27Theme.colors.onSurfaceMuted, fontSize = 12.sp)
    }
}

private fun sampleJobs(): List<Job> = listOf(
    Job("1", "Android Engineer", "JetBrains", "Remote (US)", "$140k–$180k", "Full-time", "Own Compose tooling and ship delightful developer experiences."),
    Job("2", "Mobile Architect", "Stripe", "New York, NY", "$180k–$230k", "Full-time", "Lead architecture for mobile payments SDKs and high-scale clients."),
    Job("3", "Kotlin Multiplatform Dev", "Airbnb", "Remote", "$150k–$200k", "Contract", "Build shared KMP modules powering the host and guest apps."),
    Job("4", "Junior Android", "Startup X", "Austin, TX", "$90k–$120k", "Full-time", "Ship features fast with Compose, Firebase, and GraphQL."),
)
