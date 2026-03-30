package com.tsrapprun.gallery

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

/**
 * Decodifica thumbnail com inSampleSize para economizar memória.
 * inSampleSize=4 → imagem 1/4 do tamanho original.
 */
actual fun decodeThumbnail(bytes: ByteArray, maxDimPx: Int): ImageBitmap? {
    return try {
        // Primeiro lê apenas as dimensões (sem decodificar pixels)
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)

        // Calcula inSampleSize ideal
        val sampleSize = calculateInSampleSize(options.outWidth, options.outHeight, maxDimPx)

        // Decodifica com tamanho reduzido
        val decodeOptions = BitmapFactory.Options().apply { inSampleSize = sampleSize }
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, decodeOptions)?.asImageBitmap()
    } catch (e: Exception) {
        null
    }
}

actual fun decodeFullImage(bytes: ByteArray): ImageBitmap? {
    return try {
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
    } catch (e: Exception) {
        null
    }
}

/** Calcula o maior inSampleSize que mantém a imagem >= maxDimPx. */
private fun calculateInSampleSize(width: Int, height: Int, maxDimPx: Int): Int {
    var inSampleSize = 1
    val largerDim = maxOf(width, height)
    while (largerDim / inSampleSize > maxDimPx * 2) {
        inSampleSize *= 2
    }
    return inSampleSize
}
