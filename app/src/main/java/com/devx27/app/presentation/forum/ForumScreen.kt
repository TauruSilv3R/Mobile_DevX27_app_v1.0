package com.devx27.app.presentation.forum

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    val comments: List<ForumComment>,
    val isLikedByMe: Boolean = false
)

data class ForumComment(
    val id: String,
    val author: String,
    val initials: String,
    val message: String,
    val timeAgo: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForumScreen(navController: NavController) {
    val initialPosts = listOf(
        ForumPost(
            "1", "Sarah Jen", "SJ",
            "How do I optimize Dijkstra's Algorithm?",
            "I'm currently trying to solve the 'Shortest Path in Grid' problem, but my priority queue implementation is TLEing on the huge test case. Any hints?",
            "2h ago", 14,
            comments = listOf(
                ForumComment("c1", "Arjun", "AS", "Try a custom comparator and reuse buffers to avoid allocations.", "1h ago"),
                ForumComment("c2", "Mike", "MR", "Also check if you can prune diagonals; that saved me ~30%.", "45m ago")
            )
        ),
        ForumPost(
            "2", "Arjun Shz", "AS",
            "Thoughts on the new Weekly Tournament format?",
            "I think the time penalty changes are really shaking up the leaderboard. What does everyone else think? Less forgiving than before.",
            "5h ago", 32,
            comments = listOf(
                ForumComment("c3", "Sarah", "SJ", "I like it, forces cleaner first submissions.", "4h ago")
            )
        ),
        ForumPost(
            "3", "Priya K", "PK",
            "Just reached Elite rank using mostly Kotlin!",
            "Super excited! I was sticking to Python for months but picking up Kotlin's functional tools made parsing strings so much cleaner here.",
            "1d ago", 108,
            comments = listOf(
                ForumComment("c4", "Dev", "DV", "Congrats! Any tips for fast IO?", "20h ago"),
                ForumComment("c5", "Lia", "LI", "Using sequences or byte arrays?", "18h ago")
            )
        ),
        ForumPost(
            "4", "Mike Ross", "MR",
            "Has anyone solved 'Valid Parentheses' efficiently without a standard Stack implementation?",
            "I noticed someone had a really wacky bitwise solution but the code is obfuscated. Looking for some insights into non-traditional approaches.",
            "2d ago", 6,
            comments = emptyList()
        )
    )

    val posts = remember { mutableStateListOf<ForumPost>().apply { addAll(initialPosts) } }
    var selectedPost by remember { mutableStateOf<ForumPost?>(null) }
    var newTitle by remember { mutableStateOf("") }
    var newContent by remember { mutableStateOf("") }
    var showCreateDialog by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

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
                IconButton(onClick = { showCreateDialog = true }) {
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
                            val updated = posts.map {
                                if (it.id == post.id) {
                                    if (it.isLikedByMe) it.copy(likes = it.likes - 1, isLikedByMe = false)
                                    else it.copy(likes = it.likes + 1, isLikedByMe = true)
                                } else it
                            }.sortedByDescending { it.likes }
                            posts.clear(); posts.addAll(updated)
                        },
                        onCommentClick = { selectedPost = post }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
    }

    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text("New Discussion", fontWeight = FontWeight.Bold) },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newTitle.isNotBlank() && newContent.isNotBlank()) {
                            val newPost = ForumPost(
                                id = System.currentTimeMillis().toString(),
                                author = "You",
                                avatarInitials = "YO",
                                title = newTitle,
                                content = newContent,
                                timeAgo = "Just now",
                                likes = 0,
                                comments = emptyList(),
                                isLikedByMe = false
                            )
                            posts.add(0, newPost)
                            newTitle = ""
                            newContent = ""
                            showCreateDialog = false
                        }
                    }
                ) { Text("Post", color = DevX27Theme.colors.xpSuccess, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) { Text("Cancel") }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    TextField(
                        value = newTitle,
                        onValueChange = { newTitle = it },
                        placeholder = { Text("Title") },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = DevX27Theme.colors.surface,
                            unfocusedContainerColor = DevX27Theme.colors.surface,
                            focusedIndicatorColor = DevX27Theme.colors.actionColor,
                            unfocusedIndicatorColor = DevX27Theme.colors.divider,
                            focusedTextColor = DevX27Theme.colors.onBackground,
                            unfocusedTextColor = DevX27Theme.colors.onBackground,
                        ),
                        singleLine = true
                    )
                    TextField(
                        value = newContent,
                        onValueChange = { newContent = it },
                        placeholder = { Text("Write your question or idea...") },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = DevX27Theme.colors.surface,
                            unfocusedContainerColor = DevX27Theme.colors.surface,
                            focusedIndicatorColor = DevX27Theme.colors.actionColor,
                            unfocusedIndicatorColor = DevX27Theme.colors.divider,
                            focusedTextColor = DevX27Theme.colors.onBackground,
                            unfocusedTextColor = DevX27Theme.colors.onBackground,
                        ),
                        minLines = 3
                    )
                }
            }
        )
    }

    selectedPost?.let { post ->
        var commentText by remember(post.id) { mutableStateOf("") }
        val sheet = sheetState
        ModalBottomSheet(
            onDismissRequest = { selectedPost = null },
            sheetState = sheet,
            containerColor = DevX27Theme.colors.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Comments", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DevX27Theme.colors.onBackground)
                    IconButton(onClick = { selectedPost = null }) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = DevX27Theme.colors.onSurfaceMuted)
                    }
                }
                Text(post.title, fontWeight = FontWeight.SemiBold, color = DevX27Theme.colors.onBackground)
                Spacer(Modifier.height(12.dp))
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().heightIn(max = 320.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(post.comments, key = { it.id }) { comment ->
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(DevX27Theme.colors.xpSuccessBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(comment.initials, fontWeight = FontWeight.Black, color = DevX27Theme.colors.xpSuccess, fontSize = 12.sp)
                            }
                            Column {
                                Text(comment.author, fontWeight = FontWeight.SemiBold, color = DevX27Theme.colors.onBackground, fontSize = 13.sp)
                                Text(comment.message, color = DevX27Theme.colors.onSurfaceMuted, fontSize = 13.sp)
                                Text(comment.timeAgo, color = DevX27Theme.colors.onSurfaceSubtle, fontSize = 11.sp)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextField(
                        value = commentText,
                        onValueChange = { commentText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Add a comment") },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = DevX27Theme.colors.surface,
                            unfocusedContainerColor = DevX27Theme.colors.surface,
                            focusedIndicatorColor = DevX27Theme.colors.actionColor,
                            unfocusedIndicatorColor = DevX27Theme.colors.divider,
                            focusedTextColor = DevX27Theme.colors.onBackground,
                            unfocusedTextColor = DevX27Theme.colors.onBackground,
                            cursorColor = DevX27Theme.colors.actionColor
                        ),
                        singleLine = true
                    )
                    IconButton(
                        enabled = commentText.isNotBlank(),
                        onClick = {
                            val newComment = ForumComment(
                                id = "c${System.currentTimeMillis()}",
                                author = "You",
                                initials = "YO",
                                message = commentText.trim(),
                                timeAgo = "Just now"
                            )
                            val updated = posts.map {
                                if (it.id == post.id) it.copy(comments = it.comments + newComment) else it
                            }
                            posts.clear(); posts.addAll(updated)
                            selectedPost = updated.firstOrNull { it.id == post.id }
                            commentText = ""
                        }
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send", tint = if (commentText.isNotBlank()) DevX27Theme.colors.xpSuccess else DevX27Theme.colors.onSurfaceMuted)
                    }
                }
                Spacer(Modifier.height(24.dp))
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
                        post.comments.size.toString(),
                        fontSize = 13.sp,
                        color = DevX27Theme.colors.onSurfaceMuted,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
