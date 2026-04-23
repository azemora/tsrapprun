/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  MomentEntry.kt - Modelo de registro de momentos            ║
 * ║                                                             ║
 * ║  Representa uma entrada do diário/registro do usuário.      ║
 * ║  Pode ser diário ("O que aconteceu hoje?") ou               ║
 * ║  semanal ("O que aconteceu essa semana?").                  ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.moments

import kotlinx.serialization.Serializable

@Serializable
enum class MomentType {
    DAILY,
    WEEKLY
}

@Serializable
data class MomentEntry(
    val id: String,
    val text: String,
    val type: MomentType,
    val createdAt: Long,
    /** Início do período referenciado (dia ou semana). */
    val periodStart: Long,
    /** Fim do período referenciado. */
    val periodEnd: Long
)
