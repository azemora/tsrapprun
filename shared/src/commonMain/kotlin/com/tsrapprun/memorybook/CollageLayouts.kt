/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  CollageLayouts.kt - Layouts de colagem de fotos            ║
 * ║                                                             ║
 * ║  Organiza N fotos em arranjos visuais estilo scrapbook.     ║
 * ║  Cada layout usa rotações e offsets determinísticos.         ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.memorybook

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tsrapprun.camera.PhotoData
import com.tsrapprun.gallery.decodeThumbnail

// ═══════════════════════════════════════════════════
// DISPATCHER PRINCIPAL
// ═══════════════════════════════════════════════════

/**
 * Renderiza uma colagem de fotos adaptada à quantidade.
 */
@Composable
fun PhotoCollage(
    photos: List<PhotoData>,
    onLoadThumbnail: suspend (PhotoData) -> ByteArray?,
    modifier: Modifier = Modifier,
    seed: Int = 0
) {
    when {
        photos.isEmpty() -> EmptyCollagePlaceholder(modifier)
        photos.size == 1 -> SinglePhotoLayout(photos[0], onLoadThumbnail, modifier, seed)
        photos.size == 2 -> DualPhotoLayout(photos, onLoadThumbnail, modifier, seed)
        photos.size == 3 -> TriplePhotoLayout(photos, onLoadThumbnail, modifier, seed)
        else -> ScatteredPhotoLayout(photos.take(5), photos.size, onLoadThumbnail, modifier, seed)
    }
}

// ═══════════════════════════════════════════════════
// FOTO INDIVIDUAL COM MOLDURA
// ═══════════════════════════════════════════════════

@Composable
private fun FramedPhoto(
    photo: PhotoData,
    onLoadThumbnail: suspend (PhotoData) -> ByteArray?,
    modifier: Modifier = Modifier,
    rotationDeg: Float = 0f,
    showTape: Boolean = true,
    tapeSeed: Int = 0
) {
    var thumbnail by remember(photo.id) { mutableStateOf<ImageBitmap?>(null) }
    var isLoading by remember(photo.id) { mutableStateOf(true) }

    LaunchedEffect(photo.id) {
        isLoading = true
        val bytes = onLoadThumbnail(photo)
        thumbnail = bytes?.let { decodeThumbnail(it) }
        isLoading = false
    }

    Box(modifier = modifier) {
        // Foto com moldura branca
        Box(
            modifier = Modifier
                .fillMaxSize()
                .photoFrame(rotationDeg)
                .padding(6.dp), // borda branca (papel de foto)
            contentAlignment = Alignment.Center
        ) {
            when {
                isLoading -> CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp,
                    color = InkBrown.copy(alpha = 0.3f)
                )
                thumbnail != null -> Image(
                    bitmap = thumbnail!!,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(1.dp)),
                    contentScale = ContentScale.Crop
                )
                else -> Box(
                    modifier = Modifier.fillMaxSize().background(Color(0xFFE0D8C8)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("?", fontSize = 20.sp, color = InkBrown.copy(alpha = 0.3f))
                }
            }
        }

        // Fita adesiva decorativa
        if (showTape) {
            TapeStrip(
                modifier = Modifier.align(Alignment.TopCenter),
                rotationDeg = deterministicRotation(tapeSeed, 15f),
                color = if (tapeSeed % 2 == 0) TapeYellow else WashiPink
            )
        }
    }
}

// ═══════════════════════════════════════════════════
// LAYOUT: 1 FOTO
// ═══════════════════════════════════════════════════

@Composable
private fun SinglePhotoLayout(
    photo: PhotoData,
    onLoadThumbnail: suspend (PhotoData) -> ByteArray?,
    modifier: Modifier = Modifier,
    seed: Int = 0
) {
    Box(
        modifier = modifier.fillMaxWidth().padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        FramedPhoto(
            photo = photo,
            onLoadThumbnail = onLoadThumbnail,
            modifier = Modifier.fillMaxWidth(0.75f).aspectRatio(0.8f),
            rotationDeg = deterministicRotation(seed, 3f),
            tapeSeed = seed
        )
    }
}

// ═══════════════════════════════════════════════════
// LAYOUT: 2 FOTOS (sobrepostas estilo polaroid)
// ═══════════════════════════════════════════════════

@Composable
private fun DualPhotoLayout(
    photos: List<PhotoData>,
    onLoadThumbnail: suspend (PhotoData) -> ByteArray?,
    modifier: Modifier = Modifier,
    seed: Int = 0
) {
    Box(
        modifier = modifier.fillMaxWidth().padding(20.dp).height(280.dp),
        contentAlignment = Alignment.Center
    ) {
        // Foto de trás (esquerda, rotação negativa)
        FramedPhoto(
            photo = photos[0],
            onLoadThumbnail = onLoadThumbnail,
            modifier = Modifier
                .size(170.dp)
                .align(Alignment.CenterStart)
                .offset(y = 10.dp),
            rotationDeg = deterministicRotation(seed, 6f) - 4f,
            tapeSeed = seed
        )
        // Foto da frente (direita, rotação positiva)
        FramedPhoto(
            photo = photos[1],
            onLoadThumbnail = onLoadThumbnail,
            modifier = Modifier
                .size(170.dp)
                .align(Alignment.CenterEnd)
                .offset(y = (-10).dp),
            rotationDeg = deterministicRotation(seed + 1, 6f) + 3f,
            tapeSeed = seed + 1
        )
    }
}

// ═══════════════════════════════════════════════════
// LAYOUT: 3 FOTOS (triângulo)
// ═══════════════════════════════════════════════════

@Composable
private fun TriplePhotoLayout(
    photos: List<PhotoData>,
    onLoadThumbnail: suspend (PhotoData) -> ByteArray?,
    modifier: Modifier = Modifier,
    seed: Int = 0
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Duas fotos em cima
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            FramedPhoto(
                photo = photos[0],
                onLoadThumbnail = onLoadThumbnail,
                modifier = Modifier.size(140.dp),
                rotationDeg = deterministicRotation(seed, 5f) - 3f,
                tapeSeed = seed
            )
            FramedPhoto(
                photo = photos[1],
                onLoadThumbnail = onLoadThumbnail,
                modifier = Modifier.size(140.dp),
                rotationDeg = deterministicRotation(seed + 1, 5f) + 2f,
                tapeSeed = seed + 1
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        // Uma foto centralizada embaixo
        FramedPhoto(
            photo = photos[2],
            onLoadThumbnail = onLoadThumbnail,
            modifier = Modifier.size(150.dp),
            rotationDeg = deterministicRotation(seed + 2, 4f),
            tapeSeed = seed + 2
        )
    }
}

// ═══════════════════════════════════════════════════
// LAYOUT: 4+ FOTOS (espalhadas/fan)
// ═══════════════════════════════════════════════════

@Composable
private fun ScatteredPhotoLayout(
    photos: List<PhotoData>,
    totalCount: Int,
    onLoadThumbnail: suspend (PhotoData) -> ByteArray?,
    modifier: Modifier = Modifier,
    seed: Int = 0
) {
    Box(
        modifier = modifier.fillMaxWidth().padding(12.dp).height(340.dp),
        contentAlignment = Alignment.Center
    ) {
        // Fotos de fundo (menores, mais rotação)
        photos.drop(1).take(4).forEachIndexed { index, photo ->
            val i = index + 1
            val xOff = deterministicOffset(seed + i * 7, 60f)
            val yOff = deterministicOffset(seed + i * 13, 50f)
            FramedPhoto(
                photo = photo,
                onLoadThumbnail = onLoadThumbnail,
                modifier = Modifier
                    .size(120.dp)
                    .offset(x = xOff.dp, y = yOff.dp),
                rotationDeg = deterministicRotation(seed + i, 12f),
                tapeSeed = seed + i,
                showTape = index < 2 // tape só nas 2 primeiras
            )
        }

        // Foto principal (maior, no centro, menos rotação)
        FramedPhoto(
            photo = photos[0],
            onLoadThumbnail = onLoadThumbnail,
            modifier = Modifier.size(170.dp),
            rotationDeg = deterministicRotation(seed, 3f),
            tapeSeed = seed
        )

        // Badge "+N mais"
        if (totalCount > 5) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
                    .background(InkBrown.copy(alpha = 0.7f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "+${totalCount - 5} mais",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════
// PLACEHOLDER VAZIO
// ═══════════════════════════════════════════════════

@Composable
private fun EmptyCollagePlaceholder(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxWidth().height(200.dp).padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Sem fotos neste evento",
            style = captionStyle(),
            fontSize = 16.sp
        )
    }
}
