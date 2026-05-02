/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  NotificationScheduler.android.kt - Implementação Android   ║
 * ║                                                             ║
 * ║  - Diário: PeriodicWorkRequest (24h, KEEP se existente)     ║
 * ║  - One-shot: OneTimeWorkRequest com initialDelay             ║
 * ║  - Cancelamento via uniqueWorkName / WorkManager cancelByTag ║
 * ║  - Permissão: POST_NOTIFICATIONS (SDK 33+) via Activity      ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.notifications

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * @param context  ApplicationContext preferencialmente.
 * @param activity Activity atual (usada SOMENTE para pedir permissão runtime).
 *                 Pode ser null: nesse caso requestPermission() retorna o estado atual
 *                 sem disparar prompt.
 */
actual class NotificationScheduler(
    private val context: Context,
    private val activity: ComponentActivity? = null
) {

    private val workManager get() = WorkManager.getInstance(context)

    init {
        // Garante o canal em tempo de construção — barato e idempotente.
        MemoryNotificationWorker.ensureChannel(context)
    }

    actual suspend fun requestPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        val already = ContextCompat.checkSelfPermission(
            context, Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
        if (already) return true

        val act = activity ?: return false
        return suspendCoroutine { cont ->
            val launcher = act.activityResultRegistry.register(
                "tsr-notif-perm",
                ActivityResultContracts.RequestPermission()
            ) { granted -> cont.resume(granted) }
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    actual fun scheduleDailyMemoryPrompt(hour: Int, minute: Int) {
        val initialDelay = computeInitialDelayMs(hour, minute)

        val data = Data.Builder()
            .putString(MemoryNotificationWorker.KEY_KIND, MemoryNotificationWorker.KIND_DAILY)
            .putString(MemoryNotificationWorker.KEY_TITLE, "Memória do dia")
            .putString(
                MemoryNotificationWorker.KEY_BODY,
                "O que aconteceu hoje que você quer lembrar?"
            )
            .putString(MemoryNotificationWorker.KEY_TAG, DAILY_WORK_NAME)
            .build()

        val request = PeriodicWorkRequestBuilder<MemoryNotificationWorker>(
            1, TimeUnit.DAYS
        )
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .addTag(DAILY_WORK_NAME)
            .build()

        workManager.enqueueUniquePeriodicWork(
            DAILY_WORK_NAME,
            // UPDATE garante que reagendar atualize o horário sem duplicar.
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    actual fun cancelDailyMemoryPrompt() {
        workManager.cancelUniqueWork(DAILY_WORK_NAME)
    }

    actual fun scheduleMemoryReminder(
        id: String,
        triggerAtMillis: Long,
        title: String,
        body: String
    ) {
        val now = System.currentTimeMillis()
        val delay = (triggerAtMillis - now).coerceAtLeast(0L)

        val data = Data.Builder()
            .putString(MemoryNotificationWorker.KEY_KIND, MemoryNotificationWorker.KIND_REMINDER)
            .putString(MemoryNotificationWorker.KEY_TITLE, title)
            .putString(MemoryNotificationWorker.KEY_BODY, body)
            .putString(MemoryNotificationWorker.KEY_TAG, id)
            .build()

        val request = OneTimeWorkRequestBuilder<MemoryNotificationWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .addTag(REMINDER_TAG)
            .addTag(id)
            .build()

        // REPLACE = idempotência: reagendar a mesma foto sobrescreve.
        workManager.enqueueUniqueWork(
            uniqueName(id),
            ExistingWorkPolicy.REPLACE,
            request
        )
    }

    actual fun cancelMemoryReminder(id: String) {
        workManager.cancelUniqueWork(uniqueName(id))
    }

    actual fun cancelAll() {
        workManager.cancelAllWorkByTag(REMINDER_TAG)
        workManager.cancelUniqueWork(DAILY_WORK_NAME)
    }

    // ── helpers ──

    private fun uniqueName(reminderId: String) = "tsr-reminder:$reminderId"

    private fun computeInitialDelayMs(hour: Int, minute: Int): Long {
        val now = Calendar.getInstance()
        val target = (now.clone() as Calendar).apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        if (!target.after(now)) target.add(Calendar.DAY_OF_YEAR, 1)
        return target.timeInMillis - now.timeInMillis
    }

    companion object {
        private const val DAILY_WORK_NAME = "tsr-daily-memory-prompt"
        private const val REMINDER_TAG = "tsr-reminder"
    }
}
