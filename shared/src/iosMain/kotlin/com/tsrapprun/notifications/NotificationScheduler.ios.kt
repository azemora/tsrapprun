/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  NotificationScheduler.ios.kt - Implementação iOS           ║
 * ║                                                             ║
 * ║  - UNUserNotificationCenter + UNCalendarNotificationTrigger  ║
 * ║    para o prompt diário (repeats = true).                   ║
 * ║  - UNTimeIntervalNotificationTrigger p/ one-shot reminders.  ║
 * ║  - Identifier = id fornecido → idempotência natural.         ║
 * ║                                                             ║
 * ║  DEEP LINK: userInfo["kind"] + ["tag"] é lido pelo           ║
 * ║  AppDelegate/SceneDelegate no lado Swift ao abrir o app.     ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.notifications

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSDate
import platform.Foundation.NSDateComponents
import platform.Foundation.timeIntervalSince1970
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNCalendarNotificationTrigger
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationSound
import platform.UserNotifications.UNTimeIntervalNotificationTrigger
import platform.UserNotifications.UNUserNotificationCenter
import kotlin.coroutines.resume

@OptIn(ExperimentalForeignApi::class)
actual class NotificationScheduler {

    private val center get() = UNUserNotificationCenter.currentNotificationCenter()

    actual suspend fun requestPermission(): Boolean =
        suspendCancellableCoroutine { cont ->
            val options = UNAuthorizationOptionAlert or
                UNAuthorizationOptionSound or
                UNAuthorizationOptionBadge
            center.requestAuthorizationWithOptions(options) { granted, _ ->
                cont.resume(granted)
            }
        }

    actual fun scheduleDailyMemoryPrompt(hour: Int, minute: Int) {
        // Identifier estável → reagendar sobrescreve o anterior.
        center.removePendingNotificationRequestsWithIdentifiers(listOf(DAILY_ID))

        val components = NSDateComponents().apply {
            this.hour = hour.toLong()
            this.minute = minute.toLong()
        }
        val trigger = UNCalendarNotificationTrigger.triggerWithDateMatchingComponents(
            dateComponents = components,
            repeats = true
        )

        val content = UNMutableNotificationContent().apply {
            setTitle("Memória do dia")
            setBody("O que aconteceu hoje que você quer lembrar?")
            setSound(UNNotificationSound.defaultSound())
            setUserInfo(mapOf("kind" to "daily", "tag" to DAILY_ID))
        }

        val request = UNNotificationRequest.requestWithIdentifier(
            identifier = DAILY_ID,
            content = content,
            trigger = trigger
        )
        center.addNotificationRequest(request) { _ -> }
    }

    actual fun cancelDailyMemoryPrompt() {
        center.removePendingNotificationRequestsWithIdentifiers(listOf(DAILY_ID))
    }

    actual fun scheduleMemoryReminder(
        id: String,
        triggerAtMillis: Long,
        title: String,
        body: String
    ) {
        // Cancela anterior com o mesmo id para re-agendar limpo.
        center.removePendingNotificationRequestsWithIdentifiers(listOf(id))

        val nowMs = (NSDate().timeIntervalSince1970 * 1000.0).toLong()
        val delaySec = ((triggerAtMillis - nowMs) / 1000.0).coerceAtLeast(1.0)

        val trigger = UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(
            timeInterval = delaySec,
            repeats = false
        )

        val content = UNMutableNotificationContent().apply {
            setTitle(title)
            setBody(body)
            setSound(UNNotificationSound.defaultSound())
            setUserInfo(mapOf("kind" to "reminder", "tag" to id))
        }

        val request = UNNotificationRequest.requestWithIdentifier(
            identifier = id,
            content = content,
            trigger = trigger
        )
        center.addNotificationRequest(request) { _ -> }
    }

    actual fun cancelMemoryReminder(id: String) {
        center.removePendingNotificationRequestsWithIdentifiers(listOf(id))
    }

    actual fun cancelAll() {
        center.removeAllPendingNotificationRequests()
        center.removeAllDeliveredNotifications()
    }

    companion object {
        private const val DAILY_ID = "tsr-daily-memory-prompt"
    }
}
