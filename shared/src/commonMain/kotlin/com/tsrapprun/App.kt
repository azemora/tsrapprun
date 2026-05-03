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
import com.tsrapprun.memorybook.MemoryBookScreen
import com.tsrapprun.moments.MomentEntry
import com.tsrapprun.moments.MomentRegistrationScreen
import com.tsrapprun.moments.MomentType
import com.tsrapprun.moments.MomentsListScreen
import com.tsrapprun.gallery.EventListScreen
import com.tsrapprun.gallery.FrontPageScreen
import com.tsrapprun.gallery.GalleryScreen
import com.tsrapprun.gallery.EventMenuActions
import com.tsrapprun.gallery.PhotoGridScreen
import com.tsrapprun.gallery.PhotoViewerScreen
import com.tsrapprun.navigation.NavigationScreen
import com.tsrapprun.platform.dayBoundsMillis
import com.tsrapprun.platform.newUuid
import com.tsrapprun.platform.nowMillis
import com.tsrapprun.platform.weekBoundsMillis
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Callbacks agrupados para manter App() organizado.
 */
data class AppCallbacks(
    val onSignInClick: () -> Unit = {},
    val onSignOutClick: () -> Unit = {},
    val onImportPhotos: () -> Unit = {},
    val onImportPhotosToEvent: (eventId: String) -> Unit = {},
    val onSaveEventPhoto: suspend (ByteArray) -> String = { "" },
    val onSaveEvent: suspend (EventData) -> Unit = {},
    val onDeletePhoto: suspend (PhotoData) -> Unit = {},
    val onDeleteEvent: suspend (String) -> Unit = {},
    val onRenameEvent: suspend (String, String) -> Unit = { _, _ -> },
    val onLoadPhoto: suspend (PhotoData) -> ByteArray? = { null },
    val onUpdatePhotosEventId: suspend (List<String>, String) -> Unit = { _, _ -> },
    val onSaveMoment: suspend (MomentEntry) -> Unit = {},
    val onDeleteMoment: suspend (String) -> Unit = {},
    val onRefreshData: suspend () -> Unit = {}
)

/**
 * Estado reativo da UI agrupado.
 */
data class AppUiState(
    val photoCount: Int = 0,
    val storageUsedMB: String = "0.0",
    val events: List<EventData> = emptyList(),
    val allPhotos: List<PhotoData> = emptyList(),
    val moments: List<MomentEntry> = emptyList()
)

@Composable
fun App(
    authState: StateFlow<AuthState>,
    callbacks: AppCallbacks,
    uiState: AppUiState
) {
    val currentAuthState by authState.collectAsState()
    var screen by remember { mutableStateOf<NavigationScreen>(NavigationScreen.FrontPage) }
    val scope = rememberCoroutineScope()

    MaterialTheme {
        when (currentAuthState) {
            is AuthState.Authenticated -> {
                val userData = (currentAuthState as AuthState.Authenticated).userData

                when (val nav = screen) {
                    // ── Front Page (tela inicial visual) ──
                    is NavigationScreen.FrontPage -> {
                        FrontPageScreen(
                            events = uiState.events,
                            allPhotos = uiState.allPhotos,
                            onLoadThumbnail = { callbacks.onLoadPhoto(it) },
                            onOpenEvent = { event ->
                                screen = NavigationScreen.EventGallery(event.id, event.name)
                            },
                            onOpenEventList = { screen = NavigationScreen.EventList },
                            onCreate = { screen = NavigationScreen.EventRegistration },
                            onOpenSettings = { screen = NavigationScreen.Home },
                            onOpenMemoryBook = { screen = NavigationScreen.MemoryBook },
                            onOpenMoments = { screen = NavigationScreen.MomentsList }
                        )
                    }

                    // ── Home (gerenciamento: perfil, usage, eventos) ──
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
                            onSignOutClick = callbacks.onSignOutClick,
                            onBack = { screen = NavigationScreen.FrontPage }
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
                                screen = NavigationScreen.FrontPage
                            },
                            onError = {},
                            onClose = { screen = NavigationScreen.FrontPage }
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
                                    screen = NavigationScreen.FrontPage
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
                                    val eventId = newUuid()
                                    val event = EventData(
                                        id = eventId,
                                        name = eventName,
                                        createdAt = nowMillis(),
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
                                screen = NavigationScreen.FrontPage
                            }
                        )
                    }

                    // ── Lista de eventos (Ver tudo) ──
                    is NavigationScreen.EventList -> {
                        EventListScreen(
                            events = uiState.events,
                            allPhotos = uiState.allPhotos,
                            onLoadThumbnail = { callbacks.onLoadPhoto(it) },
                            onOpenEvent = { event ->
                                screen = NavigationScreen.EventGallery(event.id, event.name)
                            },
                            onBack = { screen = NavigationScreen.FrontPage }
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
                            onBack = { screen = NavigationScreen.FrontPage }
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
                            onBack = { screen = NavigationScreen.FrontPage }
                        )
                    }

                    // ── Grid: fotos de um evento ──
                    is NavigationScreen.EventGallery -> {
                        val eventPhotos = uiState.allPhotos
                            .filter { it.eventId == nav.eventId }
                            .sortedByDescending { it.capturedAt }

                        // Nome atualizado (pode ter sido renomeado)
                        val currentEventName = uiState.events
                            .find { it.id == nav.eventId }?.name ?: nav.eventName

                        PhotoGridScreen(
                            title = currentEventName,
                            photos = eventPhotos,
                            onLoadThumbnail = { callbacks.onLoadPhoto(it) },
                            onPhotoClick = { index ->
                                screen = NavigationScreen.PhotoViewer(index, nav.eventId)
                            },
                            onBack = { screen = NavigationScreen.FrontPage },
                            eventMenuActions = EventMenuActions(
                                eventName = currentEventName,
                                onImportPhotos = {
                                    callbacks.onImportPhotosToEvent(nav.eventId)
                                },
                                onRenameEvent = { newName ->
                                    scope.launch {
                                        callbacks.onRenameEvent(nav.eventId, newName)
                                        callbacks.onRefreshData()
                                        screen = NavigationScreen.EventGallery(nav.eventId, newName)
                                    }
                                },
                                onDeletePhotos = {
                                    scope.launch {
                                        callbacks.onDeleteEvent(nav.eventId)
                                        callbacks.onRefreshData()
                                        screen = NavigationScreen.FrontPage
                                    }
                                }
                            )
                        )
                    }

                    // ── Livro de Memórias ──
                    is NavigationScreen.MemoryBook -> {
                        MemoryBookScreen(
                            events = uiState.events,
                            allPhotos = uiState.allPhotos,
                            onLoadThumbnail = { callbacks.onLoadPhoto(it) },
                            onBack = { screen = NavigationScreen.FrontPage }
                        )
                    }

                    // ── Lista de Registros ──
                    is NavigationScreen.MomentsList -> {
                        MomentsListScreen(
                            moments = uiState.moments,
                            onAddDaily = {
                                screen = NavigationScreen.MomentRegistration("DAILY")
                            },
                            onAddWeekly = {
                                screen = NavigationScreen.MomentRegistration("WEEKLY")
                            },
                            onDeleteMoment = { momentId ->
                                scope.launch {
                                    callbacks.onDeleteMoment(momentId)
                                    callbacks.onRefreshData()
                                }
                            },
                            onBack = { screen = NavigationScreen.FrontPage }
                        )
                    }

                    // ── Registro de Momento ──
                    is NavigationScreen.MomentRegistration -> {
                        val momentType = if (nav.type == "WEEKLY") MomentType.WEEKLY else MomentType.DAILY
                        MomentRegistrationScreen(
                            type = momentType,
                            onSave = { text ->
                                scope.launch {
                                    val now = nowMillis()
                                    val (periodStart, periodEnd) = if (momentType == MomentType.WEEKLY) {
                                        weekBoundsMillis()
                                    } else {
                                        dayBoundsMillis()
                                    }

                                    val moment = MomentEntry(
                                        id = newUuid(),
                                        text = text,
                                        type = momentType,
                                        createdAt = now,
                                        periodStart = periodStart,
                                        periodEnd = periodEnd
                                    )
                                    callbacks.onSaveMoment(moment)
                                    callbacks.onRefreshData()
                                    screen = NavigationScreen.MomentsList
                                }
                            },
                            onCancel = { screen = NavigationScreen.MomentsList }
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
