/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  PhotoLoader.ios.kt — decoder iOS via Skia.                  ║
 * ║                                                              ║
 * ║  Compose Multiplatform usa Skia internamente; Image.makeFrom ║
 * ║  Encoded aceita JPEG/PNG/HEIC e devolve um SkImage que       ║
 * ║  convertemos para ImageBitmap.                               ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.ui.photos

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.jetbrains.skia.Image

actual fun decodeImageBitmap(bytes: ByteArray): ImageBitmap? = try {
    Image.makeFromEncoded(bytes).toComposeImageBitmap()
} catch (t: Throwable) {
    null
}
