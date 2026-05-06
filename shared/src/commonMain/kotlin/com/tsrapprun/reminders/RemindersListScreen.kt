/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  RemindersListScreen.kt — lista de lembretes salvos.         ║
 * ║                                                              ║
 * ║  Cards estilo "post-it" (Butter sutil), com checkbox pra     ║
 * ║  marcar como completo. Long-press → excluir.                 ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.reminders

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tsrapprun.platform.dateComponentsOf
import com.tsrapprun.ui.chrome.Butter
import com.tsrapprun.ui.chrome.OliveDeep
import com.tsrapprun.ui.chrome.ScreenHeader
import com.tsrapprun.ui.chrome.Tag
import com.tsrapprun.ui.chrome.italicSerifText
import com.tsrapprun.ui.theme.CozyAmberDeep
import com.tsrapprun.ui.theme.CozyCream
import com.tsrapprun.ui.theme.CozyCreamDeep
import com.tsrapprun.ui.theme.CozyOlive

@Composable
fun RemindersListScreen(
    reminders: List<Reminder>,
    onAdd: () -> Unit,
    onToggleComplete: (Reminder) -> Unit,
    onDelete: (String) -> Unit,
    onBack: () -> Unit,
    highlightId: String? = null
) {
    var showDeleteId by remember { mutableStateOf<String?>(null) }
    val listState = androidx.compose.foundation.lazy.rememberLazyListState()
    val sortedAll = remember(reminders) {
        val (pending, done) = reminders.partition { !it.completed }
        pending + done
    }
    // Auto-scroll pra item destacado
    androidx.compose.runtime.LaunchedEffect(highlightId, sortedAll) {
        if (highlightId != null) {
            val idx = sortedAll.indexOfFirst { it.id == highlightId }
            if (idx >= 0) {
                // +1 pra contar com o cabeçalho de seção
                listState.animateScrollToItem(idx + 1)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(CozyCream)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            ScreenHeader(
                chapter = "vol. 01 — lembretes",
                title = italicSerifText(
                    prefix = "seus ",
                    italic = "lembretes",
                    italicColor = CozyAmberDeep,
                    defaultColor = OliveDeep
                ),
                subtitle = "coisas que você não pode esquecer ✦",
                onBack = onBack,
                rightContent = {
                    Surface(
                        modifier = Modifier.size(32.dp).clickable(onClick = onAdd),
                        shape = CircleShape,
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

            if (reminders.isEmpty()) {
                EmptyState(onAdd = onAdd)
            } else {
                val grouped = reminders.partition { !it.completed }
                val pending = grouped.first
                val done = grouped.second
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize().padding(horizontal = 22.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 24.dp)
                ) {
                    if (pending.isNotEmpty()) {
                        item("h-pending") { SectionLabel("pendentes") }
                        items(pending) { r ->
                            ReminderCard(
                                reminder = r,
                                isHighlighted = r.id == highlightId,
                                onToggle = { onToggleComplete(r) },
                                onLongPress = { showDeleteId = r.id }
                            )
                            Spacer(Modifier.height(10.dp))
                        }
                    }
                    if (done.isNotEmpty()) {
                        item("h-done") {
                            Spacer(Modifier.height(8.dp))
                            SectionLabel("já feito")
                            Text(
                                "segure um lembrete pra excluir",
                                fontFamily = FontFamily.Serif,
                                fontStyle = FontStyle.Italic,
                                fontSize = 11.sp,
                                color = CozyOlive.copy(alpha = 0.6f),
                                modifier = Modifier.padding(start = 28.dp, bottom = 8.dp)
                            )
                        }
                        items(done) { r ->
                            ReminderCard(
                                reminder = r,
                                isHighlighted = r.id == highlightId,
                                onToggle = { onToggleComplete(r) },
                                onLongPress = { showDeleteId = r.id }
                            )
                            Spacer(Modifier.height(10.dp))
                        }
                    }
                }
            }
        }
    }

    if (showDeleteId != null) {
        val id = showDeleteId!!
        AlertDialog(
            onDismissRequest = { showDeleteId = null },
            containerColor = CozyCream,
            shape = RoundedCornerShape(20.dp),
            title = { Text("excluir lembrete?", fontWeight = FontWeight.SemiBold, color = OliveDeep) },
            text = { Text("essa ação não pode ser desfeita.", color = OliveDeep) },
            confirmButton = {
                TextButton(onClick = {
                    onDelete(id)
                    showDeleteId = null
                }) { Text("excluir", color = Color(0xFFB85450), fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteId = null }) {
                    Text("cancelar", color = OliveDeep)
                }
            }
        )
    }
}

private inline fun <T> LazyListScopeShim(items: List<T>, key: (T) -> String, crossinline content: (T) -> Unit) = Unit

private fun androidx.compose.foundation.lazy.LazyListScope.items(list: List<Reminder>, content: @Composable (Reminder) -> Unit) {
    items(count = list.size, key = { i -> list[i].id }) { i -> content(list[i]) }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ReminderCard(
    reminder: Reminder,
    isHighlighted: Boolean = false,
    onToggle: () -> Unit,
    onLongPress: () -> Unit
) {
    val borderColor = when {
        isHighlighted -> CozyAmberDeep
        reminder.completed -> CozyAmberDeep.copy(alpha = 0.15f)
        else -> CozyAmberDeep.copy(alpha = 0.35f)
    }
    val borderWidth = if (isHighlighted) 2.4.dp else 1.4.dp
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(onClick = onToggle, onLongClick = onLongPress),
        shape = RoundedCornerShape(14.dp),
        color = if (reminder.completed) CozyCreamDeep else Butter.copy(alpha = 0.55f),
        border = BorderStroke(borderWidth, borderColor),
        shadowElevation = if (isHighlighted) 8.dp else 0.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox circular
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(if (reminder.completed) OliveDeep else Color.Transparent)
                    .let {
                        if (!reminder.completed) it
                        else it
                    },
                contentAlignment = Alignment.Center
            ) {
                if (reminder.completed) {
                    Text("✓", color = CozyCream, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                } else {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(Color.Transparent)
                    )
                    // Borda manual via Surface
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    reminder.text,
                    fontFamily = FontFamily.Serif,
                    fontStyle = if (reminder.completed) FontStyle.Italic else FontStyle.Normal,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (reminder.completed) CozyOlive.copy(alpha = 0.6f) else OliveDeep,
                    textDecoration = if (reminder.completed) TextDecoration.LineThrough else TextDecoration.None,
                    lineHeight = 20.sp
                )
                Spacer(Modifier.height(4.dp))
                if (reminder.dueAt != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🗓", fontSize = 11.sp)
                        Spacer(Modifier.width(4.dp))
                        Text(
                            formatDueLabel(reminder.dueAt),
                            fontFamily = FontFamily.Serif,
                            fontStyle = FontStyle.Italic,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (reminder.completed) CozyOlive.copy(alpha = 0.6f) else CozyAmberDeep
                        )
                    }
                } else {
                    Text(
                        formatRelativeDate(reminder.createdAt),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.5.sp,
                        color = CozyOlive.copy(alpha = 0.6f),
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}

private fun formatDueLabel(dueAt: Long): String {
    val now = com.tsrapprun.platform.nowMillis()
    val diffDays = ((dueAt - now) / (24L * 60 * 60 * 1000)).toInt()
    val short = listOf("jan", "fev", "mar", "abr", "mai", "jun", "jul", "ago", "set", "out", "nov", "dez")
    val c = dateComponentsOf(dueAt)
    return when {
        diffDays < 0 -> "atrasado · ${c.day} ${short[c.monthIndex]}"
        diffDays == 0 -> "hoje"
        diffDays == 1 -> "amanhã"
        diffDays in 2..6 -> "em $diffDays dias · ${c.day} ${short[c.monthIndex]}"
        else -> "${c.day} ${short[c.monthIndex]}"
    }
}

@Composable
private fun SectionLabel(label: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 6.dp, bottom = 8.dp),
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
private fun EmptyState(onAdd: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            italicSerifText(prefix = "ainda sem ", italic = "lembretes", italicColor = CozyAmberDeep, defaultColor = OliveDeep),
            fontSize = 22.sp,
            letterSpacing = (-0.4).sp
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "toque em + acima e fale o que não pode esquecer",
            fontFamily = FontFamily.Serif,
            fontStyle = FontStyle.Italic,
            fontSize = 13.sp,
            color = CozyOlive.copy(alpha = 0.7f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

private fun formatRelativeDate(epochMillis: Long): String {
    val c = dateComponentsOf(epochMillis)
    val short = listOf("jan", "fev", "mar", "abr", "mai", "jun", "jul", "ago", "set", "out", "nov", "dez")
    return "${c.day} ${short[c.monthIndex]} · ${c.hour.toString().padStart(2, '0')}h${c.minute.toString().padStart(2, '0')}"
}
