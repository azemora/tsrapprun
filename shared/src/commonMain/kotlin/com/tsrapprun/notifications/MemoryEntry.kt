/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  MemoryEntry.kt - Entrada do Diário "Memória do Dia"        ║
 * ║                                                             ║
 * ║  Texto curto que o usuário escreve ao responder o prompt    ║
 * ║  diário de notificação. Armazenado APENAS localmente        ║
 * ║  (nunca sincronizado com a nuvem).                          ║
 * ║                                                             ║
 * ║  SEGURANÇA:                                                 ║
 * ║  - Apenas texto (sem binários)                              ║
 * ║  - Persistido em storage criptografado local                ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.notifications

import kotlinx.serialization.Serializable

/**
 * Uma entrada do diário pessoal associada a um dia.
 *
 * @property date           Data no formato "YYYY-MM-DD" (chave primária).
 * @property text           Texto livre escrito pelo usuário.
 * @property linkedPhotoId  Foto opcional associada à memória.
 * @property createdAt      Timestamp de criação (epoch millis).
 */
@Serializable
data class MemoryEntry(
    val date: String,
    val text: String,
    val linkedPhotoId: String? = null,
    val createdAt: Long
)
