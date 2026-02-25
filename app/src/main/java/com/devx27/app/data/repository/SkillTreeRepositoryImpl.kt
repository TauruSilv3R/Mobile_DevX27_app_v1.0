package com.devx27.app.data.repository

import com.devx27.app.domain.model.SkillCategory
import com.devx27.app.domain.model.SkillEdge
import com.devx27.app.domain.model.SkillGraph
import com.devx27.app.domain.model.SkillNode
import com.devx27.app.domain.repository.SkillTreeRepository
import javax.inject.Inject
import javax.inject.Singleton

// ─────────────────────────────────────────────────────────────────────────────
// SkillTreeRepositoryImpl — defines the full skill graph as a constant structure.
//
// World-space layout (Canvas dp units, origin = top-left of the Canvas world):
//
//                     [Fundamentals] (400, 60)
//                           │
//          ┌────────────────┼──────────────────┐
//       [DSA]           [Backend]           [Algos]
//      (120,200)        (400,200)           (680,200)
//      / │  \           / │  \              /  │  \
//  [Arr][LL][Tree] [REST][DB][SysD]    [Sort][BS][Graph]
//  (40,360)(140,360)(240,360)(320,360)(420,360)(520,360) (600,360)(700,360)(800,360)
//       │                                          │
//      [DP]                                  [Adv.Graph]
//    (140,500)                               (700,500)
//                                        [AI / ML]
//                                         (400,640)
// ─────────────────────────────────────────────────────────────────────────────
@Singleton
class SkillTreeRepositoryImpl @Inject constructor() : SkillTreeRepository {

    // ── Static node definitions ───────────────────────────────────────────────
    private val nodes: List<SkillNode> = listOf(

        // ── Root ─────────────────────────────────────────────────────────────
        SkillNode(
            id           = "root",
            label        = "Fundamentals",
            icon         = "⚡",
            description  = "Start your journey",
            xpThreshold  = 0,
            parentIds    = emptyList(),
            x            = 400f, y = 60f,
            category     = SkillCategory.CORE,
        ),

        // ── Level 1 branches ─────────────────────────────────────────────────
        SkillNode(id = "dsa",        label = "DSA",           icon = "🌳", description = "Data Structures & Algorithms", xpThreshold = 0,   parentIds = listOf("root"),  x = 120f,  y = 200f, category = SkillCategory.DSA),
        SkillNode(id = "backend",    label = "Backend",       icon = "⚙️", description = "Backend Engineering",          xpThreshold = 50,  parentIds = listOf("root"),  x = 400f,  y = 200f, category = SkillCategory.BACKEND),
        SkillNode(id = "algorithms", label = "Algorithms",    icon = "∑",  description = "Core algorithmic thinking",   xpThreshold = 0,   parentIds = listOf("root"),  x = 680f,  y = 200f, category = SkillCategory.ALGORITHMS),

        // ── DSA branch ───────────────────────────────────────────────────────
        SkillNode(id = "arrays",     label = "Arrays",        icon = "[]", description = "Arrays & Strings",            xpThreshold = 0,   parentIds = listOf("dsa"),   x = 40f,   y = 360f, category = SkillCategory.DSA),
        SkillNode(id = "linkedlist", label = "Linked Lists",  icon = "↔", description = "Singly & doubly linked lists", xpThreshold = 50,  parentIds = listOf("dsa"),   x = 140f,  y = 360f, category = SkillCategory.DSA),
        SkillNode(id = "trees",      label = "Trees",         icon = "🌲", description = "Binary trees & BSTs",         xpThreshold = 100, parentIds = listOf("dsa"),   x = 240f,  y = 360f, category = SkillCategory.DSA),
        SkillNode(id = "dp",         label = "Dyn. Prog",     icon = "DP", description = "Dynamic Programming",         xpThreshold = 300, parentIds = listOf("trees"), x = 140f,  y = 500f, category = SkillCategory.DSA),

        // ── Backend branch ────────────────────────────────────────────────────
        SkillNode(id = "rest",       label = "REST APIs",     icon = "🌐", description = "HTTP & REST design",          xpThreshold = 50,  parentIds = listOf("backend"),  x = 320f, y = 360f, category = SkillCategory.BACKEND),
        SkillNode(id = "databases",  label = "Databases",     icon = "🗄", description = "SQL & NoSQL",                 xpThreshold = 100, parentIds = listOf("backend"),  x = 420f, y = 360f, category = SkillCategory.BACKEND),
        SkillNode(id = "sysdesign",  label = "Sys. Design",   icon = "🏗", description = "System Architecture",        xpThreshold = 400, parentIds = listOf("databases"), x = 420f, y = 500f, category = SkillCategory.SYSTEM),

        // ── Algorithms branch ─────────────────────────────────────────────────
        SkillNode(id = "sorting",    label = "Sorting",       icon = "↕", description = "Sorting algorithms",          xpThreshold = 0,   parentIds = listOf("algorithms"), x = 600f, y = 360f, category = SkillCategory.ALGORITHMS),
        SkillNode(id = "binsearch",  label = "Bin. Search",   icon = "🔍", description = "Binary search & variants",   xpThreshold = 60,  parentIds = listOf("algorithms"), x = 700f, y = 360f, category = SkillCategory.ALGORITHMS),
        SkillNode(id = "graphtraversal", label = "Graphs",    icon = "🕸", description = "BFS, DFS, Dijkstra",         xpThreshold = 150, parentIds = listOf("algorithms"), x = 800f, y = 360f, category = SkillCategory.ALGORITHMS),
        SkillNode(id = "advgraph",   label = "Adv. Graphs",   icon = "🌌", description = "Bellman-Ford, A*, Floyd",    xpThreshold = 400, parentIds = listOf("graphtraversal"), x = 760f, y = 500f, category = SkillCategory.ALGORITHMS),

        // ── AI / ML — unlocks at high XP ─────────────────────────────────────
        SkillNode(id = "aiml",       label = "AI / ML",       icon = "🤖", description = "Machine Learning & AI",      xpThreshold = 800, parentIds = listOf("sysdesign","advgraph"), x = 490f, y = 640f, category = SkillCategory.AI),
    )

    // ── Edges (derived from parentIds) ────────────────────────────────────────
    private val edges: List<SkillEdge> = nodes
        .flatMap { node -> node.parentIds.map { parentId -> SkillEdge(parentId, node.id) } }

    // ── Unlock computation ────────────────────────────────────────────────────
    override fun getSkillGraph(userXp: Int): SkillGraph {
        val nodeMap      = nodes.associateBy { it.id }
        val unlockedIds  = mutableSetOf<String>()

        // BFS from root — a node unlocks if:
        //   1. All parents are unlocked, AND
        //   2. userXp >= node.xpThreshold
        val queue = ArrayDeque<SkillNode>()
        queue.addLast(nodes.first { it.id == "root" })

        while (queue.isNotEmpty()) {
            val current    = queue.removeFirst()
            val allParents = current.parentIds.all { it in unlockedIds }
            val xpMet      = userXp >= current.xpThreshold

            if ((current.parentIds.isEmpty() || allParents) && xpMet) {
                unlockedIds.add(current.id)
                // Enqueue children
                nodes.filter { current.id in it.parentIds }.forEach { queue.addLast(it) }
            }
        }

        return SkillGraph(nodes = nodes, edges = edges, unlockedIds = unlockedIds)
    }
}
