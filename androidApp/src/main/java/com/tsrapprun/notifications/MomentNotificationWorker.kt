/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  MomentNotificationWorker.kt - Worker de Notificações       ║
 * ║                                                             ║
 * ║  Dispara notificações diárias e semanais via WorkManager.   ║
 * ║  "O que aconteceu hoje?" / "O que aconteceu essa semana?"   ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.android.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.tsrapprun.android.MainActivity
import java.util.concurrent.TimeUnit

class MomentNotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        const val CHANNEL_ID = "moment_reminders"
        const val DAILY_WORK_NAME = "daily_moment_reminder"
        const val WEEKLY_WORK_NAME = "weekly_moment_reminder"

        private const val KEY_TYPE = "notification_type"
        const val TYPE_DAILY = "DAILY"
        const val TYPE_WEEKLY = "WEEKLY"

        /**
         * Cria o canal de notificação (necessário API 26+).
         */
        fun createNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    "Lembretes de Registro",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Lembretes para registrar seus momentos"
                }
                val manager = context.getSystemService(NotificationManager::class.java)
                manager.createNotificationChannel(channel)
            }
        }

        /**
         * Agenda as notificações periódicas.
         */
        fun scheduleNotifications(context: Context) {
            val workManager = WorkManager.getInstance(context)

            // Notificação diária (a cada 24h)
            val dailyWork = PeriodicWorkRequestBuilder<MomentNotificationWorker>(
                24, TimeUnit.HOURS
            )
                .setInputData(
                    androidx.work.Data.Builder()
                        .putString(KEY_TYPE, TYPE_DAILY)
                        .build()
                )
                .setInitialDelay(calculateDailyDelay(), TimeUnit.MILLISECONDS)
                .build()

            workManager.enqueueUniquePeriodicWork(
                DAILY_WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                dailyWork
            )

            // Notificação semanal (a cada 7 dias)
            val weeklyWork = PeriodicWorkRequestBuilder<MomentNotificationWorker>(
                7, TimeUnit.DAYS
            )
                .setInputData(
                    androidx.work.Data.Builder()
                        .putString(KEY_TYPE, TYPE_WEEKLY)
                        .build()
                )
                .setInitialDelay(calculateWeeklyDelay(), TimeUnit.MILLISECONDS)
                .build()

            workManager.enqueueUniquePeriodicWork(
                WEEKLY_WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                weeklyWork
            )
        }

        /**
         * Calcula delay até as 20h de hoje (ou amanhã se já passou).
         */
        private fun calculateDailyDelay(): Long {
            val now = java.util.Calendar.getInstance()
            val target = java.util.Calendar.getInstance().apply {
                set(java.util.Calendar.HOUR_OF_DAY, 20)
                set(java.util.Calendar.MINUTE, 0)
                set(java.util.Calendar.SECOND, 0)
                set(java.util.Calendar.MILLISECOND, 0)
            }
            if (target.before(now)) {
                target.add(java.util.Calendar.DAY_OF_YEAR, 1)
            }
            return target.timeInMillis - now.timeInMillis
        }

        /**
         * Calcula delay até domingo às 18h.
         */
        private fun calculateWeeklyDelay(): Long {
            val now = java.util.Calendar.getInstance()
            val target = java.util.Calendar.getInstance().apply {
                set(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.SUNDAY)
                set(java.util.Calendar.HOUR_OF_DAY, 18)
                set(java.util.Calendar.MINUTE, 0)
                set(java.util.Calendar.SECOND, 0)
                set(java.util.Calendar.MILLISECOND, 0)
            }
            if (target.before(now)) {
                target.add(java.util.Calendar.WEEK_OF_YEAR, 1)
            }
            return target.timeInMillis - now.timeInMillis
        }
    }

    override suspend fun doWork(): Result {
        val type = inputData.getString(KEY_TYPE) ?: TYPE_DAILY
        showNotification(type)
        return Result.success()
    }

    private fun showNotification(type: String) {
        // Verifica permissão em API 33+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        val (title, text, notifId) = when (type) {
            TYPE_WEEKLY -> Triple(
                "O que aconteceu essa semana?",
                "Registre os momentos da sua semana antes que eles passem!",
                2
            )
            else -> Triple(
                "O que aconteceu hoje?",
                "Tire um momento para registrar o seu dia.",
                1
            )
        }

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("open_moment_type", type)
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext, notifId, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_edit)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(applicationContext).notify(notifId, notification)
    }
}
