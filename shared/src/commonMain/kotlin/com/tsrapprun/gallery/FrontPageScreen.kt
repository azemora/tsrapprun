/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  FrontPageScreen.kt — Home estilo "profile + menu"           ║
 * ║                                                              ║
 * ║  Inspirado em apps com cabeçalho ilustrado em sage e card    ║
 * ║  branco arredondado deslizando por cima.                     ║
 * ║                                                              ║
 * ║  Cabeçalho:                                                  ║
 * ║   • Avatar circular (inicial do nome em sage profundo)       ║
 * ║   • Nome em destaque                                         ║
 * ║   • Badge hexagonal-like com fase atual                      ║
 * ║   • 3 stats: idade · próximo marco · data                    ║
 * ║                                                              ║
 * ║  Card branco:                                                ║
 * ║   • Menu vertical: Histórias · Calendário · Memórias ·       ║
 * ║     Galeria · Registros                                      ║
 * ║   • Bottom bar com Registrar como CTA principal              ║
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
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tsrapprun.camera.EventData
import com.tsrapprun.camera.PhotoData
import com.tsrapprun.child.AgeCalculator
import com.tsrapprun.child.ChildProfile
import com.tsrapprun.child.LifePhase
import com.tsrapprun.platform.dateComponentsOf
import com.tsrapprun.platform.nowMillis
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
    childProfile: ChildProfile?,
    events: List<EventData>,
    allPhotos: List<PhotoData>,
    onLoadThumbnail: suspend (PhotoData) -> ByteArray?,
    onOpenEvent: (EventData) -> Unit,
    onOpenEventList: () -> Unit,
    onCreate: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenMemoryBook: () -> Unit = {},
    onOpenMoments: () -> Unit = {},
    onOpenStories: () -> Unit = {},
    onOpenCalendar: () -> Unit = {}
) {
    val sortedEvents = remember(events) { events.sortedByDescending { it.createdAt } }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CozySageMist)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 110.dp)
        ) {
            // ── HEADER em gradiente sage com cantos arredondados embaixo ──
            ProfileHeader(
                profile = childProfile,
                eventCount = events.size,
                photoCount = allPhotos.size,
                onSettingsClick = onOpenSettings
            )

            Spacer(Modifier.height(24.dp))

            // ── MENU em card único ──
            MenuCard(
                hasEvents = sortedEvents.isNotEmpty(),
                onOpenStories = onOpenStories,
                onOpenCalendar = onOpenCalendar,
                onOpenMoments = onOpenMoments,
                onOpenEvents = onOpenEventList,
                onOpenMemoryBook = onOpenMemoryBook
            )

            Spacer(Modifier.height(24.dp))
        }

        // ── Bottom bar fixa ──
        BottomCreateBar(
            onCreate = onCreate,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(WindowInsets.navigationBars)
        )
    }
}

// ═══════════════════════════════════════════════════
// HEADER — perfil da criança
// ═══════════════════════════════════════════════════

@Composable
private fun ProfileHeader(
    profile: ChildProfile?,
    eventCount: Int,
    photoCount: Int,
    onSettingsClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 36.dp, bottomEnd = 36.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(CozySage, CozySage.copy(alpha = 0.85f))
                )
            )
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
        Column {
            // Top row: app name + settings
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "TSR",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.85f),
                    letterSpacing = 4.sp
                )
                Spacer(Modifier.weight(1f))
                IconCircle(
                    symbol = "⚙",
                    onClick = onSettingsClick,
                    bgAlpha = 0.22f
                )
            }

            Spacer(Modifier.height(20.dp))

            // Avatar + nome (centralizados)
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Avatar(profile = profile)

                Spacer(Modifier.height(14.dp))

                Text(
                    text = profile?.firstName ?: "—",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = (-0.8).sp
                )

                if (profile != null) {
                    Spacer(Modifier.height(6.dp))
                    AgeBadge(profile)
                }
            }

            Spacer(Modifier.height(20.dp))

            // Stats em 3 colunas
            if (profile != null) {
                StatsBlock(profile = profile, eventCount = eventCount, photoCount = photoCount)
            }
        }
    }
}

@Composable
private fun Avatar(profile: ChildProfile?) {
    Box(
        modifier = Modifier
            .size(96.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.92f))
            .border(width = 4.dp, color = Color.White.copy(alpha = 0.5f), shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = profile?.firstName?.firstOrNull()?.uppercase() ?: "?",
            fontSize = 44.sp,
            fontWeight = FontWeight.Bold,
            color = CozySage,
            letterSpacing = (-1).sp
        )
    }
}

@Composable
private fun AgeBadge(profile: ChildProfile) {
    val now = nowMillis()
    val age = remember(profile) { AgeCalculator.calculateAge(profile, now) }
    val label = when (age.phase) {
        LifePhase.PREGNANCY ->
            "${age.pregnancyWeeksRemaining} ${if (age.pregnancyWeeksRemaining == 1) "semana" else "semanas"} até nascer"
        LifePhase.NEWBORN ->
            "${age.daysOfLife} ${if (age.daysOfLife == 1) "dia" else "dias"} de vida"
        LifePhase.BABY ->
            "${age.months} ${if (age.months == 1) "mês" else "meses"}"
        LifePhase.TODDLER ->
            "${age.years} ${if (age.years == 1) "aninho" else "aninhos"}"
    }
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color.White.copy(alpha = 0.22f)
    ) {
        Text(
            label,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
            letterSpacing = 0.2.sp
        )
    }
}

@Composable
private fun StatsBlock(profile: ChildProfile, eventCount: Int, photoCount: Int) {
    val now = nowMillis()
    val next = remember(profile) { AgeCalculator.nextMilestone(profile, now) }
    val today = remember(now) {
        val c = dateComponentsOf(now)
        "${c.day.toString().padStart(2, '0')}/${(c.monthIndex + 1).toString().padStart(2, '0')}"
    }

    Surface(
        shape = RoundedCornerShape(22.dp),
        color = Color.White.copy(alpha = 0.18f)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp, horizontal = 8.dp)
        ) {
            HeaderStat(
                modifier = Modifier.weight(1f),
                value = "${next.daysUntil}",
                label = "dias até\n${next.label}"
            )
            HeaderStatDivider()
            HeaderStat(
                modifier = Modifier.weight(1f),
                value = "$photoCount",
                label = "fotos"
            )
            HeaderStatDivider()
            HeaderStat(
                modifier = Modifier.weight(1f),
                value = today,
                label = "hoje"
            )
        }
    }
}

@Composable
private fun HeaderStat(modifier: Modifier, value: String, label: String) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            value,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            letterSpacing = (-0.5).sp
        )
        Spacer(Modifier.height(2.dp))
        Text(
            label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White.copy(alpha = 0.85f),
            textAlign = TextAlign.Center,
            letterSpacing = 0.3.sp,
            lineHeight = 13.sp
        )
    }
}

@Composable
private fun HeaderStatDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(34.dp)
            .background(Color.White.copy(alpha = 0.3f))
    )
}

@Composable
private fun IconCircle(symbol: String, onClick: () -> Unit, bgAlpha: Float = 0.22f) {
    Surface(
        modifier = Modifier.size(40.dp).clickable(onClick = onClick),
        shape = CircleShape,
        color = Color.White.copy(alpha = bgAlpha)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(symbol, fontSize = 18.sp, color = Color.White)
        }
    }
}

// ═══════════════════════════════════════════════════
// MENU CARD
// ═══════════════════════════════════════════════════

@Composable
private fun MenuCard(
    hasEvents: Boolean,
    onOpenStories: () -> Unit,
    onOpenCalendar: () -> Unit,
    onOpenMoments: () -> Unit,
    onOpenEvents: () -> Unit,
    onOpenMemoryBook: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, CozyTan.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            MenuRow(
                title = "Histórias",
                subtitle = "leituras pra dormir",
                iconBg = CozyCreamDeep,
                iconSymbol = "📚",
                onClick = onOpenStories
            )
            MenuDivider()
            MenuRow(
                title = "Calendário",
                subtitle = "feriados e marcos",
                iconBg = CozySage.copy(alpha = 0.2f),
                iconSymbol = "📅",
                onClick = onOpenCalendar
            )
            MenuDivider()
            MenuRow(
                title = "Registros",
                subtitle = "diário de momentos",
                iconBg = CozyAmber.copy(alpha = 0.2f),
                iconSymbol = "✎",
                onClick = onOpenMoments
            )
            MenuDivider()
            MenuRow(
                title = "Eventos",
                subtitle = "fotos por ocasião",
                iconBg = CozyGold.copy(alpha = 0.25f),
                iconSymbol = "✦",
                onClick = onOpenEvents
            )
            MenuDivider()
            MenuRow(
                title = "Livro de memórias",
                subtitle = if (hasEvents) "scrapbook do bebê" else "cadastre eventos primeiro",
                iconBg = CozyTan.copy(alpha = 0.6f),
                iconSymbol = "◫",
                enabled = hasEvents,
                onClick = onOpenMemoryBook
            )
        }
    }
}

@Composable
private fun MenuRow(
    title: String,
    subtitle: String,
    iconBg: Color,
    iconSymbol: String,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Text(
                iconSymbol,
                fontSize = 22.sp,
                color = if (enabled) CozyOlive else CozyOlive.copy(alpha = 0.4f)
            )
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (enabled) CozyInk else CozyInk.copy(alpha = 0.4f),
                letterSpacing = (-0.2).sp
            )
            Spacer(Modifier.height(1.dp))
            Text(
                subtitle,
                fontSize = 12.sp,
                color = CozyOlive.copy(alpha = if (enabled) 0.65f else 0.35f),
                fontWeight = FontWeight.Normal
            )
        }
        Text(
            "›",
            fontSize = 22.sp,
            fontWeight = FontWeight.Light,
            color = if (enabled) CozyOlive.copy(alpha = 0.5f) else CozyOlive.copy(alpha = 0.25f)
        )
    }
}

@Composable
private fun MenuDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 78.dp)
            .height(1.dp)
            .background(CozyTan.copy(alpha = 0.4f))
    )
}

// ═══════════════════════════════════════════════════
// BOTTOM BAR
// ═══════════════════════════════════════════════════

@Composable
private fun BottomCreateBar(
    onCreate: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier
                        .height(48.dp)
                        .fillMaxWidth()
                        .clickable(onClick = onCreate),
                    shape = RoundedCornerShape(24.dp),
                    color = CozyAmber
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Registrar momento",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            letterSpacing = (-0.2).sp
                        )
                    }
                }
            }
        }
    }
}
