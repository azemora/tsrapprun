/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  CalendarScreen.kt — tradução de fullapp/screens/calendar.jsx║
 * ║                                                              ║
 * ║  Header editorial "abril 2025" com chevrons.                 ║
 * ║  Grid de dias coloridos por tipo (mesversário/feriado/evento)║
 * ║  + legenda + lista "próximos".                               ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.calendar

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tsrapprun.platform.dateComponentsOf
import com.tsrapprun.platform.nowMillis
import com.tsrapprun.ui.chrome.Butter
import com.tsrapprun.ui.chrome.OliveDeep
import com.tsrapprun.ui.chrome.Pastels
import com.tsrapprun.ui.chrome.Peach
import com.tsrapprun.ui.chrome.ScreenHeader
import com.tsrapprun.ui.chrome.Tag
import com.tsrapprun.ui.chrome.italicSerifText
import com.tsrapprun.ui.theme.CozyAmber
import com.tsrapprun.ui.theme.CozyAmberDeep
import com.tsrapprun.ui.theme.CozyCream
import com.tsrapprun.ui.theme.CozyCreamDeep
import com.tsrapprun.ui.theme.CozyOlive
import com.tsrapprun.ui.theme.CozySage
import com.tsrapprun.ui.theme.CozySageMist

private enum class DayKind { MESVERSARIO, FERIADO, EVENTO, LEMBRETE }
private data class CalendarMark(
    val type: DayKind,
    val label: String,
    val reminderId: String? = null,
    val eventId: String? = null,
    val mesversarioMonth: Int? = null
)

private val MONTH_NAMES = listOf(
    "janeiro", "fevereiro", "março", "abril", "maio", "junho",
    "julho", "agosto", "setembro", "outubro", "novembro", "dezembro"
)
private val MONTH_SHORT = listOf(
    "jan", "fev", "mar", "abr", "mai", "jun", "jul", "ago", "set", "out", "nov", "dez"
)

private val BR_HOLIDAYS: Map<Int, List<Pair<Int, String>>> = mapOf(
    0 to listOf(1 to "Confraternização"),
    3 to listOf(21 to "Tiradentes"),
    4 to listOf(1 to "Dia do Trabalho"),
    8 to listOf(7 to "Independência"),
    9 to listOf(12 to "N. Sra. Aparecida"),
    10 to listOf(2 to "Finados", 15 to "Proclamação", 20 to "Consciência Negra"),
    11 to listOf(25 to "Natal")
)

@Composable
fun CalendarScreen(
    onBack: () -> Unit,
    highlightMillis: Long? = null,
    reminders: List<com.tsrapprun.reminders.Reminder> = emptyList(),
    events: List<com.tsrapprun.camera.EventData> = emptyList(),
    moments: List<com.tsrapprun.moments.MomentEntry> = emptyList(),
    onOpenReminder: (reminderId: String) -> Unit = {},
    onOpenEvent: (com.tsrapprun.camera.EventData) -> Unit = {},
    onOpenMesversario: (monthsCompleted: Int) -> Unit = {}
) {
    val today = remember { dateComponentsOf(nowMillis()) }
    val highlight = remember(highlightMillis) {
        highlightMillis?.let { dateComponentsOf(it) }
    }
    // Se houver destaque, abre o calendário no mês/ano dele.
    var displayedMonth by remember { mutableStateOf(highlight?.monthIndex ?: today.monthIndex) }
    var displayedYear by remember { mutableStateOf(highlight?.year ?: today.year) }

    val marks = remember(displayedMonth, displayedYear, reminders, events, moments) {
        buildMap<Int, CalendarMark> {
            // Camada 1: feriados (base)
            BR_HOLIDAYS[displayedMonth].orEmpty().forEach { (day, name) ->
                put(day, CalendarMark(DayKind.FERIADO, name))
            }
            // Camada 2: eventos (registros com fotos) — sobrescreve feriado
            events.forEach { event ->
                val c = dateComponentsOf(event.createdAt)
                if (c.year == displayedYear && c.monthIndex == displayedMonth) {
                    put(c.day, CalendarMark(
                        DayKind.EVENTO, event.name.take(40), eventId = event.id
                    ))
                }
            }
            // Camada 3: mesversários (auto-gerados) — sobrescreve evento
            moments.forEach { m ->
                if (m.type != com.tsrapprun.moments.MomentType.MESVERSARIO) return@forEach
                val c = dateComponentsOf(m.createdAt)
                if (c.year == displayedYear && c.monthIndex == displayedMonth) {
                    put(c.day, CalendarMark(
                        DayKind.MESVERSARIO,
                        "${m.milestoneNumber} ${if (m.milestoneNumber == 1) "mês" else "meses"}",
                        mesversarioMonth = m.milestoneNumber
                    ))
                }
            }
            // Camada 4: lembretes (alta prioridade — mais recente do usuário)
            reminders.forEach { r ->
                val due = r.dueAt ?: return@forEach
                val c = dateComponentsOf(due)
                if (c.year == displayedYear && c.monthIndex == displayedMonth) {
                    put(c.day, CalendarMark(DayKind.LEMBRETE, r.text.take(40), reminderId = r.id))
                }
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
                .verticalScroll(rememberScrollState())
        ) {
            ScreenHeader(
                chapter = "vol. 01 — calendário",
                title = italicSerifText(
                    prefix = "${MONTH_NAMES[displayedMonth]} ",
                    italic = "$displayedYear",
                    italicColor = CozyAmberDeep,
                    defaultColor = OliveDeep
                ),
                onBack = onBack,
                rightContent = {
                    Row {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clickable {
                                    if (displayedMonth == 0) {
                                        displayedMonth = 11; displayedYear -= 1
                                    } else displayedMonth -= 1
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("‹", fontSize = 22.sp, fontWeight = FontWeight.Light, color = OliveDeep)
                        }
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clickable {
                                    if (displayedMonth == 11) {
                                        displayedMonth = 0; displayedYear += 1
                                    } else displayedMonth += 1
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("›", fontSize = 22.sp, fontWeight = FontWeight.Light, color = OliveDeep)
                        }
                    }
                }
            )

            // Grid
            Column(modifier = Modifier.padding(horizontal = 22.dp)) {
                WeekdayHeader()
                Spacer(Modifier.height(6.dp))
                MonthGrid(
                    year = displayedYear,
                    monthIndex = displayedMonth,
                    today = today,
                    marks = marks,
                    highlight = highlight,
                    onMarkClick = { mark ->
                        when {
                            mark.reminderId != null -> onOpenReminder(mark.reminderId)
                            mark.mesversarioMonth != null -> onOpenMesversario(mark.mesversarioMonth)
                            mark.eventId != null -> {
                                events.find { it.id == mark.eventId }?.let { onOpenEvent(it) }
                            }
                            else -> { /* feriado: sem ação */ }
                        }
                    }
                )
            }

            Spacer(Modifier.height(16.dp))

            // Legend
            Row(
                modifier = Modifier.padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                LegendItem(color = Butter, label = "mesversário")
                LegendItem(color = Peach, label = "feriado")
                LegendItem(color = CozySage, label = "lembrete")
            }

            Spacer(Modifier.height(20.dp))

            // Próximos
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(Modifier.width(14.dp).height(1.dp).background(CozyOlive.copy(alpha = 0.4f)))
                Spacer(Modifier.width(10.dp))
                Tag("próximos", color = OliveDeep)
                Spacer(Modifier.width(10.dp))
                Box(Modifier.weight(1f).height(1.dp).background(CozyOlive.copy(alpha = 0.18f)))
            }

            Spacer(Modifier.height(10.dp))

            BR_HOLIDAYS[displayedMonth].orEmpty().forEach { (day, name) ->
                UpcomingRow(
                    dateLabel = "${MONTH_SHORT[displayedMonth]} ${day.toString().padStart(2, '0')}",
                    title = name,
                    kind = "feriado",
                    accent = Peach
                )
                Spacer(Modifier.height(10.dp))
            }

            Spacer(Modifier.height(80.dp))
        }
    }
}

@Composable
private fun WeekdayHeader() {
    val days = listOf("d", "s", "t", "q", "q", "s", "s")
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
        days.forEach { d ->
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    d.uppercase(),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.5.sp,
                    color = CozyOlive.copy(alpha = 0.6f),
                    letterSpacing = 1.4.sp
                )
            }
        }
    }
}

@Composable
private fun MonthGrid(
    year: Int,
    monthIndex: Int,
    today: com.tsrapprun.platform.DateTimeComponents,
    marks: Map<Int, CalendarMark>,
    highlight: com.tsrapprun.platform.DateTimeComponents?,
    onMarkClick: (CalendarMark) -> Unit
) {
    val firstWeekday = remember(year, monthIndex) { zellerWeekday(year, monthIndex + 1, 1) }
    val daysInMonth = remember(year, monthIndex) { daysInMonth(year, monthIndex) }
    val cells = mutableListOf<Int?>()
    repeat(firstWeekday) { cells.add(null) }
    for (d in 1..daysInMonth) cells.add(d)
    while (cells.size % 7 != 0) cells.add(null)

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        cells.chunked(7).forEach { row ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                row.forEach { day ->
                    val mark = day?.let { marks[it] }
                    val isClickable = mark != null && mark.type != DayKind.FERIADO
                    DayCell(
                        modifier = Modifier.weight(1f),
                        day = day,
                        isToday = day == today.day && monthIndex == today.monthIndex && year == today.year,
                        isHighlighted = day != null && highlight != null &&
                                day == highlight.day &&
                                monthIndex == highlight.monthIndex &&
                                year == highlight.year,
                        mark = mark,
                        onClick = if (isClickable) {
                            { onMarkClick(mark!!) }
                        } else null
                    )
                }
            }
        }
    }
}

@Composable
private fun DayCell(
    modifier: Modifier,
    day: Int?,
    isToday: Boolean,
    isHighlighted: Boolean = false,
    mark: CalendarMark?,
    onClick: (() -> Unit)? = null
) {
    val bg = when {
        isHighlighted -> Butter
        isToday -> OliveDeep
        mark?.type == DayKind.MESVERSARIO -> Butter
        mark?.type == DayKind.FERIADO -> Peach
        mark?.type == DayKind.EVENTO -> CozySageMist
        mark?.type == DayKind.LEMBRETE -> CozySage
        else -> Color.Transparent
    }
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(10.dp))
            .background(bg)
            .let { m -> if (onClick != null) m.clickable(onClick = onClick) else m }
            .let { m ->
                when {
                    isHighlighted -> m.border(
                        BorderStroke(2.dp, CozyAmberDeep),
                        RoundedCornerShape(10.dp)
                    )
                    mark != null && !isToday -> m.border(
                        BorderStroke(1.2.dp, CozyOlive.copy(alpha = 0.4f)),
                        RoundedCornerShape(10.dp)
                    )
                    else -> m
                }
            },
        contentAlignment = Alignment.Center
    ) {
        if (day != null) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "$day",
                    fontFamily = FontFamily.Serif,
                    fontSize = if (isHighlighted) 17.sp else 15.sp,
                    fontWeight = if (isToday || isHighlighted) FontWeight.Bold else FontWeight.Medium,
                    color = when {
                        isHighlighted -> CozyAmberDeep
                        isToday -> CozyCream
                        else -> OliveDeep
                    }
                )
                if (isHighlighted) {
                    Spacer(Modifier.height(1.dp))
                    Text(
                        "✦",
                        fontSize = 9.sp,
                        color = CozyAmberDeep
                    )
                } else if (mark != null && !isToday) {
                    Spacer(Modifier.height(1.dp))
                    Box(
                        Modifier.size(4.dp).clip(RoundedCornerShape(2.dp)).background(CozyAmberDeep)
                    )
                }
            }
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .size(width = 10.dp, height = 10.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(color)
                .border(BorderStroke(1.dp, OliveDeep.copy(alpha = 0.2f)), RoundedCornerShape(3.dp))
        )
        Spacer(Modifier.width(6.dp))
        Text(
            label,
            fontSize = 11.sp,
            color = CozyOlive.copy(alpha = 0.8f)
        )
    }
}

@Composable
private fun UpcomingRow(dateLabel: String, title: String, kind: String, accent: Color) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
        shape = RoundedCornerShape(14.dp),
        color = CozyCreamDeep,
        border = BorderStroke(1.4.dp, OliveDeep.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(accent.copy(alpha = 0.4f))
                    .border(BorderStroke(1.4.dp, accent), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    dateLabel,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = CozyAmberDeep,
                    textAlign = TextAlign.Center,
                    lineHeight = 11.sp
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Tag(kind, color = CozyAmberDeep)
                Spacer(Modifier.height(2.dp))
                Text(
                    title,
                    fontFamily = FontFamily.Serif,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = OliveDeep,
                    letterSpacing = (-0.3).sp
                )
            }
            Text("›", fontSize = 18.sp, color = CozyOlive, fontWeight = FontWeight.Light)
        }
    }
}

private fun daysInMonth(year: Int, monthIndex: Int): Int = when (monthIndex) {
    0, 2, 4, 6, 7, 9, 11 -> 31
    3, 5, 8, 10 -> 30
    1 -> if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) 29 else 28
    else -> 30
}

private fun zellerWeekday(year: Int, month1Based: Int, day: Int): Int {
    var m = month1Based
    var y = year
    if (m < 3) { m += 12; y -= 1 }
    val K = y % 100
    val J = y / 100
    val h = (day + (13 * (m + 1)) / 5 + K + K / 4 + J / 4 + 5 * J) % 7
    return ((h + 6) % 7)
}
