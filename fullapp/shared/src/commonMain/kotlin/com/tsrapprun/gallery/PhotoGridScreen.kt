/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  PhotoGridScreen.kt - Grid de Fotos                         ║
 * ║                                                             ║
 * ║  Exibe thumbnails em grid 3 colunas.                        ║
 * ║  Fotos decifradas em memória (never on disk unencrypted).   ║
 * ║  LazyVerticalGrid só renderiza itens visíveis.              ║
 * ║                                                             ║
 * ║  Quando exibindo um evento: menu ⋮ com opções de            ║
 * ║  importar fotos, renomear e excluir.                        ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.gallery

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
 * Opções de menu para evento (null = sem menu, ex: "Todas as Fotos").
 */
data class EventMenuActions(
    val eventName: String,
    val onImportPhotos: () -> Unit,
    val onRenameEvent: (newName: String) -> Unit,
    val onDeletePhotos: () -> Unit
)

/**
 * Grid de fotos com thumbnails decifradas.
 */
@Composable
fun PhotoGridScreen(
    title: String,
    photos: List<PhotoData>,
    onLoadThumbnail: suspend (PhotoData) -> ByteArray?,
    onPhotoClick: (index: Int) -> Unit,
    onBack: () -> Unit,
    eventMenuActions: EventMenuActions? = null
) {
    // ── Diálogos de menu ──
    var showRenameDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(modifier = Modifier.fillMaxSize()) {
            // ── Header (abaixo da status bar) ──
            Row(
                modifier = Modifier.fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onBack) {
                    Text("\u2190 Voltar", style = MaterialTheme.typography.titleMedium)
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(title, style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.weight(1f))

                if (eventMenuActions != null) {
                    // ── Menu ⋮ para eventos ──
                    EventDropdownMenu(
                        onImport = eventMenuActions.onImportPhotos,
                        onRename = { showRenameDialog = true },
                        onDelete = { showDeleteConfirmDialog = true }
                    )
                } else {
                    Text("${photos.size}", style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(16.dp))
                }
            }

            if (photos.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Nenhuma foto encontrada",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.5f))
                        if (eventMenuActions != null) {
                            Spacer(modifier = Modifier.height(16.dp))
                            OutlinedButton(onClick = eventMenuActions.onImportPhotos) {
                                Text("Importar Fotos")
                            }
                        }
                    }
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

    // ── Diálogo: Renomear evento ──
    if (showRenameDialog && eventMenuActions != null) {
        RenameEventDialog(
            currentName = eventMenuActions.eventName,
            onConfirm = { newName ->
                eventMenuActions.onRenameEvent(newName)
                showRenameDialog = false
            },
            onDismiss = { showRenameDialog = false }
        )
    }

    // ── Diálogo: Confirmar exclusão de evento ──
    if (showDeleteConfirmDialog && eventMenuActions != null) {
        DeleteEventDialog(
            eventName = eventMenuActions.eventName,
            photoCount = photos.size,
            onConfirm = {
                eventMenuActions.onDeletePhotos()
                showDeleteConfirmDialog = false
            },
            onDismiss = { showDeleteConfirmDialog = false }
        )
    }
}

// ═══════════════════════════════════════════════════
// MENU DROPDOWN (⋮)
// ═══════════════════════════════════════════════════

@Composable
private fun EventDropdownMenu(
    onImport: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton(onClick = { expanded = true }) {
            Text(
                text = "\u22EE",  // ⋮
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Importar Fotos") },
                onClick = {
                    expanded = false
                    onImport()
                }
            )
            DropdownMenuItem(
                text = { Text("Renomear Evento") },
                onClick = {
                    expanded = false
                    onRename()
                }
            )
            DropdownMenuItem(
                text = {
                    Text(
                        "Excluir Evento",
                        color = MaterialTheme.colorScheme.error
                    )
                },
                onClick = {
                    expanded = false
                    onDelete()
                }
            )
        }
    }
}

// ═══════════════════════════════════════════════════
// DIÁLOGOS
// ═══════════════════════════════════════════════════

@Composable
private fun RenameEventDialog(
    currentName: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(currentName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Renomear Evento") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nome do evento") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = { if (name.isNotBlank()) onConfirm(name.trim()) },
                enabled = name.isNotBlank() && name.trim() != currentName
            ) {
                Text("Salvar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun DeleteEventDialog(
    eventName: String,
    photoCount: Int,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Excluir Evento") },
        text = {
            Text(
                "Excluir \"$eventName\" e todas as $photoCount fotos?\n\n" +
                        "As fotos salvas no album da galeria do Android n\u00e3o ser\u00e3o afetadas."
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Excluir")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

// ═══════════════════════════════════════════════════
// CÉLULA DE THUMBNAIL
// ═══════════════════════════════════════════════════

@Composable
private fun PhotoThumbnailCell(
    photo: PhotoData,
    onLoadThumbnail: suspend (PhotoData) -> ByteArray?,
    onClick: () -> Unit
) {
    var thumbnail by remember(photo.id) { mutableStateOf<ImageBitmap?>(null) }
    var isLoading by remember(photo.id) { mutableStateOf(true) }

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
                // Decoder não disponível (ex: iOS sem decode JPEG implementado).
                // Em vez de "Erro", mostra um pastel determinístico por foto pra
                // diferenciá-las visualmente — útil em modo simulação.
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(pastelColorFor(photo.id))
                ) {
                    Box(
                        modifier = Modifier.align(Alignment.BottomStart)
                            .background(Color.Black.copy(alpha = 0.4f))
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = formatShortDate(photo.capturedAt),
                            fontSize = 9.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

/** Paleta pastel determinística — derivada do hash do id da foto. */
private val PastelPalette: List<Color> = listOf(
    Color(0xFFE8C4B8), // pêssego
    Color(0xFFD4E0C9), // sage claro
    Color(0xFFF1DCBE), // areia clara
    Color(0xFFC9DBE8), // azul céu
    Color(0xFFE6CFE8), // lilás
    Color(0xFFE0D9C8), // creme
    Color(0xFFC8E0D7), // verde menta
    Color(0xFFF2D6D2)  // rosa pó
)

private fun pastelColorFor(id: String): Color {
    if (id.isEmpty()) return PastelPalette[0]
    var h = 0
    for (c in id) h = (h * 31 + c.code) and 0x7FFFFFFF
    return PastelPalette[h % PastelPalette.size]
}

private fun formatShortDate(epochMillis: Long): String {
    val c = com.tsrapprun.platform.dateComponentsOf(epochMillis)
    val d = c.day.toString().padStart(2, '0')
    val m = (c.monthIndex + 1).toString().padStart(2, '0')
    return "$d/$m"
}
