/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  MomentRegistrationScreen.kt - Registro de Momento          ║
 * ║                                                             ║
 * ║  Tela simples para o usuário escrever o que aconteceu       ║
 * ║  hoje ou esta semana.                                       ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.moments

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MomentRegistrationScreen(
    type: MomentType,
    onSave: (text: String) -> Unit,
    onCancel: () -> Unit
) {
    var text by remember { mutableStateOf("") }

    val title = when (type) {
        MomentType.DAILY -> "O que aconteceu hoje?"
        MomentType.WEEKLY -> "O que aconteceu essa semana?"
    }

    val hint = when (type) {
        MomentType.DAILY -> "Escreva sobre o seu dia..."
        MomentType.WEEKLY -> "Resuma sua semana..."
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(24.dp)
        ) {
            // ── Header ──
            TextButton(onClick = onCancel) {
                Text("\u2190 Voltar", style = MaterialTheme.typography.titleMedium)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Emoji decorativo ──
            Text(
                text = if (type == MomentType.DAILY) "\uD83D\uDDD3\uFE0F" else "\uD83D\uDCDD",
                fontSize = 40.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ── Título ──
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = formatTodayDate(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ── Campo de texto ──
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                placeholder = { Text(hint) },
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ── Botão salvar ──
            Button(
                onClick = { if (text.isNotBlank()) onSave(text.trim()) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                enabled = text.isNotBlank()
            ) {
                Text("Salvar Registro", fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

private fun formatTodayDate(): String {
    val c = com.tsrapprun.platform.dateComponentsOf(com.tsrapprun.platform.nowMillis())
    val d = c.day
    val m = c.monthIndex
    val y = c.year
    val months = listOf(
        "janeiro", "fevereiro", "mar\u00e7o", "abril", "maio", "junho",
        "julho", "agosto", "setembro", "outubro", "novembro", "dezembro"
    )
    return "$d de ${months[m]} de $y"
}
