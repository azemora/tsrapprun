package com.tsrapprun

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import com.tsrapprun.camera.toByteArray
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.ComposeUIViewController
import com.tsrapprun.auth.IosAuthBridge
import com.tsrapprun.camera.EventData
import com.tsrapprun.camera.PhotoData
import com.tsrapprun.child.ChildProfile
import com.tsrapprun.child.ChildProfileSanitizer
import com.tsrapprun.child.MilestoneSynthesizer
import com.tsrapprun.moments.MomentDraft
import com.tsrapprun.moments.MomentEntry
import com.tsrapprun.notifications.IosNotificationBridge
import com.tsrapprun.platform.newUuid
import com.tsrapprun.platform.nowMillis
import com.tsrapprun.storage.LocalPhotoStorage

fun MainViewController(
    authBridge: IosAuthBridge,
    storage: LocalPhotoStorage
) = ComposeUIViewController {
    var photoCount by remember { mutableStateOf(0) }
    var storageUsedMB by remember { mutableStateOf("0.0") }
    var events by remember { mutableStateOf<List<EventData>>(emptyList()) }
    var allPhotos by remember { mutableStateOf<List<PhotoData>>(emptyList()) }
    var moments by remember { mutableStateOf<List<MomentEntry>>(emptyList()) }
    var childProfile by remember { mutableStateOf<ChildProfile?>(null) }
    var pendingMesversario by remember { mutableStateOf(0) }
    var momentDraft by remember { mutableStateOf<MomentDraft?>(null) }
    var reminders by remember { mutableStateOf<List<com.tsrapprun.reminders.Reminder>>(emptyList()) }
    val pendingNotificationAction by IosNotificationBridge.pendingActionFlow.collectAsState()
    val bgScope = androidx.compose.runtime.rememberCoroutineScope()

    suspend fun refreshData() {
        // Carrega perfil primeiro
        val profile = storage.getChildProfile()
        childProfile = profile

        // Sintetiza marcos (semanas e mesversários) que ainda não existem
        if (profile != null) {
            val result = MilestoneSynthesizer.synthesizeFor(profile, storage)
            if (result.hasNewMesversario && result.latestNewMonth > profile.lastSeenMonthCount) {
                pendingMesversario = result.latestNewMonth
            }
            // Atualiza counters do perfil para evitar re-celebrar o mesmo mês
            if (result.hasNewMesversario || result.newDayEntries > 0 || result.newPregnancyEntries > 0) {
                val updated = profile.copy(
                    lastSeenMonthCount = maxOf(profile.lastSeenMonthCount, result.latestNewMonth),
                    lastSeenDayCount = maxOf(profile.lastSeenDayCount, result.currentAge.daysOfLife),
                    lastSeenWeekCount = maxOf(profile.lastSeenWeekCount, result.currentAge.pregnancyWeeksRemaining)
                )
                storage.saveChildProfile(updated)
                childProfile = updated
                // (Re)agenda lembretes da criança
                IosNotificationBridge.onScheduleChildNotifications(
                    updated.firstName,
                    updated.birthdateMillis
                )
            }
        }

        allPhotos = storage.listPhotos()
        photoCount = allPhotos.size
        events = storage.listEvents()
        moments = storage.listMoments()
        momentDraft = storage.getMomentDraft()
        reminders = storage.listReminders()
        val totalBytes = storage.getTotalStorageUsed()
        storageUsedMB = formatMb(totalBytes)
    }

    LaunchedEffect(Unit) { refreshData() }

    App(
        authState = authBridge.authState,
        callbacks = AppCallbacks(
            onSignInClick = { authBridge.onSignInClick() },
            onSignOutClick = { authBridge.onSignOutClick() },
            onImportPhotos = {
                com.tsrapprun.photos.IosPhotoPickerBridge.onPick(null) { dataList, _ ->
                    bgScope.launch {
                        dataList.forEach { d ->
                            storage.savePhoto(imageBytes = d.toByteArray())
                        }
                        refreshData()
                    }
                }
            },
            onImportPhotosToEvent = { eventId ->
                com.tsrapprun.photos.IosPhotoPickerBridge.onPick(eventId) { dataList, _ ->
                    bgScope.launch {
                        dataList.forEach { d ->
                            storage.savePhoto(imageBytes = d.toByteArray(), eventId = eventId)
                        }
                        events.find { it.id == eventId }?.let { e ->
                            val newCount = storage.listPhotosByEvent(eventId).size
                            storage.updateEvent(e.copy(photoCount = newCount))
                        }
                        refreshData()
                    }
                }
            },
            onSaveEventPhoto = { bytes ->
                storage.savePhoto(imageBytes = bytes).id
            },
            onSaveEvent = { event -> storage.saveEvent(event) },
            onDeletePhoto = { photo -> storage.deletePhoto(photo) },
            onDeleteEvent = { eventId -> storage.deleteEvent(eventId) },
            onRenameEvent = { eventId, newName ->
                events.find { it.id == eventId }?.let {
                    storage.updateEvent(it.copy(name = newName))
                }
            },
            onLoadPhoto = { photo -> storage.loadPhoto(photo) },
            onUpdatePhotosEventId = { photoIds, eventId ->
                storage.updatePhotosEventId(photoIds, eventId)
            },
            onSaveMoment = { moment -> storage.saveMoment(moment) },
            onDeleteMoment = { momentId -> storage.deleteMoment(momentId) },
            onRefreshData = { refreshData() },
            onTestNotification = { IosNotificationBridge.onTestNotification() },
            onTestMesversario = {
                val name = childProfile?.firstName ?: ""
                IosNotificationBridge.onTestMesversario(name)
            },
            onTestAniversario = {
                val name = childProfile?.firstName ?: ""
                IosNotificationBridge.onTestAniversario(name)
            },
            onSaveMomentDraft = { draft ->
                storage.saveMomentDraft(draft)
                momentDraft = draft
            },
            onClearMomentDraft = {
                storage.clearMomentDraft()
                momentDraft = null
            },
            onSaveChildProfile = { firstName, birthdateMillis, isPregnancy, parentFirstName ->
                // Defesa em profundidade: re-sanitiza no caller também.
                when (val result = ChildProfileSanitizer.sanitize(
                    rawName = firstName,
                    birthdateMillis = birthdateMillis,
                    isPregnancy = isPregnancy,
                    nowMillis = nowMillis()
                )) {
                    is ChildProfileSanitizer.Result.Valid -> {
                        val existing = storage.getChildProfile()
                        val toSave = ChildProfile(
                            id = existing?.id ?: newUuid(),
                            firstName = result.firstName,
                            birthdateMillis = result.birthdateMillis,
                            isPregnancy = result.isPregnancy,
                            createdAtMillis = existing?.createdAtMillis ?: nowMillis(),
                            lastSeenMonthCount = existing?.lastSeenMonthCount ?: 0,
                            lastSeenWeekCount = existing?.lastSeenWeekCount ?: 0,
                            lastSeenDayCount = existing?.lastSeenDayCount ?: 0,
                            parentFirstName = parentFirstName.ifBlank { null }
                        )
                        storage.saveChildProfile(toSave)
                    }
                    is ChildProfileSanitizer.Result.Invalid -> {
                        // Caller deve tratar; aqui apenas não persistimos.
                    }
                }
            },
            onSaveReminder = { reminder ->
                storage.saveReminder(reminder)
                reminders = storage.listReminders()
            },
            onUpdateReminder = { reminder ->
                storage.updateReminder(reminder)
                reminders = storage.listReminders()
            },
            onDeleteReminder = { id ->
                storage.deleteReminder(id)
                reminders = storage.listReminders()
            },
            onClearNotificationAction = {
                IosNotificationBridge.setPendingAction(null)
            },
            onPickChildAvatar = {
                com.tsrapprun.photos.IosPhotoPickerBridge.onPick(null) { dataList, _ ->
                    bgScope.launch {
                        val firstBytes = dataList.firstOrNull()?.toByteArray() ?: return@launch
                        val saved = storage.savePhoto(imageBytes = firstBytes)
                        val current = storage.getChildProfile() ?: return@launch
                        storage.saveChildProfile(current.copy(avatarPhotoId = saved.id))
                        refreshData()
                    }
                }
            }
        ),
        uiState = AppUiState(
            photoCount = photoCount,
            storageUsedMB = storageUsedMB,
            events = events,
            allPhotos = allPhotos,
            moments = moments,
            childProfile = childProfile,
            pendingMesversarioMonth = pendingMesversario,
            momentDraft = momentDraft,
            reminders = reminders,
            pendingNotificationAction = pendingNotificationAction
        )
    )
}

/** Formata bytes em MB com uma casa decimal (sem depender de String.format). */
private fun formatMb(totalBytes: Long): String {
    val mb = totalBytes / (1024.0 * 1024.0)
    val whole = mb.toLong()
    val frac = ((mb - whole) * 10).toLong().coerceIn(0L, 9L)
    return "$whole.$frac"
}
