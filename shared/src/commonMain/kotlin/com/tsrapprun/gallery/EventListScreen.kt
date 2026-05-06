/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  EventListScreen.kt — tradução de fullapp/screens/events.jsx ║
 * ║  (EventListA)                                                ║
 * ║                                                              ║
 * ║  Lista de eventos com photo stack mini (2 polaroides         ║
 * ║  sobrepostas), tag de data, título italic e contagem de      ║
 * ║  fotos. Agrupados por ano.                                   ║
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tsrapprun.camera.EventData
import com.tsrapprun.camera.PhotoData
import com.tsrapprun.platform.dateComponentsOf
import com.tsrapprun.ui.chrome.OliveDeep
import com.tsrapprun.ui.chrome.PhotoGray
import com.tsrapprun.ui.chrome.PhotoGrayLight
import com.tsrapprun.ui.chrome.ScreenHeader
import com.tsrapprun.ui.chrome.Tag
import com.tsrapprun.ui.chrome.italicSerifText
import com.tsrapprun.ui.theme.CozyAmberDeep
import com.tsrapprun.ui.theme.CozyCream
import com.tsrapprun.ui.theme.CozyCreamDeep
import com.tsrapprun.ui.theme.CozyOlive

@Composable
fun EventListScreen(
    events: List<EventData>,
    allPhotos: List<PhotoData>,
    onLoadThumbnail: suspend (PhotoData) -> ByteArray?,
    onOpenEvent: (EventData) -> Unit,
    onCreate: () -> Unit = {},
    onBack: () -> Unit
) {
    // Mostra apenas eventos marcados como marco.
    val sorted = remember(events) {
        events.filter { it.isMilestone }.sortedByDescending { it.createdAt }
    }
    // groupBy preserva ordem de inserção (LinkedHashMap), e como sorted já está
    // descendente por data, os anos saem do mais recente pro mais antigo.
    val groups: Map<Int, List<EventData>> = remember(sorted) {
        sorted.groupBy { dateComponentsOf(it.createdAt).year }
    }

    Box(modifier = Modifier.fillMaxSize().background(CozyCream)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            ScreenHeader(
                chapter = "vol. 01 — marcos",
                title = italicSerifText(
                    prefix = "seus ",
                    italic = "marcos",
                    italicColor = CozyAmberDeep,
                    defaultColor = OliveDeep
                ),
                subtitle = "primeiras vezes, aniversários, momentos especiais ✦",
                onBack = onBack,
                rightContent = {
                    Surface(
                        modifier = Modifier.size(32.dp).clickable(onClick = onCreate),
                        shape = androidx.compose.foundation.shape.CircleShape,
                        color = OliveDeep,
                        shadowElevation = 3.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                "+",
                                fontFamily = FontFamily.Serif,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = CozyCream
                            )
                        }
                    }
                }
            )

            if (sorted.isEmpty()) {
                EmptyState()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 22.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 24.dp)
                ) {
                    groups.entries.forEach { entry ->
                        val year = entry.key
                        val list = entry.value
                        item(key = "h$year") {
                            YearSash(label = year.toString())
                        }
                        items(count = list.size, key = { i -> list[i].id }) { index ->
                            val event = list[index]
                            val thumb = remember(event.thumbnailPhotoId, allPhotos) {
                                allPhotos.find { it.id == event.thumbnailPhotoId }
                            }
                            EventRow(
                                event = event,
                                thumbnailPhoto = thumb,
                                onLoadThumbnail = onLoadThumbnail,
                                onClick = { onOpenEvent(event) }
                            )
                            Spacer(Modifier.height(12.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun YearSash(label: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.width(14.dp).height(1.dp).background(CozyOlive.copy(alpha = 0.4f)))
        Spacer(Modifier.width(10.dp))
        Tag(label, color = OliveDeep)
        Spacer(Modifier.width(10.dp))
        Box(Modifier.weight(1f).height(1.dp).background(CozyOlive.copy(alpha = 0.18f)))
    }
}

@Composable
private fun EventRow(
    event: EventData,
    thumbnailPhoto: PhotoData?,
    onLoadThumbnail: suspend (PhotoData) -> ByteArray?,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = CozyCreamDeep,
        border = BorderStroke(1.4.dp, CozyOlive.copy(alpha = 0.12f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Photo stack mini
            PhotoStackMini(event = event, thumbnailPhoto = thumbnailPhoto, onLoadThumbnail = onLoadThumbnail)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Tag(formatDate(event.createdAt), color = OliveDeep)
                Spacer(Modifier.height(2.dp))
                Text(
                    event.name,
                    fontFamily = FontFamily.Serif,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Medium,
                    color = OliveDeep,
                    letterSpacing = (-0.3).sp,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "${event.photoCount} ${if (event.photoCount == 1) "foto" else "fotos"}",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    color = CozyOlive.copy(alpha = 0.6f)
                )
            }
            Text("›", fontSize = 18.sp, color = CozyOlive, fontWeight = FontWeight.Light)
        }
    }
}

@Composable
private fun PhotoStackMini(
    event: EventData,
    thumbnailPhoto: PhotoData?,
    onLoadThumbnail: suspend (PhotoData) -> ByteArray?
) {
    val bitmap = com.tsrapprun.ui.photos.rememberPhotoBitmap(thumbnailPhoto, onLoadThumbnail)
    Box(modifier = Modifier.size(width = 76.dp, height = 80.dp)) {
        Surface(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 8.dp, top = 6.dp)
                .size(width = 64.dp, height = 70.dp)
                .graphicsLayer { rotationZ = -6f },
            shape = RoundedCornerShape(4.dp),
            color = CozyCream,
            border = BorderStroke(1.dp, CozyOlive.copy(alpha = 0.35f)),
            shadowElevation = 2.dp
        ) {
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(PhotoGrayLight)
            )
        }
        Surface(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(width = 60.dp, height = 66.dp)
                .graphicsLayer { rotationZ = 6f },
            shape = RoundedCornerShape(4.dp),
            color = CozyCream,
            border = BorderStroke(1.dp, CozyOlive.copy(alpha = 0.35f)),
            shadowElevation = 4.dp
        ) {
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(PhotoGray),
                contentAlignment = Alignment.Center
            ) {
                if (bitmap != null) {
                    androidx.compose.foundation.Image(
                        bitmap = bitmap,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                } else {
                    Text(
                        event.name.firstOrNull()?.uppercase() ?: "✿",
                        fontSize = 18.sp,
                        fontFamily = FontFamily.Serif,
                        fontStyle = FontStyle.Italic,
                        color = CozyCream.copy(alpha = 0.85f)
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier.fillMaxSize().padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            italicSerifText(prefix = "ainda sem ", italic = "marcos", italicColor = CozyAmberDeep, defaultColor = OliveDeep),
            fontSize = 22.sp,
            letterSpacing = (-0.4).sp
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "marque um registro como marco ✦ na hora de criar",
            fontSize = 13.sp,
            color = CozyOlive.copy(alpha = 0.65f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

private fun formatDate(epochMillis: Long): String {
    val c = dateComponentsOf(epochMillis)
    val short = listOf("jan", "fev", "mar", "abr", "mai", "jun", "jul", "ago", "set", "out", "nov", "dez")
    return "${c.day} ${short[c.monthIndex]}"
}
