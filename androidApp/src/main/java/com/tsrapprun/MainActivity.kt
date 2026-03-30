/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  MainActivity.kt - Activity Principal                       ║
 * ║                                                             ║
 * ║  Inicializa repositórios, registra photo picker,            ║
 * ║  e conecta tudo à UI via AppCallbacks e AppUiState.         ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.android

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
import com.tsrapprun.storage.LocalPhotoStorage
import kotlinx.coroutines.launch
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : ComponentActivity() {

    private lateinit var authRepository: AuthRepository
    private lateinit var photoStorage: LocalPhotoStorage

    // Estado reativo para a UI
    private var photoCount by mutableIntStateOf(0)
    private var storageUsedMB by mutableStateOf("0.0")
    private var events by mutableStateOf<List<EventData>>(emptyList())
    private var allPhotos by mutableStateOf<List<PhotoData>>(emptyList())

    // Photo picker launcher (registrado antes de onCreate)
    private lateinit var photoPickerLauncher: ActivityResultLauncher<PickVisualMediaRequest>

    // EventId de destino para importação (null = fotos avulsas)
    private var importTargetEventId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ── Registra photo picker ANTES de setContent ──
        photoPickerLauncher = registerForActivityResult(
            ActivityResultContracts.PickMultipleVisualMedia()
        ) { uris: List<Uri> ->
            if (uris.isNotEmpty()) {
                val targetEventId = importTargetEventId
                importTargetEventId = null
                lifecycleScope.launch {
                    importPhotosFromUris(uris, targetEventId)
                }
            } else {
                importTargetEventId = null
            }
        }

        // ── Inicializa repositórios ──
        authRepository = AuthRepository(
            context = this,
            webClientId = BuildConfig.WEB_CLIENT_ID
        )
        photoStorage = LocalPhotoStorage(context = this)

        // ── Verifica sessão + carrega dados ──
        lifecycleScope.launch {
            authRepository.checkCurrentSession()
            refreshData()
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
                        importTargetEventId = null
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    onImportPhotosToEvent = { eventId ->
                        importTargetEventId = eventId
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    onSaveEventPhoto = { bytes ->
                        val photo = photoStorage.savePhoto(imageBytes = bytes)
                        photo.id
                    },
                    onSaveEvent = { event ->
                        photoStorage.saveEvent(event)
                    },
                    onDeletePhoto = { photo ->
                        photoStorage.deletePhoto(photo)
                    },
                    onDeleteEvent = { eventId ->
                        photoStorage.deleteEvent(eventId)
                    },
                    onRenameEvent = { eventId, newName ->
                        val event = events.find { it.id == eventId } ?: return@AppCallbacks
                        photoStorage.updateEvent(event.copy(name = newName))
                    },
                    onLoadPhoto = { photo ->
                        photoStorage.loadPhoto(photo)
                    },
                    onUpdatePhotosEventId = { photoIds, eventId ->
                        photoStorage.updatePhotosEventId(photoIds, eventId)
                    },
                    onRefreshData = { refreshData() }
                ),
                uiState = AppUiState(
                    photoCount = photoCount,
                    storageUsedMB = storageUsedMB,
                    events = events,
                    allPhotos = allPhotos
                )
            )
        }
    }

    /**
     * Importa fotos selecionadas pelo photo picker.
     * Lê bytes + EXIF, salva criptografado sequencialmente.
     *
     * @param eventId Se não-nulo, associa as fotos importadas a este evento
     *                e atualiza a contagem de fotos do evento.
     */
    private suspend fun importPhotosFromUris(uris: List<Uri>, eventId: String? = null) {
        var importedCount = 0
        for (uri in uris) {
            try {
                val exifDate = extractExifDate(uri)
                val bytes = contentResolver.openInputStream(uri)?.use { it.readBytes() }
                    ?: continue

                photoStorage.savePhoto(
                    imageBytes = bytes,
                    eventId = eventId,
                    capturedAt = exifDate
                )
                importedCount++
            } catch (e: Exception) {
                android.util.Log.e("MainActivity", "Erro ao importar foto", e)
            }
        }

        // Atualiza contagem de fotos do evento se importou para um evento
        if (eventId != null && importedCount > 0) {
            val event = photoStorage.listEvents().find { it.id == eventId }
            if (event != null) {
                val updatedCount = photoStorage.listPhotosByEvent(eventId).size
                photoStorage.updateEvent(
                    event.copy(
                        photoCount = updatedCount,
                        thumbnailPhotoId = event.thumbnailPhotoId
                            ?: photoStorage.listPhotosByEvent(eventId).firstOrNull()?.id
                    )
                )
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
