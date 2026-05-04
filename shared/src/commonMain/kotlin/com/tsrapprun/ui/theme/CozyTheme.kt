/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  CozyTheme.kt — Paleta e tema "cozy/friendly"                ║
 * ║                                                              ║
 * ║  Inspirado em UIs cottagecore/whimsical: tons de sage, mel,  ║
 * ║  bege e creme. Cantos arredondados generosos, sombras suaves,║
 * ║  contraste calmo.                                            ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// ══════════════════════════════════════════════════════════════
// PALETA
// ══════════════════════════════════════════════════════════════

/** Sage médio — primária; cards de ação positiva, top bars. */
val CozySage = Color(0xFF8FA876)

/** Sage claro — fundo geral do app. */
val CozySageMist = Color(0xFFE2EAD3)

/** Sage muito claro — superfície de cards principais. */
val CozySageSoft = Color(0xFFEFF3E4)

/** Verde oliva profundo — texto e acentos. */
val CozyOlive = Color(0xFF54683E)

/** Âmbar quente — cards de "registrar" e ações primárias. */
val CozyAmber = Color(0xFFCB8B45)

/** Âmbar escuro — pressed/hover ou borda. */
val CozyAmberDeep = Color(0xFFA86F32)

/** Amarelo dourado — cards de stats/secundários. */
val CozyGold = Color(0xFFD9B25C)

/** Bege/areia — cards terciários. */
val CozyTan = Color(0xFFC8B998)

/** Creme — fundo de páginas do livro / cards "papel". */
val CozyCream = Color(0xFFF5EFE0)

/** Creme profundo — destaque sutil sobre creme. */
val CozyCreamDeep = Color(0xFFEAE0C9)

/** Marrom tinta — texto sobre creme (papel envelhecido). */
val CozyInk = Color(0xFF3E2E1E)

/** Vermelho-tijolo — erros/destrutivo (usado raramente). */
val CozyBrick = Color(0xFFB85450)

// ══════════════════════════════════════════════════════════════
// COLOR SCHEME (Material3)
// ══════════════════════════════════════════════════════════════

private val CozyColorScheme = lightColorScheme(
    primary = CozySage,
    onPrimary = Color.White,
    primaryContainer = CozySageSoft,
    onPrimaryContainer = CozyOlive,

    secondary = CozyAmber,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF1DCBE),
    onSecondaryContainer = CozyAmberDeep,

    tertiary = CozyGold,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFF7E8C2),
    onTertiaryContainer = Color(0xFF7A5C1F),

    background = CozySageMist,
    onBackground = CozyOlive,

    surface = CozyCream,
    onSurface = CozyInk,
    surfaceVariant = CozyTan.copy(alpha = 0.45f),
    onSurfaceVariant = CozyOlive,

    error = CozyBrick,
    onError = Color.White,
    errorContainer = Color(0xFFF5D9D7),
    onErrorContainer = CozyBrick,

    outline = CozyOlive.copy(alpha = 0.35f),
    outlineVariant = CozyTan
)

// ══════════════════════════════════════════════════════════════
// FORMAS (cantos generosamente arredondados)
// ══════════════════════════════════════════════════════════════

private val CozyShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(14.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(36.dp)
)

// ══════════════════════════════════════════════════════════════
// THEME
// ══════════════════════════════════════════════════════════════

@Composable
fun CozyTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = CozyColorScheme,
        shapes = CozyShapes,
        content = content
    )
}
