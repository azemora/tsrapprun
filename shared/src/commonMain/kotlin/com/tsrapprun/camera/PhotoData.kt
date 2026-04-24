/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  PhotoData.kt - Modelo de Dados de Foto                     ║
 * ║                                                             ║
 * ║  Representa os metadados de uma foto capturada.             ║
 * ║  O arquivo binário da foto fica no filesystem local         ║
 * ║  criptografado — este modelo armazena apenas referências.   ║
 * ║                                                             ║
 * ║  SEGURANÇA:                                                 ║
 * ║  - Nenhum dado binário da foto nesta classe                 ║
 * ║  - filePath aponta para arquivo criptografado (AES-256)     ║
 * ║  - Imutável (data class) para evitar alterações em memória  ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.camera

import kotlinx.serialization.Serializable

/**
 * Metadados de uma foto capturada.
 *
 * @property id       Identificador único da foto (UUID).
 * @property fileName Nome do arquivo no filesystem local.
 * @property filePath Caminho absoluto do arquivo criptografado no dispositivo.
 * @property eventId  ID do evento associado (null se foto avulsa).
 * @property capturedAt Timestamp de quando a foto foi tirada (epoch millis).
 * @property sizeBytes Tamanho do arquivo em bytes.
 * @property isUploadedToCloud Se a foto já foi enviada para a nuvem.
 *                             Local-first: padrão é false.
 */
@Serializable
data class PhotoData(
    val id: String,
    val fileName: String,
    val filePath: String,
    val eventId: String? = null,
    val capturedAt: Long,
    val sizeBytes: Long = 0,
    val isUploadedToCloud: Boolean = false
)
