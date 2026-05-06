/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  LoginScreen.kt — tradução de fullapp/screens/login.jsx      ║
 * ║                                                              ║
 * ║  Fundo sage com mini scrapbook (2 polaroides + folha + selo) ║
 * ║  + título editorial "memórias acolhedoras, seguras & suas"   ║
 * ║  + botões "continuar com Google" / "continuar como visitante"║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.auth

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tsrapprun.ui.chrome.Butter
import com.tsrapprun.ui.chrome.OliveDeep
import com.tsrapprun.ui.chrome.Peach
import com.tsrapprun.ui.chrome.Polaroid
import com.tsrapprun.ui.chrome.Stamp
import com.tsrapprun.ui.chrome.Tag
import com.tsrapprun.ui.theme.CozyCream
import com.tsrapprun.ui.theme.CozySage

@Composable
fun LoginScreen(
    authState: AuthState,
    onSignInClick: () -> Unit,
    onContinueAsGuest: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CozySage)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(horizontal = 28.dp, vertical = 32.dp)
        ) {
            // ── Eyebrow ──
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .width(22.dp)
                        .height(1.dp)
                        .background(CozyCream.copy(alpha = 0.5f))
                )
                Spacer(Modifier.width(10.dp))
                Tag("cresci·com — entrar", color = CozyCream)
                Spacer(Modifier.width(10.dp))
                Box(
                    Modifier
                        .weight(1f)
                        .height(1.dp)
                        .background(CozyCream.copy(alpha = 0.18f))
                )
            }

            Spacer(Modifier.weight(1f))

            // ── Mini scrapbook ──
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                Polaroid(
                    modifier = Modifier.align(Alignment.TopStart),
                    tone = Peach,
                    caption = "dia 1",
                    width = 120.dp,
                    photoHeight = 110.dp,
                    rotation = -8f
                )
                Polaroid(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 18.dp),
                    tone = Butter,
                    caption = "primeira vez",
                    width = 108.dp,
                    photoHeight = 96.dp,
                    rotation = 7f
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 86.dp, bottom = 8.dp)
                        .graphicsLayer { rotationZ = -18f }
                ) {
                    Text("✿", fontSize = 36.sp, color = CozyCream)
                }
                Stamp(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 20.dp, bottom = 0.dp),
                    label = "01",
                    sub = "capítulo",
                    color = Butter,
                    rotation = 6f,
                    size = 56.dp
                )
            }

            Spacer(Modifier.height(28.dp))

            // ── Título ──
            Text(
                buildAnnotatedString {
                    withStyle(SpanStyle(color = CozyCream, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Normal)) {
                        append("memórias\n")
                    }
                    withStyle(SpanStyle(color = Butter, fontFamily = FontFamily.Serif, fontStyle = FontStyle.Italic, fontWeight = FontWeight.Light)) {
                        append("acolhedoras")
                    }
                    withStyle(SpanStyle(color = CozyCream, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Normal)) {
                        append(",\nseguras & suas.")
                    }
                },
                fontSize = 36.sp,
                lineHeight = 35.sp,
                letterSpacing = (-1.1).sp
            )

            Spacer(Modifier.height(14.dp))

            Text(
                "para começar, escolha como entrar.",
                fontSize = 13.5.sp,
                color = CozyCream.copy(alpha = 0.78f),
                lineHeight = 21.sp
            )

            Spacer(Modifier.weight(1f))

            // ── CTA stack ──
            when (authState) {
                is AuthState.Loading -> LoadingState()
                is AuthState.Error -> {
                    ErrorState(authState.message)
                    Spacer(Modifier.height(12.dp))
                    GoogleButton(onSignInClick)
                    Spacer(Modifier.height(10.dp))
                    GuestButton(onContinueAsGuest)
                }
                is AuthState.Unauthenticated -> {
                    GoogleButton(onSignInClick)
                    Spacer(Modifier.height(10.dp))
                    GuestButton(onContinueAsGuest)
                }
                is AuthState.Authenticated -> {
                    // navegação cuida
                }
            }

            Spacer(Modifier.height(12.dp))

            Text(
                "🔒 ficam só no seu aparelho",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                letterSpacing = 0.4.sp,
                color = CozyCream.copy(alpha = 0.6f),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun GoogleButton(onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = CozyCream,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Text("G", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4285F4))
            }
            Spacer(Modifier.width(10.dp))
            Text(
                "continuar com Google",
                fontFamily = FontFamily.Serif,
                fontSize = 15.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = OliveDeep,
                letterSpacing = (-0.1).sp
            )
        }
    }
}

@Composable
private fun GuestButton(onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = Color.Transparent,
        border = BorderStroke(1.4.dp, CozyCream)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "continuar como convidado",
                fontFamily = FontFamily.Serif,
                fontSize = 14.5.sp,
                fontWeight = FontWeight.Medium,
                color = CozyCream
            )
        }
    }
}

@Composable
private fun LoadingState() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(28.dp),
            color = CozyCream,
            strokeWidth = 2.5.dp
        )
        Spacer(Modifier.width(12.dp))
        Text(
            "preparando suas memórias…",
            fontFamily = FontFamily.Serif,
            fontStyle = FontStyle.Italic,
            fontSize = 14.sp,
            color = CozyCream.copy(alpha = 0.85f)
        )
    }
}

@Composable
private fun ErrorState(message: String) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = CozyCream.copy(alpha = 0.18f),
        border = BorderStroke(1.dp, CozyCream.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            message,
            fontSize = 13.sp,
            color = CozyCream,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(14.dp)
        )
    }
}
