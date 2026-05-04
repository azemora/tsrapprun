/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  HomeScreen.kt — Tela de gerenciamento (paleta cozy)         ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.auth

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tsrapprun.camera.EventData
import com.tsrapprun.security.UserData
import com.tsrapprun.ui.theme.CozyAmber
import com.tsrapprun.ui.theme.CozyBrick
import com.tsrapprun.ui.theme.CozyCream
import com.tsrapprun.ui.theme.CozyCreamDeep
import com.tsrapprun.ui.theme.CozyGold
import com.tsrapprun.ui.theme.CozyInk
import com.tsrapprun.ui.theme.CozyOlive
import com.tsrapprun.ui.theme.CozySage
import com.tsrapprun.ui.theme.CozySageMist
import com.tsrapprun.ui.theme.CozyTan

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
    onTestNotification: () -> Unit = {},
    onSignOutClick: () -> Unit,
    onBack: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(CozySageMist, CozyCream)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(horizontal = 20.dp, vertical = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // ── Header com voltar ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(40.dp).clickable(onClick = onBack),
                    shape = CircleShape,
                    color = CozyCream,
                    border = androidx.compose.foundation.BorderStroke(1.dp, CozyTan)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("←", fontSize = 18.sp, color = CozyOlive)
                    }
                }
                Spacer(Modifier.weight(1f))
                Text(
                    "ajustes",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = CozyOlive
                )
                Spacer(Modifier.weight(1f))
                Spacer(Modifier.size(40.dp))
            }

            Spacer(Modifier.height(20.dp))

            // ── Card de perfil ──
            ProfileCard(userData = userData)

            Spacer(Modifier.height(16.dp))

            // ── Estatísticas (dois cards lado a lado) ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    value = "$photoCount",
                    label = "fotos",
                    emoji = "📸",
                    bg = CozyAmber,
                    fg = Color.White
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    value = "$storageUsedMB MB",
                    label = "armazenado",
                    emoji = "💾",
                    bg = CozyGold,
                    fg = Color.White
                )
            }

            Spacer(Modifier.height(20.dp))

            // ── Ações ──
            Text(
                "o que você quer fazer?",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = CozyOlive.copy(alpha = 0.7f),
                modifier = Modifier.padding(start = 8.dp)
            )

            Spacer(Modifier.height(10.dp))

            ActionButton(
                label = "registrar evento",
                emoji = "📷",
                bg = CozySage,
                fg = Color.White,
                onClick = onRegisterEvent
            )
            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SmallActionButton(
                    modifier = Modifier.weight(1f),
                    label = "importar",
                    emoji = "📥",
                    onClick = onImportPhotos
                )
                SmallActionButton(
                    modifier = Modifier.weight(1f),
                    label = "galeria",
                    emoji = "🖼️",
                    onClick = onOpenGallery
                )
            }

            Spacer(Modifier.height(8.dp))

            Text(
                "🔒 fotos criptografadas localmente",
                fontSize = 11.sp,
                color = CozyOlive.copy(alpha = 0.55f),
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                textAlign = TextAlign.Center
            )

            // ── Eventos recentes ──
            if (events.isNotEmpty()) {
                Spacer(Modifier.height(28.dp))
                Text(
                    "eventos recentes",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = CozyOlive
                )
                Spacer(Modifier.height(10.dp))
                events.sortedByDescending { it.createdAt }.take(5).forEach { event ->
                    EventRow(
                        event = event,
                        onClick = { onOpenEvent(event) }
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }

            Spacer(Modifier.height(28.dp))

            // ── Testar notificação (debug) ──
            SmallActionButton(
                modifier = Modifier.fillMaxWidth(),
                label = "testar notificação (5s)",
                emoji = "🔔",
                onClick = onTestNotification
            )
            Text(
                "envia uma notificação em ~5s pra validar permissões. coloque o app em segundo plano pra ver o pop-up.",
                fontSize = 11.sp,
                color = CozyOlive.copy(alpha = 0.55f),
                modifier = Modifier.fillMaxWidth().padding(top = 6.dp, start = 6.dp, end = 6.dp),
                textAlign = TextAlign.Center,
                lineHeight = 16.sp
            )

            Spacer(Modifier.height(20.dp))

            // ── Sair (destrutivo, mais discreto) ──
            Button(
                onClick = onSignOutClick,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = CozyBrick
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Text("sair da conta", fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ProfileCard(userData: UserData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(56.dp),
                shape = CircleShape,
                color = CozySage
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = userData.displayName?.firstOrNull()?.uppercase() ?: "?",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                userData.displayName?.let {
                    Text(
                        it,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = CozyOlive,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                userData.email?.let {
                    Text(
                        it,
                        fontSize = 12.sp,
                        color = CozyOlive.copy(alpha = 0.65f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    modifier: Modifier,
    value: String,
    label: String,
    emoji: String,
    bg: Color,
    fg: Color
) {
    Card(
        modifier = modifier.height(110.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = bg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(emoji, fontSize = 22.sp)
            Column {
                Text(value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = fg)
                Text(label, fontSize = 12.sp, color = fg.copy(alpha = 0.85f))
            }
        }
    }
}

@Composable
private fun ActionButton(
    label: String,
    emoji: String,
    bg: Color,
    fg: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(28.dp),
        color = bg
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(emoji, fontSize = 22.sp)
            Spacer(Modifier.width(12.dp))
            Text(
                label,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = fg
            )
        }
    }
}

@Composable
private fun SmallActionButton(
    modifier: Modifier,
    label: String,
    emoji: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(50.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(25.dp),
        color = CozyCream,
        border = androidx.compose.foundation.BorderStroke(1.dp, CozyTan)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(emoji, fontSize = 16.sp)
            Spacer(Modifier.width(6.dp))
            Text(label, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = CozyOlive)
        }
    }
}

@Composable
private fun EventRow(event: EventData, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = RoundedCornerShape(14.dp),
                color = CozyCreamDeep
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "${event.photoCount}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = CozyInk
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = CozyInk,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = formatDate(event.createdAt),
                    fontSize = 12.sp,
                    color = CozyOlive.copy(alpha = 0.6f)
                )
            }
            Text(
                text = "${event.photoCount} ${if (event.photoCount == 1) "foto" else "fotos"}",
                fontSize = 12.sp,
                color = CozyAmber,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

private fun formatDate(epochMillis: Long): String {
    val c = com.tsrapprun.platform.dateComponentsOf(epochMillis)
    val day = c.day.toString().padStart(2, '0')
    val month = (c.monthIndex + 1).toString().padStart(2, '0')
    val year = c.year
    val hour = c.hour.toString().padStart(2, '0')
    val min = c.minute.toString().padStart(2, '0')
    return "$day/$month/$year $hour:$min"
}
