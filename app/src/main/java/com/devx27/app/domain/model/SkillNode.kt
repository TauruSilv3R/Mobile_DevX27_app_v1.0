package com.devx27.app.domain.model

// ─────────────────────────────────────────────────────────────────────────────
// SkillNode — vertex in the Skill Tree graph.
// Each node requires [xpThreshold] to be unlocked and can only unlock
// after all nodes in [parentIds] are themselves unlocked.
// ─────────────────────────────────────────────────────────────────────────────
data class SkillNode(
    val id: String,
    val label: String,
    val icon: String,               // Emoji / unicode symbol used inside the circle
    val description: String,
    val xpThreshold: Int,           // Cumulative XP needed to unlock this node
    val parentIds: List<String>,    // Direct parents in the skill graph
    val x: Float,                   // Logical X position in the Canvas world-space (dp)
    val y: Float,                   // Logical Y position in the Canvas world-space (dp)
    val category: SkillCategory,
)

enum class SkillCategory(val displayName: String) {
    CORE("Core"),
    DSA("Data Structures & Algorithms"),
    BACKEND("Backend Engineering"),
    ALGORITHMS("Algorithms"),
    SYSTEM("System Design"),
    AI("AI / ML"),
}

// ─────────────────────────────────────────────────────────────────────────────
// SkillEdge — directed edge between two SkillNodes in the graph
// ─────────────────────────────────────────────────────────────────────────────
data class SkillEdge(
    val fromId: String,
    val toId: String,
)

// ─────────────────────────────────────────────────────────────────────────────
// SkillGraph — the complete tree structure with adjacency resolved
// ─────────────────────────────────────────────────────────────────────────────
data class SkillGraph(
    val nodes: List<SkillNode>,
    val edges: List<SkillEdge>,
    val unlockedIds: Set<String>,    // Derived from user's current XP
)
