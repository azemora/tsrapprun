/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  ScrapbookDecorations.kt - Elementos visuais decorativos    ║
 * ║                                                             ║
 * ║  Fitas adesivas, molduras, texturas e estilos de texto      ║
 * ║  para o visual de scrapbook/álbum de memórias.              ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.memorybook

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs

// ═══════════════════════════════════════════════════
// CORES DO SCRAPBOOK
// ═══════════════════════════════════════════════════

/** Creme claro — fundo de página. */
val PageCream = Color(0xFFF5F0E1)

/** Marrom couro — capa. */
val CoverBrown = Color(0xFF6B4226)

/** Marrom escuro — contracapa. */
val BackCoverBrown = Color(0xFF4A2E18)

/** Fita adesiva amarela translúcida. */
val TapeYellow = Color(0xAAF5E6A8)

/** Fita washi rosa suave. */
val WashiPink = Color(0x55E8A0BF)

/** Fita washi verde menta. */
val WashiMint = Color(0x55A8D8B9)

/** Cor do texto "escrito à mão". */
val InkBrown = Color(0xFF3E2723)

/** Dourado para detalhes na capa. */
val GoldAccent = Color(0xFFD4A847)

// ═══════════════════════════════════════════════════
// ROTAÇÃO DETERMINÍSTICA
// ═══════════════════════════════════════════════════

/**
 * Rotação pseudo-aleatória baseada em seed.
 * Estável entre recomposições (sem jitter).
 */
fun deterministicRotation(seed: Int, range: Float = 5f): Float {
    val hash = ((seed.toLong() * 2654435761L) and 0xFFFFFFFFL).toInt()
    return ((abs(hash) % (range * 200).toInt()) / 100f) - range
}

/**
 * Offset pseudo-aleatório baseado em seed.
 */
fun deterministicOffset(seed: Int, range: Float = 10f): Float {
    val hash = ((seed.toLong() * 1640531527L) and 0xFFFFFFFFL).toInt()
    return ((abs(hash) % (range * 200).toInt()) / 100f) - range
}

// ═══════════════════════════════════════════════════
// FITA ADESIVA (TAPE)
// ═══════════════════════════════════════════════════

/**
 * Pequeno pedaço de fita adesiva decorativa.
 */
@Composable
fun TapeStrip(
    modifier: Modifier = Modifier,
    rotationDeg: Float = 0f,
    color: Color = TapeYellow,
    width: Dp = 48.dp,
    height: Dp = 14.dp
) {
    Box(
        modifier = modifier
            .graphicsLayer { rotationZ = rotationDeg }
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(2.dp))
            .background(color)
    )
}

// ═══════════════════════════════════════════════════
// MODIFIER EXTENSIONS
// ═══════════════════════════════════════════════════

/**
 * Moldura de foto estilo polaroid — borda branca + sombra + rotação.
 */
fun Modifier.photoFrame(
    rotationDeg: Float = 0f,
    elevation: Dp = 4.dp
): Modifier = this
    .graphicsLayer {
        rotationZ = rotationDeg
        shadowElevation = elevation.toPx()
    }
    .shadow(elevation, RoundedCornerShape(2.dp))
    .clip(RoundedCornerShape(2.dp))
    .background(Color.White)

/**
 * Fundo de página do scrapbook (creme/papel kraft).
 */
fun Modifier.pageBackground(): Modifier = this
    .background(PageCream)

// ═══════════════════════════════════════════════════
// ESTILOS DE TEXTO
// ═══════════════════════════════════════════════════

/** Título grande na capa do livro. */
fun coverTitleStyle() = TextStyle(
    fontFamily = FontFamily.Cursive,
    fontSize = 32.sp,
    fontWeight = FontWeight.Bold,
    color = GoldAccent,
    letterSpacing = 1.sp
)

/** Nome do evento em estilo "escrito à mão". */
fun eventTitleStyle() = TextStyle(
    fontFamily = FontFamily.Cursive,
    fontSize = 24.sp,
    fontWeight = FontWeight.SemiBold,
    color = InkBrown,
    letterSpacing = 0.5.sp
)

/** Data do evento em cursivo menor. */
fun eventDateStyle() = TextStyle(
    fontFamily = FontFamily.Cursive,
    fontSize = 14.sp,
    fontWeight = FontWeight.Normal,
    color = InkBrown.copy(alpha = 0.6f)
)

/** Texto pequeno informativo. */
fun captionStyle() = TextStyle(
    fontFamily = FontFamily.Cursive,
    fontSize = 12.sp,
    color = InkBrown.copy(alpha = 0.5f)
)
