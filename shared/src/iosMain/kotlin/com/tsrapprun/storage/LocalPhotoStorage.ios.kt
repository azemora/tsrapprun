package com.tsrapprun.storage

import com.tsrapprun.camera.EventData
import com.tsrapprun.camera.PhotoData
import com.tsrapprun.moments.MomentEntry

actual class LocalPhotoStorage {
    actual suspend fun savePhoto(imageBytes: ByteArray, eventId: String?, capturedAt: Long?): PhotoData {
        throw NotImplementedError("iOS storage not yet implemented")
    }
    actual suspend fun loadPhoto(photoData: PhotoData): ByteArray? = null
    actual suspend fun deletePhoto(photoData: PhotoData): Boolean = false
    actual suspend fun listPhotos(): List<PhotoData> = emptyList()
    actual suspend fun listPhotosByEvent(eventId: String): List<PhotoData> = emptyList()
    actual suspend fun getTotalStorageUsed(): Long = 0
    actual suspend fun saveEvent(event: EventData) {}
    actual suspend fun listEvents(): List<EventData> = emptyList()
    actual suspend fun updateEvent(event: EventData) {}
    actual suspend fun deleteEvent(eventId: String): Boolean = false
    actual suspend fun updatePhotosEventId(photoIds: List<String>, eventId: String) {}
    actual suspend fun saveMoment(moment: MomentEntry) {}
    actual suspend fun listMoments(): List<MomentEntry> = emptyList()
    actual suspend fun updateMoment(moment: MomentEntry) {}
    actual suspend fun deleteMoment(momentId: String): Boolean = false
}
