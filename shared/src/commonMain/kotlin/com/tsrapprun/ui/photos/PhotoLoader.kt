/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  PhotoLoader.kt — decoder de bytes → ImageBitmap (expect)    ║
 * ║                                                              ║
 * ║  iOS/desktop: usa Skia (org.jetbrains.skia.Image).           ║
 * ║  Android: BitmapFactory.decodeByteArray.                     ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.ui.photos

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import com.tsrapprun.camera.PhotoData

/** Decodifica JPEG/PNG bytes para ImageBitmap. Null em caso de erro. */
expect fun decodeImageBitmap(bytes: ByteArray): ImageBitmap?

/**
 * Carrega a foto via loader e cacheia o ImageBitmap por `photo.id`.
 * Uso em qualquer composable que queira mostrar a foto:
 *
 * ```
 * val bmp = rememberPhotoBitmap(photo, onLoadThumbnail)
 * if (bmp != null) Image(bmp, contentDescription = null)
 * else FallbackPlaceholder()
 * ```
 */
@Composable
fun rememberPhotoBitmap(
    photo: PhotoData?,
    loader: suspend (PhotoData) -> ByteArray?
): ImageBitmap? {
    var bitmap by remember(photo?.id) { mutableStateOf<ImageBitmap?>(null) }
    LaunchedEffect(photo?.id) {
        val p = photo ?: return@LaunchedEffect
        val bytes = loader(p) ?: return@LaunchedEffect
        bitmap = decodeImageBitmap(bytes)
    }
    return bitmap
}

/** Variante por bytes (útil pra fotos ainda não persistidas, ex: avatar do cadastro). */
@Composable
fun rememberPhotoBitmapFromBytes(bytes: ByteArray?): ImageBitmap? {
    var bitmap by remember(bytes?.contentHashCode()) { mutableStateOf<ImageBitmap?>(null) }
    LaunchedEffect(bytes?.contentHashCode()) {
        val b = bytes ?: return@LaunchedEffect
        bitmap = decodeImageBitmap(b)
    }
    return bitmap
}
