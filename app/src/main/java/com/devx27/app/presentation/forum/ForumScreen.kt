package com.devx27.app.presentation.forum

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.devx27.app.presentation.theme.DevX27Theme

data class ForumPost(
    val id: String,
    val author: String,
    val avatarInitials: String,
    val title: String,
    val content: String,
    val timeAgo: String,
    val likes: Int,
    val comments: Int,
    val isLikedByMe: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForumScreen(navController: NavController) {
    var posts by remember {
        mutableStateOf(
            listOf(
                ForumPost("1", "Sarah Jen", "SJ", "How do I optimize Dijkstra's Algorithm?", "I'm currently trying to solve the 'Shortest Path in Grid' problem, but my priority queue implementation is TLEing on the huge test case. Any hints?", "2h ago", 14, 5),
                ForumPost("2", "Arjun Shz", "AS", "Thoughts on the new Weekly Tournament format?", "I think the time penalty changes are really shaking up the leaderboard. What does everyone else think? Less forgiving than before.", "5h ago", 32, 12),
                ForumPost("3", "Priya K", "PK", "Just reached Elite rank using mostly Kotlin!", "Super excited! I was sticking to Python for months but picking up Kotlin's functional tools made parsing strings so much cleaner here.", "1d ago", 108, 22),
                ForumPost("4", "Mike Ross", "MR", "Has anyone solved 'Valid Parentheses' efficiently without a standard Stack implementation?", "I noticed someone had a really wacky bitwise solution but the code is obfuscated. Looking for some insights into non-traditional approaches.", "2d ago", 6, 1)
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DevX27Theme.colors.background)
    ) {
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Discussions",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = DevX27Theme.colors.onBackground
                )
                IconButton(onClick = { /* TODO Add Post */ }) {
                    Icon(Icons.Default.Add, contentDescription = "New Discussion", tint = DevX27Theme.colors.xpSuccess)
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(posts, key = { it.id }) { post ->
                    ForumPostCard(
                        post = post,
                        onLikeClick = {
                            posts = posts.map {
                                if (it.id == post.id) {
                                    if (it.isLikedByMe) it.copy(likes = it.likes - 1, isLikedByMe = false)
                                    else it.copy(likes = it.likes + 1, isLikedByMe = true)
                                } else it
                            }
                        },
                        onCommentClick = { /* TODO open comments */ }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
    }
}

@Composable
private fun ForumPostCard(
    post: ForumPost,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onCommentClick),
        colors = CardDefaults.cardColors(containerColor = DevX27Theme.colors.surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(DevX27Theme.colors.xpSuccessBg),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        post.avatarInitials,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = DevX27Theme.colors.xpSuccess
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(post.author, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = DevX27Theme.colors.onBackground)
                    Text(post.timeAgo, fontSize = 11.sp, color = DevX27Theme.colors.onSurfaceSubtle)
                }
            }
            Spacer(Modifier.height(12.dp))
            Text(post.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DevX27Theme.colors.onBackground)
            Spacer(Modifier.height(6.dp))
            Text(post.content, fontSize = 13.sp, color = DevX27Theme.colors.onSurfaceMuted, lineHeight = 18.sp)
            
            Spacer(Modifier.height(16.dp))
            Box(
                modifier = Modifier.fillMaxWidth().height(1.dp).background(DevX27Theme.colors.divider)
            )
            Spacer(Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                Row(
                    modifier = Modifier.clip(RoundedCornerShape(8.dp)).clickable(onClick = onLikeClick).padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        if (post.isLikedByMe) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (post.isLikedByMe) DevX27Theme.colors.xpSuccess else DevX27Theme.colors.onSurfaceMuted,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        post.likes.toString(),
                        fontSize = 13.sp,
                        color = if (post.isLikedByMe) DevX27Theme.colors.xpSuccess else DevX27Theme.colors.onSurfaceMuted,
                        fontWeight = FontWeight.Medium
                    )
                }

                Row(
                    modifier = Modifier.clip(RoundedCornerShape(8.dp)).clickable(onClick = onCommentClick).padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        Icons.Default.ChatBubbleOutline,
                        contentDescription = "Comment",
                        tint = DevX27Theme.colors.onSurfaceMuted,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        post.comments.toString(),
                        fontSize = 13.sp,
                        color = DevX27Theme.colors.onSurfaceMuted,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
