/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  TsrChrome.kt — widgets compartilhados (tradução de          ║
 * ║  fullapp/tsr-chrome.jsx)                                     ║
 * ║                                                              ║
 * ║  Linguagem: scrapbook editorial cozy.                        ║
 * ║   • Tag       — etiqueta monospace UPPERCASE                 ║
 * ║   • ScreenHeader — eyebrow + título Fraunces italic          ║
 * ║   • TabBar    — 5 ícones (início/calendário/registros/…)    ║
 * ║   • Polaroid  — papel cream + foto colorida + caption Caveat ║
 * ║   • Stamp     — selo circular tipográfico                    ║
 * ║   • TornDivider — onda SVG de papel rasgado                  ║
 * ║   • PrimaryButton / GhostButton                              ║
 * ║   • MomentCard / Stitched (borda costurada)                  ║
 * ║   • CreamScreen / SageScreen — wrappers de fundo             ║
 * ║   • italicSerifText — palavra em italic accent               ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.ui.chrome

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tsrapprun.ui.theme.CozyAmber
import com.tsrapprun.ui.theme.CozyAmberDeep
import com.tsrapprun.ui.theme.CozyCream
import com.tsrapprun.ui.theme.CozyCreamDeep
import com.tsrapprun.ui.theme.CozyInk
import com.tsrapprun.ui.theme.CozyOlive
import com.tsrapprun.ui.theme.CozySage
import com.tsrapprun.ui.theme.CozySageMist
import com.tsrapprun.ui.theme.CozyTan

// ════════════════════════════════════════════════════════════
// PALETA EXTRA (espelha tsr-system.jsx)
// ════════════════════════════════════════════════════════════

val OliveDeep = Color(0xFF3F4F2E)
val Butter = Color(0xFFFCE4A7)
val Peach = Color(0xFFF2D6D2)
val Sky = Color(0xFFC9DBE8)
val Lilac = Color(0xFFE6CFE8)
val Mint = Color(0xFFC8E0D7)
val Brick = Color(0xFFB85450)

/** Cinza-escuro neutro para placeholders de fotos (simula foto em B&W). */
val PhotoGray = Color(0xFF6B7568)
val PhotoGrayLight = Color(0xFFA8B0A6)

object Pastels {
    val peach = Peach
    val butter = Butter
    val sky = Sky
    val lilac = Lilac
    val mint = Mint
    val sage = Color(0xFFC4D4B0)
    val cream = CozyCream

    fun toneFor(name: String): Color = when (name.lowercase()) {
        "peach" -> peach
        "butter" -> butter
        "sky" -> sky
        "lilac" -> lilac
        "mint" -> mint
        "sage" -> sage
        else -> peach
    }

    fun forIndex(i: Int): Color = listOf(peach, butter, sky, lilac, mint, sage)
        .let { it[((i % it.size) + it.size) % it.size] }
}

// ════════════════════════════════════════════════════════════
// ITALIC SERIF accent string
// ════════════════════════════════════════════════════════════

/**
 * Constrói AnnotatedString tipo "memórias *acolhedoras*, suas".
 *
 * @param defaultColor cor das partes regulares
 * @param italicColor cor da parte italic (acento)
 */
fun italicSerifText(
    prefix: String = "",
    italic: String,
    suffix: String = "",
    defaultColor: Color = CozyInk,
    italicColor: Color = CozyAmberDeep,
    defaultWeight: FontWeight = FontWeight.Medium,
    italicWeight: FontWeight = FontWeight.Light
): AnnotatedString = buildAnnotatedString {
    if (prefix.isNotEmpty()) {
        withStyle(SpanStyle(color = defaultColor, fontWeight = defaultWeight)) { append(prefix) }
    }
    withStyle(
        SpanStyle(
            color = italicColor,
            fontFamily = FontFamily.Serif,
            fontStyle = FontStyle.Italic,
            fontWeight = italicWeight
        )
    ) { append(italic) }
    if (suffix.isNotEmpty()) {
        withStyle(SpanStyle(color = defaultColor, fontWeight = defaultWeight)) { append(suffix) }
    }
}

// ════════════════════════════════════════════════════════════
// TAG (etiqueta monospace UPPERCASE)
// ════════════════════════════════════════════════════════════

@Composable
fun Tag(
    text: String,
    color: Color = CozyOlive,
    bg: Color = Color.Transparent,
    fontSize: TextUnit = 9.5.sp,
    modifier: Modifier = Modifier
) {
    val isTransparent = bg.alpha == 0f
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(999.dp),
        color = bg
    ) {
        Text(
            text.uppercase(),
            fontFamily = FontFamily.Monospace,
            fontSize = fontSize,
            letterSpacing = 1.4.sp,
            color = if (isTransparent) color.copy(alpha = 0.7f) else color,
            modifier = Modifier.padding(
                horizontal = if (isTransparent) 0.dp else 8.dp,
                vertical = if (isTransparent) 0.dp else 4.dp
            )
        )
    }
}

// ════════════════════════════════════════════════════════════
// SCREEN HEADER
// ════════════════════════════════════════════════════════════

@Composable
fun ScreenHeader(
    chapter: String? = null,
    title: AnnotatedString? = null,
    subtitle: String? = null,
    onBack: (() -> Unit)? = null,
    rightContent: (@Composable () -> Unit)? = null,
    color: Color = OliveDeep,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(32.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (onBack != null) {
                Box(
                    modifier = Modifier.size(32.dp).clickable(onClick = onBack),
                    contentAlignment = Alignment.Center
                ) {
                    Text("‹", fontSize = 24.sp, fontWeight = FontWeight.Light, color = color)
                }
            } else Spacer(Modifier.size(32.dp))

            if (chapter != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.width(18.dp).height(1.dp).background(color.copy(alpha = 0.4f)))
                    Spacer(Modifier.width(8.dp))
                    Tag(chapter, color = color, fontSize = 9.5.sp)
                    Spacer(Modifier.width(8.dp))
                    Box(Modifier.width(18.dp).height(1.dp).background(color.copy(alpha = 0.4f)))
                }
            }

            if (rightContent != null) {
                Box { rightContent() }
            } else Spacer(Modifier.size(32.dp))
        }

        if (title != null) {
            Spacer(Modifier.height(14.dp))
            Text(
                title,
                fontFamily = FontFamily.Serif,
                fontSize = 32.sp,
                fontWeight = FontWeight.Normal,
                lineHeight = 32.sp,
                letterSpacing = (-0.9).sp,
                color = color
            )
        }
        if (subtitle != null) {
            Spacer(Modifier.height(8.dp))
            Text(
                subtitle,
                fontSize = 13.sp,
                color = color.copy(alpha = 0.65f),
                lineHeight = 19.sp
            )
        }
    }
}

// ════════════════════════════════════════════════════════════
// TAB BAR (bottom nav 5 ícones)
// ════════════════════════════════════════════════════════════

enum class TabItem(val id: String, val label: String, val symbol: String) {
    HOME("home", "início", "✿"),
    CALENDAR("calendar", "calendário", "▤"),
    MOMENTS("moments", "registros", "✎"),
    EVENTS("events", "eventos", "❍"),
    BOOK("book", "livro", "▦")
}

@Composable
fun TabBar(
    active: TabItem,
    onChange: (TabItem) -> Unit,
    bg: Color = CozyCream,
    color: Color = OliveDeep
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = bg,
        shadowElevation = 0.dp,
        border = BorderStroke(1.dp, color.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TabItem.values().forEach { item ->
                val isActive = item == active
                Column(
                    modifier = Modifier
                        .clickable { onChange(item) }
                        .padding(horizontal = 6.dp, vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        item.symbol,
                        fontSize = 20.sp,
                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                        color = if (isActive) color else color.copy(alpha = 0.5f)
                    )
                    Spacer(Modifier.height(3.dp))
                    Text(
                        item.label,
                        fontSize = 9.5.sp,
                        fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Medium,
                        color = if (isActive) color else color.copy(alpha = 0.5f),
                        letterSpacing = 0.2.sp
                    )
                    if (isActive) {
                        Spacer(Modifier.height(3.dp))
                        Box(
                            Modifier
                                .size(4.dp)
                                .clip(CircleShape)
                                .background(color)
                        )
                    }
                }
            }
        }
    }
}

// ════════════════════════════════════════════════════════════
// POLAROID
// ════════════════════════════════════════════════════════════

@Composable
fun Polaroid(
    modifier: Modifier = Modifier,
    tone: Color = Peach,
    caption: String? = null,
    width: Dp = 130.dp,
    photoHeight: Dp = 130.dp,
    rotation: Float = 0f,
    frameColor: Color = CozyCream
) {
    Box(
        modifier = modifier.graphicsLayer { rotationZ = rotation }
    ) {
        Surface(
            modifier = Modifier.width(width),
            shape = RoundedCornerShape(4.dp),
            color = frameColor,
            shadowElevation = 6.dp
        ) {
            Column(
                modifier = Modifier.padding(8.dp).padding(bottom = if (caption != null) 4.dp else 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(photoHeight)
                        .clip(RoundedCornerShape(2.dp))
                        .background(diagonalStripeBrush(tone))
                )
                if (caption != null) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        caption,
                        fontFamily = FontFamily.Cursive,
                        fontSize = 16.sp,
                        color = OliveDeep,
                        lineHeight = 18.sp,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

/** Listras diagonais sutis dão textura de "foto placeholder". */
fun diagonalStripeBrush(base: Color): Brush =
    Brush.linearGradient(
        colors = listOf(base, base.copy(alpha = 0.85f), base),
        start = Offset(0f, 0f),
        end = Offset(40f, 40f)
    )

// ════════════════════════════════════════════════════════════
// STAMP (selo circular)
// ════════════════════════════════════════════════════════════

@Composable
fun Stamp(
    label: String,
    sub: String = "capítulo",
    size: Dp = 64.dp,
    color: Color = CozyAmberDeep,
    rotation: Float = 0f,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size)
            .graphicsLayer { rotationZ = rotation }
            .clip(CircleShape)
            .background(color.copy(alpha = 0.0f))
            .border(width = 1.5.dp, color = color, shape = CircleShape)
            .border(
                width = 1.dp,
                color = color.copy(alpha = 0.4f),
                shape = CircleShape
            )
            .padding(6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                sub.uppercase(),
                fontFamily = FontFamily.Monospace,
                fontSize = 7.sp,
                letterSpacing = 1.4.sp,
                color = color
            )
            Text(
                label,
                fontFamily = FontFamily.Serif,
                fontStyle = FontStyle.Italic,
                fontSize = (size.value * 0.32f).sp,
                fontWeight = FontWeight.Bold,
                color = color,
                lineHeight = (size.value * 0.32f).sp
            )
        }
    }
}

// ════════════════════════════════════════════════════════════
// TORN DIVIDER (onda)
// ════════════════════════════════════════════════════════════

@Composable
fun TornDivider(color: Color = CozyCream, height: Dp = 12.dp, flip: Boolean = false) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .drawBehind {
                val w = this.size.width
                val h = this.size.height
                val path = Path()
                val midY = h * 0.5f
                if (flip) {
                    path.moveTo(0f, 0f)
                    var x = 0f
                    while (x < w) {
                        path.relativeQuadraticBezierTo(20f, -midY * 0.6f, 40f, 0f)
                        path.relativeQuadraticBezierTo(20f, midY * 0.6f, 40f, 0f)
                        x += 80f
                    }
                    path.lineTo(w, h)
                    path.lineTo(0f, h)
                    path.close()
                } else {
                    path.moveTo(0f, midY)
                    var x = 0f
                    while (x < w) {
                        path.relativeQuadraticBezierTo(20f, -midY * 0.6f, 40f, 0f)
                        path.relativeQuadraticBezierTo(20f, midY * 0.6f, 40f, 0f)
                        x += 80f
                    }
                    path.lineTo(w, h)
                    path.lineTo(0f, h)
                    path.close()
                }
                drawPath(path, color)
            }
    )
}

// ════════════════════════════════════════════════════════════
// BUTTONS
// ════════════════════════════════════════════════════════════

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    background: Color = OliveDeep,
    contentColor: Color = CozyCream,
    showArrow: Boolean = true
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = background,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 22.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text,
                fontFamily = FontFamily.Serif,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = contentColor,
                letterSpacing = (-0.2).sp
            )
            if (showArrow) {
                Spacer(Modifier.width(10.dp))
                Text("→", fontSize = 18.sp, color = contentColor, fontWeight = FontWeight.Light)
            }
        }
    }
}

@Composable
fun GhostButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = OliveDeep,
    pillShape: Boolean = true
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = if (pillShape) RoundedCornerShape(999.dp) else RoundedCornerShape(18.dp),
        color = Color.Transparent,
        border = BorderStroke(1.5.dp, color)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text,
                fontFamily = FontFamily.Serif,
                fontSize = 14.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = color,
                letterSpacing = (-0.1).sp
            )
        }
    }
}

// ════════════════════════════════════════════════════════════
// MOMENT CARD (mini polaroide com nota)
// ════════════════════════════════════════════════════════════

enum class MomentKind {
    DAILY, WEEKLY, MESVERSARIO, PREGNANCY_WEEK, DAY_OF_LIFE
}

private data class KindStyle(val bg: Color, val label: String, val accent: Color, val tone: Color)

private fun styleFor(kind: MomentKind): KindStyle = when (kind) {
    MomentKind.DAILY -> KindStyle(CozyCream, "diário", CozyAmberDeep, Peach)
    MomentKind.WEEKLY -> KindStyle(CozyCream, "semanal", CozyOlive, Pastels.sage)
    MomentKind.MESVERSARIO -> KindStyle(Butter, "mesversário", CozyAmberDeep, Butter)
    MomentKind.PREGNANCY_WEEK -> KindStyle(Peach, "semana", CozyAmberDeep, Peach)
    MomentKind.DAY_OF_LIFE -> KindStyle(CozyCream, "dia da vida", CozyOlive, Pastels.sage)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MomentCard(
    kind: MomentKind,
    title: String,
    date: String,
    note: String? = null,
    rotation: Float = 0f,
    mini: Boolean = false,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null
) {
    val s = styleFor(kind)
    val photoHeight = if (mini) 90.dp else 130.dp
    val cornerRadius = if (mini) 10.dp else 14.dp

    Surface(
        modifier = modifier
            .graphicsLayer { rotationZ = rotation }
            .let {
                if (onClick != null || onLongClick != null) {
                    it.combinedClickable(
                        onClick = onClick ?: {},
                        onLongClick = onLongClick
                    )
                } else it
            },
        shape = RoundedCornerShape(cornerRadius),
        color = s.bg,
        shadowElevation = 4.dp,
        border = BorderStroke(1.dp, OliveDeep.copy(alpha = 0.06f))
    ) {
        Column(modifier = Modifier.padding(if (mini) 8.dp else 10.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(photoHeight)
                    .clip(RoundedCornerShape(if (mini) 6.dp else 10.dp))
                    .background(diagonalStripeBrush(s.tone))
            )
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Tag(s.label, color = s.accent, fontSize = 8.5.sp)
                Text(
                    date,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.5.sp,
                    color = CozyOlive.copy(alpha = 0.6f)
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                title,
                fontFamily = FontFamily.Serif,
                fontSize = if (mini) 13.sp else 15.sp,
                fontWeight = FontWeight.Medium,
                color = OliveDeep,
                lineHeight = (if (mini) 16 else 18).sp,
                letterSpacing = (-0.2).sp
            )
            if (!note.isNullOrBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    note,
                    fontFamily = FontFamily.Cursive,
                    fontSize = if (mini) 13.sp else 15.sp,
                    color = CozyOlive.copy(alpha = 0.85f),
                    lineHeight = (if (mini) 15 else 17).sp
                )
            }
        }
    }
}

// ════════════════════════════════════════════════════════════
// SCREEN WRAPPERS
// ════════════════════════════════════════════════════════════

@Composable
fun CreamScreen(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CozyCream)
    ) {
        Column(modifier = Modifier.fillMaxSize()) { content() }
    }
}

@Composable
fun SageScreen(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(CozySage, CozySage.copy(alpha = 0.92f)))
            )
    ) {
        // Sutilezas: sem blob real (Compose puro), mas adicionamos overlay
        Column(modifier = Modifier.fillMaxSize()) { content() }
    }
}

// ════════════════════════════════════════════════════════════
// STITCHED FRAME (borda tracejada)
// ════════════════════════════════════════════════════════════

@Composable
fun Stitched(
    modifier: Modifier = Modifier,
    color: Color = CozyOlive,
    radius: Dp = 6.dp,
    bg: Color = CozyCream,
    padding: Dp = 6.dp,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(radius),
        color = bg,
        shadowElevation = 4.dp,
        border = BorderStroke(1.dp, color.copy(alpha = 0.4f))
    ) {
        Box(modifier = Modifier.padding(padding)) {
            content()
        }
    }
}
