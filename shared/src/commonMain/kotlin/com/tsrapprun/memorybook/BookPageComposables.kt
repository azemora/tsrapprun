/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  BookPageComposables.kt - Páginas do Livro de Memórias      ║
 * ║                                                             ║
 * ║  Capa, páginas de evento (colagem + texto) e contracapa.   ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.memorybook

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tsrapprun.camera.EventData
import com.tsrapprun.camera.PhotoData

// ═══════════════════════════════════════════════════
// CAPA
// ═══════════════════════════════════════════════════

@Composable
fun CoverPage(
    eventCount: Int,
    dateRange: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CoverBrown)
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        // Borda dupla decorativa
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(2.dp, GoldAccent.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                .padding(8.dp)
                .border(1.dp, GoldAccent.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Ornamento superior
                Text(
                    text = "\u2022 \u2022 \u2022",  // • • •
                    color = GoldAccent.copy(alpha = 0.5f),
                    fontSize = 24.sp,
                    letterSpacing = 12.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Título principal
                Text(
                    text = "Livro de",
                    style = coverTitleStyle().copy(fontSize = 22.sp),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Mem\u00f3rias",
                    style = coverTitleStyle().copy(fontSize = 38.sp),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Linha decorativa
                Box(
                    modifier = Modifier
                        .width(120.dp)
                        .height(1.dp)
                        .background(GoldAccent.copy(alpha = 0.5f))
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Subtítulo
                if (eventCount > 0) {
                    Text(
                        text = "$eventCount evento${if (eventCount != 1) "s" else ""}",
                        color = GoldAccent.copy(alpha = 0.7f),
                        fontFamily = FontFamily.Cursive,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                    if (dateRange.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = dateRange,
                            color = GoldAccent.copy(alpha = 0.5f),
                            fontFamily = FontFamily.Cursive,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Ornamento inferior
                Text(
                    text = "\u2766",  // ❦
                    color = GoldAccent.copy(alpha = 0.4f),
                    fontSize = 28.sp
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════
// PÁGINA DE EVENTO
// ═══════════════════════════════════════════════════

@Composable
fun EventSpreadPage(
    event: EventData,
    photos: List<PhotoData>,
    onLoadThumbnail: suspend (PhotoData) -> ByteArray?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .pageBackground()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = 16.dp, bottom = 24.dp)
        ) {
            // ── Fita washi decorativa no topo ──
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .padding(horizontal = 40.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                WashiMint,
                                WashiPink,
                                WashiMint
                            )
                        )
                    )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ── Nome do evento ──
            Text(
                text = event.name,
                style = eventTitleStyle(),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            )

            // ── Data ──
            Text(
                text = formatEventDatePretty(event.createdAt),
                style = eventDateStyle(),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ── Colagem de fotos ──
            PhotoCollage(
                photos = photos,
                onLoadThumbnail = onLoadThumbnail,
                modifier = Modifier.fillMaxWidth(),
                seed = event.id.hashCode()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ── Rodapé decorativo ──
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 40.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                DotDivider()
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Contagem de fotos
            Text(
                text = "${photos.size} foto${if (photos.size != 1) "s" else ""}",
                style = captionStyle(),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// ═══════════════════════════════════════════════════
// CONTRACAPA
// ═══════════════════════════════════════════════════

@Composable
fun BackCoverPage(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackCoverBrown)
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        // Borda
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(1.dp, GoldAccent.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "\u2765",  // ❥
                    color = GoldAccent.copy(alpha = 0.5f),
                    fontSize = 32.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "At\u00e9 a pr\u00f3xima",
                    fontFamily = FontFamily.Cursive,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldAccent.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "mem\u00f3ria...",
                    fontFamily = FontFamily.Cursive,
                    fontSize = 20.sp,
                    color = GoldAccent.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(40.dp))

                // Marca do app
                Text(
                    text = "TSR App",
                    fontSize = 11.sp,
                    color = GoldAccent.copy(alpha = 0.25f),
                    letterSpacing = 4.sp,
                    fontWeight = FontWeight.Light
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════
// HELPERS
// ═══════════════════════════════════════════════════

@Composable
private fun DotDivider() {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(5) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(InkBrown.copy(alpha = 0.2f))
            )
        }
    }
}

private fun formatEventDatePretty(epochMillis: Long): String {
    val cal = java.util.Calendar.getInstance().apply { timeInMillis = epochMillis }
    val d = cal.get(java.util.Calendar.DAY_OF_MONTH)
    val m = cal.get(java.util.Calendar.MONTH)
    val y = cal.get(java.util.Calendar.YEAR)
    val months = listOf(
        "janeiro", "fevereiro", "mar\u00e7o", "abril", "maio", "junho",
        "julho", "agosto", "setembro", "outubro", "novembro", "dezembro"
    )
    return "$d de ${months[m]}, $y"
}

/**
 * Calcula range de datas para a capa: "jan 2025 — mar 2026".
 */
fun buildDateRange(events: List<EventData>): String {
    if (events.isEmpty()) return ""
    val sorted = events.sortedBy { it.createdAt }
    val first = sorted.first().createdAt
    val last = sorted.last().createdAt
    return "${formatShortMonthYear(first)} \u2014 ${formatShortMonthYear(last)}"
}

private fun formatShortMonthYear(epochMillis: Long): String {
    val cal = java.util.Calendar.getInstance().apply { timeInMillis = epochMillis }
    val m = cal.get(java.util.Calendar.MONTH)
    val y = cal.get(java.util.Calendar.YEAR)
    val months = listOf(
        "jan", "fev", "mar", "abr", "mai", "jun",
        "jul", "ago", "set", "out", "nov", "dez"
    )
    return "${months[m]} $y"
}
