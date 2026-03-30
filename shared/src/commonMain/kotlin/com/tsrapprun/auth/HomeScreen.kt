/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  HomeScreen.kt - Tela Principal redesenhada                  ║
 * ║                                                             ║
 * ║  3 ações principais + eventos recentes + estatísticas.      ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tsrapprun.camera.EventData
import com.tsrapprun.security.UserData

/**
 * Tela principal após autenticação.
 */
@Composable
fun HomeScreen(
    userData: UserData,
    photoCount: Int = 0,
    storageUsedMB: String = "0.0",
    events: List<EventData> = emptyList(),
    onRegisterEvent: () -> Unit = {},
    onImportPhotos: () -> Unit = {},
    onOpenGallery: () -> Unit = {},
    onOpenEvent: (EventData) -> Unit = {},
    onSignOutClick: () -> Unit,
    onBack: () -> Unit = {}
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ── Voltar para FrontPage ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onBack) {
                    Text("\u2190 Voltar", style = MaterialTheme.typography.titleMedium)
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    "Gerenciamento",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.width(80.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ── Perfil compacto ──
            ProfileRow(userData = userData)

            Spacer(modifier = Modifier.height(20.dp))

            // ── Estatísticas ──
            StatsCard(photoCount = photoCount, storageUsedMB = storageUsedMB)

            Spacer(modifier = Modifier.height(24.dp))

            // ── Botão principal: Registrar Evento ──
            Button(
                onClick = onRegisterEvent,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text("Registrar Evento", fontSize = 18.sp, fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ── Botões secundários: Importar e Galeria ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onImportPhotos,
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text("Importar Fotos", fontSize = 14.sp)
                }

                OutlinedButton(
                    onClick = onOpenGallery,
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text("Galeria", fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Fotos criptografadas localmente (AES-256)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )

            // ── Eventos Recentes ──
            if (events.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Eventos Recentes",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Mostra até 5 eventos mais recentes
                events.sortedByDescending { it.createdAt }.take(5).forEach { event ->
                    EventCard(
                        event = event,
                        onClick = { onOpenEvent(event) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Logout ──
            Button(
                onClick = onSignOutClick,
                modifier = Modifier.fillMaxWidth().height(44.dp),
                shape = RoundedCornerShape(22.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Sair da conta", color = MaterialTheme.colorScheme.onError)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/** Perfil compacto em linha (avatar + nome + email). */
@Composable
private fun ProfileRow(userData: UserData) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(44.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary
        ) {
            Text(
                text = userData.displayName?.firstOrNull()?.uppercase() ?: "?",
                modifier = Modifier.fillMaxSize().padding(top = 6.dp),
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            userData.displayName?.let {
                Text(it, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            }
            userData.email?.let {
                Text(it, style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
            }
        }
    }
}

/** Card de estatísticas (fotos + MB). */
@Composable
private fun StatsCard(photoCount: Int, storageUsedMB: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("$photoCount", style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
                Text("fotos", style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("$storageUsedMB MB", style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
                Text("armazenamento", style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer)
            }
        }
    }
}

/** Card de evento recente (nome, data, contagem). */
@Composable
private fun EventCard(event: EventData, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ícone placeholder do evento
            Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    text = "${event.photoCount}",
                    modifier = Modifier.fillMaxSize().padding(top = 8.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = formatDate(event.createdAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
            Text(
                text = "${event.photoCount} fotos",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/** Formata epoch millis para DD/MM/YYYY HH:mm. */
private fun formatDate(epochMillis: Long): String {
    // Formato simples sem dependência de java.time (compatível com minSdk 24)
    val cal = java.util.Calendar.getInstance().apply { timeInMillis = epochMillis }
    val day = cal.get(java.util.Calendar.DAY_OF_MONTH).toString().padStart(2, '0')
    val month = (cal.get(java.util.Calendar.MONTH) + 1).toString().padStart(2, '0')
    val year = cal.get(java.util.Calendar.YEAR)
    val hour = cal.get(java.util.Calendar.HOUR_OF_DAY).toString().padStart(2, '0')
    val min = cal.get(java.util.Calendar.MINUTE).toString().padStart(2, '0')
    return "$day/$month/$year $hour:$min"
}
