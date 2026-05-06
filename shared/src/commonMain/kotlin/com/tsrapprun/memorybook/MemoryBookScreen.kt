/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  MemoryBookScreen.kt — tradução de                           ║
 * ║  fullapp/screens/memory-book.jsx                             ║
 * ║                                                              ║
 * ║  Capa "livro da Manu" + spread (2 páginas) com selo de       ║
 * ║  capítulo, polaroides com captions Caveat e folha sticker.   ║
 * ║  Navegação cap. anterior / próximo + dots de progresso.      ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.memorybook

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
import com.tsrapprun.camera.EventData
import com.tsrapprun.camera.PhotoData
import com.tsrapprun.child.ChildProfile
import com.tsrapprun.ui.chrome.Butter
import com.tsrapprun.ui.chrome.OliveDeep
import com.tsrapprun.ui.chrome.Pastels
import com.tsrapprun.ui.chrome.Peach
import com.tsrapprun.ui.chrome.Polaroid
import com.tsrapprun.ui.chrome.ScreenHeader
import com.tsrapprun.ui.chrome.Stamp
import com.tsrapprun.ui.chrome.Tag
import com.tsrapprun.ui.chrome.italicSerifText
import com.tsrapprun.ui.theme.CozyAmberDeep
import com.tsrapprun.ui.theme.CozyCream
import com.tsrapprun.ui.theme.CozyCreamDeep
import com.tsrapprun.ui.theme.CozyOlive
import com.tsrapprun.ui.theme.CozySage

@Composable
fun MemoryBookScreen(
    events: List<EventData>,
    allPhotos: List<PhotoData>,
    onLoadThumbnail: suspend (PhotoData) -> ByteArray?,
    onBack: () -> Unit,
    childName: String? = null
) {
    val totalChapters = if (events.isNotEmpty()) events.size else 7
    var currentChapter by remember { mutableStateOf(1) }

    Box(modifier = Modifier.fillMaxSize().background(CozyCreamDeep)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            ScreenHeader(
                chapter = "livro de memórias",
                title = italicSerifText(
                    prefix = "livro ",
                    italic = "${if (childName != null) "de $childName" else "do bebê"}",
                    italicColor = CozyAmberDeep,
                    defaultColor = OliveDeep
                ),
                subtitle = "cada página um capítulo. tocar para abrir.",
                onBack = onBack,
                rightContent = {
                    Box(
                        modifier = Modifier.size(36.dp).clickable { /* download */ },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("↓", fontSize = 20.sp, color = CozyOlive, fontWeight = FontWeight.Light)
                    }
                }
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp, vertical = 4.dp)
            ) {
                BookSpread(currentChapter = currentChapter)
                Spacer(Modifier.height(14.dp))
                ChapterNav(
                    currentChapter = currentChapter,
                    totalChapters = totalChapters.coerceIn(1, 12),
                    onPrev = { if (currentChapter > 1) currentChapter -= 1 },
                    onNext = { if (currentChapter < totalChapters.coerceAtMost(12)) currentChapter += 1 }
                )
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun BookSpread(currentChapter: Int) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = CozyCream,
        shadowElevation = 12.dp,
        border = BorderStroke(1.dp, OliveDeep.copy(alpha = 0.08f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Chapter heading
            Row(verticalAlignment = Alignment.CenterVertically) {
                Stamp(
                    label = currentChapter.toString().padStart(2, '0'),
                    sub = "capítulo",
                    color = CozyAmberDeep,
                    rotation = -6f,
                    size = 48.dp
                )
                Spacer(Modifier.width(10.dp))
                Column {
                    Tag("mês ${currentChapter.toString().padStart(2, '0')}", color = OliveDeep)
                    Spacer(Modifier.height(2.dp))
                    Text(
                        italicSerifText(
                            prefix = "",
                            italic = wordOf(currentChapter),
                            suffix = " ${if (currentChapter == 1) "mês" else "meses"}",
                            italicColor = CozyAmberDeep,
                            defaultColor = OliveDeep,
                            italicWeight = FontWeight.Light
                        ),
                        fontSize = 22.sp,
                        lineHeight = 22.sp,
                        letterSpacing = (-0.4).sp
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Spread: 2 colunas com binding line ao centro (visualizado pelo gap maior)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // LEFT page
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Polaroid(
                        modifier = Modifier.fillMaxWidth(),
                        tone = Peach,
                        caption = "22 mar",
                        photoHeight = 110.dp,
                        rotation = -2f
                    )
                    Text(
                        "começou a engatinhar de costas, fica brava quando não chega.",
                        fontFamily = FontFamily.Cursive,
                        fontSize = 14.sp,
                        color = OliveDeep,
                        lineHeight = 16.sp
                    )
                    Polaroid(
                        modifier = Modifier.fillMaxWidth(),
                        tone = Butter,
                        caption = "26 mar",
                        photoHeight = 90.dp,
                        rotation = 2.2f
                    )
                }
                // Binding line — separator vertical
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(360.dp)
                        .background(CozyOlive.copy(alpha = 0.18f))
                )
                // RIGHT page
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "primeira papinha! batata-doce. abriu sorrisão.",
                        fontFamily = FontFamily.Cursive,
                        fontSize = 14.sp,
                        color = OliveDeep,
                        lineHeight = 16.sp
                    )
                    Polaroid(
                        modifier = Modifier.fillMaxWidth(),
                        tone = Pastels.sage,
                        caption = "2 abr ✿",
                        photoHeight = 100.dp,
                        rotation = -1.5f
                    )
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "✿",
                            fontSize = 24.sp,
                            color = CozySage,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                    Text(
                        "✦ $currentChapter ${if (currentChapter == 1) "mês" else "meses"} redondos ✦",
                        fontFamily = FontFamily.Cursive,
                        fontSize = 14.sp,
                        color = CozyAmberDeep,
                        lineHeight = 16.sp,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End
                    )
                }
            }

            Spacer(Modifier.height(18.dp))

            // Page numbers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "${currentChapter * 2}".padStart(2, '0'),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp,
                    color = CozyOlive.copy(alpha = 0.55f),
                    letterSpacing = 1.sp
                )
                Text(
                    "${currentChapter * 2 + 1}".padStart(2, '0'),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp,
                    color = CozyOlive.copy(alpha = 0.55f),
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@Composable
private fun ChapterNav(
    currentChapter: Int,
    totalChapters: Int,
    onPrev: () -> Unit,
    onNext: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Prev
        Surface(
            modifier = Modifier.clickable(enabled = currentChapter > 1, onClick = onPrev),
            shape = RoundedCornerShape(999.dp),
            color = CozyCream,
            border = BorderStroke(1.4.dp, CozyOlive.copy(alpha = 0.13f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("‹", fontSize = 14.sp, color = OliveDeep, fontWeight = FontWeight.Bold)
                Spacer(Modifier.width(6.dp))
                Text(
                    "cap. ${(currentChapter - 1).coerceAtLeast(1).toString().padStart(2, '0')}",
                    fontFamily = FontFamily.Serif,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = OliveDeep
                )
            }
        }

        // Dots
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            (1..totalChapters).forEach { i ->
                val isCurrent = i == currentChapter
                Box(
                    modifier = Modifier
                        .size(width = if (isCurrent) 18.dp else 4.dp, height = 4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(if (isCurrent) CozyAmberDeep else CozyOlive.copy(alpha = 0.2f))
                )
            }
        }

        // Next
        Surface(
            modifier = Modifier.clickable(enabled = currentChapter < totalChapters, onClick = onNext),
            shape = RoundedCornerShape(999.dp),
            color = OliveDeep
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "cap. ${(currentChapter + 1).coerceAtMost(totalChapters).toString().padStart(2, '0')}",
                    fontFamily = FontFamily.Serif,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = CozyCream
                )
                Spacer(Modifier.width(6.dp))
                Text("›", fontSize = 14.sp, color = CozyCream, fontWeight = FontWeight.Bold)
            }
        }
    }
}

private fun wordOf(n: Int): String = listOf(
    "zero", "um", "dois", "três", "quatro", "cinco",
    "seis", "sete", "oito", "nove", "dez", "onze", "doze"
).getOrNull(n) ?: "$n"
