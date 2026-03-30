/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  FrontPageScreen.kt - Tela Inicial Visual                    ║
 * ║                                                             ║
 * ║  Inspirada em UI de galeria moderna:                        ║
 * ║  - Reels/stories no topo (eventos para revisitar)           ║
 * ║  - Grid masonry de preview de eventos/álbuns                ║
 * ║  - Botão central "Criar" na bottom bar                      ║
 * ║  - Ícone de engrenagem → tela de gerenciamento              ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.gallery

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tsrapprun.camera.EventData
import com.tsrapprun.camera.PhotoData

/**
 * Tela inicial visual do app.
 *
 * @param events Lista de eventos criados.
 * @param allPhotos Todas as fotos para lookup de thumbnails.
 * @param onLoadThumbnail Carrega bytes de uma foto para thumbnail.
 * @param onOpenEvent Abre grid de fotos de um evento.
 * @param onOpenEventList Abre lista completa de eventos ("Ver tudo").
 * @param onCreate Abre fluxo de criação de evento (câmera).
 * @param onOpenSettings Abre tela de gerenciamento (Home atual).
 */
@Composable
fun FrontPageScreen(
    events: List<EventData>,
    allPhotos: List<PhotoData>,
    onLoadThumbnail: suspend (PhotoData) -> ByteArray?,
    onOpenEvent: (EventData) -> Unit,
    onOpenEventList: () -> Unit,
    onCreate: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenMemoryBook: () -> Unit = {}
) {
    val sortedEvents = remember(events) {
        events.sortedByDescending { it.createdAt }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // ── Conteúdo scrollável ──
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 100.dp) // espaço para o botão Criar
            ) {
                // ── Top Bar ──
                TopBar(onOpenSettings = onOpenSettings)

                // ── Reels de Eventos ──
                if (sortedEvents.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    EventReelsRow(
                        events = sortedEvents,
                        allPhotos = allPhotos,
                        onLoadThumbnail = onLoadThumbnail,
                        onOpenEvent = onOpenEvent
                    )
                }

                // ── Seção: Seus Eventos ──
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Seus Eventos",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    if (events.isNotEmpty()) {
                        Text(
                            "Ver tudo",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable(onClick = onOpenEventList)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // ── Grid Masonry de Eventos ──
                if (sortedEvents.isNotEmpty()) {
                    EventMasonryGrid(
                        events = sortedEvents,
                        allPhotos = allPhotos,
                        onLoadThumbnail = onLoadThumbnail,
                        onOpenEvent = onOpenEvent
                    )
                } else {
                    // ── Estado vazio ──
                    EmptyState()
                }
            }

            // ── Bottom Bar com botões ──
            BottomCreateBar(
                onCreate = onCreate,
                onOpenMemoryBook = onOpenMemoryBook,
                hasEvents = sortedEvents.isNotEmpty(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .windowInsetsPadding(WindowInsets.navigationBars)
            )
        }
    }
}

// ═══════════════════════════════════════════════════
// TOP BAR
// ═══════════════════════════════════════════════════

@Composable
private fun TopBar(onOpenSettings: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "TSR App",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.weight(1f))
        // Ícone engrenagem → Gerenciamento
        IconButton(onClick = onOpenSettings) {
            // Engrenagem usando texto (sem dependência de ícone externo)
            Text(
                text = "\u2699",  // ⚙
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

// ═══════════════════════════════════════════════════
// REELS DE EVENTOS (stories circulares)
// ═══════════════════════════════════════════════════

@Composable
private fun EventReelsRow(
    events: List<EventData>,
    allPhotos: List<PhotoData>,
    onLoadThumbnail: suspend (PhotoData) -> ByteArray?,
    onOpenEvent: (EventData) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(events.take(10), key = { it.id }) { event ->
            EventReelCircle(
                event = event,
                allPhotos = allPhotos,
                onLoadThumbnail = onLoadThumbnail,
                onClick = { onOpenEvent(event) }
            )
        }
    }
}

@Composable
private fun EventReelCircle(
    event: EventData,
    allPhotos: List<PhotoData>,
    onLoadThumbnail: suspend (PhotoData) -> ByteArray?,
    onClick: () -> Unit
) {
    val thumbnailPhoto = remember(event.thumbnailPhotoId, allPhotos) {
        allPhotos.find { it.id == event.thumbnailPhotoId }
    }

    var thumbnail by remember(event.id) { mutableStateOf<ImageBitmap?>(null) }
    var isLoading by remember(event.id) { mutableStateOf(true) }

    LaunchedEffect(thumbnailPhoto?.id) {
        if (thumbnailPhoto != null) {
            isLoading = true
            val bytes = onLoadThumbnail(thumbnailPhoto)
            thumbnail = bytes?.let { decodeThumbnail(it) }
            isLoading = false
        } else {
            isLoading = false
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        // Círculo com borda gradiente (estilo Instagram)
        Box(
            modifier = Modifier
                .size(72.dp)
                .border(
                    width = 2.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFE8715A),  // coral
                            Color(0xFFF2994A),  // laranja
                            Color(0xFFE8715A)
                        )
                    ),
                    shape = CircleShape
                )
                .padding(3.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            when {
                isLoading -> CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
                thumbnail != null -> Image(
                    bitmap = thumbnail!!,
                    contentDescription = event.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                else -> Text(
                    text = event.name.take(1).uppercase(),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = event.name,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(72.dp)
        )
    }
}

// ═══════════════════════════════════════════════════
// GRID MASONRY DE EVENTOS
// ═══════════════════════════════════════════════════

@Composable
private fun EventMasonryGrid(
    events: List<EventData>,
    allPhotos: List<PhotoData>,
    onLoadThumbnail: suspend (PhotoData) -> ByteArray?,
    onOpenEvent: (EventData) -> Unit
) {
    // Distribui eventos em 2 colunas alternadas
    val leftColumn = events.filterIndexed { i, _ -> i % 2 == 0 }
    val rightColumn = events.filterIndexed { i, _ -> i % 2 == 1 }

    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Coluna esquerda - cards maiores (aspect ratio 0.75)
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            leftColumn.forEachIndexed { index, event ->
                EventPreviewCard(
                    event = event,
                    allPhotos = allPhotos,
                    onLoadThumbnail = onLoadThumbnail,
                    onClick = { onOpenEvent(event) },
                    // Alterna entre cards altos e quadrados
                    aspectRatio = if (index % 2 == 0) 0.75f else 1f
                )
            }
        }

        // Coluna direita - padrão invertido
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            rightColumn.forEachIndexed { index, event ->
                EventPreviewCard(
                    event = event,
                    allPhotos = allPhotos,
                    onLoadThumbnail = onLoadThumbnail,
                    onClick = { onOpenEvent(event) },
                    aspectRatio = if (index % 2 == 0) 1f else 0.75f
                )
            }
        }
    }
}

@Composable
private fun EventPreviewCard(
    event: EventData,
    allPhotos: List<PhotoData>,
    onLoadThumbnail: suspend (PhotoData) -> ByteArray?,
    onClick: () -> Unit,
    aspectRatio: Float = 1f
) {
    val thumbnailPhoto = remember(event.thumbnailPhotoId, allPhotos) {
        allPhotos.find { it.id == event.thumbnailPhotoId }
    }

    var thumbnail by remember(event.id) { mutableStateOf<ImageBitmap?>(null) }
    var isLoading by remember(event.id) { mutableStateOf(true) }

    LaunchedEffect(thumbnailPhoto?.id) {
        if (thumbnailPhoto != null) {
            isLoading = true
            val bytes = onLoadThumbnail(thumbnailPhoto)
            thumbnail = bytes?.let { decodeThumbnail(it) }
            isLoading = false
        } else {
            isLoading = false
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(aspectRatio)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Thumbnail de fundo
            when {
                isLoading -> Box(
                    modifier = Modifier.fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(strokeWidth = 2.dp)
                }
                thumbnail != null -> Image(
                    bitmap = thumbnail!!,
                    contentDescription = event.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                else -> Box(
                    modifier = Modifier.fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = event.name.take(2).uppercase(),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                    )
                }
            }

            // Overlay com gradiente na parte inferior
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.7f)
                            )
                        )
                    )
                    .padding(12.dp)
            ) {
                Column {
                    Text(
                        text = event.name,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${event.photoCount} fotos",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════
// BOTTOM BAR COM BOTÃO CRIAR
// ═══════════════════════════════════════════════════

@Composable
private fun BottomCreateBar(
    onCreate: () -> Unit,
    onOpenMemoryBook: () -> Unit,
    hasEvents: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        MaterialTheme.colorScheme.background.copy(alpha = 0.95f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Botão "Ver Livro" (só aparece se tem eventos)
            if (hasEvents) {
                OutlinedButton(
                    onClick = onOpenMemoryBook,
                    modifier = Modifier.height(56.dp).weight(1f),
                    shape = RoundedCornerShape(28.dp),
                    border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        text = "\uD83D\uDCD6 Livro",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Button(
                onClick = onCreate,
                modifier = Modifier.height(56.dp).then(
                    if (hasEvents) Modifier.weight(1f) else Modifier.fillMaxWidth(0.5f)
                ),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 8.dp
                )
            ) {
                Text(
                    text = "+ Criar",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════
// ESTADO VAZIO
// ═══════════════════════════════════════════════════

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 40.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "\uD83D\uDCF7",  // camera emoji
            fontSize = 48.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Nenhum evento ainda",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Toque em \"Criar\" para registrar\nseu primeiro evento!",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )
    }
}
