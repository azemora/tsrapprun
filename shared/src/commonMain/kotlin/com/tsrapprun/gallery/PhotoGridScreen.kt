/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  PhotoGridScreen.kt — tradução de PhotoGridA (events.jsx)    ║
 * ║                                                              ║
 * ║  Header editorial "passeio / no parque" italic + grid 3-col  ║
 * ║  com primeira célula maior (CAPA, span 2 linhas) em cinza-   ║
 * ║  escuro. Nota Caveat embaixo + footer com 2 ações.           ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.gallery

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import com.tsrapprun.camera.PhotoData
import com.tsrapprun.ui.photos.rememberPhotoBitmap
import com.tsrapprun.platform.dateComponentsOf
import com.tsrapprun.ui.chrome.Butter
import com.tsrapprun.ui.chrome.OliveDeep
import com.tsrapprun.ui.chrome.Peach
import com.tsrapprun.ui.chrome.PhotoGray
import com.tsrapprun.ui.chrome.ScreenHeader
import com.tsrapprun.ui.chrome.Stamp
import com.tsrapprun.ui.chrome.Tag
import com.tsrapprun.ui.chrome.italicSerifText
import com.tsrapprun.ui.theme.CozyAmberDeep
import com.tsrapprun.ui.theme.CozyCream
import com.tsrapprun.ui.theme.CozyCreamDeep
import com.tsrapprun.ui.theme.CozyOlive

/** Opções de menu para evento (null = sem menu, ex: "Todas as Fotos"). */
data class EventMenuActions(
    val eventName: String,
    val onImportPhotos: () -> Unit,
    val onRenameEvent: (newName: String) -> Unit,
    val onDeletePhotos: () -> Unit
)

@Composable
fun PhotoGridScreen(
    title: String,
    photos: List<PhotoData>,
    onLoadThumbnail: suspend (PhotoData) -> ByteArray?,
    onPhotoClick: (index: Int) -> Unit,
    onBack: () -> Unit,
    eventMenuActions: EventMenuActions? = null,
    note: String? = null
) {
    var showRenameDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    val (firstWord, secondWord) = remember(title) { splitTitle(title) }
    val dateLabel = remember(photos) {
        photos.firstOrNull()?.let { formatShortDate(it.capturedAt) } ?: ""
    }
    val countLabel = "${photos.size} fotos"

    Box(modifier = Modifier.fillMaxSize().background(CozyCream)) {
        // BACKDROP: foto cobrindo a tela com cor sólida sobre ela.
        // Quando o carregamento real de foto for ligado, troque PhotoGray
        // por uma Image. Por enquanto o placeholder cinza serve.
        if (photos.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(PhotoGray.copy(alpha = 0.55f))
            )
            // Overlay cream com alpha pra dar legibilidade ao conteúdo
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(CozyCream.copy(alpha = 0.82f))
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            ScreenHeader(
                chapter = "evento · $dateLabel — $countLabel",
                title = italicSerifText(
                    prefix = "$firstWord\n",
                    italic = secondWord,
                    italicColor = CozyAmberDeep,
                    defaultColor = OliveDeep
                ),
                onBack = onBack,
                rightContent = {
                    Row {
                        Box(modifier = Modifier.size(34.dp).clickable { /* share */ }, contentAlignment = Alignment.Center) {
                            Text("↑", fontSize = 18.sp, color = OliveDeep, fontWeight = FontWeight.Light)
                        }
                        if (eventMenuActions != null) {
                            Box(modifier = Modifier.size(34.dp).clickable { showRenameDialog = true }, contentAlignment = Alignment.Center) {
                                Text("⋯", fontSize = 18.sp, color = OliveDeep, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            )

            when {
                photos.isEmpty() -> EmptyState()

                // ── Single-photo: layout celebratório com polaroid hero ──
                photos.size == 1 -> SinglePhotoLayout(
                    photo = photos[0],
                    note = note,
                    loader = onLoadThumbnail,
                    onPhotoClick = { onPhotoClick(0) }
                )

                // ── Multi-photo: mosaico ──
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 100.dp)
                    ) {
                        item(key = "grid") {
                            MosaicGrid(
                                photos = photos,
                                loader = onLoadThumbnail,
                                onPhotoClick = onPhotoClick
                            )
                        }
                        item(key = "note") {
                            Spacer(Modifier.height(16.dp))
                            EventNoteCard(note)
                        }
                    }
                }
            }
        }

        // Footer ações
        if (photos.isNotEmpty()) {
            FooterActions(
                onAddPhotos = { eventMenuActions?.onImportPhotos?.invoke() },
                onAddToBook = { /* TODO */ },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .windowInsetsPadding(WindowInsets.safeDrawing)
            )
        }
    }

    if (showRenameDialog && eventMenuActions != null) {
        RenameEventDialog(
            currentName = eventMenuActions.eventName,
            onConfirm = {
                eventMenuActions.onRenameEvent(it)
                showRenameDialog = false
            },
            onDismiss = { showRenameDialog = false }
        )
    }

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

// ════════════════════════════════════════════════════════════
// SINGLE-PHOTO LAYOUT — hero com polaroid sobre o backdrop
// ════════════════════════════════════════════════════════════

@Composable
private fun SinglePhotoLayout(
    photo: PhotoData,
    note: String?,
    loader: suspend (PhotoData) -> ByteArray?,
    onPhotoClick: () -> Unit
) {
    val bitmap = rememberPhotoBitmap(photo, loader)
    val dateText = remember(photo) { formatLongDate(photo.capturedAt) }
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(8.dp))

        // Hero: polaroid grande com leve rotação, sombra rica.
        // Box envolve pra permitir Stamp circular no canto.
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .width(280.dp)
                    .graphicsLayer { rotationZ = -2f }
                    .clickable(onClick = onPhotoClick),
                shape = RoundedCornerShape(6.dp),
                color = CozyCream,
                shadowElevation = 18.dp
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(3.dp))
                            .background(PhotoGray),
                        contentAlignment = Alignment.Center
                    ) {
                        if (bitmap != null) {
                            Image(
                                bitmap = bitmap,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    Text(
                        dateText,
                        fontFamily = FontFamily.Cursive,
                        fontSize = 22.sp,
                        color = OliveDeep,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(Modifier.height(6.dp))
                }
            }
            // Stamp circular sobreposta no canto superior direito do polaroid
            Stamp(
                label = "01",
                sub = "registro",
                color = Butter,
                rotation = 8f,
                size = 60.dp,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-8).dp, y = (-4).dp)
            )
        }

        Spacer(Modifier.height(28.dp))

        // Nota abaixo (só aparece se houver nota escrita)
        EventNoteCard(note)
    }
}

private fun formatLongDate(epochMillis: Long): String {
    val c = dateComponentsOf(epochMillis)
    val long = listOf(
        "janeiro", "fevereiro", "março", "abril", "maio", "junho",
        "julho", "agosto", "setembro", "outubro", "novembro", "dezembro"
    )
    return "${c.day} de ${long[c.monthIndex]} de ${c.year}"
}

// ════════════════════════════════════════════════════════════
// MOSAIC GRID — primeira célula é capa (col 1, span 2 linhas)
// ════════════════════════════════════════════════════════════

@Composable
private fun MosaicGrid(
    photos: List<PhotoData>,
    loader: suspend (PhotoData) -> ByteArray?,
    onPhotoClick: (Int) -> Unit
) {
    if (photos.isEmpty()) return

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            CoverCell(
                modifier = Modifier.fillMaxWidth().aspectRatio(1f / 2.06f),
                photo = photos[0],
                loader = loader,
                onClick = { onPhotoClick(0) }
            )
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            if (photos.size > 1) PhotoCell(Modifier.fillMaxWidth().aspectRatio(1f), photos[1], loader, 1, onPhotoClick)
            if (photos.size > 2) PhotoCell(Modifier.fillMaxWidth().aspectRatio(1f), photos[2], loader, 2, onPhotoClick)
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            if (photos.size > 3) PhotoCell(Modifier.fillMaxWidth().aspectRatio(1f), photos[3], loader, 3, onPhotoClick)
            if (photos.size > 4) PhotoCell(Modifier.fillMaxWidth().aspectRatio(1f), photos[4], loader, 4, onPhotoClick)
        }
    }

    val rest = photos.drop(5)
    if (rest.isNotEmpty()) {
        Spacer(Modifier.height(6.dp))
        rest.chunked(3).forEachIndexed { rowIdx, row ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = if (rowIdx == 0) 0.dp else 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                row.forEachIndexed { colIdx, photo ->
                    val realIndex = 5 + rowIdx * 3 + colIdx
                    PhotoCell(Modifier.weight(1f).aspectRatio(1f), photo, loader, realIndex, onPhotoClick)
                }
                repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun CoverCell(
    modifier: Modifier,
    photo: PhotoData,
    loader: suspend (PhotoData) -> ByteArray?,
    onClick: () -> Unit
) {
    val bitmap = rememberPhotoBitmap(photo, loader)
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(PhotoGray)
            .clickable(onClick = onClick)
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        Surface(
            modifier = Modifier.align(Alignment.TopStart).padding(8.dp),
            shape = RoundedCornerShape(999.dp),
            color = Color.White.copy(alpha = 0.9f)
        ) {
            Tag("capa", color = OliveDeep, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
        }
    }
}

@Composable
private fun PhotoCell(
    modifier: Modifier,
    photo: PhotoData,
    loader: suspend (PhotoData) -> ByteArray?,
    index: Int,
    onClick: (Int) -> Unit
) {
    val bitmap = rememberPhotoBitmap(photo, loader)
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(PhotoGray)
            .clickable { onClick(index) }
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
    }
}

// ════════════════════════════════════════════════════════════
// EVENT NOTE CARD (Caveat handwritten)
// ════════════════════════════════════════════════════════════

@Composable
private fun EventNoteCard(note: String?) {
    if (note.isNullOrBlank()) return
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = CozyCreamDeep,
        border = BorderStroke(1.4.dp, CozyOlive.copy(alpha = 0.33f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Tag("nota do dia", color = OliveDeep)
            Spacer(Modifier.height(6.dp))
            Text(
                note,
                fontFamily = FontFamily.Cursive,
                fontSize = 19.sp,
                color = OliveDeep,
                lineHeight = 22.sp
            )
        }
    }
}

// ════════════════════════════════════════════════════════════
// FOOTER ACTIONS
// ════════════════════════════════════════════════════════════

@Composable
private fun FooterActions(
    onAddPhotos: () -> Unit,
    onAddToBook: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(CozyCream)
            .padding(horizontal = 24.dp, vertical = 12.dp)
            .padding(top = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Surface(
            modifier = Modifier.weight(1f).clickable(onClick = onAddPhotos),
            shape = RoundedCornerShape(14.dp),
            color = CozyCreamDeep,
            border = BorderStroke(1.4.dp, CozyOlive.copy(alpha = 0.2f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("+", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = OliveDeep)
                Spacer(Modifier.width(6.dp))
                Text(
                    "adicionar fotos",
                    fontFamily = FontFamily.Serif,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = OliveDeep
                )
            }
        }
        Surface(
            modifier = Modifier.weight(1f).clickable(onClick = onAddToBook),
            shape = RoundedCornerShape(14.dp),
            color = OliveDeep
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("▦", fontSize = 14.sp, color = CozyCream)
                Spacer(Modifier.width(6.dp))
                Text(
                    "ao livro",
                    fontFamily = FontFamily.Serif,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = CozyCream
                )
            }
        }
    }
}

// ════════════════════════════════════════════════════════════
// EMPTY + DIALOGS
// ════════════════════════════════════════════════════════════

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier.fillMaxSize().padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "nenhuma foto ainda",
            fontFamily = FontFamily.Serif,
            fontSize = 22.sp,
            color = CozyOlive
        )
    }
}

@Composable
private fun RenameEventDialog(
    currentName: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(currentName) }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CozyCream,
        shape = RoundedCornerShape(20.dp),
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
            ) { Text("Salvar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
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
        containerColor = CozyCream,
        shape = RoundedCornerShape(20.dp),
        title = { Text("Excluir Evento") },
        text = {
            Text("Excluir \"$eventName\" e todas as $photoCount fotos?")
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB85450))
            ) { Text("Excluir") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

// ════════════════════════════════════════════════════════════
// HELPERS
// ════════════════════════════════════════════════════════════

private fun splitTitle(title: String): Pair<String, String> {
    val words = title.trim().split(" ")
    return when {
        words.size <= 1 -> title to ""
        words.size == 2 -> words[0] to words[1]
        else -> {
            val mid = words.size / 2
            words.take(mid).joinToString(" ") to words.drop(mid).joinToString(" ")
        }
    }
}

private fun formatShortDate(epochMillis: Long): String {
    val c = dateComponentsOf(epochMillis)
    val short = listOf("jan", "fev", "mar", "abr", "mai", "jun", "jul", "ago", "set", "out", "nov", "dez")
    return "${c.day} ${short[c.monthIndex]}"
}
