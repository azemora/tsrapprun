/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  Reminder.kt — modelo de lembrete simples (texto via voz).   ║
 * ║                                                              ║
 * ║  Diferente de Registro (que documenta um momento), Lembrete  ║
 * ║  é uma to-do leve — algo que o pai/mãe quer não esquecer:    ║
 * ║  "comprar fralda", "ligar pra pediatra", "vacinar amanhã".   ║
 * ║                                                              ║
 * ║  Persiste em JSON criptografado igual aos outros entities.   ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.reminders

import kotlinx.serialization.Serializable

@Serializable
data class Reminder(
    val id: String,
    val text: String,
    val createdAt: Long,
    val completed: Boolean = false,
    /**
     * Data prevista (epoch ms) — opcional. Quando definida, o lembrete
     * aparece marcado no calendário e pode disparar lembrete local.
     * Auto-detectada da transcrição ("amanhã", "próxima quarta") ou
     * setada manualmente via chips na captura.
     */
    val dueAt: Long? = null
)
