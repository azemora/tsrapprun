/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  ThumbnailLoader.kt - Decodificação de Thumbnails           ║
 * ║                                                             ║
 * ║  Decodifica bytes de foto em ImageBitmap reduzido           ║
 * ║  para exibição em grid. Platform-specific porque usa        ║
 * ║  BitmapFactory (Android) / UIImage (iOS).                   ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.gallery

import androidx.compose.ui.graphics.ImageBitmap

/**
 * Decodifica bytes de imagem em thumbnail reduzido.
 *
 * @param bytes Bytes da imagem (JPEG decifrada).
 * @param maxDimPx Dimensão máxima do thumbnail em pixels.
 * @return ImageBitmap reduzido ou null se falhar.
 */
expect fun decodeThumbnail(bytes: ByteArray, maxDimPx: Int = 300): ImageBitmap?

/**
 * Decodifica bytes de imagem em tamanho completo.
 *
 * @param bytes Bytes da imagem (JPEG decifrada).
 * @return ImageBitmap completo ou null se falhar.
 */
expect fun decodeFullImage(bytes: ByteArray): ImageBitmap?
