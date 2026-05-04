/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  MomentEntry.kt - Modelo de registro de momentos            ║
 * ║                                                             ║
 * ║  Tipos:                                                      ║
 * ║   • DAILY / WEEKLY    — entradas manuais do usuário          ║
 * ║   • PREGNANCY_WEEK    — auto: semana N pra nascer (gestação) ║
 * ║   • DAY_OF_LIFE       — auto: dia N de vida (recém-nascido)  ║
 * ║   • MESVERSARIO       — auto: mês N de vida (até 1 ano)      ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.moments

import kotlinx.serialization.Serializable

@Serializable
enum class MomentType {
    /** Manual diário — "o que aconteceu hoje?". */
    DAILY,

    /** Manual semanal — "o que aconteceu essa semana?". */
    WEEKLY,

    /** Auto: countdown semanal de gestação (`milestoneNumber` = semanas pra nascer). */
    PREGNANCY_WEEK,

    /** Auto: dia N de vida (1..30, antes do primeiro mesversário). */
    DAY_OF_LIFE,

    /** Auto: mesversário (1..12 meses de vida). */
    MESVERSARIO
}

@Serializable
data class MomentEntry(
    val id: String,
    val text: String,
    val type: MomentType,
    val createdAt: Long,
    val periodStart: Long,
    val periodEnd: Long,
    /**
     * Número do marco:
     *  • PREGNANCY_WEEK → semanas restantes pra nascer
     *  • DAY_OF_LIFE → dia da vida (1..30)
     *  • MESVERSARIO → mês de vida (1..12)
     *  • DAILY/WEEKLY → 0
     */
    val milestoneNumber: Int = 0
)
