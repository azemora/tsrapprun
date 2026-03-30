/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  MemoryBookScreen.kt - Livro de Memórias                    ║
 * ║                                                             ║
 * ║  Exibe todos os eventos como um livro com animação de       ║
 * ║  virar páginas. Cada evento é uma página com colagem        ║
 * ║  estilo scrapbook.                                          ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.memorybook

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tsrapprun.camera.EventData
import com.tsrapprun.camera.PhotoData
import kotlin.math.abs

@Composable
fun MemoryBookScreen(
    events: List<EventData>,
    allPhotos: List<PhotoData>,
    onLoadThumbnail: suspend (PhotoData) -> ByteArray?,
    onBack: () -> Unit
) {
    val bookPages = remember(events, allPhotos) {
        buildBookPages(events, allPhotos)
    }

    val dateRange = remember(events) { buildDateRange(events) }

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { bookPages.size }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2C2C2C)) // fundo escuro (mesa)
    ) {
        // ── Livro (pager com animação de virar página) ──
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 56.dp, bottom = 40.dp, start = 8.dp, end = 8.dp),
            pageSpacing = 0.dp
        ) { pageIndex ->
            val pageOffset = (pagerState.currentPage - pageIndex) +
                    pagerState.currentPageOffsetFraction

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        // Efeito de virar página (rotação em Y com perspectiva)
                        val rotation = pageOffset * -25f
                        rotationY = rotation.coerceIn(-25f, 25f)
                        cameraDistance = 12f * density

                        // Escala sutil durante transição
                        val scale = 1f - (abs(pageOffset) * 0.08f).coerceAtMost(0.12f)
                        scaleX = scale
                        scaleY = scale

                        // Fade para profundidade
                        alpha = (1f - abs(pageOffset) * 0.4f).coerceIn(0.3f, 1f)

                        // Ponto de origem: borda esquerda (como um livro real)
                        transformOrigin = TransformOrigin(0f, 0.5f)
                    }
                    .clip(RoundedCornerShape(4.dp))
            ) {
                // Renderiza a página
                when (val page = bookPages[pageIndex]) {
                    is BookPage.Cover -> CoverPage(
                        eventCount = events.size,
                        dateRange = dateRange,
                        modifier = Modifier.fillMaxSize()
                    )
                    is BookPage.EventSpread -> EventSpreadPage(
                        event = page.event,
                        photos = page.photos,
                        onLoadThumbnail = onLoadThumbnail,
                        modifier = Modifier.fillMaxSize()
                    )
                    is BookPage.BackCover -> BackCoverPage(
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Sombra na "lombada" do livro (borda esquerda)
                Box(
                    modifier = Modifier
                        .width(8.dp)
                        .fillMaxSize()
                        .align(Alignment.CenterStart)
                        .background(
                            androidx.compose.ui.graphics.Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.15f),
                                    Color.Transparent
                                )
                            )
                        )
                )
            }
        }

        // ── Top bar: voltar + indicador de página ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .background(Color.Black.copy(alpha = 0.4f))
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack) {
                Text("\u2190 Voltar", color = Color.White)
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "P\u00e1gina ${pagerState.currentPage + 1} de ${bookPages.size}",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.weight(1f))
            // Balanceamento
            Spacer(modifier = Modifier.width(80.dp))
        }

        // ── Indicador de swipe (primeira vez) ──
        if (pagerState.currentPage == 0) {
            Text(
                text = "Deslize para ler \u2192",
                color = Color.White.copy(alpha = 0.4f),
                fontSize = 12.sp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 12.dp)
            )
        }
    }
}
