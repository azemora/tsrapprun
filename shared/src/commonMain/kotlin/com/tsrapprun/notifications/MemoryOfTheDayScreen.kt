/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  MemoryOfTheDayScreen.kt - Tela "Memória do Dia"            ║
 * ║                                                             ║
 * ║  Acessada ao tocar a notificação diária.                    ║
 * ║  Carrega a entrada do dia (se houver) ou começa vazia.      ║
 * ║  Salva localmente ao confirmar — 1 entrada por dia.         ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.notifications

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

/**
 * @param dateKey          "YYYY-MM-DD" do dia que está sendo registrado.
 * @param loadEntry        Carrega entrada existente (ou null).
 * @param saveEntry        Persiste a entrada.
 * @param onBack           Voltar para Home.
 */
@Composable
fun MemoryOfTheDayScreen(
    dateKey: String,
    loadEntry: suspend (String) -> MemoryEntry?,
    saveEntry: suspend (MemoryEntry) -> Unit,
    onBack: () -> Unit
) {
    var text by remember { mutableStateOf("") }
    var loaded by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(dateKey) {
        val existing = loadEntry(dateKey)
        if (existing != null) text = existing.text
        loaded = true
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Memória do dia",
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = dateKey,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "O que aconteceu hoje que você quer lembrar?",
            style = MaterialTheme.typography.bodyLarge
        )

        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.fillMaxWidth(),
            minLines = 6,
            enabled = loaded,
            label = { Text("Sua memória") }
        )

        Spacer(Modifier.height(8.dp))

        Button(
            enabled = loaded && text.isNotBlank(),
            onClick = {
                scope.launch {
                    saveEntry(
                        MemoryEntry(
                            date = dateKey,
                            text = text.trim(),
                            linkedPhotoId = null,
                            createdAt = com.tsrapprun.currentTimeMillis()
                        )
                    )
                    onBack()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Salvar memória") }

        TextButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Voltar")
        }
    }
}
