/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  MomentDetailScreen.kt — detalhe full-screen de um           ║
 * ║  registro de texto (DAILY/WEEKLY).                           ║
 * ║                                                              ║
 * ║  Layout: header editorial + título italic Fraunces +         ║
 * ║  nota em card Caveat + footer com excluir.                   ║
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tsrapprun.platform.dateComponentsOf
import com.tsrapprun.ui.chrome.OliveDeep
import com.tsrapprun.ui.chrome.ScreenHeader
import com.tsrapprun.ui.chrome.Tag
import com.tsrapprun.ui.chrome.italicSerifText
import com.tsrapprun.ui.theme.CozyAmberDeep
import com.tsrapprun.ui.theme.CozyCream
import com.tsrapprun.ui.theme.CozyCreamDeep
import com.tsrapprun.ui.theme.CozyOlive

@Composable
fun MomentDetailScreen(
    moment: MomentEntry,
    onBack: () -> Unit,
    onDelete: () -> Unit
) {
    var showConfirmDelete by remember { mutableStateOf(false) }

    // Tenta separar título e nota se o texto foi salvo no formato "título — nota".
    val (titleText, noteText) = remember(moment) {
        val parts = moment.text.split(" — ", limit = 2)
        if (parts.size == 2) parts[0] to parts[1] else moment.text to ""
    }

    val typeLabel = if (moment.type == MomentType.WEEKLY) "semanal" else "diário"

    Box(modifier = Modifier.fillMaxSize().background(CozyCream)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            ScreenHeader(
                chapter = "$typeLabel · ${formatLongDate(moment.createdAt)}",
                title = italicSerifText(
                    prefix = if (titleText.isBlank()) "" else "",
                    italic = titleText.ifBlank { typeLabel },
                    italicColor = CozyAmberDeep,
                    defaultColor = OliveDeep
                ),
                onBack = onBack
            )

            // Nota em card Caveat (estilo "EventNoteCard")
            if (noteText.isNotBlank()) {
                Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = CozyCreamDeep,
                        border = BorderStroke(1.4.dp, CozyOlive.copy(alpha = 0.33f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Tag("nota do dia", color = OliveDeep)
                            Spacer(Modifier.height(8.dp))
                            Text(
                                noteText,
                                fontFamily = FontFamily.Cursive,
                                fontSize = 22.sp,
                                color = OliveDeep,
                                lineHeight = 26.sp
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Stats: tipo + data
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatChip(
                    modifier = Modifier.weight(1f),
                    label = "tipo",
                    value = typeLabel
                )
                StatChip(
                    modifier = Modifier.weight(1f),
                    label = "registrado em",
                    value = formatShortDate(moment.createdAt)
                )
            }

            Spacer(Modifier.height(24.dp))
        }

        // Footer com excluir (sutil, rodapé direito)
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Surface(
                modifier = Modifier.clickable { showConfirmDelete = true },
                shape = RoundedCornerShape(999.dp),
                color = Color.Transparent,
                border = BorderStroke(1.dp, Color(0xFFB85450).copy(alpha = 0.5f))
            ) {
                Text(
                    "excluir registro",
                    fontFamily = FontFamily.Serif,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFB85450),
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                )
            }
        }
    }

    if (showConfirmDelete) {
        AlertDialog(
            onDismissRequest = { showConfirmDelete = false },
            containerColor = CozyCream,
            shape = RoundedCornerShape(20.dp),
            title = { Text("excluir registro?", fontWeight = FontWeight.SemiBold, color = OliveDeep) },
            text = { Text("essa ação não pode ser desfeita.", color = OliveDeep) },
            confirmButton = {
                TextButton(onClick = {
                    showConfirmDelete = false
                    onDelete()
                }) {
                    Text("excluir", color = Color(0xFFB85450), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDelete = false }) {
                    Text("cancelar", color = OliveDeep)
                }
            }
        )
    }
}

@Composable
private fun StatChip(modifier: Modifier, label: String, value: String) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = CozyCreamDeep,
        border = BorderStroke(1.4.dp, CozyOlive.copy(alpha = 0.18f))
    ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
            Tag(label, color = OliveDeep)
            Spacer(Modifier.height(2.dp))
            Text(
                value,
                fontFamily = FontFamily.Serif,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = OliveDeep,
                letterSpacing = (-0.3).sp
            )
        }
    }
}

private fun formatLongDate(epochMillis: Long): String {
    val c = dateComponentsOf(epochMillis)
    val long = listOf(
        "janeiro", "fevereiro", "março", "abril", "maio", "junho",
        "julho", "agosto", "setembro", "outubro", "novembro", "dezembro"
    )
    return "${c.day} de ${long[c.monthIndex]}"
}

private fun formatShortDate(epochMillis: Long): String {
    val c = dateComponentsOf(epochMillis)
    val short = listOf("jan", "fev", "mar", "abr", "mai", "jun", "jul", "ago", "set", "out", "nov", "dez")
    return "${c.day} ${short[c.monthIndex]} ${c.year}"
}
