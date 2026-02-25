# DevX27 — Phase 2 Walkthrough: Core Engine
**Code Editor UI + XP Validation Engine**

---

## 🗂️ New Files in Phase 2

```
presentation/
├── editor/
│   ├── SyntaxTheme.kt          ← Token-to-color mapping (VS Code–inspired, True Black)
│   ├── SyntaxHighlighter.kt    ← Regex tokeniser: Python + Kotlin
│   ├── CodeTextField.kt        ← BasicTextField with live AnnotatedString highlighting
│   ├── LineNumbers.kt          ← Fixed-width gutter, auto-width by digit count
│   ├── CodeEditorScreen.kt     ← Full editor screen with FAB cluster
│   └── CodeEditorViewModel.kt  ← State, run/submit lifecycle
│
└── components/
    ├── DevX27LoadingSpinner.kt  ← Custom Canvas arc spinner (XP green)
    └── SubmissionResultOverlay.kt ← Success/failure card with XP animation

data/repository/
├── MockChallengeRepositoryImpl.kt ← 7 pre-seeded challenges, mock evaluator
└── MockXPRepositoryImpl.kt        ← In-memory XP state + 10-person leaderboard

build.gradle.kts (root) → exportApk task bumped to v1.1_CoreEngine
```

---

## 🎨 Code Editor UI — Walkthrough

### Layout Structure
```
┌────────────────────────────────────┐
│  ← Challenge Title     🐍 Python ▾ │  TopAppBar
├────────────────────────────────────┤
│  1 │ def solution(nums):            │
│  2 │     # Write here              │  ← CodeTextField (scrollable X+Y)
│  3 │     pass                      │
│  4 │                               │
│  … │  …                            │
├────────────────────────────────────┤
│                        ▶ [Run]     │
│                 [↑ Submit]         │  ← Spring-animated FAB cluster
└────────────────────────────────────┘
```

### Syntax Highlighting Tokens
| Token      | Color       | Examples                    |
|-----------|-------------|------------------------------|
| Keyword   | `#569CD6`   | `def`, `fun`, `class`, `if`  |
| String    | `#CE9178`   | `"hello"`, `'world'`         |
| Comment   | `#6A9955`   | `# comment`, `// comment`    |
| Number    | `#B5CEA8`   | `42`, `3.14`                 |
| Function  | `#DCDCAA`   | `solution(`, `main(`         |
| Type      | `#4EC9B0`   | `List`, `Int`, `String`      |
| Operator  | `#D4D4D4`   | `=`, `+`, `->`, `:`          |
| Annotation| `#BBBBBB`   | `@Composable`, `@Override`   |

### FAB Spring Animation Logic
```kotlin
// AnimatedScaleFAB in CodeEditorScreen.kt
val scale by animateFloatAsState(
    targetValue   = if (enabled) 1f else 0.88f,
    animationSpec = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,  // slight bounce
        stiffness    = Spring.StiffnessLow,
    ),
)
```
When submission starts, both FABs scale down to 88% with a spring bounce — communicating "locked" state physically.

---

## ⚡ XP Validation Engine — Walkthrough

### Submission Flow (sequence)
```
User taps Submit
      │
      ▼
CodeEditorViewModel.onSubmit()
      │
      ▼
isSubmitting = true → DevX27LoadingSpinner shown
      │
      ▼
SubmitSolutionUseCase.invoke(challengeId, code, language)
      │
      ├─ MockChallengeRepositoryImpl.submitSolution()
      │    • delay(1500ms) — simulates evaluation
      │    • PASS if code.length > 30 && no "pass"/"return 0" stub
      │    • Returns SubmissionResult(passed, xpAwarded, feedback, ms)
      │
      ▼
If passed → XPRepository.awardXP(userId, xp, challengeId)
                  • Increments in-memory _xp, _level, _solved
      │
      ▼
isSubmitting = false → SubmissionResultOverlay shown
```

### XP Animation Sequence (physics-based)
```
LaunchedEffect(result) {
  1. haptic.performHapticFeedback(HapticFeedbackType.LongPress)   // physical
  2. scaleAnim.animateTo(1f, spring(DampingRatioMediumBouncy))    // card pop-in
  3. xpBarFill.animateTo(1f, spring(DampingRatioLowBouncy))       // ← KEY
     └── DampingRatioLowBouncy = overshoot then settle (iOS-level)
     └── StiffnessLow = slow, deliberate fill (not instant)
  4. glowAlpha → 0.6f in 300ms → 0f in 600ms                     // neon pulse
}
```

### Physics vs Linear — Why Spring?
| Approach   | Feel             | When to use         |
|-----------|-----------------|---------------------|
| `tween()`  | Robotic, instant | Loading, skeletons   |
| `spring()` | Alive, physical  | Rewards, success states |

The XP bar uses `DampingRatioLowBouncy` so the bar visually **overshoots** 100% then snaps back — this is the same physics model used in iOS UIKit spring animations.

---

## 🔧 MockSubmission Evaluator Logic

```kotlin
// MockChallengeRepositoryImpl.kt
val passed = code.length > 30        // not empty
          && !code.contains("pass")  // not stub  
          && !code.contains("return 0") // not stub

// Pass → returns xpReward from the matched challenge
// Fail → feedback: "Failed test case 2: expected [0,1] but got []"
```

**To test a PASS:** Write a `two_sum` solution that replaces `pass` with real logic.
**To test a FAIL:** Leave the starter `pass` in place and tap Submit.

---

## 🏗️ Build — exportApk v1.1

```bash
# From the project root:
./gradlew exportApk

# Output:
# ✅ APK exported → .../DevX27-mob/build-output/DevX27_v1.1_CoreEngine.apk
```

The task in `build.gradle.kts`:
```kotlin
val exportApk by tasks.registering {
    group        = "DevX27"
    description  = "...DevX27_v1.1_CoreEngine.apk"
    dependsOn(":app:assembleDebug")
    doLast {
        apkSource.copyTo(File(outputDir, "DevX27_v1.1_CoreEngine.apk"), overwrite = true)
    }
}
```

---

## 🚀 What's Ready for Phase 3

| Feature                      | Status     | Notes |
|-----------------------------|-----------|-------|
| Code editor (Python/Kotlin)  | ✅ Ready   | Swap to real compose-code-editor for more langs |
| Syntax highlighting          | ✅ Ready   | Regex tokenizer, extend KEYWORD sets easily |
| Spring FAB animations        | ✅ Ready   | Physics-based, iOS-parity |
| Mock submission evaluator    | ✅ Ready   | Replace with Firebase Functions call |
| Haptic feedback on success   | ✅ Ready   | HapticFeedbackType.LongPress |
| XP bar spring animation      | ✅ Ready   | DampingRatioLowBouncy overshoot |
| Neon glow pulse              | ✅ Ready   | glowAlpha Animatable |
| Mock leaderboard (10 users)  | ✅ Ready   | Swap MockXPRepositoryImpl for Firestore |
| exportApk v1.1               | ✅ Ready   | `./gradlew exportApk` |
| Firebase Functions evaluator | ⏳ Phase 3 | Wire up `evaluateSolution` Cloud Function |
| Auth flow screens            | ⏳ Phase 3 | Login/Register UI |
| Real Firestore XP listener   | ⏳ Phase 3 | Swap Mock repos, add google-services.json |
