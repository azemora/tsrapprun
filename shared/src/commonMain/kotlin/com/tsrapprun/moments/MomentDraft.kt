/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  MomentDraft.kt — rascunho de registro em andamento          ║
 * ║                                                              ║
 * ║  Persistido em arquivo único (substituído a cada save).      ║
 * ║  Limpo quando o registro é guardado de fato.                 ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.moments

import kotlinx.serialization.Serializable

@Serializable
data class MomentDraft(
    val title: String,
    val note: String,
    val type: String, // "DAILY" | "WEEKLY" | "MILESTONE"
    val updatedAt: Long
)
