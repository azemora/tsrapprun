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
                photoStorage.savePhoto(
                    imageBytes = bytes,
                    eventId = null,
                    capturedAt = exifDate
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
