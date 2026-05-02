/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  MainActivity.kt - Activity Principal                       ║
 * ║                                                             ║
 * ║  Inicializa repositórios, registra photo picker, configura  ║
 * ║  sistema de notificações (diário + throwbacks) e conecta    ║
 * ║  tudo à UI via AppCallbacks e AppUiState.                   ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.android

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.lifecycleScope
import com.tsrapprun.App
import com.tsrapprun.AppCallbacks
import com.tsrapprun.AppUiState
import com.tsrapprun.auth.AuthRepository
import com.tsrapprun.camera.EventData
import com.tsrapprun.camera.PhotoData
import com.tsrapprun.navigation.NavigationScreen
import com.tsrapprun.notifications.MemoryNotificationWorker
import com.tsrapprun.notifications.MemoryOfTheDayRepository
import com.tsrapprun.notifications.MemoryReminderService
import com.tsrapprun.notifications.NotificationScheduler
import com.tsrapprun.notifications.todayDateKey
import com.tsrapprun.storage.LocalPhotoStorage
import kotlinx.coroutines.launch
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : ComponentActivity() {

    private lateinit var authRepository: AuthRepository
    private lateinit var photoStorage: LocalPhotoStorage
    private lateinit var notificationScheduler: NotificationScheduler
    private lateinit var memoryRepository: MemoryOfTheDayRepository
    private lateinit var reminderService: MemoryReminderService

    // Estado reativo para a UI
    private var photoCount by mutableIntStateOf(0)
    private var storageUsedMB by mutableStateOf("0.0")
    private var events by mutableStateOf<List<EventData>>(emptyList())
    private var allPhotos by mutableStateOf<List<PhotoData>>(emptyList())

    // Tela inicial derivada de deep link (pode ser sobrescrita ao receber notificação).
    private var initialScreen: NavigationScreen = NavigationScreen.Home

    // Photo picker launcher (registrado antes de onCreate)
    private lateinit var photoPickerLauncher: ActivityResultLauncher<PickVisualMediaRequest>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ── Registra photo picker ANTES de setContent ──
        photoPickerLauncher = registerForActivityResult(
            ActivityResultContracts.PickMultipleVisualMedia()
        ) { uris: List<Uri> ->
            if (uris.isNotEmpty()) {
                lifecycleScope.launch {
                    importPhotosFromUris(uris)
                }
            }
        }

        // ── Inicializa repositórios ──
        authRepository = AuthRepository(
            context = this,
            webClientId = "869869816608-gq8gis31up7bkn4stvd9n5clk1k0i4oi.apps.googleusercontent.com"
        )
        photoStorage = LocalPhotoStorage(context = this)
        notificationScheduler = NotificationScheduler(context = applicationContext, activity = this)
        memoryRepository = MemoryOfTheDayRepository(context = applicationContext)
        reminderService = MemoryReminderService(notificationScheduler)

        // ── Deep link: veio de notificação? ──
        initialScreen = resolveInitialScreenFromIntent(intent)

        // ── Sessão + dados + permissão + agendamento diário ──
        lifecycleScope.launch {
            authRepository.checkCurrentSession()
            refreshData()

            // Pede permissão (Android 13+). Silenciosa em versões anteriores.
            val granted = notificationScheduler.requestPermission()
            if (granted) {
                // 20:00 locais — horário razoável para revisão do dia.
                notificationScheduler.scheduleDailyMemoryPrompt(hour = 20, minute = 0)
            }
        }

        // ── Configura a UI ──
        setContent {
            App(
                authState = authRepository.authState,
                callbacks = AppCallbacks(
                    onSignInClick = {
                        lifecycleScope.launch { authRepository.signInWithGoogle() }
                    },
                    onSignOutClick = {
                        lifecycleScope.launch { authRepository.signOut() }
                    },
                    onImportPhotos = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    onSaveEventPhoto = { bytes ->
                        val photo = photoStorage.savePhoto(imageBytes = bytes)
                        // Agenda lembranças futuras (1s/1m/6m/1a) para a foto recém-capturada.
                        reminderService.scheduleForPhoto(
                            photoId = photo.id,
                            capturedAtMs = photo.capturedAt
                        )
                        photo.id
                    },
                    onSaveEvent = { event ->
                        photoStorage.saveEvent(event)
                    },
                    onDeletePhoto = { photo ->
                        // Ao deletar a foto, cancela suas lembranças futuras.
                        reminderService.cancelForPhoto(photo.id)
                        photoStorage.deletePhoto(photo)
                    },
                    onLoadPhoto = { photo ->
                        photoStorage.loadPhoto(photo)
                    },
                    onUpdatePhotosEventId = { photoIds, eventId ->
                        photoStorage.updatePhotosEventId(photoIds, eventId)
                    },
                    onRefreshData = { refreshData() },
                    onLoadMemoryEntry = { date -> memoryRepository.getEntry(date) },
                    onSaveMemoryEntry = { entry -> memoryRepository.saveEntry(entry) }
                ),
                uiState = AppUiState(
                    photoCount = photoCount,
                    storageUsedMB = storageUsedMB,
                    events = events,
                    allPhotos = allPhotos
                ),
                initialScreen = initialScreen
            )
        }
    }

    /**
     * Activity launchMode é padrão; novas intents chegam aqui se o app estiver em foreground.
     * Reavaliamos o deep link — mas a mudança só terá efeito na próxima setContent.
     * Para simplicidade, apenas registramos — usuário pode navegar manualmente.
     */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    /**
     * Lê extras do Intent gerado pelo Worker de notificação.
     * Se a notificação foi do tipo "daily", abre direto a tela de Memória do Dia.
     */
    private fun resolveInitialScreenFromIntent(intent: Intent?): NavigationScreen {
        intent ?: return NavigationScreen.Home
        val kind = intent.getStringExtra(MemoryNotificationWorker.EXTRA_DEEPLINK_KIND)
            ?: return NavigationScreen.Home
        return when (kind) {
            MemoryNotificationWorker.KIND_DAILY,
            MemoryNotificationWorker.KIND_REMINDER -> NavigationScreen.MemoryOfTheDay(
                dateKey = todayDateKey(System.currentTimeMillis())
            )
            else -> NavigationScreen.Home
        }
    }

    /**
     * Importa fotos selecionadas pelo photo picker.
     * Lê bytes + EXIF, salva criptografado sequencialmente.
     */
    private suspend fun importPhotosFromUris(uris: List<Uri>) {
        for (uri in uris) {
            try {
                // Lê EXIF para data original da foto
                val exifDate = extractExifDate(uri)

                // Lê bytes da foto
                val bytes = contentResolver.openInputStream(uri)?.use { it.readBytes() }
                    ?: continue

                // Salva criptografado com data EXIF preservada
                val saved = photoStorage.savePhoto(
                    imageBytes = bytes,
                    eventId = null,
                    capturedAt = exifDate
                )
                // Agenda lembranças futuras a partir da data REAL da foto (EXIF).
                // Janelas já vencidas (foto antiga) são ignoradas pelo service.
                reminderService.scheduleForPhoto(
                    photoId = saved.id,
                    capturedAtMs = saved.capturedAt
                )
            } catch (e: Exception) {
                // Pula fotos com erro, continua com as próximas
                android.util.Log.e("MainActivity", "Erro ao importar foto", e)
            }
        }
        refreshData()
    }

    /**
     * Extrai data de captura dos metadados EXIF da foto.
     * Retorna epoch millis ou null se não encontrar.
     */
    private fun extractExifDate(uri: Uri): Long? {
        return try {
            contentResolver.openInputStream(uri)?.use { stream: InputStream ->
                val exif = ExifInterface(stream)
                val dateStr = exif.getAttribute(ExifInterface.TAG_DATETIME_ORIGINAL)
                    ?: exif.getAttribute(ExifInterface.TAG_DATETIME)
                    ?: return@use null
                // Formato EXIF: "2024:03:15 14:30:00"
                val sdf = SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.US)
                sdf.parse(dateStr)?.time
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Atualiza todos os contadores e listas da UI.
     */
    private suspend fun refreshData() {
        allPhotos = photoStorage.listPhotos()
        photoCount = allPhotos.size
        events = photoStorage.listEvents()
        val totalBytes = photoStorage.getTotalStorageUsed()
        storageUsedMB = String.format("%.1f", totalBytes / (1024.0 * 1024.0))
    }
}
