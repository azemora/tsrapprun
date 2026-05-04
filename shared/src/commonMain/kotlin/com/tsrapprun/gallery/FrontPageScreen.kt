/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  FrontPageScreen.kt — Tela inicial cozy                      ║
 * ║                                                              ║
 * ║  Hero card no topo (saudação + stats), atalhos coloridos,    ║
 * ║  reels horizontais e grid de eventos. Bottom bar flutuante. ║
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import com.tsrapprun.ui.theme.CozyAmber
import com.tsrapprun.ui.theme.CozyCream
import com.tsrapprun.ui.theme.CozyCreamDeep
import com.tsrapprun.ui.theme.CozyGold
import com.tsrapprun.ui.theme.CozyInk
import com.tsrapprun.ui.theme.CozyOlive
import com.tsrapprun.ui.theme.CozySage
import com.tsrapprun.ui.theme.CozySageMist
import com.tsrapprun.ui.theme.CozyTan

@Composable
fun FrontPageScreen(
    events: List<EventData>,
    allPhotos: List<PhotoData>,
    onLoadThumbnail: suspend (PhotoData) -> ByteArray?,
    onOpenEvent: (EventData) -> Unit,
    onOpenEventList: () -> Unit,
    onCreate: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenMemoryBook: () -> Unit = {},
    onOpenMoments: () -> Unit = {}
) {
    val sortedEvents = remember(events) {
        events.sortedByDescending { it.createdAt }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(CozySageMist, CozyCream)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 110.dp)
        ) {
            TopBar(onOpenSettings = onOpenSettings, onOpenMoments = onOpenMoments)

            Spacer(Modifier.height(8.dp))

            // ── Hero card (clicável → lista de eventos) ──
            HeroCard(
                eventCount = events.size,
                photoCount = allPhotos.size,
                onClick = onOpenEventList
            )

            Spacer(Modifier.height(20.dp))

            // ── Atalhos coloridos (estilo cards da referência) ──
            QuickActions(
                onCreate = onCreate,
                onOpenMoments = onOpenMoments,
                onOpenMemoryBook = onOpenMemoryBook,
                hasEvents = sortedEvents.isNotEmpty()
            )

            // ── Reels (se tiver eventos) ──
            if (sortedEvents.isNotEmpty()) {
                Spacer(Modifier.height(28.dp))
                SectionHeader(
                    title = "reviva",
                    actionLabel = if (events.size > 10) "ver tudo" else null,
                    onAction = onOpenEventList
                )
                Spacer(Modifier.height(12.dp))
                EventReelsRow(
                    events = sortedEvents,
                    allPhotos = allPhotos,
                    onLoadThumbnail = onLoadThumbnail,
                    onOpenEvent = onOpenEvent
                )
            }

            // ── Grid masonry ──
            Spacer(Modifier.height(28.dp))

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "seus eventos",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = CozyOlive
                )
                if (events.isNotEmpty()) {
                    Text(
                        "ver tudo",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = CozyAmber,
                        modifier = Modifier.clickable(onClick = onOpenEventList)
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            if (sortedEvents.isNotEmpty()) {
                EventMasonryGrid(
                    events = sortedEvents,
                    allPhotos = allPhotos,
                    onLoadThumbnail = onLoadThumbnail,
                    onOpenEvent = onOpenEvent
                )
            } else {
                EmptyState()
            }
        }

        // ── Bottom bar flutuante ──
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

// ═══════════════════════════════════════════════════
// TOP BAR
// ═══════════════════════════════════════════════════

@Composable
private fun TopBar(onOpenSettings: () -> Unit, onOpenMoments: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(36.dp),
            shape = CircleShape,
            color = CozySage
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text("🌿", fontSize = 18.sp)
            }
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(
                "olá!",
                fontSize = 13.sp,
                color = CozyOlive.copy(alpha = 0.7f)
            )
            Text(
                "TSR App",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = CozyOlive
            )
        }

        Spacer(Modifier.weight(1f))

        IconChip(emoji = "📝", onClick = onOpenMoments)
        Spacer(Modifier.width(8.dp))
        IconChip(emoji = "⚙️", onClick = onOpenSettings)
    }
}

@Composable
private fun IconChip(emoji: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.size(40.dp).clickable(onClick = onClick),
        shape = CircleShape,
        color = CozyCream,
        border = androidx.compose.foundation.BorderStroke(1.dp, CozyTan)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(emoji, fontSize = 18.sp)
        }
    }
}

// ═══════════════════════════════════════════════════
// HERO CARD (saudação + stats)
// ═══════════════════════════════════════════════════

@Composable
private fun HeroCard(eventCount: Int, photoCount: Int, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = CozySage),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🍃", fontSize = 24.sp)
                Spacer(Modifier.width(8.dp))
                Text(
                    "suas memórias",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        "$photoCount",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        lineHeight = 50.sp
                    )
                    Text(
                        "fotos guardadas",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White.copy(alpha = 0.18f)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "$eventCount",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            if (eventCount == 1) "evento" else "eventos",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════
// QUICK ACTIONS (cards coloridos)
// ═══════════════════════════════════════════════════

@Composable
private fun QuickActions(
    onCreate: () -> Unit,
    onOpenMoments: () -> Unit,
    onOpenMemoryBook: () -> Unit,
    hasEvents: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ActionCard(
            modifier = Modifier.weight(1f),
            label = "registrar\nmomento",
            emoji = "📷",
            bg = CozyAmber,
            fg = Color.White,
            onClick = onCreate
        )
        ActionCard(
            modifier = Modifier.weight(1f),
            label = "registros\ndo dia",
            emoji = "✍️",
            bg = CozyGold,
            fg = Color.White,
            onClick = onOpenMoments
        )
    }
    Spacer(Modifier.height(12.dp))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ActionCard(
            modifier = Modifier.weight(1f),
            label = "livro de\nmemórias",
            emoji = "📖",
            bg = CozyTan,
            fg = CozyInk,
            enabled = hasEvents,
            onClick = onOpenMemoryBook
        )
        ActionCard(
            modifier = Modifier.weight(1f),
            label = "todas\nas fotos",
            emoji = "🖼️",
            bg = CozyCreamDeep,
            fg = CozyInk,
            onClick = { /* TODO: open all photos */ }
        )
    }
}

@Composable
private fun ActionCard(
    modifier: Modifier = Modifier,
    label: String,
    emoji: String,
    bg: Color,
    fg: Color,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(110.dp)
            .clickable(enabled = enabled, onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (enabled) bg else bg.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(emoji, fontSize = 28.sp)
            Text(
                label,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = fg,
                lineHeight = 18.sp
            )
        }
    }
}

// ═══════════════════════════════════════════════════
// SECTION HEADER
// ═══════════════════════════════════════════════════

@Composable
private fun SectionHeader(
    title: String,
    actionLabel: String? = null,
    onAction: () -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            title,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = CozyOlive
        )
        if (actionLabel != null) {
            Text(
                actionLabel,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = CozyAmber,
                modifier = Modifier.clickable(onClick = onAction)
            )
        }
    }
}

// ═══════════════════════════════════════════════════
// REELS DE EVENTOS
// ═══════════════════════════════════════════════════

@Composable
private fun EventReelsRow(
    events: List<EventData>,
    allPhotos: List<PhotoData>,
    onLoadThumbnail: suspend (PhotoData) -> ByteArray?,
    onOpenEvent: (EventData) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 24.dp),
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
        Box(
            modifier = Modifier
                .size(76.dp)
                .border(
                    width = 2.5.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(CozyAmber, CozyGold, CozyAmber)
                    ),
                    shape = CircleShape
                )
                .padding(3.dp)
                .clip(CircleShape)
                .background(CozyCream),
            contentAlignment = Alignment.Center
        ) {
            when {
                isLoading -> CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = CozySage,
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
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = CozyOlive
                )
            }
        }

        Spacer(Modifier.height(6.dp))

        Text(
            text = event.name,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = CozyOlive,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(76.dp)
        )
    }
}

// ═══════════════════════════════════════════════════
// GRID MASONRY
// ═══════════════════════════════════════════════════

@Composable
private fun EventMasonryGrid(
    events: List<EventData>,
    allPhotos: List<PhotoData>,
    onLoadThumbnail: suspend (PhotoData) -> ByteArray?,
    onOpenEvent: (EventData) -> Unit
) {
    val leftColumn = events.filterIndexed { i, _ -> i % 2 == 0 }
    val rightColumn = events.filterIndexed { i, _ -> i % 2 == 1 }

    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            leftColumn.forEachIndexed { index, event ->
                EventPreviewCard(
                    event = event,
                    allPhotos = allPhotos,
                    onLoadThumbnail = onLoadThumbnail,
                    onClick = { onOpenEvent(event) },
                    aspectRatio = if (index % 2 == 0) 0.78f else 1f
                )
            }
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            rightColumn.forEachIndexed { index, event ->
                EventPreviewCard(
                    event = event,
                    allPhotos = allPhotos,
                    onLoadThumbnail = onLoadThumbnail,
                    onClick = { onOpenEvent(event) },
                    aspectRatio = if (index % 2 == 0) 1f else 0.78f
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
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = CozyCreamDeep)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                isLoading -> Box(
                    modifier = Modifier.fillMaxSize().background(CozyCreamDeep),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = CozySage,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(24.dp)
                    )
                }
                thumbnail != null -> Image(
                    bitmap = thumbnail!!,
                    contentDescription = event.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                else -> Box(
                    modifier = Modifier.fillMaxSize().background(CozyTan.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = event.name.take(2).uppercase(),
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = CozyOlive.copy(alpha = 0.55f)
                    )
                }
            }

            // Overlay com gradiente suave + nome
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.55f)
                            )
                        )
                    )
                    .padding(horizontal = 14.dp, vertical = 12.dp)
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
                        text = "${event.photoCount} ${if (event.photoCount == 1) "foto" else "fotos"}",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════
// BOTTOM CREATE BAR
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
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(36.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Botão home (ativo) — verde sage com badge "+"
                Surface(
                    modifier = Modifier
                        .height(52.dp)
                        .weight(1f)
                        .clickable(onClick = onCreate),
                    shape = RoundedCornerShape(26.dp),
                    color = CozySage
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("📸", fontSize = 18.sp)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "registrar",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }

                if (hasEvents) {
                    Surface(
                        modifier = Modifier
                            .size(52.dp)
                            .clickable(onClick = onOpenMemoryBook),
                        shape = CircleShape,
                        color = CozyCream,
                        border = androidx.compose.foundation.BorderStroke(1.dp, CozyTan)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("📖", fontSize = 22.sp)
                        }
                    }
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════
// EMPTY STATE
// ═══════════════════════════════════════════════════

@Composable
private fun EmptyState() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = CozyCream),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("🌱", fontSize = 56.sp)
            Spacer(Modifier.height(16.dp))
            Text(
                "sem eventos ainda",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = CozyOlive
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "toque em \"registrar\"\npara plantar a primeira memória",
                fontSize = 13.sp,
                color = CozyOlive.copy(alpha = 0.65f),
                textAlign = TextAlign.Center,
                lineHeight = 19.sp
            )
        }
    }
}
