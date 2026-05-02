/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  MemoryReminderService.kt - Lógica das Lembranças           ║
 * ║                                                             ║
 * ║  Decide QUANDO e O QUÊ lembrar o usuário sobre fotos        ║
 * ║  antigas. Janelas fixas: 1 semana, 1 mês, 6 meses, 1 ano.   ║
 * ║                                                             ║
 * ║  Pure Kotlin: sem dependência de plataforma. Delega         ║
 * ║  o agendamento físico ao [NotificationScheduler].           ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.notifications

import com.tsrapprun.currentTimeMillis

/**
 * Agenda e cancela as 4 lembranças associadas a uma foto.
 *
 * IDs gerados são determinísticos: "${photoId}-${suffix}".
 * Isso garante idempotência: reagendar a mesma foto sobrescreve
 * as notificações antigas em vez de duplicar.
 */
class MemoryReminderService(private val scheduler: NotificationScheduler) {

    companion object {
        // Offsets em millis. Usamos constantes calendário-simplificado:
        // 30 dias = mês, 180 dias = 6 meses, 365 dias = ano.
        // Suficiente para lembranças emocionais (precisão de alguns dias não importa).
        private const val DAY_MS = 24L * 60 * 60 * 1000
        const val ONE_WEEK_MS = 7L * DAY_MS
        const val ONE_MONTH_MS = 30L * DAY_MS
        const val SIX_MONTHS_MS = 180L * DAY_MS
        const val ONE_YEAR_MS = 365L * DAY_MS

        private val WINDOWS = listOf(
            Window("1w", ONE_WEEK_MS, "Há 1 semana", "Uma memória de 7 dias atrás. Que tal revisitar?"),
            Window("1m", ONE_MONTH_MS, "Há 1 mês", "Essa memória completa 1 mês hoje."),
            Window("6m", SIX_MONTHS_MS, "Há 6 meses", "Meio ano atrás você registrava esta memória."),
            Window("1y", ONE_YEAR_MS, "Primeiro aniversário", "Hoje faz 1 ano dessa memória especial.")
        )

        private data class Window(val suffix: String, val offsetMs: Long, val title: String, val body: String)
    }

    /**
     * Agenda as 4 lembranças futuras a partir da data de captura da foto.
     * Janelas cujo trigger já passou são puladas (útil para fotos importadas do passado).
     *
     * @param photoId        ID único da foto (usado para formar IDs das notificações).
     * @param capturedAtMs   Timestamp da captura (epoch millis). Mesmo campo do PhotoData.
     * @param contextName    Nome do evento/contexto, se houver (aparece no body).
     */
    fun scheduleForPhoto(photoId: String, capturedAtMs: Long, contextName: String? = null) {
        val now = currentTimeMillis()
        for (w in WINDOWS) {
            val triggerAt = capturedAtMs + w.offsetMs
            if (triggerAt <= now) continue
            val body = if (contextName.isNullOrBlank()) w.body else "${w.body} (${contextName})"
            scheduler.scheduleMemoryReminder(
                id = reminderId(photoId, w.suffix),
                triggerAtMillis = triggerAt,
                title = w.title,
                body = body
            )
        }
    }

    /** Cancela todas as 4 lembranças de uma foto. Chamar ao deletar a foto. */
    fun cancelForPhoto(photoId: String) {
        WINDOWS.forEach { w -> scheduler.cancelMemoryReminder(reminderId(photoId, w.suffix)) }
    }

    private fun reminderId(photoId: String, suffix: String) = "$photoId-$suffix"
}
