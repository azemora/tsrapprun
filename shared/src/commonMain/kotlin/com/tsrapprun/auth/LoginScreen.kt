/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  LoginScreen.kt — Tela de login (paleta cozy)                ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.tsrapprun.ui.theme.CozySage
import com.tsrapprun.ui.theme.CozySageMist

@Composable
fun LoginScreen(
    authState: AuthState,
    onSignInClick: () -> Unit
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
                .padding(horizontal = 28.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // ── Símbolo / mascote ──
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(CozySage),
                contentAlignment = Alignment.Center
            ) {
                Text("🌿", fontSize = 48.sp) // 🌿
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "TSR App",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = CozyOlive
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "memórias acolhedoras,\nseguras e suas",
                fontSize = 15.sp,
                color = CozyOlive.copy(alpha = 0.75f),
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(Modifier.height(48.dp))

            // ── Conteúdo dinâmico ──
            when (authState) {
                is AuthState.Loading -> LoadingContent()
                is AuthState.Unauthenticated -> GoogleSignInButton(onClick = onSignInClick)
                is AuthState.Error -> ErrorContent(
                    message = authState.message,
                    onRetryClick = onSignInClick
                )
                is AuthState.Authenticated -> { /* navegação cuida */ }
            }

            Spacer(Modifier.height(48.dp))

            DataProtectionNotice()
        }
    }
}

@Composable
private fun LoadingContent() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(40.dp),
            color = CozySage,
            strokeWidth = 3.dp
        )
        Text(
            text = "preparando suas memórias…",
            fontSize = 14.sp,
            color = CozyOlive.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun GoogleSignInButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        shape = RoundedCornerShape(30.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = CozyAmber,
            contentColor = Color.White
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 1.dp
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "G",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4285F4)
                )
            }
            Spacer(Modifier.width(14.dp))
            Text(
                text = "Entrar com Google",
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetryClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Text(
                text = message,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onErrorContainer,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(20.dp)
            )
        }

        OutlinedButton(
            onClick = onRetryClick,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(26.dp),
            border = BorderStroke(1.5.dp, CozyAmber),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = CozyAmber
            )
        ) {
            Text("Tentar novamente", fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun DataProtectionNotice() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CozyCream),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, CozyGold.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🌿 seus dados ficam aqui com você",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = CozyOlive
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "fotos criptografadas localmente. envio para a nuvem é opcional. usamos só seu nome e email para identificação.",
                fontSize = 12.sp,
                color = CozyOlive.copy(alpha = 0.65f),
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )
        }
    }
}
