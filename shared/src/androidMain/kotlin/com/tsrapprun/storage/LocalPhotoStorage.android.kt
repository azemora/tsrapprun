/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  LocalPhotoStorage.android.kt - Armazenamento Android       ║
 * ║                                                             ║
 * ║  Fotos e eventos criptografados com AES-256-GCM             ║
 * ║  via Android Keystore (hardware-backed).                    ║
 * ║                                                             ║
 * ║  Estrutura no disco:                                        ║
 * ║  /data/data/com.tsrapprun.android/files/                    ║
 * ║  ├── photos/              → arquivos .enc criptografados    ║
 * ║  ├── photo_index.json.enc → índice de fotos                 ║
 * ║  └── event_index.json.enc → índice de eventos               ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.storage

import android.content.Context
import android.util.Log
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKey
import com.tsrapprun.camera.EventData
import com.tsrapprun.camera.PhotoData
import com.tsrapprun.child.ChildProfile
import com.tsrapprun.gallery.GalleryMediaSaver
import com.tsrapprun.moments.MomentDraft
import com.tsrapprun.moments.MomentEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.util.UUID

actual class LocalPhotoStorage(private val context: Context) {

    companion object {
        private const val TAG = "LocalPhotoStorage"
        private const val PHOTOS_DIR = "photos"
        private const val PHOTO_INDEX_FILE = "photo_index.json.enc"
        private const val EVENT_INDEX_FILE = "event_index.json.enc"
        private const val MOMENT_INDEX_FILE = "moment_index.json.enc"
        private const val CHILD_PROFILE_FILE = "child_profile.json.enc"
        private const val MOMENT_DRAFT_FILE = "moment_draft.json.enc"
        private const val REMINDER_INDEX_FILE = "reminder_index.json.enc"
    }

    private val masterKey: MasterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val photosDir: File by lazy {
        File(context.filesDir, PHOTOS_DIR).also { it.mkdirs() }
    }

    private val photoIndexMutex = Mutex()
    private val eventIndexMutex = Mutex()
    private val momentIndexMutex = Mutex()
    private val reminderIndexMutex = Mutex()
    private val childProfileMutex = Mutex()
    private val momentDraftMutex = Mutex()

    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = false
    }

    // ══════════════════════════════════════════════
    // OPERAÇÕES DE FOTOS
    // ══════════════════════════════════════════════

    /**
     * Salva foto criptografada. Aceita capturedAt opcional
     * para preservar data EXIF ao importar fotos externas.
     */
    actual suspend fun savePhoto(
        imageBytes: ByteArray,
        eventId: String?,
        capturedAt: Long?
    ): PhotoData {
        return withContext(Dispatchers.IO) {
            val photoId = UUID.randomUUID().toString()
            val timestamp = capturedAt ?: System.currentTimeMillis()
            val fileName = "${photoId}_${timestamp}.enc"
            val file = File(photosDir, fileName)

            try {
                val encryptedFile = EncryptedFile.Builder(
                    context, file, masterKey,
                    EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
                ).build()

                encryptedFile.openFileOutput().use { out ->
                    out.write(imageBytes)
                    out.flush()
                }

                val photoData = PhotoData(
                    id = photoId,
                    fileName = fileName,
                    filePath = file.absolutePath,
                    eventId = eventId,
                    capturedAt = timestamp,
                    sizeBytes = imageBytes.size.toLong(),
                    isUploadedToCloud = false
                )

                updatePhotoIndex { it + photoData }
                Log.d(TAG, "Foto salva: $fileName (${imageBytes.size} bytes)")

                // Salva cópia no álbum público "TSR App Run" da galeria
                // para que as fotos persistam mesmo se o app for desinstalado
                GalleryMediaSaver.saveToGallery(
                    context = context,
                    imageBytes = imageBytes,
                    displayName = "TSR_${photoId}_${timestamp}"
                )

                photoData
            } catch (e: Exception) {
                file.delete()
                Log.e(TAG, "Erro ao salvar foto", e)
                throw e
            }
        }
    }

    actual suspend fun loadPhoto(photoData: PhotoData): ByteArray? {
        return withContext(Dispatchers.IO) {
            val file = File(photoData.filePath)
            if (!file.exists()) return@withContext null
            try {
                val encryptedFile = EncryptedFile.Builder(
                    context, file, masterKey,
                    EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
                ).build()
                encryptedFile.openFileInput().use { it.readBytes() }
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao carregar foto: ${photoData.fileName}", e)
                null
            }
        }
    }

    actual suspend fun deletePhoto(photoData: PhotoData): Boolean {
        return withContext(Dispatchers.IO) {
            val deleted = File(photoData.filePath).delete()
            if (deleted) {
                updatePhotoIndex { photos -> photos.filter { it.id != photoData.id } }
            }
            deleted
        }
    }

    actual suspend fun listPhotos(): List<PhotoData> {
        return withContext(Dispatchers.IO) { readPhotoIndex() }
    }

    actual suspend fun listPhotosByEvent(eventId: String): List<PhotoData> {
        return withContext(Dispatchers.IO) {
            readPhotoIndex().filter { it.eventId == eventId }
        }
    }

    actual suspend fun getTotalStorageUsed(): Long {
        return withContext(Dispatchers.IO) {
            readPhotoIndex().sumOf { it.sizeBytes }
        }
    }

    // ══════════════════════════════════════════════
    // OPERAÇÕES DE EVENTOS
    // ══════════════════════════════════════════════

    actual suspend fun saveEvent(event: EventData) {
        withContext(Dispatchers.IO) {
            updateEventIndex { it + event }
            Log.d(TAG, "Evento salvo: ${event.name}")
        }
    }

    actual suspend fun listEvents(): List<EventData> {
        return withContext(Dispatchers.IO) { readEventIndex() }
    }

    actual suspend fun updateEvent(event: EventData) {
        withContext(Dispatchers.IO) {
            updateEventIndex { events ->
                events.map { if (it.id == event.id) event else it }
            }
        }
    }

    /**
     * Deleta um evento e TODAS as suas fotos associadas.
     * Primeiro deleta os arquivos de foto, depois remove dos índices.
     */
    actual suspend fun deleteEvent(eventId: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                // Deleta todas as fotos do evento
                val eventPhotos = readPhotoIndex().filter { it.eventId == eventId }
                eventPhotos.forEach { photo ->
                    File(photo.filePath).delete()
                }

                // Remove fotos do índice
                updatePhotoIndex { photos ->
                    photos.filter { it.eventId != eventId }
                }

                // Remove evento do índice
                updateEventIndex { events ->
                    events.filter { it.id != eventId }
                }

                Log.d(TAG, "Evento deletado: $eventId (${eventPhotos.size} fotos)")
                true
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao deletar evento", e)
                false
            }
        }
    }

    /**
     * Atribui eventId a uma lista de fotos (batch update).
     * Usado após o usuário nomear o evento — as fotos foram
     * salvas durante a captura com eventId=null.
     */
    actual suspend fun updatePhotosEventId(photoIds: List<String>, eventId: String) {
        withContext(Dispatchers.IO) {
            updatePhotoIndex { photos ->
                photos.map { photo ->
                    if (photo.id in photoIds) photo.copy(eventId = eventId)
                    else photo
                }
            }
            Log.d(TAG, "EventId atualizado em ${photoIds.size} fotos")
        }
    }

    // ══════════════════════════════════════════════
    // ÍNDICE DE FOTOS (criptografado)
    // ══════════════════════════════════════════════

    private fun readPhotoIndex(): List<PhotoData> {
        val indexFile = File(context.filesDir, PHOTO_INDEX_FILE)
        if (!indexFile.exists()) return emptyList()
        return try {
            val encryptedFile = EncryptedFile.Builder(
                context, indexFile, masterKey,
                EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
            ).build()
            val jsonString = encryptedFile.openFileInput().use { it.readBytes().decodeToString() }
            json.decodeFromString<List<PhotoData>>(jsonString)
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao ler índice de fotos", e)
            emptyList()
        }
    }

    private suspend fun updatePhotoIndex(transform: (List<PhotoData>) -> List<PhotoData>) {
        photoIndexMutex.withLock {
            val current = readPhotoIndex()
            val updated = transform(current)
            val jsonString = json.encodeToString(updated)

            val indexFile = File(context.filesDir, PHOTO_INDEX_FILE)
            indexFile.delete() // EncryptedFile não suporta sobrescrita

            val encryptedFile = EncryptedFile.Builder(
                context, indexFile, masterKey,
                EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
            ).build()
            encryptedFile.openFileOutput().use { out ->
                out.write(jsonString.toByteArray())
                out.flush()
            }
        }
    }

    // ══════════════════════════════════════════════
    // ÍNDICE DE EVENTOS (criptografado)
    // ══════════════════════════════════════════════

    private fun readEventIndex(): List<EventData> {
        val indexFile = File(context.filesDir, EVENT_INDEX_FILE)
        if (!indexFile.exists()) return emptyList()
        return try {
            val encryptedFile = EncryptedFile.Builder(
                context, indexFile, masterKey,
                EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
            ).build()
            val jsonString = encryptedFile.openFileInput().use { it.readBytes().decodeToString() }
            json.decodeFromString<List<EventData>>(jsonString)
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao ler índice de eventos", e)
            emptyList()
        }
    }

    private suspend fun updateEventIndex(transform: (List<EventData>) -> List<EventData>) {
        eventIndexMutex.withLock {
            val current = readEventIndex()
            val updated = transform(current)
            val jsonString = json.encodeToString(updated)

            val indexFile = File(context.filesDir, EVENT_INDEX_FILE)
            indexFile.delete()

            val encryptedFile = EncryptedFile.Builder(
                context, indexFile, masterKey,
                EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
            ).build()
            encryptedFile.openFileOutput().use { out ->
                out.write(jsonString.toByteArray())
                out.flush()
            }
        }
    }

    // ══════════════════════════════════════════════
    // OPERAÇÕES DE MOMENTOS (Registros)
    // ══════════════════════════════════════════════

    actual suspend fun saveMoment(moment: MomentEntry) {
        withContext(Dispatchers.IO) {
            updateMomentIndex { it + moment }
            Log.d(TAG, "Momento salvo: ${moment.type} - ${moment.id}")
        }
    }

    actual suspend fun listMoments(): List<MomentEntry> {
        return withContext(Dispatchers.IO) { readMomentIndex() }
    }

    actual suspend fun updateMoment(moment: MomentEntry) {
        withContext(Dispatchers.IO) {
            updateMomentIndex { moments ->
                moments.map { if (it.id == moment.id) moment else it }
            }
        }
    }

    actual suspend fun deleteMoment(momentId: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                updateMomentIndex { moments ->
                    moments.filter { it.id != momentId }
                }
                true
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao deletar momento", e)
                false
            }
        }
    }

    // ══════════════════════════════════════════════
    // PERFIL DA CRIANÇA (criptografado)
    // ══════════════════════════════════════════════

    actual suspend fun saveChildProfile(profile: ChildProfile) {
        withContext(Dispatchers.IO) {
            childProfileMutex.withLock {
                val jsonString = json.encodeToString(profile)
                val file = File(context.filesDir, CHILD_PROFILE_FILE)
                file.delete()
                val encryptedFile = EncryptedFile.Builder(
                    context, file, masterKey,
                    EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
                ).build()
                encryptedFile.openFileOutput().use { it.write(jsonString.toByteArray()); it.flush() }
            }
        }
    }

    actual suspend fun getChildProfile(): ChildProfile? {
        return withContext(Dispatchers.IO) {
            val file = File(context.filesDir, CHILD_PROFILE_FILE)
            if (!file.exists()) return@withContext null
            try {
                val encryptedFile = EncryptedFile.Builder(
                    context, file, masterKey,
                    EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
                ).build()
                val jsonString = encryptedFile.openFileInput().use { it.readBytes().decodeToString() }
                json.decodeFromString<ChildProfile>(jsonString)
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao ler perfil da criança", e)
                null
            }
        }
    }

    // ── RASCUNHO ──

    actual suspend fun saveMomentDraft(draft: MomentDraft) {
        withContext(Dispatchers.IO) {
            momentDraftMutex.withLock {
                val jsonString = json.encodeToString(draft)
                val file = File(context.filesDir, MOMENT_DRAFT_FILE)
                file.delete()
                val encryptedFile = EncryptedFile.Builder(
                    context, file, masterKey,
                    EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
                ).build()
                encryptedFile.openFileOutput().use { it.write(jsonString.toByteArray()); it.flush() }
            }
        }
    }

    actual suspend fun getMomentDraft(): MomentDraft? {
        return withContext(Dispatchers.IO) {
            val file = File(context.filesDir, MOMENT_DRAFT_FILE)
            if (!file.exists()) return@withContext null
            try {
                val encryptedFile = EncryptedFile.Builder(
                    context, file, masterKey,
                    EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
                ).build()
                val jsonString = encryptedFile.openFileInput().use { it.readBytes().decodeToString() }
                json.decodeFromString<MomentDraft>(jsonString)
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao ler rascunho", e)
                null
            }
        }
    }

    actual suspend fun clearMomentDraft() {
        withContext(Dispatchers.IO) {
            momentDraftMutex.withLock {
                File(context.filesDir, MOMENT_DRAFT_FILE).delete()
                Unit
            }
        }
    }

    // ══════════════════════════════════════════════
    // ÍNDICE DE MOMENTOS (criptografado)
    // ══════════════════════════════════════════════

    private fun readMomentIndex(): List<MomentEntry> {
        val indexFile = File(context.filesDir, MOMENT_INDEX_FILE)
        if (!indexFile.exists()) return emptyList()
        return try {
            val encryptedFile = EncryptedFile.Builder(
                context, indexFile, masterKey,
                EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
            ).build()
            val jsonString = encryptedFile.openFileInput().use { it.readBytes().decodeToString() }
            json.decodeFromString<List<MomentEntry>>(jsonString)
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao ler índice de momentos", e)
            emptyList()
        }
    }

    private suspend fun updateMomentIndex(transform: (List<MomentEntry>) -> List<MomentEntry>) {
        momentIndexMutex.withLock {
            val current = readMomentIndex()
            val updated = transform(current)
            val jsonString = json.encodeToString(updated)

            val indexFile = File(context.filesDir, MOMENT_INDEX_FILE)
            indexFile.delete()

            val encryptedFile = EncryptedFile.Builder(
                context, indexFile, masterKey,
                EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
            ).build()
            encryptedFile.openFileOutput().use { out ->
                out.write(jsonString.toByteArray())
                out.flush()
            }
        }
    }

    // ══════════════════════════════════════════════
    // LEMBRETES
    // ══════════════════════════════════════════════

    actual suspend fun saveReminder(reminder: com.tsrapprun.reminders.Reminder) {
        updateReminderIndex { it + reminder }
    }

    actual suspend fun listReminders(): List<com.tsrapprun.reminders.Reminder> =
        withContext(Dispatchers.IO) { readReminderIndex().sortedByDescending { it.createdAt } }

    actual suspend fun updateReminder(reminder: com.tsrapprun.reminders.Reminder) {
        updateReminderIndex { list -> list.map { if (it.id == reminder.id) reminder else it } }
    }

    actual suspend fun deleteReminder(reminderId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            updateReminderIndex { list -> list.filter { it.id != reminderId } }
            true
        } catch (t: Throwable) { false }
    }

    private fun readReminderIndex(): List<com.tsrapprun.reminders.Reminder> {
        val indexFile = File(context.filesDir, REMINDER_INDEX_FILE)
        if (!indexFile.exists()) return emptyList()
        return try {
            val encryptedFile = EncryptedFile.Builder(
                context, indexFile, masterKey,
                EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
            ).build()
            val jsonString = encryptedFile.openFileInput().use { it.readBytes().decodeToString() }
            json.decodeFromString<List<com.tsrapprun.reminders.Reminder>>(jsonString)
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao ler índice de lembretes", e)
            emptyList()
        }
    }

    private suspend fun updateReminderIndex(
        transform: (List<com.tsrapprun.reminders.Reminder>) -> List<com.tsrapprun.reminders.Reminder>
    ) {
        reminderIndexMutex.withLock {
            val current = readReminderIndex()
            val updated = transform(current)
            val jsonString = json.encodeToString(updated)
            val indexFile = File(context.filesDir, REMINDER_INDEX_FILE)
            indexFile.delete()
            val encryptedFile = EncryptedFile.Builder(
                context, indexFile, masterKey,
                EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
            ).build()
            encryptedFile.openFileOutput().use { out ->
                out.write(jsonString.toByteArray())
                out.flush()
            }
        }
    }
}
