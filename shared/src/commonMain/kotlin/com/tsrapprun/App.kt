/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  App.kt - Composable Raiz com Navegação por Estado          ║
 * ║                                                             ║
 * ║  Gerencia todas as telas via NavigationScreen sealed class. ║
 * ║                                                             ║
 * ║  SEGURANÇA: Telas protegidas só acessíveis com auth.        ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.tsrapprun.auth.AuthState
import com.tsrapprun.auth.HomeScreen
import com.tsrapprun.auth.LoginScreen
import com.tsrapprun.camera.CameraScreen
import com.tsrapprun.camera.EventCameraScreen
import com.tsrapprun.camera.EventData
import com.tsrapprun.camera.EventNamingScreen
import com.tsrapprun.camera.PhotoData
import com.tsrapprun.gallery.GalleryScreen
import com.tsrapprun.gallery.PhotoGridScreen
import com.tsrapprun.gallery.PhotoViewerScreen
import com.tsrapprun.navigation.NavigationScreen
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Callbacks agrupados para manter App() organizado.
 */
data class AppCallbacks(
    val onSignInClick: () -> Unit = {},
    val onSignOutClick: () -> Unit = {},
    val onImportPhotos: () -> Unit = {},
    val onSaveEventPhoto: suspend (ByteArray) -> String = { "" },
    val onSaveEvent: suspend (EventData) -> Unit = {},
    val onDeletePhoto: suspend (PhotoData) -> Unit = {},
    val onLoadPhoto: suspend (PhotoData) -> ByteArray? = { null },
    val onUpdatePhotosEventId: suspend (List<String>, String) -> Unit = { _, _ -> },
    val onRefreshData: suspend () -> Unit = {}
)

/**
 * Estado reativo da UI agrupado.
 */
data class AppUiState(
    val photoCount: Int = 0,
    val storageUsedMB: String = "0.0",
    val events: List<EventData> = emptyList(),
    val allPhotos: List<PhotoData> = emptyList()
)

@Composable
fun App(
    authState: StateFlow<AuthState>,
    callbacks: AppCallbacks,
    uiState: AppUiState
) {
    val currentAuthState by authState.collectAsState()
    var screen by remember { mutableStateOf<NavigationScreen>(NavigationScreen.Home) }
    val scope = rememberCoroutineScope()

    MaterialTheme {
        when (currentAuthState) {
            is AuthState.Authenticated -> {
                val userData = (currentAuthState as AuthState.Authenticated).userData

                when (val nav = screen) {
                    // ── Home ──
                    is NavigationScreen.Home -> {
                        HomeScreen(
                            userData = userData,
                            photoCount = uiState.photoCount,
                            storageUsedMB = uiState.storageUsedMB,
                            events = uiState.events,
                            onRegisterEvent = { screen = NavigationScreen.EventRegistration },
                            onImportPhotos = { callbacks.onImportPhotos() },
                            onOpenGallery = { screen = NavigationScreen.Gallery },
                            onOpenEvent = { event ->
                                screen = NavigationScreen.EventGallery(event.id, event.name)
                            },
                            onSignOutClick = callbacks.onSignOutClick
                        )
                    }

                    // ── Câmera avulsa ──
                    is NavigationScreen.Camera -> {
                        CameraScreen(
                            onPhotoCaptured = { bytes ->
                                scope.launch {
                                    callbacks.onSaveEventPhoto(bytes)
                                    callbacks.onRefreshData()
                                }
                                screen = NavigationScreen.Home
                            },
                            onError = {},
                            onClose = { screen = NavigationScreen.Home }
                        )
                    }

                    // ── Câmera contínua (registro de evento) ──
                    is NavigationScreen.EventRegistration -> {
                        EventCameraScreen(
                            onPhotoCaptured = { bytes ->
                                callbacks.onSaveEventPhoto(bytes)
                            },
                            onFinish = { photoIds ->
                                if (photoIds.isEmpty()) {
                                    screen = NavigationScreen.Home
                                } else {
                                    screen = NavigationScreen.EventNaming(photoIds)
                                }
                            },
                            onError = {}
                        )
                    }

                    // ── Nomear evento ──
                    is NavigationScreen.EventNaming -> {
                        EventNamingScreen(
                            photoCount = nav.photoIds.size,
                            onSave = { eventName ->
                                scope.launch {
                                    val eventId = java.util.UUID.randomUUID().toString()
                                    val event = EventData(
                                        id = eventId,
                                        name = eventName,
                                        createdAt = System.currentTimeMillis(),
                                        photoCount = nav.photoIds.size,
                                        thumbnailPhotoId = nav.photoIds.firstOrNull()
                                    )
                                    callbacks.onUpdatePhotosEventId(nav.photoIds, eventId)
                                    callbacks.onSaveEvent(event)
                                    callbacks.onRefreshData()
                                    screen = NavigationScreen.Home
                                }
                            },
                            onCancel = {
                                scope.launch { callbacks.onRefreshData() }
                                screen = NavigationScreen.Home
                            }
                        )
                    }

                    // ── Galeria raiz ──
                    is NavigationScreen.Gallery -> {
                        GalleryScreen(
                            events = uiState.events,
                            totalPhotoCount = uiState.photoCount,
                            onOpenAllPhotos = { screen = NavigationScreen.AllPhotos },
                            onOpenEvent = { event ->
                                screen = NavigationScreen.EventGallery(event.id, event.name)
                            },
                            onBack = { screen = NavigationScreen.Home }
                        )
                    }

                    // ── Grid: todas as fotos ──
                    is NavigationScreen.AllPhotos -> {
                        PhotoGridScreen(
                            title = "Todas as Fotos",
                            photos = uiState.allPhotos.sortedByDescending { it.capturedAt },
                            onLoadThumbnail = { callbacks.onLoadPhoto(it) },
                            onPhotoClick = { index ->
                                screen = NavigationScreen.PhotoViewer(index)
                            },
                            onBack = { screen = NavigationScreen.Gallery }
                        )
                    }

                    // ── Grid: fotos de um evento ──
                    is NavigationScreen.EventGallery -> {
                        val eventPhotos = uiState.allPhotos
                            .filter { it.eventId == nav.eventId }
                            .sortedByDescending { it.capturedAt }
                        PhotoGridScreen(
                            title = nav.eventName,
                            photos = eventPhotos,
                            onLoadThumbnail = { callbacks.onLoadPhoto(it) },
                            onPhotoClick = { index ->
                                screen = NavigationScreen.PhotoViewer(index, nav.eventId)
                            },
                            onBack = { screen = NavigationScreen.Gallery }
                        )
                    }

                    // ── Viewer fullscreen ──
                    is NavigationScreen.PhotoViewer -> {
                        val photosForViewer = if (nav.eventId != null) {
                            uiState.allPhotos.filter { it.eventId == nav.eventId }
                        } else {
                            uiState.allPhotos
                        }.sortedByDescending { it.capturedAt }

                        PhotoViewerScreen(
                            photos = photosForViewer,
                            initialIndex = nav.initialIndex,
                            onLoadPhoto = { callbacks.onLoadPhoto(it) },
                            onDelete = { photo ->
                                scope.launch {
                                    callbacks.onDeletePhoto(photo)
                                    callbacks.onRefreshData()
                                }
                                // Volta ao grid após deletar
                                screen = if (nav.eventId != null) {
                                    NavigationScreen.EventGallery(nav.eventId, "")
                                } else {
                                    NavigationScreen.AllPhotos
                                }
                            },
                            onBack = {
                                screen = if (nav.eventId != null) {
                                    NavigationScreen.EventGallery(nav.eventId, "")
                                } else {
                                    NavigationScreen.AllPhotos
                                }
                            }
                        )
                    }
                }
            }

            // ── Não autenticado / Loading / Erro → Login ──
            else -> {
                LoginScreen(
                    authState = currentAuthState,
                    onSignInClick = callbacks.onSignInClick
                )
            }
        }
    }
}

// getPlatformName() está definido em Platform.kt (expect/actual)
