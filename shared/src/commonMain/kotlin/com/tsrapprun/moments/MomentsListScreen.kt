/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  MomentsListScreen.kt - Aba "Registros" (paleta cozy)        ║
 * ║                                                              ║
 * ║  Exibe 4 tipos de entries:                                   ║
 * ║   • DAILY (manual diário)                                    ║
 * ║   • WEEKLY (manual semanal)                                  ║
 * ║   • WEEK_OF_LIFE (auto: semana N — destaque sage)            ║
 * ║   • MESVERSARIO (auto: mesversário — destaque dourado)      ║
 * ║                                                              ║
 * ║  Auto-entries não podem ser deletadas pelo usuário.          ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.moments

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tsrapprun.ui.theme.CozyAmber
import com.tsrapprun.ui.theme.CozyCream
import com.tsrapprun.ui.theme.CozyGold
import com.tsrapprun.ui.theme.CozyInk
import com.tsrapprun.ui.theme.CozyOlive
import com.tsrapprun.ui.theme.CozySage
import com.tsrapprun.ui.theme.CozySageMist
import com.tsrapprun.ui.theme.CozyTan

@Composable
fun MomentsListScreen(
    moments: List<MomentEntry>,
    onAddDaily: () -> Unit,
    onAddWeekly: () -> Unit,
    onDeleteMoment: (String) -> Unit,
    onOpenMesversario: ((Int) -> Unit)? = null,
    onBack: () -> Unit
) {
    val sortedMoments = remember(moments) {
        moments.sortedByDescending { it.createdAt }
    }

    var showDeleteDialog by remember { mutableStateOf<String?>(null) }
    var showAddOptions by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = CozySageMist
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.safeDrawing)
            ) {
                // ── Header ──
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(40.dp).clickable(onClick = onBack),
                        shape = CircleShape,
                        color = CozyCream,
                        border = androidx.compose.foundation.BorderStroke(1.dp, CozyTan)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("←", fontSize = 18.sp, color = CozyOlive)
                        }
                    }
                    Spacer(Modifier.weight(1f))
                    Text(
                        "registros",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = CozyOlive
                    )
                    Spacer(Modifier.weight(1f))
                    Spacer(Modifier.size(40.dp))
                }

                if (sortedMoments.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("📝", fontSize = 56.sp)
                            Spacer(Modifier.height(16.dp))
                            Text(
                                "nenhum registro ainda",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = CozyOlive
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                "toque no + para registrar\no que está acontecendo!",
                                fontSize = 13.sp,
                                color = CozyOlive.copy(alpha = 0.65f),
                                textAlign = TextAlign.Center,
                                lineHeight = 19.sp
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(sortedMoments, key = { it.id }) { moment ->
                            MomentCard(
                                moment = moment,
                                onDelete = if (moment.type == MomentType.DAILY || moment.type == MomentType.WEEKLY) {
                                    { showDeleteDialog = moment.id }
                                } else null,
                                onOpenMesversario = onOpenMesversario
                            )
                        }
                        item { Spacer(Modifier.height(80.dp)) }
                    }
                }
            }

            // ── FAB ──
            FloatingActionButton(
                onClick = { showAddOptions = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp),
                containerColor = CozyAmber,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Text("+", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
        }
    }

    if (showAddOptions) {
        AlertDialog(
            onDismissRequest = { showAddOptions = false },
            title = { Text("novo registro", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { showAddOptions = false; onAddDaily() },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CozySage)
                    ) {
                        Text("🗓️  o que aconteceu hoje?")
                    }
                    Button(
                        onClick = { showAddOptions = false; onAddWeekly() },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CozyAmber)
                    ) {
                        Text("📝  o que aconteceu essa semana?")
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showAddOptions = false }) {
                    Text("cancelar", color = CozyOlive)
                }
            }
        )
    }

    if (showDeleteDialog != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("excluir registro?") },
            text = { Text("essa ação não pode ser desfeita.") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog?.let { onDeleteMoment(it) }
                        showDeleteDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB85450))
                ) {
                    Text("excluir")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("cancelar", color = CozyOlive)
                }
            }
        )
    }
}

// ═══════════════════════════════════════════════════
// CARDS POR TIPO
// ═══════════════════════════════════════════════════

@Composable
private fun MomentCard(
    moment: MomentEntry,
    onDelete: (() -> Unit)? = null,
    onOpenMesversario: ((Int) -> Unit)? = null
) {
    when (moment.type) {
        MomentType.MESVERSARIO -> MesversarioCard(moment, onOpenMesversario)
        MomentType.WEEK_OF_LIFE -> WeekOfLifeCard(moment)
        MomentType.DAILY -> ManualCard(
            moment = moment,
            badgeLabel = "diário",
            badgeEmoji = "🗓️",
            badgeColor = CozySage,
            onDelete = onDelete
        )
        MomentType.WEEKLY -> ManualCard(
            moment = moment,
            badgeLabel = "semanal",
            badgeEmoji = "📝",
            badgeColor = CozyAmber,
            onDelete = onDelete
        )
    }
}

@Composable
private fun MesversarioCard(moment: MomentEntry, onOpenMesversario: ((Int) -> Unit)?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onOpenMesversario != null) {
                onOpenMesversario?.invoke(moment.milestoneNumber)
            },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(colors = listOf(CozyGold, CozyAmber))
                )
                .padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.22f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🎉", fontSize = 28.sp)
                }
                Spacer(Modifier.width(14.dp))
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "mesversário",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        moment.text,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "toque para ver a comemoração",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }
        }
    }
}

@Composable
private fun WeekOfLifeCard(moment: MomentEntry) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CozySage.copy(alpha = 0.18f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CozySage.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(CozySage),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "${moment.milestoneNumber}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "marco semanal",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = CozyOlive.copy(alpha = 0.7f)
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    moment.text,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = CozyOlive,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun ManualCard(
    moment: MomentEntry,
    badgeLabel: String,
    badgeEmoji: String,
    badgeColor: Color,
    onDelete: (() -> Unit)?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(badgeColor.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "$badgeEmoji $badgeLabel",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = badgeColor
                    )
                }
                Spacer(Modifier.weight(1f))
                Text(
                    text = formatMomentDate(moment.createdAt),
                    fontSize = 11.sp,
                    color = CozyOlive.copy(alpha = 0.55f)
                )
                if (onDelete != null) {
                    Spacer(Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .clickable(onClick = onDelete),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "✕",
                            fontSize = 13.sp,
                            color = CozyOlive.copy(alpha = 0.4f)
                        )
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
            Text(
                text = moment.text,
                fontSize = 14.sp,
                color = CozyInk,
                lineHeight = 20.sp,
                maxLines = 8,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

private fun formatMomentDate(epochMillis: Long): String {
    val c = com.tsrapprun.platform.dateComponentsOf(epochMillis)
    val d = c.day.toString().padStart(2, '0')
    val m = (c.monthIndex + 1).toString().padStart(2, '0')
    val y = c.year
    val h = c.hour.toString().padStart(2, '0')
    val min = c.minute.toString().padStart(2, '0')
    return "$d/$m/$y $h:$min"
}
