@file:OptIn(
    kotlinx.cinterop.ExperimentalForeignApi::class,
    kotlinx.cinterop.BetaInteropApi::class
)

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  LocalPhotoStorage.ios.kt — Armazenamento iOS                ║
 * ║                                                              ║
 * ║  Persiste fotos e índices em Documents/ via NSFileManager.   ║
 * ║                                                              ║
 * ║  Estrutura no disco:                                         ║
 * ║   <Documents>/photos/<id>_<ts>.jpg   — bytes da foto        ║
 * ║   <Documents>/photo_index.json       — metadados             ║
 * ║   <Documents>/event_index.json       — eventos               ║
 * ║   <Documents>/moment_index.json      — momentos              ║
 * ║                                                              ║
 * ║  NOTA: por enquanto sem criptografia em repouso (a versão    ║
 * ║  Android usa AES-256-GCM via Android Keystore). Para iOS,    ║
 * ║  o sandbox do app já isola os dados de outros apps; chamar   ║
 * ║  CommonCrypto via cinterop fica para uma próxima passada.   ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.storage

import com.tsrapprun.camera.EventData
import com.tsrapprun.camera.PhotoData
import com.tsrapprun.child.ChildProfile
import com.tsrapprun.moments.MomentEntry
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import platform.Foundation.NSData
import platform.Foundation.NSDate
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUUID
import platform.Foundation.NSUserDomainMask
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create
import platform.Foundation.dataUsingEncoding
import platform.Foundation.dataWithContentsOfFile
import platform.Foundation.timeIntervalSince1970
import platform.Foundation.writeToFile
import platform.posix.memcpy

actual class LocalPhotoStorage {

    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = false
    }

    private val photoIndexMutex = Mutex()
    private val eventIndexMutex = Mutex()
    private val momentIndexMutex = Mutex()

    private val documentsDir: String by lazy {
        val urls = NSFileManager.defaultManager.URLsForDirectory(
            directory = NSDocumentDirectory,
            inDomains = NSUserDomainMask
        )
        val first = urls.firstOrNull() as? NSURL
        first?.path ?: error("Could not resolve Documents directory")
    }

    private val photosDir: String by lazy {
        val dir = "$documentsDir/photos"
        NSFileManager.defaultManager.createDirectoryAtPath(
            path = dir,
            withIntermediateDirectories = true,
            attributes = null,
            error = null
        )
        dir
    }

    private val photoIndexFile: String get() = "$documentsDir/photo_index.json"
    private val eventIndexFile: String get() = "$documentsDir/event_index.json"
    private val momentIndexFile: String get() = "$documentsDir/moment_index.json"
    private val childProfileFile: String get() = "$documentsDir/child_profile.json"
    private val childProfileMutex = Mutex()

    // ══════════════════════════════════════════════
    // FOTOS
    // ══════════════════════════════════════════════

    actual suspend fun savePhoto(
        imageBytes: ByteArray,
        eventId: String?,
        capturedAt: Long?
    ): PhotoData = withContext(Dispatchers.Default) {
        val photoId = NSUUID().UUIDString
        val timestamp = capturedAt ?: nowMillis()
        val fileName = "${photoId}_${timestamp}.jpg"
        val filePath = "$photosDir/$fileName"

        imageBytes.toNSData().writeToFile(path = filePath, atomically = true)

        val photoData = PhotoData(
            id = photoId,
            fileName = fileName,
            filePath = filePath,
            eventId = eventId,
            capturedAt = timestamp,
            sizeBytes = imageBytes.size.toLong(),
            isUploadedToCloud = false
        )

        updatePhotoIndex { it + photoData }
        photoData
    }

    actual suspend fun loadPhoto(photoData: PhotoData): ByteArray? = withContext(Dispatchers.Default) {
        NSData.dataWithContentsOfFile(photoData.filePath)?.toByteArray()
    }

    actual suspend fun deletePhoto(photoData: PhotoData): Boolean = withContext(Dispatchers.Default) {
        val ok = NSFileManager.defaultManager.removeItemAtPath(photoData.filePath, null)
        if (ok) updatePhotoIndex { photos -> photos.filter { it.id != photoData.id } }
        ok
    }

    actual suspend fun listPhotos(): List<PhotoData> = withContext(Dispatchers.Default) {
        readPhotoIndex()
    }

    actual suspend fun listPhotosByEvent(eventId: String): List<PhotoData> = withContext(Dispatchers.Default) {
        readPhotoIndex().filter { it.eventId == eventId }
    }

    actual suspend fun getTotalStorageUsed(): Long = withContext(Dispatchers.Default) {
        readPhotoIndex().sumOf { it.sizeBytes }
    }

    // ══════════════════════════════════════════════
    // EVENTOS
    // ══════════════════════════════════════════════

    actual suspend fun saveEvent(event: EventData) {
        updateEventIndex { it + event }
    }

    actual suspend fun listEvents(): List<EventData> = withContext(Dispatchers.Default) {
        readEventIndex()
    }

    actual suspend fun updateEvent(event: EventData) {
        updateEventIndex { events ->
            events.map { if (it.id == event.id) event else it }
        }
    }

    actual suspend fun deleteEvent(eventId: String): Boolean = withContext(Dispatchers.Default) {
        try {
            val eventPhotos = readPhotoIndex().filter { it.eventId == eventId }
            eventPhotos.forEach {
                NSFileManager.defaultManager.removeItemAtPath(it.filePath, null)
            }
            updatePhotoIndex { photos -> photos.filter { it.eventId != eventId } }
            updateEventIndex { events -> events.filter { it.id != eventId } }
            true
        } catch (t: Throwable) {
            false
        }
    }

    actual suspend fun updatePhotosEventId(photoIds: List<String>, eventId: String) {
        updatePhotoIndex { photos ->
            photos.map { p ->
                if (p.id in photoIds) p.copy(eventId = eventId) else p
            }
        }
    }

    // ══════════════════════════════════════════════
    // MOMENTOS
    // ══════════════════════════════════════════════

    actual suspend fun saveMoment(moment: MomentEntry) {
        updateMomentIndex { it + moment }
    }

    actual suspend fun listMoments(): List<MomentEntry> = withContext(Dispatchers.Default) {
        readMomentIndex()
    }

    actual suspend fun updateMoment(moment: MomentEntry) {
        updateMomentIndex { moments ->
            moments.map { if (it.id == moment.id) moment else it }
        }
    }

    actual suspend fun deleteMoment(momentId: String): Boolean = withContext(Dispatchers.Default) {
        try {
            updateMomentIndex { moments -> moments.filter { it.id != momentId } }
            true
        } catch (t: Throwable) {
            false
        }
    }

    // ══════════════════════════════════════════════
    // PERFIL DA CRIANÇA
    // ══════════════════════════════════════════════

    actual suspend fun saveChildProfile(profile: ChildProfile) = withContext(Dispatchers.Default) {
        childProfileMutex.withLock {
            writeJsonText(childProfileFile, json.encodeToString(profile))
        }
    }

    actual suspend fun getChildProfile(): ChildProfile? = withContext(Dispatchers.Default) {
        readJsonText(childProfileFile)?.let { text ->
            runCatching { json.decodeFromString<ChildProfile>(text) }.getOrNull()
        }
    }

    // ══════════════════════════════════════════════
    // ÍNDICES
    // ══════════════════════════════════════════════

    private fun readPhotoIndex(): List<PhotoData> = readJsonText(photoIndexFile)?.let {
        runCatching { json.decodeFromString<List<PhotoData>>(it) }.getOrNull()
    } ?: emptyList()

    private suspend fun updatePhotoIndex(transform: (List<PhotoData>) -> List<PhotoData>) {
        photoIndexMutex.withLock {
            val updated = transform(readPhotoIndex())
            writeJsonText(photoIndexFile, json.encodeToString(updated))
        }
    }

    private fun readEventIndex(): List<EventData> = readJsonText(eventIndexFile)?.let {
        runCatching { json.decodeFromString<List<EventData>>(it) }.getOrNull()
    } ?: emptyList()

    private suspend fun updateEventIndex(transform: (List<EventData>) -> List<EventData>) {
        eventIndexMutex.withLock {
            val updated = transform(readEventIndex())
            writeJsonText(eventIndexFile, json.encodeToString(updated))
        }
    }

    private fun readMomentIndex(): List<MomentEntry> = readJsonText(momentIndexFile)?.let {
        runCatching { json.decodeFromString<List<MomentEntry>>(it) }.getOrNull()
    } ?: emptyList()

    private suspend fun updateMomentIndex(transform: (List<MomentEntry>) -> List<MomentEntry>) {
        momentIndexMutex.withLock {
            val updated = transform(readMomentIndex())
            writeJsonText(momentIndexFile, json.encodeToString(updated))
        }
    }

    // ══════════════════════════════════════════════
    // I/O HELPERS
    // ══════════════════════════════════════════════

    private fun readJsonText(path: String): String? {
        val data = NSData.dataWithContentsOfFile(path) ?: return null
        return platform.Foundation.NSString.create(
            data = data,
            encoding = NSUTF8StringEncoding
        )?.toString()
    }

    private fun writeJsonText(path: String, text: String) {
        val nsstr = platform.Foundation.NSString.create(string = text)
        val data = nsstr.dataUsingEncoding(NSUTF8StringEncoding) ?: return
        data.writeToFile(path = path, atomically = true)
    }

    private fun nowMillis(): Long {
        return (NSDate().timeIntervalSince1970 * 1000.0).toLong()
    }
}

// ══════════════════════════════════════════════
// ByteArray ↔ NSData
// ══════════════════════════════════════════════

private fun ByteArray.toNSData(): NSData {
    if (this.isEmpty()) return NSData()
    return this.usePinned { pinned ->
        NSData.create(bytes = pinned.addressOf(0), length = this.size.toULong())
    }
}

private fun NSData.toByteArray(): ByteArray {
    val len = length.toInt()
    if (len == 0) return ByteArray(0)
    return ByteArray(len).apply {
        usePinned { pinned ->
            memcpy(pinned.addressOf(0), this@toByteArray.bytes, this@toByteArray.length)
        }
    }
}
