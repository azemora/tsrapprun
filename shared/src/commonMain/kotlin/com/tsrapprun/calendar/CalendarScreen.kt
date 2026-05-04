/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  CalendarScreen.kt — Calendário com feriados marcados        ║
 * ║                                                              ║
 * ║  Layout inspirado em calendários ilustrados:                 ║
 * ║   • Top: ilustração colorida com gradiente suave             ║
 * ║   • Meio: nome do mês em destaque                            ║
 * ║   • Grid: dias da semana + dias do mês                       ║
 * ║   • Domingos e feriados em destaque (vermelho-tijolo)        ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.calendar

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tsrapprun.platform.dateComponentsOf
import com.tsrapprun.platform.epochMillisFromComponents
import com.tsrapprun.platform.nowMillis
import com.tsrapprun.ui.theme.CozyAmber
import com.tsrapprun.ui.theme.CozyBrick
import com.tsrapprun.ui.theme.CozyCream
import com.tsrapprun.ui.theme.CozyCreamDeep
import com.tsrapprun.ui.theme.CozyGold
import com.tsrapprun.ui.theme.CozyInk
import com.tsrapprun.ui.theme.CozyOlive
import com.tsrapprun.ui.theme.CozySage
import com.tsrapprun.ui.theme.CozyTan

// ── Feriados nacionais brasileiros (fixos) ──
// monthIndex (0-based) → list of (day, holidayName)
private val BR_HOLIDAYS: Map<Int, List<Pair<Int, String>>> = mapOf(
    0 to listOf(1 to "Confraternização Universal"),
    3 to listOf(21 to "Tiradentes"),
    4 to listOf(1 to "Dia do Trabalho"),
    8 to listOf(7 to "Independência"),
    9 to listOf(12 to "Nossa Sra. Aparecida"),
    10 to listOf(2 to "Finados", 15 to "Proclamação da República", 20 to "Consciência Negra"),
    11 to listOf(25 to "Natal")
)

private val MONTH_NAMES = listOf(
    "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
    "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"
)

private val MONTH_GRADIENTS: List<List<Color>> = listOf(
    listOf(Color(0xFFE0EFCB), Color(0xFFC9DBE8)), // jan
    listOf(Color(0xFFF2D6D2), Color(0xFFE6CFE8)), // fev — carnaval
    listOf(Color(0xFFD4E0C9), Color(0xFFE0EFCB)), // mar
    listOf(Color(0xFFFCE4A7), Color(0xFFF2D6D2)), // abr
    listOf(Color(0xFFE0EFCB), Color(0xFFC9DBE8)), // mai
    listOf(Color(0xFFFCE4A7), Color(0xFFE0EFCB)), // jun
    listOf(Color(0xFFE2DAE9), Color(0xFFC9DBE8)), // jul
    listOf(Color(0xFFF2D6D2), Color(0xFFFCE4A7)), // ago
    listOf(Color(0xFFE0EFCB), Color(0xFFFCE4A7)), // set
    listOf(Color(0xFFE6CFE8), Color(0xFFF2D6D2)), // out
    listOf(Color(0xFFE2DAE9), Color(0xFFE6CFE8)), // nov
    listOf(Color(0xFFFCE4A7), Color(0xFFE6CFE8))  // dez
)

private val MONTH_EMOJIS = listOf("🎆", "🎭", "🌷", "🐰", "🌼", "🌽", "☀️", "🌻", "🍂", "🎃", "🌺", "🎄")

@Composable
fun CalendarScreen(onBack: () -> Unit) {
    val now = nowMillis()
    val today = remember(now) { dateComponentsOf(now) }
    var displayedMonth by remember { mutableStateOf(today.monthIndex) }
    var displayedYear by remember { mutableStateOf(today.year) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CozyCream)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .verticalScroll(rememberScrollState())
        ) {
            // Header com ilustração
            CalendarHeader(
                monthIndex = displayedMonth,
                year = displayedYear,
                onBack = onBack,
                onPrev = {
                    if (displayedMonth == 0) {
                        displayedMonth = 11
                        displayedYear -= 1
                    } else displayedMonth -= 1
                },
                onNext = {
                    if (displayedMonth == 11) {
                        displayedMonth = 0
                        displayedYear += 1
                    } else displayedMonth += 1
                }
            )

            // Card branco com calendário
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp, bottom = 24.dp),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CozyTan.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        MONTH_NAMES[displayedMonth].uppercase(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = CozyInk,
                        letterSpacing = 4.sp,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        "$displayedYear",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = CozyOlive.copy(alpha = 0.6f),
                        letterSpacing = 2.sp,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(20.dp))

                    DayOfWeekHeader()

                    Spacer(Modifier.height(8.dp))

                    MonthGrid(
                        year = displayedYear,
                        monthIndex = displayedMonth,
                        today = today
                    )

                    val holidays = BR_HOLIDAYS[displayedMonth].orEmpty()
                    if (holidays.isNotEmpty()) {
                        Spacer(Modifier.height(20.dp))
                        Text(
                            "Feriados",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = CozyOlive,
                            letterSpacing = 2.sp
                        )
                        Spacer(Modifier.height(10.dp))
                        holidays.forEach { (day, name) ->
                            HolidayRow(day = day, name = name)
                            Spacer(Modifier.height(6.dp))
                        }
                    }
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════
// HEADER
// ═══════════════════════════════════════════════════

@Composable
private fun CalendarHeader(
    monthIndex: Int,
    year: Int,
    onBack: () -> Unit,
    onPrev: () -> Unit,
    onNext: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 36.dp, bottomEnd = 36.dp))
            .background(Brush.verticalGradient(MONTH_GRADIENTS[monthIndex]))
            .padding(20.dp)
    ) {
        Column {
            // Top bar
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(38.dp).clickable(onClick = onBack),
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.7f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("←", fontSize = 18.sp, color = CozyInk)
                    }
                }
                Spacer(Modifier.weight(1f))
                Text(
                    "Calendário",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = CozyInk,
                    letterSpacing = 0.3.sp
                )
                Spacer(Modifier.weight(1f))
                Spacer(Modifier.size(38.dp))
            }

            Spacer(Modifier.height(20.dp))

            // Ilustração placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(MONTH_EMOJIS[monthIndex], fontSize = 84.sp)
            }

            Spacer(Modifier.height(12.dp))

            // Navegação mês
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavArrow(symbol = "‹", onClick = onPrev)
                Spacer(Modifier.weight(1f))
                Spacer(Modifier.weight(1f))
                NavArrow(symbol = "›", onClick = onNext)
            }
        }
    }
}

@Composable
private fun NavArrow(symbol: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.size(36.dp).clickable(onClick = onClick),
        shape = CircleShape,
        color = Color.White.copy(alpha = 0.7f)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                symbol,
                fontSize = 22.sp,
                fontWeight = FontWeight.Light,
                color = CozyInk
            )
        }
    }
}

// ═══════════════════════════════════════════════════
// GRID
// ═══════════════════════════════════════════════════

@Composable
private fun DayOfWeekHeader() {
    val days = listOf("D", "S", "T", "Q", "Q", "S", "S")
    Row(modifier = Modifier.fillMaxWidth()) {
        days.forEachIndexed { i, d ->
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    d,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (i == 0) CozyBrick else CozyOlive.copy(alpha = 0.7f),
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

@Composable
private fun MonthGrid(
    year: Int,
    monthIndex: Int,
    today: com.tsrapprun.platform.DateTimeComponents
) {
    val firstDayWeekday = remember(year, monthIndex) {
        // Calcula dia da semana do dia 1 do mês usando epoch
        // 0=Sun, 1=Mon, ... 6=Sat (igual ao iOS NSCalendar / Java Calendar - 1)
        val firstMs = epochMillisFromComponents(year, monthIndex, 1, 0, 0)
        // Reuse: precisamos do dia da semana — derivamos via dateComponentsOf
        // Como DateTimeComponents não expõe weekday, derivamos via Zeller's congruence
        zellerWeekday(year, monthIndex + 1, 1)
    }
    val daysInMonth = remember(year, monthIndex) { daysInMonth(year, monthIndex) }
    val holidays = BR_HOLIDAYS[monthIndex].orEmpty().map { it.first }.toSet()

    val cells = mutableListOf<Int?>()
    repeat(firstDayWeekday) { cells.add(null) }
    for (d in 1..daysInMonth) cells.add(d)
    while (cells.size % 7 != 0) cells.add(null)

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        cells.chunked(7).forEach { row ->
            Row(modifier = Modifier.fillMaxWidth()) {
                row.forEachIndexed { i, day ->
                    DayCell(
                        modifier = Modifier.weight(1f),
                        day = day,
                        isSunday = i == 0,
                        isHoliday = day != null && day in holidays,
                        isToday = day == today.day && monthIndex == today.monthIndex && year == today.year
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
    isSunday: Boolean,
    isHoliday: Boolean,
    isToday: Boolean
) {
    Box(
        modifier = modifier.height(36.dp),
        contentAlignment = Alignment.Center
    ) {
        if (day == null) return@Box
        val color = when {
            isHoliday -> CozyBrick
            isSunday -> CozyBrick
            else -> CozyInk
        }
        if (isToday) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(CozyAmber),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "$day",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        } else {
            Text(
                "$day",
                fontSize = 13.sp,
                fontWeight = if (isHoliday) FontWeight.Bold else FontWeight.Medium,
                color = color
            )
        }
    }
}

@Composable
private fun HolidayRow(day: Int, name: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(CozyBrick.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "$day",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = CozyBrick
            )
        }
        Spacer(Modifier.width(10.dp))
        Text(
            name,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = CozyInk
        )
    }
}

// ═══════════════════════════════════════════════════
// HELPERS
// ═══════════════════════════════════════════════════

private fun daysInMonth(year: Int, monthIndex: Int): Int = when (monthIndex) {
    0, 2, 4, 6, 7, 9, 11 -> 31
    3, 5, 8, 10 -> 30
    1 -> if (isLeap(year)) 29 else 28
    else -> 30
}

private fun isLeap(year: Int): Boolean =
    (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)

/**
 * Zeller's congruence — devolve dia da semana com 0=Sunday.
 * Mais portátil que NSCalendar/Calendar e funciona em commonMain.
 */
private fun zellerWeekday(year: Int, month1Based: Int, day: Int): Int {
    var m = month1Based
    var y = year
    if (m < 3) {
        m += 12
        y -= 1
    }
    val K = y % 100
    val J = y / 100
    val h = (day + (13 * (m + 1)) / 5 + K + K / 4 + J / 4 + 5 * J) % 7
    // h: 0=Saturday, 1=Sunday, 2=Monday...
    // Convertemos para 0=Sunday, 1=Monday, ..., 6=Saturday
    return ((h + 6) % 7)
}
