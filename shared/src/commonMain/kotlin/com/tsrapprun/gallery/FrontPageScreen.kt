/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  FrontPageScreen.kt — Welcome (pré-auth)                     ║
 * ║                                                              ║
 * ║  Tradução de fullapp/screens/front-page.jsx (Variação D).    ║
 * ║  Tela de boas-vindas mostrada ANTES do login.                ║
 * ║  O CTA "vamos começar" leva para LoginScreen.                ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.gallery

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.tsrapprun.ui.chrome.PrimaryButton
import com.tsrapprun.ui.chrome.Stamp
import com.tsrapprun.ui.chrome.Tag
import com.tsrapprun.ui.chrome.TornDivider
import com.tsrapprun.ui.theme.CozyCream
import com.tsrapprun.ui.theme.CozySage

/**
 * Tela de boas-vindas (welcome / onboarding).
 * "vamos começar" deve navegar para [LoginScreen].
 */
@Composable
fun FrontPageScreen(
    onStart: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().background(CozySage)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            // ═══ TOP: Scrapbook collage em sage ═══
            ScrapbookHeader()

            // Onda de papel rasgado dividindo
            TornDivider(color = CozySage, height = 14.dp)

            // ═══ BOTTOM: Editorial ═══
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(start = 28.dp, end = 28.dp, top = 20.dp, bottom = 26.dp)
            ) {
                // Eyebrow "cresci·com — vol. 01"
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.width(22.dp).height(1.dp).background(Color.White.copy(alpha = 0.5f))
                    )
                    Spacer(Modifier.width(10.dp))
                    Tag("cresci·com — vol. 01", color = Color.White)
                }

                Spacer(Modifier.height(14.dp))

                // Título editorial multilinha
                Text(
                    buildAnnotatedString {
                        withStyle(SpanStyle(color = Color.White, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Normal)) {
                            append("memórias\n")
                        }
                        withStyle(SpanStyle(color = Butter, fontFamily = FontFamily.Serif, fontStyle = FontStyle.Italic, fontWeight = FontWeight.Light)) {
                            append("acolhedoras")
                        }
                        withStyle(SpanStyle(color = Color.White, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Normal)) {
                            append(",\nseguras\n")
                        }
                        withStyle(SpanStyle(color = Color.White.copy(alpha = 0.55f), fontFamily = FontFamily.Serif, fontWeight = FontWeight.Normal)) {
                            append("&")
                        }
                        withStyle(SpanStyle(color = Color.White, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Normal)) {
                            append(" suas.")
                        }
                    },
                    fontSize = 42.sp,
                    lineHeight = 41.sp,
                    letterSpacing = (-1.3).sp
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    "do diário do dia-a-dia ao livro de memórias do seu filho.",
                    fontSize = 13.5.sp,
                    color = Color.White.copy(alpha = 0.78f),
                    lineHeight = 21.sp
                )

                Spacer(Modifier.weight(1f))

                // CTA olive sólido — "vamos começar" leva ao LoginScreen
                PrimaryButton(
                    text = "vamos começar",
                    onClick = onStart,
                    background = OliveDeep,
                    contentColor = CozyCream
                )

                Spacer(Modifier.height(10.dp))

                Text(
                    "✦ ficam só no seu aparelho ✦",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.5.sp,
                    letterSpacing = 0.4.sp,
                    color = Color.White.copy(alpha = 0.65f),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun ScrapbookHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(360.dp)
    ) {
        // Polaroide rosa — "dia 1 ✿"
        Polaroid(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 28.dp, top = 50.dp),
            tone = Peach,
            caption = "dia 1 ✿",
            width = 140.dp,
            photoHeight = 130.dp,
            rotation = -7f
        )
        // Polaroide butter
        Polaroid(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 22.dp, top = 82.dp),
            tone = Butter,
            caption = null,
            width = 124.dp,
            photoHeight = 110.dp,
            rotation = 6f
        )
        // Fitas washi
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 84.dp, top = 38.dp)
                .graphicsLayer { rotationZ = -10f }
                .size(width = 56.dp, height = 18.dp)
                .background(Butter.copy(alpha = 0.55f))
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 56.dp, top = 70.dp)
                .graphicsLayer { rotationZ = 8f }
                .size(width = 52.dp, height = 18.dp)
                .background(Color.White.copy(alpha = 0.45f))
        )
        // Folha sticker
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 110.dp, bottom = 38.dp)
                .graphicsLayer { rotationZ = -22f }
        ) {
            Text("✿", fontSize = 36.sp, color = Color.White.copy(alpha = 0.85f))
        }
        // Selo de capítulo
        Stamp(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 36.dp),
            label = "01",
            sub = "capítulo",
            color = Butter,
            rotation = 6f,
            size = 70.dp
        )
    }
}
