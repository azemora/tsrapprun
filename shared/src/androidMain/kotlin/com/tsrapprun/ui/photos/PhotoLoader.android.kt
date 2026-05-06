/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  PhotoLoader.android.kt — decoder Android.                   ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.ui.photos

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

actual fun decodeImageBitmap(bytes: ByteArray): ImageBitmap? = try {
    BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
} catch (t: Throwable) {
    null
}
