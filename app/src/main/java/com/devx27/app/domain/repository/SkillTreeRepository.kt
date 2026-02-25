package com.devx27.app.domain.repository

import com.devx27.app.domain.model.SkillGraph
import kotlinx.coroutines.flow.Flow

// ─────────────────────────────────────────────────────────────────────────────
// SkillTreeRepository — provides the static skill graph structure and computes
// which nodes are unlocked based on the user's current XP.
// The graph definition itself is static; unlock state is dynamic.
// ─────────────────────────────────────────────────────────────────────────────
interface SkillTreeRepository {
    /**
     * Emits the full SkillGraph with [unlockedIds] recalculated
     * whenever [userXp] changes. Use in combination with XPRepository
     * to get a reactive skill-unlock stream.
     */
    fun getSkillGraph(userXp: Int): SkillGraph
}
