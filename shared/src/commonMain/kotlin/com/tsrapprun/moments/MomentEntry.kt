/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  MomentEntry.kt - Modelo de registro de momentos            ║
 * ║                                                             ║
 * ║  Representa uma entrada do diário do usuário, ou uma         ║
 * ║  marca automática de marco de vida (semana ou mesversário).  ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.moments

import kotlinx.serialization.Serializable

@Serializable
enum class MomentType {
    /** Entrada manual diária — "o que aconteceu hoje?". */
    DAILY,

    /** Entrada manual semanal — "o que aconteceu essa semana?". */
    WEEKLY,

    /** Auto: marco de uma nova semana de vida da criança. */
    WEEK_OF_LIFE,

    /** Auto: mesversário (cada mês de vida no primeiro ano). */
    MESVERSARIO
}

@Serializable
data class MomentEntry(
    val id: String,
    val text: String,
    val type: MomentType,
    val createdAt: Long,
    /** Início do período referenciado. */
    val periodStart: Long,
    /** Fim do período referenciado. */
    val periodEnd: Long,
    /**
     * Para [MomentType.WEEK_OF_LIFE] e [MomentType.MESVERSARIO]:
     * número da semana (1..) ou do mês (1..) da vida da criança.
     * Para tipos manuais: 0.
     */
    val milestoneNumber: Int = 0
)
