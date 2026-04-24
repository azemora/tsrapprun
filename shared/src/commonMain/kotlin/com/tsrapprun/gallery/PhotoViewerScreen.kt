/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  PhotoViewerScreen.kt - Visualizador Fullscreen              ║
 * ║                                                             ║
 * ║  Exibe foto em tela cheia com swipe para navegar.           ║
 * ║  Mostra metadados (data, evento) e permite deletar.         ║
 * ║                                                             ║
 * ║  SEGURANÇA: Fotos decifradas em memória, nunca em disco.    ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.gallery

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tsrapprun.camera.PhotoData

/**
 * Visualizador fullscreen com swipe horizontal entre fotos.
 *
 * @param photos Lista de fotos a navegar.
 * @param initialIndex Índice inicial (foto que o usuário tocou no grid).
 * @param onLoadPhoto Carrega bytes decifrados da foto.
 * @param onDelete Deleta foto (com confirmação).
 * @param onBack Voltar ao grid.
 */
@Composable
fun PhotoViewerScreen(
    photos: List<PhotoData>,
    initialIndex: Int,
    onLoadPhoto: suspend (PhotoData) -> ByteArray?,
    onDelete: (PhotoData) -> Unit,
    onBack: () -> Unit
) {
    if (photos.isEmpty()) {
        onBack()
        return
    }

    val pagerState = rememberPagerState(
        initialPage = initialIndex.coerceIn(0, photos.lastIndex),
        pageCount = { photos.size }
    )

    var showDeleteDialog by remember { mutableStateOf(false) }

    Surface(modifier = Modifier.fillMaxSize(), color = Color.Black) {
        Box(modifier = Modifier.fillMaxSize()) {
            // ── Pager horizontal (swipe entre fotos) ──
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                PhotoPage(
                    photo = photos[page],
                    onLoadPhoto = onLoadPhoto
                )
            }

            // ── Header: voltar + contador (abaixo da status bar) ──
            Row(
                modifier = Modifier.fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onBack) {
                    Text("← Voltar", color = Color.White)
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "${pagerState.currentPage + 1} / ${photos.size}",
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.weight(1f))
                TextButton(onClick = { showDeleteDialog = true }) {
                    Text("Deletar", color = Color(0xFFFF6B6B))
                }
            }

            // ── Footer: metadados ──
            val currentPhoto = photos.getOrNull(pagerState.currentPage)
            if (currentPhoto != null) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(Color.Black.copy(alpha = 0.6f))
                        .padding(16.dp)
                ) {
                    Text(
                        text = formatFullDate(currentPhoto.capturedAt),
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = formatFileSize(currentPhoto.sizeBytes),
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }

    // ── Diálogo de confirmação de exclusão ──
    if (showDeleteDialog) {
        val photoToDelete = photos.getOrNull(pagerState.currentPage)
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Deletar foto?") },
            text = { Text("Esta ação não pode ser desfeita. A foto será removida permanentemente do dispositivo.") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        photoToDelete?.let { onDelete(it) }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Deletar")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

/** Página individual do pager: carrega e exibe uma foto. */
@Composable
private fun PhotoPage(
    photo: PhotoData,
    onLoadPhoto: suspend (PhotoData) -> ByteArray?
) {
    var bitmap by remember(photo.id) { mutableStateOf<ImageBitmap?>(null) }
    var isLoading by remember(photo.id) { mutableStateOf(true) }

    LaunchedEffect(photo.id) {
        isLoading = true
        val bytes = onLoadPhoto(photo)
        bitmap = bytes?.let { decodeFullImage(it) }
        isLoading = false
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when {
            isLoading -> CircularProgressIndicator(color = Color.White)
            bitmap != null -> {
                Image(
                    bitmap = bitmap!!,
                    contentDescription = "Foto",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }
            else -> Text("Erro ao carregar", color = Color.White.copy(0.5f))
        }
    }
}

private fun formatFullDate(epochMillis: Long): String {
    val cal = java.util.Calendar.getInstance().apply { timeInMillis = epochMillis }
    val d = cal.get(java.util.Calendar.DAY_OF_MONTH).toString().padStart(2, '0')
    val m = (cal.get(java.util.Calendar.MONTH) + 1).toString().padStart(2, '0')
    val y = cal.get(java.util.Calendar.YEAR)
    val h = cal.get(java.util.Calendar.HOUR_OF_DAY).toString().padStart(2, '0')
    val min = cal.get(java.util.Calendar.MINUTE).toString().padStart(2, '0')
    return "$d/$m/$y às $h:$min"
}

private fun formatFileSize(bytes: Long): String {
    return when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> "${bytes / 1024} KB"
        else -> String.format("%.1f MB", bytes / (1024.0 * 1024.0))
    }
}
