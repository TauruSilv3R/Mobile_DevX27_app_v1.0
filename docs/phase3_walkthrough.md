# DevX27 — Phase 3 Walkthrough: Visual Skill Tree + Real-Time Leaderboard

---

## 📁 New Files in Phase 3

```
domain/
├── model/SkillNode.kt              ← SkillNode, SkillEdge, SkillGraph, SkillCategory
└── repository/SkillTreeRepository.kt

data/repository/
├── SkillTreeRepositoryImpl.kt      ← 15 nodes, BFS unlock algo, edge derivation
└── MockXPRepositoryImpl.kt         ← (updated: 10-user leaderboard)

presentation/
├── skilltree/
│   ├── SkillTreeViewModel.kt       ← XP flow → graph recomputation
│   └── SkillTreeScreen.kt          ← Canvas renderer + pan/zoom + detail panel
└── leaderboard/
    ├── LeaderboardViewModel.kt     ← (rebuilt: current-user rank detection)
    └── LeaderboardScreen.kt        ← (rebuilt: podium + itemsIndexed LazyColumn)

build.gradle.kts → exportApk v1.2_Growth
```

---

## 🌳 Task 1: Skill Tree — Visual Preview

### Node Structure (world-space coordinates, dp)

```
                    ⚡ Fundamentals (400, 60)
                            │
         ┌──────────────────┼───────────────────┐
      🌳 DSA              ⚙️ Backend           ∑ Algorithms
      (120, 200)          (400, 200)            (680, 200)
      /    │    \          /   │   \             /   │   \
   [] Arr  ↔ LL  🌲Trees  🌐REST 🗄DB  🏗SysD  ↕Sort 🔍BS 🕸Graph
  (40,360)(140,360)(240,360)(320,360)(420,360)(420,500)(600,360)(700,360)(800,360)
       │                      │                          │
     DP DP              🏗 SysDesign                🌌 AdvGraph
    (140,500)             (420,500)                 (760,500)
                                    \              /
                                      🤖 AI / ML
                                       (490, 640)
```

### XP Unlock Thresholds

| Skill             | XP Required | Category    |
|------------------|------------|-------------|
| Fundamentals      | 0           | Core        |
| DSA, Algorithms   | 0           | —           |
| Backend           | 50          | Backend     |
| Arrays, Sorting   | 0           | DSA / Algo  |
| Linked Lists      | 50          | DSA         |
| REST APIs         | 50          | Backend     |
| Binary Search     | 60          | Algorithms  |
| Trees             | 100         | DSA         |
| Databases         | 100         | Backend     |
| Graph Traversal   | 150         | Algorithms  |
| Dynamic Prog.     | 300         | DSA         |
| System Design     | 400         | System      |
| Adv. Graph        | 400         | Algorithms  |
| **AI / ML**       | **800**     | AI          |

### BFS Unlock Algorithm
```kotlin
// SkillTreeRepositoryImpl.getSkillGraph(userXp)
// A node unlocks when:
//   (1) All parent nodes are already unlocked, AND
//   (2) userXp >= node.xpThreshold

BFS from root:
  dequeue "root" → xp=0, no parents → ✅ unlock
  enqueue children: [DSA, Backend, Algorithms]
  dequeue "DSA" → xp=0, parent=root✅ → ✅ unlock
  dequeue "Backend" → xp=50 needed; user has 340 → ✅ unlock
  dequeue "Algorithms" → xp=0 → ✅ unlock
  dequeue "Arrays" → xp=0, parent=DSA✅ → ✅ unlock
  ...etc until xpThreshold > userXp → 🔒 locked
```

### Canvas Rendering Pipeline
```
Canvas {
  translate(panOffset.x, panOffset.y) {      ← pan transform
    scale(zoomFactor, Offset.Zero) {          ← zoom from top-left
      
      // ① Edges (drawn first, beneath nodes)
      forEach(graph.edges) {
        if (both endpoints unlocked)  → neon green (#1BB661), alpha=0.85, width=2.5dp
        else                          → grey divider,           alpha=0.35, width=1.5dp
        drawLine(start=nodeCircumference, end=nodeCircumference, cap=Round)
      }
      
      // ② Nodes
      forEach(graph.nodes) {
        if (unlocked) drawCircle(glowRing, radius=nodeRadius*1.5, alpha=0.15)
        if (selected) drawCircle(accentRing, stroke=2dp)
        drawCircle(fill)    ← green(alpha=0.15) or grey
        drawCircle(border)  ← green or grey
        drawText(icon)      ← centered in circle
        drawText(label)     ← below circle, 9sp
      }
    }
  }
}
```

### Pan & Zoom Gesture Handling
```kotlin
Modifier
  // Two-finger: pinch-zoom + pan simultaneously
  .pointerInput(Unit) {
      detectTransformGestures { _, pan, zoomChange, _ ->
          zoom = (zoom * zoomChange).coerceIn(0.3f, 3.5f)
          panOffset += pan
      }
  }
  // Single tap: node selection
  .pointerInput(graph) {
      detectTapGestures { tapOffset ->
          // Convert screen → world coords
          val worldTap = Offset(
              (tapOffset.x - panOffset.x) / zoom,
              (tapOffset.y - panOffset.y) / zoom,
          )
          // Find nearest node within radius
          val tapped = graph.nodes.firstOrNull { node ->
              distance(worldTap, nodeCenter(node)) <= nodeRadiusPx
          }
          viewModel.onNodeSelected(tapped)
      }
  }
```

---

## 🏆 Task 2: Real-Time Leaderboard — Architecture

### Firestore Query (XPRepositoryImpl — production)
```kotlin
// XPRepositoryImpl.getLeaderboard()
firestore.collection("users")
    .orderBy("totalXp", Query.Direction.DESCENDING)
    .limit(50)
    .addSnapshotListener { snapshot, error ->
        // Called automatically when ANY user's XP changes
        val entries = snapshot.documents.map { doc -> LeaderboardEntry(...) }
        trySend(Result.success(entries))
    }
// ↑ This fires in real-time — every XP award by ANY user updates
//   the leaderboard for EVERY device currently watching it.
```

### Current-User Highlighting Logic
```kotlin
// LeaderboardViewModel
val rank = entries.indexOfFirst { it.userId == currentUserId }
               .takeIf { it >= 0 }
               ?.let { it + 1 }   // Convert 0-index to 1-based rank

// LeaderboardScreen — row rendering
if (isCurrentUser) {
    containerColor = xpSuccessBg            // Green tinted row
    leftAccentBar  = 3dp wide, xpSuccess    // Left edge bar
    "YOU" badge    = XP green               // Label pill
}
```

### LazyColumn Optimization — Why `itemsIndexed` + Stable Keys?
```kotlin
LazyColumn {
    itemsIndexed(
        items = entries,
        key   = { _, entry -> entry.userId },  // ← CRITICAL
    ) { index, entry ->
        LeaderboardRow(rank = index + 4, entry = entry, ...)
    }
}

// Without key: every Firestore update → full list recompose → visible flicker
// With stable key: only CHANGED rows recompose → smooth real-time feel
// This is the same technique used by Twitter/X for timeline updates.
```

### Leaderboard UI Structure
```
┌─────────────────────────────────────┐
│  Leaderboard          [You: #7]     │  ← Header with rank pill
│  Global • Real-time                 │
│  ● Live — updates automatically     │
├─────────────────────────────────────┤
│ 🥈 Wei   🥇 Arjun   🥉 Priya       │  ← Podium (spring pop-in)
│ [110dp]  [140dp]   [90dp]          │
├─────────────────────────────────────┤
│ #4  Sofia Rossi     Lv.7 · 30✓  3500 XP ⚡ │
│ #5  Marcus Johnson  Lv.7 · 27✓  3200 XP ⚡ │
│ ▌#7  Dev X27  [YOU]  Lv.6 · 22✓  2750 XP ⚡ │  ← Green tinted + accent bar
│ #8  Lena Müller     Lv.5 · 19✓  2400 XP ⚡ │
│ …                                          │
└─────────────────────────────────────┘
```

---

## 🏗️ Build — exportApk v1.2_Growth

```bash
./gradlew exportApk
# → build-output/DevX27_v1.2_Growth.apk
```

---

## 🚀 Phase 4 Readiness

| Feature                      | Status      | Notes |
|-----------------------------|------------|-------|
| Skill Tree Canvas            | ✅ Ready    | 15 nodes, BFS unlock, pan/zoom/tap |
| Node detail panel            | ✅ Ready    | Slide-up with XP-to-go display |
| Leaderboard real-time UI     | ✅ Ready    | Podium + itemsIndexed stable keys |
| Current-user row highlight   | ✅ Ready    | Green accent bar + YOU badge |
| exportApk v1.2               | ✅ Ready    | `./gradlew exportApk` |
| Auth Login / Register UI     | ⏳ Phase 4  | Email/Password SignIn screens |
| Real Firestore XP listener   | ⏳ Phase 4  | Swap MockXPRepositoryImpl |
| Firebase Functions evaluator | ⏳ Phase 4  | Wire `evaluateSolution` Cloud Fn |
| Push notifications (XP gain) | ⏳ Future   | FCM integration |
| Offline mode                 | ⏳ Future   | Firestore persistent cache already set up |
