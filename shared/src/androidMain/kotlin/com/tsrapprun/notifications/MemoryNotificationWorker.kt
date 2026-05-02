/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  MemoryNotificationWorker.kt - Publica Notificações Locais ║
 * ║                                                             ║
 * ║  Worker executado pelo WorkManager quando chega o horário    ║
 * ║  de uma notificação (diária ou throwback).                  ║
 * ║                                                             ║
 * ║  SEGURANÇA: Lê apenas título/corpo dos InputData,           ║
 * ║  nenhum acesso à foto criptografada. Uma foto não é         ║
 * ║  anexada na notificação — o usuário abre o app para ver.    ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.notifications

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
import androidx.work.Worker
import androidx.work.WorkerParameters

/**
 * Worker que cria e dispara a notificação local.
 *
 * InputData esperado:
 * - [KEY_KIND]      String ("daily" ou "reminder")
 * - [KEY_TITLE]     String
 * - [KEY_BODY]      String
 * - [KEY_TAG]       String (identificador estável da notificação; usado pelo cancelAll/cancel)
 */
class MemoryNotificationWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {
        val kind = inputData.getString(KEY_KIND) ?: KIND_REMINDER
        val title = inputData.getString(KEY_TITLE) ?: return Result.failure()
        val body = inputData.getString(KEY_BODY) ?: return Result.failure()
        val tag = inputData.getString(KEY_TAG) ?: kind

        ensureChannel(applicationContext)

        // Verifica permissão em runtime (Android 13+). Sem permissão, silenciosamente não posta.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) return Result.success()
        }

        val deepLinkIntent = buildDeepLinkIntent(applicationContext, kind, tag)
        val pendingFlags =
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE

        // requestCode único por tag evita que múltiplas notificações compartilhem o mesmo PendingIntent.
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            tag.hashCode(),
            deepLinkIntent,
            pendingFlags
        )

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat.from(applicationContext)
            .notify(tag, NOTIFICATION_ID, notification)

        return Result.success()
    }

    private fun buildDeepLinkIntent(context: Context, kind: String, tag: String): Intent {
        // Launch intent do próprio app — preenchido pelo Android com o launcher activity.
        val launchIntent = context.packageManager
            .getLaunchIntentForPackage(context.packageName)
            ?: Intent()
        launchIntent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        launchIntent.putExtra(EXTRA_DEEPLINK_KIND, kind)
        launchIntent.putExtra(EXTRA_DEEPLINK_TAG, tag)
        return launchIntent
    }

    companion object {
        const val KEY_KIND = "kind"
        const val KEY_TITLE = "title"
        const val KEY_BODY = "body"
        const val KEY_TAG = "tag"

        const val KIND_DAILY = "daily"
        const val KIND_REMINDER = "reminder"

        const val CHANNEL_ID = "tsr_memories"
        private const val CHANNEL_NAME = "Memórias"
        private const val CHANNEL_DESC =
            "Prompt diário da memória do dia e lembranças de fotos antigas."
        private const val NOTIFICATION_ID = 1001

        /** Extras lidos pelo MainActivity para decidir se abre a tela de Memória do Dia. */
        const val EXTRA_DEEPLINK_KIND = "tsr.deeplink.kind"
        const val EXTRA_DEEPLINK_TAG = "tsr.deeplink.tag"

        /** Garante que o NotificationChannel existe (idempotente). */
        fun ensureChannel(context: Context) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
            val manager = context.getSystemService(NotificationManager::class.java) ?: return
            if (manager.getNotificationChannel(CHANNEL_ID) != null) return
            val channel = NotificationChannel(
                CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = CHANNEL_DESC }
            manager.createNotificationChannel(channel)
        }
    }
}
