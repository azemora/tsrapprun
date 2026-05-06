/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  EventData.kt - Modelo de Dados de Evento                   ║
 * ║                                                             ║
 * ║  Representa um evento fotográfico (ex: festa, reunião).     ║
 * ║  Agrupa fotos por sessão de captura.                        ║
 * ║                                                             ║
 * ║  SEGURANÇA: Apenas metadados — nenhum dado binário.         ║
 * ║  Armazenado em índice JSON criptografado (AES-256-GCM).    ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.camera

import kotlinx.serialization.Serializable

/**
 * Metadados de um evento fotográfico.
 *
 * @property id               UUID único do evento.
 * @property name             Nome dado pelo usuário (ex: "Aniversário Maria").
 * @property createdAt        Timestamp de criação (epoch millis).
 * @property photoCount       Contagem desnormalizada para exibição rápida.
 * @property thumbnailPhotoId ID da primeira foto, usada como thumbnail na galeria.
 */
@Serializable
data class EventData(
    val id: String,
    val name: String,
    val createdAt: Long,
    val photoCount: Int = 0,
    val thumbnailPhotoId: String? = null,
    /** Nota opcional escrita pelo usuário (Caveat handwritten no detail). */
    val note: String? = null,
    /** Marcado como "marco" — primeira palavra, primeira papinha, festa, etc. */
    val isMilestone: Boolean = false
)
