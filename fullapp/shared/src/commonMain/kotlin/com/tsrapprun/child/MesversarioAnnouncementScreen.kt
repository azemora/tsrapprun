/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  MesversarioAnnouncementScreen.kt — Tela de comemoração      ║
 * ║                                                              ║
 * ║  Mostrada quando a criança completa um novo mês de vida.     ║
 * ║  Animação simples (escala/fade). Texto celebrativo com       ║
 * ║  o nome e o número de meses.                                 ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.child

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tsrapprun.ui.theme.CozyAmber
import com.tsrapprun.ui.theme.CozyCream
import com.tsrapprun.ui.theme.CozyGold
import com.tsrapprun.ui.theme.CozyOlive
import com.tsrapprun.ui.theme.CozySageMist

@Composable
fun MesversarioAnnouncementScreen(
    childFirstName: String,
    monthsCompleted: Int,
    onContinue: () -> Unit,
    onOpenMomentsList: () -> Unit
) {
    var startAnim by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { startAnim = true }

    val scale by animateFloatAsState(
        targetValue = if (startAnim) 1f else 0.5f,
        animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing),
        label = "mesversario_scale"
    )
    val alpha by animateFloatAsState(
        targetValue = if (startAnim) 1f else 0f,
        animationSpec = tween(durationMillis = 600),
        label = "mesversario_alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        CozyGold,
                        CozyAmber,
                        CozyAmber
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(horizontal = 32.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // ── Confete emoji em círculo ──
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .scale(scale)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Text("🎉", fontSize = 80.sp)
            }

            Spacer(Modifier.height(24.dp))

            Text(
                "mesversário!",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.85f),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(12.dp))

            // ── Nome + idade ──
            Text(
                text = childFirstName,
                fontSize = 56.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                lineHeight = 60.sp
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = if (monthsCompleted == 1) "fez 1 mês" else "fez $monthsCompleted meses",
                fontSize = 28.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White.copy(alpha = 0.95f),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(16.dp))

            // ── Mensagem celebrativa ──
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White.copy(alpha = 0.18f))
                    .padding(20.dp)
            ) {
                Text(
                    text = celebrationMessage(monthsCompleted),
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.95f),
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )
            }

            Spacer(Modifier.height(40.dp))

            // ── Ações ──
            Button(
                onClick = onOpenMomentsList,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = CozyAmber
                )
            ) {
                Text("ver os registros do mês", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = onContinue,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(25.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color.White.copy(alpha = 0.7f)),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.White
                )
            ) {
                Text("continuar", fontSize = 15.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

private fun celebrationMessage(monthsCompleted: Int): String = when (monthsCompleted) {
    1 -> "primeiro mês completinho! tantas descobertas em tão pouco tempo."
    2 -> "dois meses de aventura. cada sorriso é um marco."
    3 -> "três meses já! que rápido o tempo voa."
    4 -> "quatro meses de muito amor e cuidado."
    5 -> "cinco meses — quanta personalidade vindo à tona!"
    6 -> "meio ano! marco grande, registro maior ainda."
    7 -> "sete meses e tantas histórias para contar."
    8 -> "oito meses — engatinhar, balbuciar, encantar."
    9 -> "nove meses fora, cada dia uma novidade."
    10 -> "dez meses já! caminhando pra um aninho."
    11 -> "onze meses — o último mesversário antes do grande dia."
    12 -> "1 ANINHO! que jornada incrível, registrada com carinho."
    else -> "mais um mês celebrado e guardado com carinho."
}
