/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  PhotoGridScreen.kt - Grid de Fotos                         ║
 * ║                                                             ║
 * ║  Exibe thumbnails em grid 3 colunas.                        ║
 * ║  Fotos decifradas em memória (never on disk unencrypted).   ║
 * ║  LazyVerticalGrid só renderiza itens visíveis.              ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.gallery

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tsrapprun.camera.PhotoData

/**
 * Grid de fotos com thumbnails decifradas.
 *
 * @param title Título da tela (nome do evento ou "Todas as Fotos").
 * @param photos Lista de fotos a exibir.
 * @param onLoadThumbnail Carrega e decifra bytes da foto para thumbnail.
 * @param onPhotoClick Toque na foto → abre viewer fullscreen.
 * @param onBack Voltar para galeria raiz.
 */
@Composable
fun PhotoGridScreen(
    title: String,
    photos: List<PhotoData>,
    onLoadThumbnail: suspend (PhotoData) -> ByteArray?,
    onPhotoClick: (index: Int) -> Unit,
    onBack: () -> Unit
) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(modifier = Modifier.fillMaxSize()) {
            // ── Header (abaixo da status bar) ──
            Row(
                modifier = Modifier.fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onBack) {
                    Text("← Voltar", style = MaterialTheme.typography.titleMedium)
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(title, style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.weight(1f))
                Text("${photos.size}", style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.width(16.dp))
            }

            if (photos.isEmpty()) {
                // ── Sem fotos ──
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Nenhuma foto encontrada",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.5f))
                }
            } else {
                // ── Grid 3 colunas ──
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.fillMaxSize().padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    itemsIndexed(photos) { index, photo ->
                        PhotoThumbnailCell(
                            photo = photo,
                            onLoadThumbnail = onLoadThumbnail,
                            onClick = { onPhotoClick(index) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Célula individual do grid: carrega thumbnail assincronamente.
 * Usa remember para cachear o bitmap decodificado.
 */
@Composable
private fun PhotoThumbnailCell(
    photo: PhotoData,
    onLoadThumbnail: suspend (PhotoData) -> ByteArray?,
    onClick: () -> Unit
) {
    var thumbnail by remember(photo.id) { mutableStateOf<ImageBitmap?>(null) }
    var isLoading by remember(photo.id) { mutableStateOf(true) }

    // Carrega thumbnail assincronamente (decifra + decodifica)
    LaunchedEffect(photo.id) {
        isLoading = true
        val bytes = onLoadThumbnail(photo)
        thumbnail = bytes?.let { decodeThumbnail(it) }
        isLoading = false
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(4.dp))
            .clickable(onClick = onClick)
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.padding(16.dp),
                    strokeWidth = 2.dp
                )
            }
            thumbnail != null -> {
                Image(
                    bitmap = thumbnail!!,
                    contentDescription = "Foto",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Data overlay no canto inferior
                Box(
                    modifier = Modifier.align(Alignment.BottomStart)
                        .background(Color.Black.copy(alpha = 0.5f))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = formatShortDate(photo.capturedAt),
                        fontSize = 9.sp,
                        color = Color.White
                    )
                }
            }
            else -> {
                Text("Erro", fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.5f))
            }
        }
    }
}

private fun formatShortDate(epochMillis: Long): String {
    val cal = java.util.Calendar.getInstance().apply { timeInMillis = epochMillis }
    val d = cal.get(java.util.Calendar.DAY_OF_MONTH).toString().padStart(2, '0')
    val m = (cal.get(java.util.Calendar.MONTH) + 1).toString().padStart(2, '0')
    return "$d/$m"
}
