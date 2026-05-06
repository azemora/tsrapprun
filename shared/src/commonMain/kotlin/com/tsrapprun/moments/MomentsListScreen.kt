/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  MomentsListScreen.kt — tradução de                          ║
 * ║  fullapp/screens/moments-list.jsx                            ║
 * ║                                                              ║
 * ║  Header editorial + chips de filtro + masonry de MomentCards.║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.moments

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tsrapprun.platform.dateComponentsOf
import com.tsrapprun.ui.chrome.MomentCard
import com.tsrapprun.ui.chrome.MomentKind
import com.tsrapprun.ui.chrome.OliveDeep
import com.tsrapprun.ui.chrome.ScreenHeader
import com.tsrapprun.ui.chrome.Tag
import com.tsrapprun.ui.chrome.italicSerifText
import com.tsrapprun.ui.theme.CozyAmberDeep
import com.tsrapprun.ui.theme.CozyCream
import com.tsrapprun.ui.theme.CozyCreamDeep
import com.tsrapprun.ui.theme.CozyOlive

private enum class MomentsFilter(val label: String) {
    TODOS("todos"),
    COM_FOTO("com foto"),
    SO_TEXTO("texto"),
    MARCO("marcos")
}

/** Item da timeline: pode ser um registro de texto ou um registro com fotos. */
private sealed class TimelineItem {
    abstract val createdAt: Long
    abstract val isMilestone: Boolean
    data class Moment(val entry: MomentEntry) : TimelineItem() {
        override val createdAt: Long get() = entry.createdAt
        override val isMilestone: Boolean get() = entry.isMarco()
    }
    data class Event(val event: com.tsrapprun.camera.EventData) : TimelineItem() {
        override val createdAt: Long get() = event.createdAt
        override val isMilestone: Boolean get() = event.isMilestone
    }
}

@Composable
fun MomentsListScreen(
    moments: List<MomentEntry>,
    events: List<com.tsrapprun.camera.EventData> = emptyList(),
    allPhotos: List<com.tsrapprun.camera.PhotoData> = emptyList(),
    onLoadPhoto: suspend (com.tsrapprun.camera.PhotoData) -> ByteArray? = { null },
    onAddRegistro: () -> Unit,
    onDeleteMoment: (String) -> Unit,
    onOpenMoment: (MomentEntry) -> Unit,
    onOpenEvent: (com.tsrapprun.camera.EventData) -> Unit = {},
    onOpenMesversario: ((Int) -> Unit)? = null,
    onBack: () -> Unit
) {
    var filter by remember { mutableStateOf(MomentsFilter.TODOS) }
    var showDeleteId by remember { mutableStateOf<String?>(null) }

    val allItems: List<TimelineItem> = remember(moments, events) {
        (moments.map { TimelineItem.Moment(it) } +
                events.map { TimelineItem.Event(it) })
            .sortedByDescending { it.createdAt }
    }

    val filtered = remember(allItems, filter) {
        allItems.filter {
            when (filter) {
                MomentsFilter.TODOS -> true
                MomentsFilter.COM_FOTO -> it is TimelineItem.Event
                MomentsFilter.SO_TEXTO -> it is TimelineItem.Moment &&
                        (it.entry.type == MomentType.DAILY || it.entry.type == MomentType.WEEKLY)
                MomentsFilter.MARCO -> it.isMilestone
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(CozyCream)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            ScreenHeader(
                chapter = "vol. 01 — registros",
                title = italicSerifText(
                    prefix = "seus ",
                    italic = "registros",
                    italicColor = CozyAmberDeep,
                    defaultColor = OliveDeep
                ),
                onBack = onBack,
                rightContent = {
                    // Botão circular "+" 32dp pra caber dentro da Row do header.
                    Surface(
                        modifier = Modifier
                            .size(32.dp)
                            .clickable(onClick = onAddRegistro),
                        shape = androidx.compose.foundation.shape.CircleShape,
                        color = OliveDeep,
                        shadowElevation = 3.dp
                    ) {
                        androidx.compose.foundation.layout.Box(
                            contentAlignment = Alignment.Center
                        ) {
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

            // Filter chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MomentsFilter.values().forEach { f ->
                    FilterChip(label = f.label, selected = f == filter, count = countFor(allItems, f)) {
                        filter = f
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            if (filtered.isEmpty()) {
                EmptyState()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 80.dp)
                ) {
                    val groups = filtered.groupBy {
                        val c = dateComponentsOf(it.createdAt)
                        "${MONTHS[c.monthIndex]} ${c.year}"
                    }
                    groups.forEach { (label, list) ->
                        item(key = "h$label") {
                            SectionTag(label)
                        }
                        item(key = "g$label") {
                            MasonryRow(
                                items = list,
                                allPhotos = allPhotos,
                                onLoadPhoto = onLoadPhoto,
                                onDeleteRequest = { showDeleteId = it },
                                onOpenMoment = onOpenMoment,
                                onOpenEvent = onOpenEvent,
                                onOpenMesversario = onOpenMesversario
                            )
                            Spacer(Modifier.height(20.dp))
                        }
                    }
                }
            }
        }
    }

    if (showDeleteId != null) {
        AlertDialog(
            onDismissRequest = { showDeleteId = null },
            containerColor = CozyCream,
            shape = RoundedCornerShape(20.dp),
            title = { Text("excluir registro?", fontWeight = FontWeight.SemiBold) },
            text = { Text("essa ação não pode ser desfeita.") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteId?.let { onDeleteMoment(it) }
                        showDeleteId = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB85450))
                ) {
                    Text("excluir")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteId = null }) { Text("cancelar", color = CozyOlive) }
            }
        )
    }
}

@Composable
private fun FilterChip(label: String, selected: Boolean, count: Int, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(999.dp),
        color = if (selected) OliveDeep else CozyCreamDeep,
        border = if (selected) null else BorderStroke(1.4.dp, OliveDeep.copy(alpha = 0.13f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                label,
                fontFamily = FontFamily.Serif,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Medium,
                color = if (selected) CozyCream else CozyOlive,
                letterSpacing = (-0.1).sp
            )
            Spacer(Modifier.width(6.dp))
            Text(
                "$count",
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                color = if (selected) CozyCream.copy(alpha = 0.7f) else CozyOlive.copy(alpha = 0.55f)
            )
        }
    }
}

@Composable
private fun SectionTag(label: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(start = 8.dp, end = 8.dp, top = 8.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier.width(14.dp).height(1.dp).background(CozyOlive.copy(alpha = 0.4f))
        )
        Spacer(Modifier.width(10.dp))
        Tag(label, color = OliveDeep)
        Spacer(Modifier.width(10.dp))
        Box(
            Modifier.weight(1f).height(1.dp).background(CozyOlive.copy(alpha = 0.18f))
        )
    }
}

@Composable
private fun MasonryRow(
    items: List<TimelineItem>,
    allPhotos: List<com.tsrapprun.camera.PhotoData>,
    onLoadPhoto: suspend (com.tsrapprun.camera.PhotoData) -> ByteArray?,
    onDeleteRequest: (String) -> Unit,
    onOpenMoment: (MomentEntry) -> Unit,
    onOpenEvent: (com.tsrapprun.camera.EventData) -> Unit,
    onOpenMesversario: ((Int) -> Unit)?
) {
    val left = items.filterIndexed { i, _ -> i % 2 == 0 }
    val right = items.filterIndexed { i, _ -> i % 2 == 1 }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            left.forEachIndexed { i, item ->
                TimelineCard(item, if (i % 2 == 0) -1.4f else -0.6f, allPhotos, onLoadPhoto, onDeleteRequest, onOpenMoment, onOpenEvent, onOpenMesversario)
            }
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            right.forEachIndexed { i, item ->
                TimelineCard(item, if (i % 2 == 0) 1.2f else 0.8f, allPhotos, onLoadPhoto, onDeleteRequest, onOpenMoment, onOpenEvent, onOpenMesversario)
            }
        }
    }
}

@Composable
private fun TimelineCard(
    item: TimelineItem,
    rotation: Float,
    allPhotos: List<com.tsrapprun.camera.PhotoData>,
    onLoadPhoto: suspend (com.tsrapprun.camera.PhotoData) -> ByteArray?,
    onDeleteRequest: (String) -> Unit,
    onOpenMoment: (MomentEntry) -> Unit,
    onOpenEvent: (com.tsrapprun.camera.EventData) -> Unit,
    onOpenMesversario: ((Int) -> Unit)?
) {
    when (item) {
        is TimelineItem.Moment -> MomentCardForEntry(
            item.entry, rotation, onDeleteRequest, onOpenMoment, onOpenMesversario
        )
        is TimelineItem.Event -> {
            val thumb = remember(item.event.thumbnailPhotoId, allPhotos) {
                allPhotos.find { it.id == item.event.thumbnailPhotoId }
            }
            EventTimelineCard(
                event = item.event,
                thumbnailPhoto = thumb,
                onLoadPhoto = onLoadPhoto,
                rotation = rotation,
                onClick = { onOpenEvent(item.event) }
            )
        }
    }
}

@Composable
private fun EventTimelineCard(
    event: com.tsrapprun.camera.EventData,
    thumbnailPhoto: com.tsrapprun.camera.PhotoData?,
    onLoadPhoto: suspend (com.tsrapprun.camera.PhotoData) -> ByteArray?,
    rotation: Float,
    onClick: () -> Unit
) {
    val short = listOf("jan", "fev", "mar", "abr", "mai", "jun", "jul", "ago", "set", "out", "nov", "dez")
    val c = dateComponentsOf(event.createdAt)
    val dateLabel = "${c.day} ${short[c.monthIndex]}"
    val countLabel = if (event.photoCount == 1) "1 foto" else "${event.photoCount} fotos"
    val bitmap = com.tsrapprun.ui.photos.rememberPhotoBitmap(thumbnailPhoto, onLoadPhoto)
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer { rotationZ = rotation }
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = CozyCreamDeep,
        border = BorderStroke(1.dp, CozyOlive.copy(alpha = 0.12f)),
        shadowElevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(com.tsrapprun.ui.chrome.PhotoGray)
            ) {
                if (bitmap != null) {
                    androidx.compose.foundation.Image(
                        bitmap = bitmap,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                }
                if (event.isMilestone) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(6.dp),
                        shape = RoundedCornerShape(999.dp),
                        color = com.tsrapprun.ui.chrome.Butter
                    ) {
                        Text(
                            "✦ marco",
                            fontFamily = FontFamily.Serif,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = OliveDeep,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }
            Spacer(Modifier.height(6.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Tag(countLabel, color = OliveDeep)
                Text(
                    dateLabel,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    color = CozyOlive.copy(alpha = 0.7f)
                )
            }
            Spacer(Modifier.height(2.dp))
            Text(
                event.name,
                fontFamily = FontFamily.Serif,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = OliveDeep,
                lineHeight = 18.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                letterSpacing = (-0.2).sp
            )
        }
    }
}

@Composable
private fun MomentCardForEntry(
    m: MomentEntry,
    rotation: Float,
    onDeleteRequest: (String) -> Unit,
    onOpenMoment: (MomentEntry) -> Unit,
    onOpenMesversario: ((Int) -> Unit)?
) {
    val kind = when (m.type) {
        MomentType.DAILY -> MomentKind.DAILY
        MomentType.WEEKLY -> MomentKind.WEEKLY
        MomentType.MESVERSARIO -> MomentKind.MESVERSARIO
        MomentType.PREGNANCY_WEEK -> MomentKind.PREGNANCY_WEEK
        MomentType.DAY_OF_LIFE -> MomentKind.DAY_OF_LIFE
    }
    MomentCard(
        kind = kind,
        title = m.text,
        date = formatDate(m.createdAt),
        note = null,
        rotation = rotation,
        mini = true,
        onClick = if (m.type == MomentType.MESVERSARIO && onOpenMesversario != null) {
            { onOpenMesversario(m.milestoneNumber) }
        } else if (m.type == MomentType.DAILY || m.type == MomentType.WEEKLY) {
            { onOpenMoment(m) }
        } else null,
        onLongClick = if (m.type == MomentType.DAILY || m.type == MomentType.WEEKLY) {
            { onDeleteRequest(m.id) }
        } else null
    )
}

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier.fillMaxSize().padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            italicSerifText(prefix = "ainda sem ", italic = "registros", italicColor = CozyAmberDeep, defaultColor = OliveDeep),
            fontSize = 22.sp,
            letterSpacing = (-0.4).sp
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "toque em + acima pra começar a guardar momentos",
            fontSize = 13.sp,
            color = CozyOlive.copy(alpha = 0.65f)
        )
    }
}

private val MONTHS = listOf(
    "janeiro", "fevereiro", "março", "abril", "maio", "junho",
    "julho", "agosto", "setembro", "outubro", "novembro", "dezembro"
)

private fun formatDate(epochMillis: Long): String {
    val c = dateComponentsOf(epochMillis)
    val short = listOf("jan", "fev", "mar", "abr", "mai", "jun", "jul", "ago", "set", "out", "nov", "dez")
    return "${c.day} ${short[c.monthIndex]}"
}

private fun countFor(items: List<TimelineItem>, filter: MomentsFilter): Int = when (filter) {
    MomentsFilter.TODOS -> items.size
    MomentsFilter.COM_FOTO -> items.count { it is TimelineItem.Event }
    MomentsFilter.SO_TEXTO -> items.count {
        it is TimelineItem.Moment && (it.entry.type == MomentType.DAILY || it.entry.type == MomentType.WEEKLY)
    }
    MomentsFilter.MARCO -> items.count { it.isMilestone }
}
