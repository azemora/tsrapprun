/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  MesversarioAnnouncementScreen.kt — tradução de              ║
 * ║  fullapp/screens/mesversario.jsx                             ║
 * ║                                                              ║
 * ║  Fundo sage com número grande italic (display 200px),        ║
 * ║  polaroide sobreposta, selo + caveat "sete!", mensagem do    ║
 * ║  mês em card tracejado e mini stats.                         ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.child

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.tsrapprun.ui.chrome.Peach
import com.tsrapprun.ui.chrome.Polaroid
import com.tsrapprun.ui.chrome.Stamp
import com.tsrapprun.ui.chrome.Tag
import com.tsrapprun.ui.theme.CozyCream
import com.tsrapprun.ui.theme.CozySage

@Composable
fun MesversarioAnnouncementScreen(
    childFirstName: String,
    monthsCompleted: Int,
    onContinue: () -> Unit,
    onOpenMomentsList: () -> Unit
) {
    val today = run {
        val c = dateComponentsOf(nowMillis())
        val months = listOf("jan", "fev", "mar", "abr", "mai", "jun", "jul", "ago", "set", "out", "nov", "dez")
        "${c.day} ${months[c.monthIndex]} ${c.year}"
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CozySage)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            // Eyebrow
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier.size(36.dp).clickable(onClick = onContinue),
                    contentAlignment = Alignment.Center
                ) {
                    Text("‹", fontSize = 24.sp, fontWeight = FontWeight.Light, color = CozyCream)
                }
                Tag("✿ mesversário", color = CozyCream)
                Box(modifier = Modifier.size(36.dp))
            }

            // HERO scrollável
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
            ) {
                // HERO: número + polaroid lado a lado, sem sobreposição
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Coluna esquerda: número gigante + stamp
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "$monthsCompleted",
                            fontFamily = FontFamily.Serif,
                            fontStyle = FontStyle.Italic,
                            fontSize = 140.sp,
                            lineHeight = 130.sp,
                            fontWeight = FontWeight.Light,
                            color = CozyCream,
                            letterSpacing = (-6).sp
                        )
                        Spacer(Modifier.height(12.dp))
                        Stamp(
                            label = monthsCompleted.toString().padStart(2, '0'),
                            sub = "capítulo",
                            color = Butter,
                            rotation = -8f,
                            size = 60.dp
                        )
                    }
                    // Coluna direita: tag + caveat + polaroid
                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        Tag("meses", color = CozyCream)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            wordOf(monthsCompleted) + "!",
                            fontFamily = FontFamily.Cursive,
                            fontSize = 26.sp,
                            color = Butter,
                            lineHeight = 26.sp
                        )
                        Spacer(Modifier.height(12.dp))
                        Polaroid(
                            tone = Peach,
                            caption = today,
                            width = 130.dp,
                            photoHeight = 120.dp,
                            rotation = 6f
                        )
                    }
                }

                Spacer(Modifier.height(28.dp))

                // Mensagem do mês
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        color = CozyCream.copy(alpha = 0.12f),
                        border = BorderStroke(1.4.dp, CozyCream.copy(alpha = 0.4f))
                    ) {
                        Text(
                            messageFor(monthsCompleted, childFirstName),
                            fontFamily = FontFamily.Serif,
                            fontStyle = FontStyle.Italic,
                            fontSize = 19.sp,
                            lineHeight = 24.sp,
                            fontWeight = FontWeight.Normal,
                            color = CozyCream,
                            letterSpacing = (-0.4).sp,
                            modifier = Modifier.padding(18.dp)
                        )
                    }
                    // Tag flutuante "mensagem do mês"
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .offset(x = 18.dp, y = (-10).dp)
                            .background(CozySage)
                            .padding(horizontal = 8.dp)
                    ) {
                        Tag("mensagem do mês", color = Butter)
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Mini stats
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    MiniStat(
                        modifier = Modifier.weight(1f),
                        tagText = "idade",
                        value = "${monthsCompleted}m"
                    )
                    MiniStat(
                        modifier = Modifier.weight(1f),
                        tagText = "registrado",
                        value = "#${monthsCompleted}"
                    )
                }

                Spacer(Modifier.height(16.dp))
            }

            // CTA
            Box(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onOpenMomentsList),
                    shape = RoundedCornerShape(18.dp),
                    color = CozyCream,
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 22.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "registrar este mesversário",
                            fontFamily = FontFamily.Serif,
                            fontSize = 15.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = OliveDeep,
                            letterSpacing = (-0.2).sp
                        )
                        Spacer(Modifier.width(10.dp))
                        Text("→", fontSize = 16.sp, color = OliveDeep, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun MiniStat(modifier: Modifier, tagText: String, value: String) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = CozyCream.copy(alpha = 0.1f),
        border = BorderStroke(1.4.dp, CozyCream.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
            Tag(tagText, color = CozyCream)
            Spacer(Modifier.height(2.dp))
            Text(
                value,
                fontFamily = FontFamily.Serif,
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium,
                color = CozyCream,
                letterSpacing = (-0.4).sp
            )
        }
    }
}

private fun wordOf(n: Int): String = listOf(
    "zero", "um", "dois", "três", "quatro", "cinco",
    "seis", "sete", "oito", "nove", "dez", "onze", "doze"
).getOrNull(n) ?: "$n"

private fun messageFor(month: Int, name: String): String = when (month) {
    1 -> "primeiro mês completinho.\ntantas descobertas em tão pouco tempo."
    2 -> "dois meses de aventura.\ncada sorriso é um marco."
    3 -> "três meses já! que rápido o tempo voa."
    4 -> "quatro meses de muito amor e cuidado."
    5 -> "cinco meses — quanta personalidade vindo à tona."
    6 -> "meio ano!\nmarco grande, registro maior ainda."
    7 -> "sete meses do mais doce dos sorrisos.\n$name descobriu o mundo dos sabores e nada vai ser igual."
    8 -> "oito meses — engatinhar, balbuciar, encantar."
    9 -> "nove meses fora,\ncada dia uma novidade."
    10 -> "dez meses já!\ncaminhando pra um aninho."
    11 -> "onze meses — o último mesversário\nantes do grande dia."
    12 -> "1 ANINHO.\nque jornada incrível, registrada com carinho."
    else -> "mais um mês celebrado e guardado com carinho."
}
