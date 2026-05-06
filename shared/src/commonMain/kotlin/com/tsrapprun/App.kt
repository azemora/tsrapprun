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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.tsrapprun.audio.AudioCaptureScreen
import com.tsrapprun.audio.splitTranscription
import com.tsrapprun.auth.AuthState
import com.tsrapprun.auth.HomeScreen
import com.tsrapprun.auth.LoginScreen
import com.tsrapprun.camera.CameraScreen
import com.tsrapprun.camera.EventCameraScreen
import com.tsrapprun.camera.EventData
import com.tsrapprun.camera.EventNamingScreen
import com.tsrapprun.camera.PhotoData
import com.tsrapprun.calendar.CalendarScreen
import com.tsrapprun.child.ChildProfile
import com.tsrapprun.child.ChildRegistrationScreen
import com.tsrapprun.child.MesversarioAnnouncementScreen
import com.tsrapprun.stories.StoriesScreen
import com.tsrapprun.memorybook.MemoryBookScreen
import com.tsrapprun.moments.MomentDetailScreen
import com.tsrapprun.moments.MomentEntry
import com.tsrapprun.moments.MomentRegistrationScreen
import com.tsrapprun.moments.MomentType
import com.tsrapprun.moments.MomentsListScreen
import com.tsrapprun.moments.detectMilestone
import com.tsrapprun.gallery.EventListScreen
import com.tsrapprun.gallery.FrontPageScreen
import com.tsrapprun.gallery.GalleryScreen
import com.tsrapprun.gallery.EventMenuActions
import com.tsrapprun.gallery.PhotoGridScreen
import com.tsrapprun.gallery.PhotoViewerScreen
import com.tsrapprun.navigation.NavigationScreen
import com.tsrapprun.reminders.Reminder
import com.tsrapprun.reminders.ReminderCaptureScreen
import com.tsrapprun.reminders.RemindersListScreen
import com.tsrapprun.platform.dayBoundsMillis
import com.tsrapprun.platform.newUuid
import com.tsrapprun.platform.nowMillis
import com.tsrapprun.platform.weekBoundsMillis
import com.tsrapprun.ui.theme.CozyTheme
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
    val onRefreshData: suspend () -> Unit = {},
    /** Dispara uma notificação local de teste (debug). */
    val onTestNotification: () -> Unit = {},
    /** Notificação de teste de mesversário (mostra preview da copy). */
    val onTestMesversario: () -> Unit = {},
    /** Notificação de teste de aniversário (mostra preview da copy). */
    val onTestAniversario: () -> Unit = {},
    /** Salva ou atualiza o perfil da criança. */
    val onSaveChildProfile: suspend (firstName: String, birthdateMillis: Long, isPregnancy: Boolean, parentFirstName: String) -> Unit = { _, _, _, _ -> },
    val onSaveMomentDraft: suspend (com.tsrapprun.moments.MomentDraft) -> Unit = {},
    val onClearMomentDraft: suspend () -> Unit = {},
    val onSaveReminder: suspend (com.tsrapprun.reminders.Reminder) -> Unit = {},
    val onUpdateReminder: suspend (com.tsrapprun.reminders.Reminder) -> Unit = {},
    val onDeleteReminder: suspend (String) -> Unit = {},
    /** Limpa a notification action pendente após o App ter navegado. */
    val onClearNotificationAction: () -> Unit = {},
    /** Abre picker pra escolher avatar do bebê e salva no perfil. */
    val onPickChildAvatar: () -> Unit = {}
)

/**
 * Estado reativo da UI agrupado.
 */
data class AppUiState(
    val photoCount: Int = 0,
    val storageUsedMB: String = "0.0",
    val events: List<EventData> = emptyList(),
    val allPhotos: List<PhotoData> = emptyList(),
    val moments: List<MomentEntry> = emptyList(),
    /** Perfil da criança — null antes do cadastro. */
    val childProfile: ChildProfile? = null,
    /** Se != 0, mostra a tela de comemoração ao entrar (limpado depois). */
    val pendingMesversarioMonth: Int = 0,
    /** Rascunho ativo de registro, se houver. */
    val momentDraft: com.tsrapprun.moments.MomentDraft? = null,
    val reminders: List<com.tsrapprun.reminders.Reminder> = emptyList(),
    /** Ação pendente de tap em notificação — App navega ao observar. */
    val pendingNotificationAction: com.tsrapprun.notifications.NotificationAction? = null
)

@Composable
fun App(
    authState: StateFlow<AuthState>,
    callbacks: AppCallbacks,
    uiState: AppUiState
) {
    val currentAuthState by authState.collectAsState()
    val scope = rememberCoroutineScope()

    // Roteamento inicial inteligente:
    //  - Sem perfil cadastrado → ChildRegistration (gating)
    //  - Mesversário pendente → MesversarioAnnouncement
    //  - Caso contrário → FrontPage
    val initialScreen: NavigationScreen = when {
        uiState.childProfile == null -> NavigationScreen.ChildRegistration(isEditing = false)
        uiState.pendingMesversarioMonth > 0 -> NavigationScreen.MesversarioAnnouncement(uiState.pendingMesversarioMonth)
        else -> NavigationScreen.FrontPage
    }
    var screen by remember { mutableStateOf<NavigationScreen>(initialScreen) }
    // Pilha de navegação pra "voltar" retornar à tela que abriu (não sempre pra Home).
    val backStack = remember { mutableStateListOf<NavigationScreen>() }

    fun navigate(target: NavigationScreen) {
        backStack.add(screen)
        screen = target
    }
    fun goBack(default: NavigationScreen = NavigationScreen.FrontPage) {
        screen = if (backStack.isNotEmpty()) backStack.removeAt(backStack.size - 1) else default
    }
    fun replaceTo(target: NavigationScreen) {
        // Substitui sem empilhar (pra fluxos terminais como save → list).
        backStack.clear()
        screen = target
    }

    // Para qual tela voltar depois da câmera single-shot (null = comportamento padrão).
    var cameraReturnTarget by remember { mutableStateOf<NavigationScreen?>(null) }
    // Fotos capturadas pendentes que estão sendo acumuladas pra um registro
    // em criação (limpa após save ou cancelamento).
    var pendingPhotoIds by remember { mutableStateOf<List<String>>(emptyList()) }
    // Texto vindo da transcrição de áudio do "registro rápido" (limpa após save).
    var prefillFromAudio by remember { mutableStateOf<String?>(null) }
    // Modo convidado: bypassa auth e usa UserData sintético. Persiste enquanto
    // o app está rodando; é resetado ao reabrir (sem persistir em storage).
    var guestMode by remember { mutableStateOf(false) }

    // Sincroniza navegação quando o perfil é cadastrado pela primeira vez OU
    // quando há um novo mesversário pendente (após sintetizar marcos).
    androidx.compose.runtime.LaunchedEffect(uiState.childProfile?.id, uiState.pendingMesversarioMonth) {
        if (screen is NavigationScreen.ChildRegistration && uiState.childProfile != null) {
            screen = NavigationScreen.FrontPage
        }
        if (uiState.pendingMesversarioMonth > 0 &&
            screen !is NavigationScreen.MesversarioAnnouncement) {
            screen = NavigationScreen.MesversarioAnnouncement(uiState.pendingMesversarioMonth)
        }
    }

    // Tap em notificação → navega pra tela alvo
    androidx.compose.runtime.LaunchedEffect(uiState.pendingNotificationAction) {
        val action = uiState.pendingNotificationAction ?: return@LaunchedEffect
        when (action.type) {
            com.tsrapprun.notifications.NotificationAction.OPEN_REGISTRATION -> {
                pendingPhotoIds = emptyList()
                prefillFromAudio = null
                navigate(NavigationScreen.MomentRegistration("DAILY"))
            }
            com.tsrapprun.notifications.NotificationAction.OPEN_MESVERSARIO -> {
                val m = if (action.month > 0) action.month else 1
                navigate(NavigationScreen.MesversarioAnnouncement(m))
            }
            com.tsrapprun.notifications.NotificationAction.OPEN_ANNIVERSARY -> {
                // Ainda não há tela de aniversário dedicada — abre calendário no
                // mês do aniversário (data de nascimento).
                val birthMs = uiState.childProfile?.birthdateMillis
                navigate(NavigationScreen.Calendar(highlightMillis = birthMs))
            }
        }
        callbacks.onClearNotificationAction()
    }

    CozyTheme {
        // Trata convidado como autenticado pra usar o mesmo grafo de telas.
        val effectiveAuthenticated = currentAuthState is AuthState.Authenticated || guestMode
        when {
            effectiveAuthenticated -> {
                val userData = (currentAuthState as? AuthState.Authenticated)?.userData
                    ?: com.tsrapprun.security.UserData(
                        userId = "guest",
                        displayName = null,
                        email = null,
                        photoUrl = null
                    )

                when (val nav = screen) {
                    // ── Cadastro do perfil da criança ──
                    is NavigationScreen.ChildRegistration -> {
                        ChildRegistrationScreen(
                            initialProfile = if (nav.isEditing) uiState.childProfile else null,
                            onSave = { firstName, birthdateMillis, isPregnancy, parentFirstName ->
                                scope.launch {
                                    callbacks.onSaveChildProfile(firstName, birthdateMillis, isPregnancy, parentFirstName)
                                    callbacks.onRefreshData()
                                    screen = NavigationScreen.FrontPage
                                }
                            },
                            onCancel = if (nav.isEditing) {
                                { screen = NavigationScreen.Home }
                            } else null,
                            onPickAvatar = { callbacks.onImportPhotos() }
                        )
                    }

                    // ── Tela de mesversário ──
                    is NavigationScreen.MesversarioAnnouncement -> {
                        val name = uiState.childProfile?.firstName ?: "seu pequeno"
                        MesversarioAnnouncementScreen(
                            childFirstName = name,
                            monthsCompleted = nav.monthsCompleted,
                            onContinue = { goBack(default = NavigationScreen.FrontPage) },
                            onOpenMomentsList = {
                                // "registrar este mesversário" → abre criação com
                                // título pré-preenchido + flag de marco.
                                pendingPhotoIds = emptyList()
                                navigate(
                                    NavigationScreen.MomentRegistration(
                                        type = "MESVERSARIO_${nav.monthsCompleted}"
                                    )
                                )
                            }
                        )
                    }

                    // ── Home pós-auth (estilo image 2: criança em destaque) ──
                    is NavigationScreen.FrontPage -> {
                        HomeScreen(
                            userData = userData,
                            childProfile = uiState.childProfile,
                            photoCount = uiState.photoCount,
                            storageUsedMB = uiState.storageUsedMB,
                            events = uiState.events,
                            moments = uiState.moments,
                            allPhotos = uiState.allPhotos,
                            onLoadPhoto = { callbacks.onLoadPhoto(it) },
                            onRegisterEvent = {
                                pendingPhotoIds = emptyList()
                                prefillFromAudio = null
                                screen = NavigationScreen.MomentRegistration("DAILY")
                            },
                            onQuickRegister = {
                                // Registro rápido: câmera contínua → áudio → criação prefilada
                                pendingPhotoIds = emptyList()
                                prefillFromAudio = null
                                cameraReturnTarget = NavigationScreen.AudioCapture
                                navigate(NavigationScreen.EventRegistration)
                            },
                            onAddReminder = { navigate(NavigationScreen.ReminderCapture) },
                            onOpenReminders = { navigate(NavigationScreen.RemindersList()) },
                            onPickAvatar = { callbacks.onPickChildAvatar() },
                            onImportPhotos = { callbacks.onImportPhotos() },
                            onOpenGallery = { navigate(NavigationScreen.Gallery) },
                            onOpenEvent = { event ->
                                navigate(NavigationScreen.EventGallery(event.id, event.name))
                            },
                            onOpenEventList = { navigate(NavigationScreen.EventList) },
                            onOpenMemoryBook = { navigate(NavigationScreen.MemoryBook) },
                            onOpenCalendar = { highlight ->
                                navigate(NavigationScreen.Calendar(highlightMillis = highlight))
                            },
                            onOpenMoments = { navigate(NavigationScreen.MomentsList) },
                            onTestNotification = callbacks.onTestNotification,
                            onTestMesversario = callbacks.onTestMesversario,
                            onTestAniversario = callbacks.onTestAniversario,
                            onSignOutClick = callbacks.onSignOutClick,
                            onBack = { screen = NavigationScreen.Home }
                        )
                    }

                    // ── Histórias ──
                    is NavigationScreen.Stories -> {
                        StoriesScreen(onBack = { goBack(default = NavigationScreen.FrontPage) })
                    }

                    // ── Calendário ──
                    is NavigationScreen.Calendar -> {
                        CalendarScreen(
                            onBack = { goBack(default = NavigationScreen.FrontPage) },
                            highlightMillis = nav.highlightMillis,
                            reminders = uiState.reminders,
                            events = uiState.events,
                            moments = uiState.moments,
                            onOpenReminder = { id ->
                                navigate(NavigationScreen.RemindersList(highlightId = id))
                            },
                            onOpenEvent = { event ->
                                navigate(NavigationScreen.EventGallery(event.id, event.name))
                            },
                            onOpenMesversario = { month ->
                                navigate(NavigationScreen.MesversarioAnnouncement(month))
                            }
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
                            onTestNotification = callbacks.onTestNotification,
                            onTestMesversario = callbacks.onTestMesversario,
                            onTestAniversario = callbacks.onTestAniversario,
                            onSignOutClick = callbacks.onSignOutClick,
                            onBack = { screen = NavigationScreen.FrontPage }
                        )
                    }

                    // ── Câmera avulsa (single-shot) ──
                    is NavigationScreen.Camera -> {
                        // Se cameraReturnTarget != null, volta pra tela que chamou
                        // (ex: MomentRegistration). Senão, padrão = FrontPage.
                        val returnTo = cameraReturnTarget ?: NavigationScreen.FrontPage
                        CameraScreen(
                            onPhotoCaptured = { bytes ->
                                scope.launch {
                                    val id = callbacks.onSaveEventPhoto(bytes)
                                    // Acumula no registro em criação se a tela de
                                    // retorno for MomentRegistration.
                                    if (returnTo is NavigationScreen.MomentRegistration) {
                                        pendingPhotoIds = pendingPhotoIds + id
                                    }
                                    callbacks.onRefreshData()
                                    screen = returnTo
                                    cameraReturnTarget = null
                                }
                            },
                            onError = {},
                            onClose = {
                                screen = returnTo
                                cameraReturnTarget = null
                            }
                        )
                    }

                    // ── Câmera contínua (registro de evento) ──
                    is NavigationScreen.EventRegistration -> {
                        // cameraReturnTarget define o fluxo:
                        //  • AudioCapture (registro rápido): 0 fotos = abort → home;
                        //    com fotos = continua pra áudio
                        //  • MomentRegistration (registro manual): qualquer caso volta
                        //    pra criação preservando o parcial
                        //  • null (fluxo legado de evento): vai pra EventNaming
                        val returnTarget = cameraReturnTarget
                        EventCameraScreen(
                            onPhotoCaptured = { bytes ->
                                callbacks.onSaveEventPhoto(bytes)
                            },
                            onFinish = { photoIds ->
                                cameraReturnTarget = null
                                when {
                                    // Registro rápido cancelado (sem fotos) → home limpo
                                    returnTarget is NavigationScreen.AudioCapture &&
                                            photoIds.isEmpty() -> {
                                        pendingPhotoIds = emptyList()
                                        prefillFromAudio = null
                                        replaceTo(NavigationScreen.FrontPage)
                                    }
                                    returnTarget != null -> {
                                        pendingPhotoIds = pendingPhotoIds + photoIds
                                        screen = returnTarget
                                    }
                                    photoIds.isEmpty() -> screen = NavigationScreen.FrontPage
                                    else -> screen = NavigationScreen.EventNaming(photoIds)
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
                                navigate(NavigationScreen.EventGallery(event.id, event.name))
                            },
                            onCreate = {
                                pendingPhotoIds = emptyList()
                                navigate(NavigationScreen.MomentRegistration("DAILY"))
                            },
                            onBack = { goBack(default = NavigationScreen.FrontPage) }
                        )
                    }

                    // ── Galeria raiz ──
                    is NavigationScreen.Gallery -> {
                        GalleryScreen(
                            events = uiState.events,
                            totalPhotoCount = uiState.photoCount,
                            onOpenAllPhotos = { navigate(NavigationScreen.AllPhotos) },
                            onOpenEvent = { event ->
                                navigate(NavigationScreen.EventGallery(event.id, event.name))
                            },
                            onBack = { goBack(default = NavigationScreen.FrontPage) }
                        )
                    }

                    // ── Grid: todas as fotos ──
                    is NavigationScreen.AllPhotos -> {
                        PhotoGridScreen(
                            title = "Todas as Fotos",
                            photos = uiState.allPhotos.sortedByDescending { it.capturedAt },
                            onLoadThumbnail = { callbacks.onLoadPhoto(it) },
                            onPhotoClick = { index ->
                                navigate(NavigationScreen.PhotoViewer(index))
                            },
                            onBack = { goBack(default = NavigationScreen.FrontPage) }
                        )
                    }

                    // ── Grid: fotos de um evento ──
                    is NavigationScreen.EventGallery -> {
                        val eventPhotos = uiState.allPhotos
                            .filter { it.eventId == nav.eventId }
                            .sortedByDescending { it.capturedAt }

                        // Evento atual com possível renomeação
                        val currentEvent = uiState.events.find { it.id == nav.eventId }
                        val currentEventName = currentEvent?.name ?: nav.eventName

                        PhotoGridScreen(
                            title = currentEventName,
                            photos = eventPhotos,
                            onLoadThumbnail = { callbacks.onLoadPhoto(it) },
                            note = currentEvent?.note,
                            onPhotoClick = { index ->
                                navigate(NavigationScreen.PhotoViewer(index, nav.eventId))
                            },
                            onBack = { goBack(default = NavigationScreen.FrontPage) },
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
                                        goBack(default = NavigationScreen.FrontPage)
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
                            onBack = { goBack(default = NavigationScreen.FrontPage) }
                        )
                    }

                    // ── Lista de Registros ──
                    is NavigationScreen.MomentsList -> {
                        MomentsListScreen(
                            moments = uiState.moments,
                            events = uiState.events,
                            allPhotos = uiState.allPhotos,
                            onLoadPhoto = { callbacks.onLoadPhoto(it) },
                            onAddRegistro = {
                                pendingPhotoIds = emptyList()
                                navigate(NavigationScreen.MomentRegistration("DAILY"))
                            },
                            onDeleteMoment = { momentId ->
                                scope.launch {
                                    callbacks.onDeleteMoment(momentId)
                                    callbacks.onRefreshData()
                                }
                            },
                            onOpenMoment = { moment ->
                                navigate(NavigationScreen.MomentDetail(moment.id))
                            },
                            onOpenEvent = { event ->
                                navigate(NavigationScreen.EventGallery(event.id, event.name))
                            },
                            onOpenMesversario = { monthsCompleted ->
                                navigate(NavigationScreen.MesversarioAnnouncement(monthsCompleted))
                            },
                            onBack = { goBack(default = NavigationScreen.FrontPage) }
                        )
                    }

                    // ── Captura de áudio para lembrete (sage) ──
                    is NavigationScreen.ReminderCapture -> {
                        ReminderCaptureScreen(
                            onConfirm = { text, dueAt ->
                                scope.launch {
                                    val r = Reminder(
                                        id = newUuid(),
                                        text = text,
                                        createdAt = nowMillis(),
                                        dueAt = dueAt
                                    )
                                    callbacks.onSaveReminder(r)
                                    callbacks.onRefreshData()
                                    replaceTo(NavigationScreen.RemindersList())
                                }
                            },
                            onBack = { goBack(default = NavigationScreen.FrontPage) }
                        )
                    }

                    // ── Lista de lembretes ──
                    is NavigationScreen.RemindersList -> {
                        RemindersListScreen(
                            reminders = uiState.reminders,
                            highlightId = nav.highlightId,
                            onAdd = { navigate(NavigationScreen.ReminderCapture) },
                            onToggleComplete = { reminder ->
                                scope.launch {
                                    callbacks.onUpdateReminder(reminder.copy(completed = !reminder.completed))
                                    callbacks.onRefreshData()
                                }
                            },
                            onDelete = { id ->
                                scope.launch {
                                    callbacks.onDeleteReminder(id)
                                    callbacks.onRefreshData()
                                }
                            },
                            onBack = { goBack(default = NavigationScreen.FrontPage) }
                        )
                    }

                    // ── Captura de áudio (registro rápido, passo 2) ──
                    is NavigationScreen.AudioCapture -> {
                        AudioCaptureScreen(
                            photoCount = pendingPhotoIds.size,
                            onConfirm = { transcription ->
                                prefillFromAudio = transcription
                                navigate(NavigationScreen.MomentRegistration("DAILY"))
                            },
                            onSkip = {
                                navigate(NavigationScreen.MomentRegistration("DAILY"))
                            },
                            onBack = { goBack(default = NavigationScreen.FrontPage) }
                        )
                    }

                    // ── Detalhe full-screen de registro de texto ──
                    is NavigationScreen.MomentDetail -> {
                        val moment = uiState.moments.find { it.id == nav.momentId }
                        if (moment == null) {
                            // Foi excluído ou não existe — volta.
                            androidx.compose.runtime.LaunchedEffect(nav.momentId) {
                                goBack(default = NavigationScreen.MomentsList)
                            }
                        } else {
                            MomentDetailScreen(
                                moment = moment,
                                onBack = { goBack(default = NavigationScreen.MomentsList) },
                                onDelete = {
                                    scope.launch {
                                        callbacks.onDeleteMoment(nav.momentId)
                                        callbacks.onRefreshData()
                                        goBack(default = NavigationScreen.MomentsList)
                                    }
                                }
                            )
                        }
                    }

                    // ── Criação unificada de registro (texto + fotos opcionais) ──
                    is NavigationScreen.MomentRegistration -> {
                        // Prefill: 1) áudio transcrito, 2) mesversário, 3) rascunho salvo.
                        val mesversarioMonth = nav.type.removePrefix("MESVERSARIO_").toIntOrNull()
                        val mesversarioTitle = if (mesversarioMonth != null) {
                            val name = uiState.childProfile?.firstName ?: ""
                            val monthLabel = if (mesversarioMonth == 1) "1 mês" else "$mesversarioMonth meses"
                            if (name.isNotBlank()) "$name fez $monthLabel ✦" else "$monthLabel ✦"
                        } else null

                        val audioParts = prefillFromAudio?.let { splitTranscription(it) }
                        val finalPrefillTitle = audioParts?.first ?: mesversarioTitle
                        val finalPrefillNote = audioParts?.second
                        val hasPrefill = finalPrefillTitle != null

                        val capturedPhotos = remember(pendingPhotoIds, uiState.allPhotos) {
                            uiState.allPhotos.filter { it.id in pendingPhotoIds }
                                .sortedBy { pendingPhotoIds.indexOf(it.id) }
                        }
                        MomentRegistrationScreen(
                            initialDraft = if (hasPrefill) null else uiState.momentDraft,
                            initialPhotoIds = pendingPhotoIds,
                            initialPhotos = capturedPhotos,
                            initialTitle = finalPrefillTitle,
                            initialNote = finalPrefillNote,
                            onLoadPhoto = { photo -> callbacks.onLoadPhoto(photo) },
                            initialIsMilestone = mesversarioMonth != null ||
                                    (audioParts != null && com.tsrapprun.moments.detectMilestone(
                                        audioParts.first, audioParts.second
                                    )),
                            onSave = { titleText, noteText, photoIds, userMarkedMilestone ->
                                scope.launch {
                                    val now = nowMillis()
                                    // Combina toggle do usuário com auto-detecção por keyword.
                                    val isMilestone = userMarkedMilestone ||
                                            detectMilestone(titleText, noteText)
                                    if (photoIds.isNotEmpty()) {
                                        val eventId = newUuid()
                                        val event = EventData(
                                            id = eventId,
                                            name = titleText.ifBlank { "registro sem nome" },
                                            createdAt = now,
                                            photoCount = photoIds.size,
                                            thumbnailPhotoId = photoIds.firstOrNull(),
                                            note = noteText.ifBlank { null },
                                            isMilestone = isMilestone
                                        )
                                        callbacks.onUpdatePhotosEventId(photoIds, eventId)
                                        callbacks.onSaveEvent(event)
                                    } else {
                                        val (periodStart, periodEnd) = dayBoundsMillis()
                                        val finalText = if (noteText.isBlank()) titleText
                                            else if (titleText.isBlank()) noteText
                                            else "$titleText — $noteText"
                                        val moment = MomentEntry(
                                            id = newUuid(),
                                            text = finalText,
                                            type = MomentType.DAILY,
                                            createdAt = now,
                                            periodStart = periodStart,
                                            periodEnd = periodEnd,
                                            isMilestone = isMilestone
                                        )
                                        callbacks.onSaveMoment(moment)
                                    }
                                    callbacks.onRefreshData()
                                    pendingPhotoIds = emptyList()
                                    prefillFromAudio = null
                                    replaceTo(NavigationScreen.MomentsList)
                                }
                            },
                            onSaveDraft = { draft -> callbacks.onSaveMomentDraft(draft) },
                            onClearDraft = { callbacks.onClearMomentDraft() },
                            onPickFromCamera = {
                                cameraReturnTarget = NavigationScreen.MomentRegistration(nav.type)
                                screen = NavigationScreen.EventRegistration
                            },
                            onPickFromGallery = { callbacks.onImportPhotos() },
                            onCancel = {
                                pendingPhotoIds = emptyList()
                                prefillFromAudio = null
                                goBack(default = NavigationScreen.FrontPage)
                            }
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

            // ── Não autenticado / Loading / Erro ──
            //  • Primeiro acesso → mostra FrontPage (welcome com "vamos começar")
            //  • Após tap → LoginScreen
            else -> {
                var welcomeSeen by remember { mutableStateOf(false) }
                if (!welcomeSeen && currentAuthState is AuthState.Unauthenticated) {
                    FrontPageScreen(onStart = { welcomeSeen = true })
                } else {
                    LoginScreen(
                        authState = currentAuthState,
                        onSignInClick = callbacks.onSignInClick,
                        onContinueAsGuest = { guestMode = true }
                    )
                }
            }
        }
    }
}

// getPlatformName() está definido em Platform.kt (expect/actual)
