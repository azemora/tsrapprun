/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  NotificationScheduler.kt - Agendador Multiplataforma       ║
 * ║                                                             ║
 * ║  Abstrai WorkManager/AlarmManager (Android) e                ║
 * ║  UNUserNotificationCenter (iOS) em uma única API.           ║
 * ║                                                             ║
 * ║  PRIVACIDADE: Notificações são 100% locais.                 ║
 * ║  - Nenhum payload sai do dispositivo                        ║
 * ║  - Nenhum push remoto (sem FCM/APNs server-push)            ║
 * ║  - Textos de title/body ficam apenas no device              ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.notifications

/**
 * API de agendamento de notificações locais.
 *
 * Contrato:
 * - Agendamentos sobrevivem a reinicializações (WorkManager persiste; UNUserNotificationCenter também).
 * - Triggers no passado são silenciosamente ignorados.
 * - Reagendar com o mesmo [id] sobrescreve a notificação anterior (idempotente).
 */
expect class NotificationScheduler {

    /**
     * Solicita permissão de notificação.
     * - Android 13+ (SDK 33): POST_NOTIFICATIONS runtime.
     * - iOS: UNUserNotificationCenter.requestAuthorization.
     * - Versões anteriores do Android retornam true imediatamente.
     *
     * @return true se concedida.
     */
    suspend fun requestPermission(): Boolean

    /**
     * Agenda o prompt DIÁRIO recorrente "Qual foi a memória do dia?".
     * Reagenda idempotentemente — chamar de novo apenas atualiza o horário.
     *
     * @param hour   0..23
     * @param minute 0..59
     */
    fun scheduleDailyMemoryPrompt(hour: Int, minute: Int)

    /** Cancela o prompt diário recorrente. */
    fun cancelDailyMemoryPrompt()

    /**
     * Agenda UMA notificação one-shot (lembrança de memória antiga).
     *
     * @param id               Identificador estável (ex: "$photoId-1m"). Reuse = sobrescreve.
     * @param triggerAtMillis  Epoch millis do disparo. Valor no passado é ignorado.
     * @param title            Título mostrado na bandeja.
     * @param body             Corpo da notificação.
     */
    fun scheduleMemoryReminder(
        id: String,
        triggerAtMillis: Long,
        title: String,
        body: String
    )

    /** Cancela uma lembrança agendada. */
    fun cancelMemoryReminder(id: String)

    /** Cancela TODAS as notificações agendadas por este app. */
    fun cancelAll()
}
