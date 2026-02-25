# DevX27 — Phase 4 Walkthrough: Profile Hub + BattleX27 Arena

---

## 📁 New Files in Phase 4

```
domain/
├── model/UserStats.kt              ← XPHistoryEntry, RecentActivity, UserStats
├── model/BattleState.kt            ← BattleOpponent, sealed BattleState
├── repository/ProfileRepository.kt
└── repository/BattleRepository.kt

data/repository/
├── MockProfileRepositoryImpl.kt    ← 7-day XP history + 5 recent activities
└── MockBattleRepositoryImpl.kt     ← State machine: Searching→MatchFound→Active

presentation/
├── profile/
│   ├── ProfileViewModel.kt         ← Uses ProfileRepository
│   ├── ProfileScreen.kt            ← Stats grid + chart + recent activity feed
│   └── XPHistoryChart.kt           ← Canvas line chart (spring draw-in)
└── battle/
    ├── BattleViewModel.kt          ← Countdown timer + opponent tracking
    ├── BattleLobbyScreen.kt        ← Idle + Searching pulse + MatchFound
    └── BattleArenaScreen.kt        ← Countdown top bar + progress bars + editor
```

---

## 🏠 Task 1: ProfileScreen Layout

```
┌─────────────────────────────────────────┐
│  [DX] Dev X27         dev@devx27.app  🚪 │  ← Avatar initials + sign out
├─────────────────────────────────────────┤
│ ┌──────────┐  ┌──────────┐  ┌─────────┐ │
│ │ Rank     │  │ Streak   │  │ Solved  │ │  ← 3-card Stats Grid
│ │  #42     │  │  3d 🔥   │  │   22    │ │
│ └──────────┘  └──────────┘  └─────────┘ │
├─────────────────────────────────────────┤
│  ⚡ 340 XP ────────────░  Level 1        │  ← XP Level bar
│  160 XP to Level 2                      │
├─────────────────────────────────────────┤
│  XP This Week                           │
│  ┌───────────────────────────────────┐  │
│  │        +340 XP                    │  │  ← XPhHistoryChart Canvas
│  │           ╭──●                    │  │    (spring draw-in, bezier curve,
│  │       ╭──╯                        │  │     gradient fill, day labels)
│  │ ──╮  ╭╯                           │  │
│  │   ╰──╯                            │  │
│  │ Mon Tue Wed Thu Fri Sat Sun       │  │
│  └───────────────────────────────────┘  │
├─────────────────────────────────────────┤
│  Recent Activity                        │
│  ✅ Two Sum         Easy  Python  20m   + 50 XP  │
│  ✅ Valid Parens    Easy  Python  1h    + 60 XP  │
│  ❌ Longest Sub     Med   Kotlin  2h          │
│  ✅ Longest Sub     Med   Kotlin  1d    +120 XP  │
│  ✅ Binary Tree     Med   Python  2d    +130 XP  │
└─────────────────────────────────────────┘
```

### XPHistoryChart Canvas Pipeline
```kotlin
// XPHistoryChart.kt — rendering in order:
Canvas {
  // ① Grid lines (3 horizontal rules, subtle alpha)
  for gridStep in 0..3 → drawLine(color=divider.copy(0.3f))

  // ② Gradient fill (Brush.verticalGradient, green → transparent)
  drawPath(fillPath, brush = verticalGradient(xpSuccessGlow..invisible))

  // ③ Bezier line (animated via drawProgress from Animatable)
  for each data point pair:
    path.cubicTo(controlX=midpoint, prev.y, controlX, curr.y, curr.x, curr.y)
  drawPath(line, Stroke(2.5dp, Round))

  // ④ Data point circles: glow ring → dark fill → green border
  drawCircle(glowRing, r=10dp); drawCircle(dotBg, r=5dp); drawCircle(green, Stroke(2dp))

  // ⑤ Day labels "Mon" … "Sun" at bottom
  drawText(dayLabel, below each point)

  // ⑥ Peak XP label "+340 XP" above highest data point
  drawText(peakLabel, above maxPoint)
}

// Spring draw-in:
val drawProgress = Animatable(0f)
LaunchedEffect { drawProgress.animateTo(1f, spring(NoBouncy, VeryLow)) }
// Only renders points[0..n * drawProgress] — reveals the chart smoothly
```

---

## ⚔️ Task 2: BattleX27 Lobby — infiniteTransition Code

### The Triple-Ring Pulse (Searching State)
```kotlin
// BattleLobbyScreen.kt
val infinite = rememberInfiniteTransition(label = "search_pulse")

// Outer ring: expands 0.6→1.4, fades to transparent over 1200ms
val outerScale by infinite.animateFloat(0.6f, 1.4f,
    infiniteRepeatable(tween(1200, FastOutSlowIn), RepeatMode.Restart))
val outerAlpha by infinite.animateFloat(0.6f, 0f,
    infiniteRepeatable(tween(1200, LinearEasing), RepeatMode.Restart))

// Middle ring: offset phase (starts at 600ms delay)
val midScale by infinite.animateFloat(0.6f, 1.2f,
    infiniteRepeatable(tween(1200, durationMillis=600, FastOutSlowIn), RepeatMode.Restart))
val midAlpha by infinite.animateFloat(0.45f, 0f,
    infiniteRepeatable(tween(1200, LinearEasing), RepeatMode.Restart))

// Inner core: subtle breathe 0.94↔1.06 at 700ms
val innerScale by infinite.animateFloat(0.94f, 1.06f,
    infiniteRepeatable(tween(700, FastOutSlowIn), RepeatMode.Reverse))

// Rendered as stacked Box layers at 120dp / 90dp / 56dp diameters
// All colored XpSuccess, scaled differently, completely pure Compose
```

### Double-Pulse Haptic on Match Found
```kotlin
LaunchedEffect(uiState.matchFound) {
    if (uiState.matchFound) {
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)  // Pulse 1
        delay(150)                                                    // Brief gap
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)  // Pulse 2
        viewModel.onMatchFoundAcknowledged()                         // Reset flag
    }
}
// On a real device this feels like a "double tap" — unmistakable match alert.
// iOS equivalent: UINotificationFeedbackGenerator().notificationOccurred(.success)
```

---

## 🏟️ BattleArena Layout
```
┌──────────────────────────────────────────┐
│  ⏱ 04:23   Two Sum              ⚡       │  ← TopBar (turns red at 30s)
│  You  ████████████░░░░░░░░  VS  Opp     │  ← Dual animated progress bars
│       65%                       40%     │
├──────────────────────────────────────────┤
│  1 │ def two_sum(nums, target):         │
│  2 │     seen = {}                      │  ← CodeEditor (reused)
│  3 │     for i, num in enumerate(nums): │
│  … │     …                             │
├──────────────────────────────────────────┤
│                              [↑ Submit]  │  ← FAB
└──────────────────────────────────────────┘
```

**Opponent progress** updates via `getOpponentProgress()` flow — slow, irregular increments simulating real typing. In production: Firestore `battles/{matchId}.opponentProgress` real-time listener.

---

## 🏗️ Build — exportApk v1.3_BattleAlpha

```bash
./gradlew exportApk
# → build-output/DevX27_v1.3_BattleAlpha.apk
```

---

## 🚀 Phase 5 Readiness

| Feature                       | Status      | Notes |
|------------------------------|------------|-------|
| Profile hub (stats + chart)   | ✅ Ready    | 7-day XP spring chart + stat grid |
| XP history Canvas line chart  | ✅ Ready    | Bezier + spring draw-in |
| Recent activity feed          | ✅ Ready    | itemsIndexed, relative timestamps |
| Battle lobby pulse animation  | ✅ Ready    | Triple-ring infiniteTransition |
| Double-pulse haptic           | ✅ Ready    | LongPress × 2 with 150ms gap |
| Battle arena countdown timer  | ✅ Ready    | Coroutine, red at 30s |
| Opponent progress bar         | ✅ Ready    | Animated, mock irregular updates |
| exportApk v1.3                | ✅ Ready    | `./gradlew exportApk` |
| Auth Login / Register UI      | ⏳ Phase 5  | Email/Password screens |
| Real Firestore battle sync    | ⏳ Phase 5  | Replace MockBattleRepositoryImpl |
| Firebase Functions evaluator  | ⏳ Phase 5  | Wire `evaluateSolution` Cloud Fn |
| Push notifications            | ⏳ Future   | FCM: match found + XP alerts |
