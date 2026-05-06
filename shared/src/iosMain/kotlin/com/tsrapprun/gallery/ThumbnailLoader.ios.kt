package com.tsrapprun.gallery

import androidx.compose.ui.graphics.ImageBitmap
import com.tsrapprun.ui.photos.decodeImageBitmap

actual fun decodeThumbnail(bytes: ByteArray, maxDimPx: Int): ImageBitmap? =
    decodeImageBitmap(bytes)

actual fun decodeFullImage(bytes: ByteArray): ImageBitmap? =
    decodeImageBitmap(bytes)
