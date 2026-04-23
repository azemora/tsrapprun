/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  LocalPhotoStorage.kt - Interface de Armazenamento Local    ║
 * ║                                                             ║
 * ║  Gerencia fotos e eventos localmente com criptografia.      ║
 * ║  Fotos: AES-256-GCM via Android Keystore / iOS Keychain.   ║
 * ║  Índices: JSON criptografado para metadados rápidos.        ║
 * ║                                                             ║
 * ║  SEGURANÇA:                                                 ║
 * ║  - Fotos SEMPRE criptografadas em repouso (at rest)         ║
 * ║  - Armazenadas no diretório privado do app (sandbox)        ║
 * ║  - Inacessíveis por outros apps (sem root/jailbreak)        ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.storage

import com.tsrapprun.camera.EventData
import com.tsrapprun.camera.PhotoData
import com.tsrapprun.moments.MomentEntry

/**
 * Interface multiplataforma para armazenamento local criptografado.
 */
expect class LocalPhotoStorage {

    // ── Operações de Fotos ──

    /**
     * Salva uma foto criptografada no filesystem local.
     *
     * @param imageBytes Bytes da imagem (câmera ou importação).
     * @param eventId    ID do evento (null para foto avulsa).
     * @param capturedAt Timestamp da captura. Se null, usa System.currentTimeMillis().
     *                   Útil para preservar data EXIF ao importar fotos.
     * @return PhotoData com metadados e caminho do arquivo salvo.
     */
    suspend fun savePhoto(
        imageBytes: ByteArray,
        eventId: String? = null,
        capturedAt: Long? = null
    ): PhotoData

    /** Recupera os bytes decifrados de uma foto. */
    suspend fun loadPhoto(photoData: PhotoData): ByteArray?

    /** Deleta uma foto do filesystem local. */
    suspend fun deletePhoto(photoData: PhotoData): Boolean

    /** Lista todas as fotos salvas localmente. */
    suspend fun listPhotos(): List<PhotoData>

    /** Lista fotos de um evento específico. */
    suspend fun listPhotosByEvent(eventId: String): List<PhotoData>

    /** Calcula o espaço total usado por fotos locais em bytes. */
    suspend fun getTotalStorageUsed(): Long

    // ── Operações de Eventos ──

    /** Salva um evento no índice criptografado. */
    suspend fun saveEvent(event: EventData)

    /** Lista todos os eventos. */
    suspend fun listEvents(): List<EventData>

    /** Atualiza um evento existente (nome, contagem, thumbnail). */
    suspend fun updateEvent(event: EventData)

    /** Deleta um evento e todas as suas fotos. */
    suspend fun deleteEvent(eventId: String): Boolean

    /**
     * Atribui eventId em batch a uma lista de fotos.
     * Usado após o usuário nomear o evento (fotos já salvas com eventId=null).
     */
    suspend fun updatePhotosEventId(photoIds: List<String>, eventId: String)

    // ── Operações de Momentos (Registros) ──

    /** Salva um registro de momento. */
    suspend fun saveMoment(moment: MomentEntry)

    /** Lista todos os momentos registrados. */
    suspend fun listMoments(): List<MomentEntry>

    /** Atualiza um momento existente. */
    suspend fun updateMoment(moment: MomentEntry)

    /** Deleta um momento pelo ID. */
    suspend fun deleteMoment(momentId: String): Boolean
}
