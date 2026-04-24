/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  EventNamingDialog.kt - Nomear Evento Após Captura          ║
 * ║                                                             ║
 * ║  Exibido após o usuário finalizar a câmera contínua.        ║
 * ║  Permite dar nome ao evento e confirmar.                    ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.camera

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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

/**
 * Tela para nomear o evento após captura contínua.
 *
 * @param photoCount Número de fotos capturadas na sessão.
 * @param onSave Callback com o nome digitado pelo usuário.
 * @param onCancel Volta sem criar evento (fotos ficam como avulsas).
 */
@Composable
fun EventNamingScreen(
    photoCount: Int,
    onSave: (eventName: String) -> Unit,
    onCancel: () -> Unit
) {
    var eventName by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // ── Ícone de sucesso ──
            Text(
                text = "$photoCount",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "foto${if (photoCount != 1) "s" else ""} capturada${if (photoCount != 1) "s" else ""}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            // ── Campo de nome do evento ──
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Nome do Evento",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = eventName,
                        onValueChange = {
                            // Limite de 200 caracteres (validação local)
                            if (it.length <= 200) {
                                eventName = it
                                showError = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Ex: Aniversário Maria, Reunião Time...") },
                        singleLine = true,
                        isError = showError
                    )
                    if (showError) {
                        Text(
                            text = "Digite um nome para o evento",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Botões ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text("Pular")
                }

                Button(
                    onClick = {
                        if (eventName.isBlank()) {
                            showError = true
                        } else {
                            onSave(eventName.trim())
                        }
                    },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text("Salvar Evento")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Se pular, as fotos ficarão na galeria geral.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                textAlign = TextAlign.Center
            )
        }
    }
}
