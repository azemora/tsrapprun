/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  MomentsListScreen.kt - Aba "Registros"                     ║
 * ║                                                             ║
 * ║  Exibe todos os registros de momentos do usuário em         ║
 * ║  ordem cronológica inversa (mais recente primeiro).          ║
 * ║  Cards com tipo (diário/semanal), data e texto.             ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.moments

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MomentsListScreen(
    moments: List<MomentEntry>,
    onAddDaily: () -> Unit,
    onAddWeekly: () -> Unit,
    onDeleteMoment: (String) -> Unit,
    onBack: () -> Unit
) {
    val sortedMoments = remember(moments) {
        moments.sortedByDescending { it.createdAt }
    }

    var showDeleteDialog by remember { mutableStateOf<String?>(null) }
    var showAddOptions by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.statusBars)
            ) {
                // ── Header ──
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onBack) {
                        Text("\u2190 Voltar", style = MaterialTheme.typography.titleMedium)
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        "Registros",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(80.dp))
                }

                if (sortedMoments.isEmpty()) {
                    // ── Estado vazio ──
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "\uD83D\uDCDD",
                                fontSize = 48.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Nenhum registro ainda",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Toque no + para registrar\no que est\u00e1 acontecendo!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                textAlign = TextAlign.Center
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
                                onDelete = { showDeleteDialog = moment.id }
                            )
                        }
                        item { Spacer(modifier = Modifier.height(80.dp)) }
                    }
                }
            }

            // ── FAB: adicionar registro ──
            FloatingActionButton(
                onClick = { showAddOptions = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Text("+", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
        }
    }

    // ── Diálogo: escolher tipo de registro ──
    if (showAddOptions) {
        AlertDialog(
            onDismissRequest = { showAddOptions = false },
            title = { Text("Novo Registro") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            showAddOptions = false
                            onAddDaily()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("\uD83D\uDDD3\uFE0F  O que aconteceu hoje?")
                    }
                    Button(
                        onClick = {
                            showAddOptions = false
                            onAddWeekly()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("\uD83D\uDCDD  O que aconteceu essa semana?")
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showAddOptions = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // ── Diálogo: confirmar exclusão ──
    if (showDeleteDialog != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Excluir registro?") },
            text = { Text("Esta a\u00e7\u00e3o n\u00e3o pode ser desfeita.") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog?.let { onDeleteMoment(it) }
                        showDeleteDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Excluir")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

// ═══════════════════════════════════════════════════
// CARD DE MOMENTO
// ═══════════════════════════════════════════════════

@Composable
private fun MomentCard(
    moment: MomentEntry,
    onDelete: () -> Unit
) {
    val typeLabel = when (moment.type) {
        MomentType.DAILY -> "Di\u00e1rio"
        MomentType.WEEKLY -> "Semanal"
    }
    val typeEmoji = when (moment.type) {
        MomentType.DAILY -> "\uD83D\uDDD3\uFE0F"
        MomentType.WEEKLY -> "\uD83D\uDCDD"
    }
    val typeColor = when (moment.type) {
        MomentType.DAILY -> Color(0xFF4CAF50)
        MomentType.WEEKLY -> Color(0xFF2196F3)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // ── Header do card: tipo + data + delete ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Badge de tipo
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(typeColor.copy(alpha = 0.1f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "$typeEmoji $typeLabel",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = typeColor
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = formatMomentDate(moment.createdAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )

                // Botão deletar
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Text(
                        text = "\u2715",  // ✕
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ── Texto do momento ──
            Text(
                text = moment.text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 8,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

private fun formatMomentDate(epochMillis: Long): String {
    val cal = java.util.Calendar.getInstance().apply { timeInMillis = epochMillis }
    val d = cal.get(java.util.Calendar.DAY_OF_MONTH).toString().padStart(2, '0')
    val m = (cal.get(java.util.Calendar.MONTH) + 1).toString().padStart(2, '0')
    val y = cal.get(java.util.Calendar.YEAR)
    val h = cal.get(java.util.Calendar.HOUR_OF_DAY).toString().padStart(2, '0')
    val min = cal.get(java.util.Calendar.MINUTE).toString().padStart(2, '0')
    return "$d/$m/$y $h:$min"
}
