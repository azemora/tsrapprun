/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  EventNamingDialog.kt — tradução de fullapp/screens/         ║
 * ║  events.jsx (EventNamingA, sage version)                     ║
 * ║                                                              ║
 * ║  Tela full-screen sage com header editorial,                 ║
 * ║  strip de polaroides preview + campos de nome e data         ║
 * ║  + CTA cream "guardar evento".                               ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.camera

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
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
import com.tsrapprun.ui.chrome.Pastels
import com.tsrapprun.ui.chrome.Peach
import com.tsrapprun.ui.chrome.Polaroid
import com.tsrapprun.ui.chrome.Tag
import com.tsrapprun.ui.chrome.italicSerifText
import com.tsrapprun.ui.theme.CozyCream
import com.tsrapprun.ui.theme.CozySage

@Composable
fun EventNamingScreen(
    photoCount: Int,
    onSave: (eventName: String) -> Unit,
    onCancel: () -> Unit
) {
    var eventName by remember { mutableStateOf("") }

    val today = remember {
        val c = dateComponentsOf(nowMillis())
        val months = listOf(
            "janeiro", "fevereiro", "março", "abril", "maio", "junho",
            "julho", "agosto", "setembro", "outubro", "novembro", "dezembro"
        )
        "hoje, ${c.day} de ${months[c.monthIndex]}"
    }

    Box(modifier = Modifier.fillMaxSize().background(CozySage)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(horizontal = 24.dp)
        ) {
            // Top row: voltar + chapter + spacer
            Row(
                modifier = Modifier.fillMaxWidth().height(40.dp).padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier.size(36.dp).clickable(onClick = onCancel),
                    contentAlignment = Alignment.Center
                ) {
                    Text("‹", fontSize = 24.sp, fontWeight = FontWeight.Light, color = CozyCream)
                }
                Tag("novo evento — passo 2", color = CozyCream)
                Box(modifier = Modifier.size(36.dp))
            }

            Spacer(Modifier.height(20.dp))

            // Título italic em creme
            Text(
                italicSerifText(
                    prefix = "como vamos chamar\n",
                    italic = "esse momento",
                    suffix = "?",
                    italicColor = Butter,
                    defaultColor = CozyCream,
                    italicWeight = FontWeight.Light
                ),
                fontSize = 32.sp,
                lineHeight = 32.sp,
                letterSpacing = (-0.9).sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Normal
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "dê um nome carinhoso. você pode editar depois.",
                fontSize = 13.sp,
                color = CozyCream.copy(alpha = 0.7f),
                lineHeight = 19.sp
            )

            Spacer(Modifier.height(20.dp))

            // Strip de polaroides preview (3 cards)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
            ) {
                Polaroid(
                    tone = Pastels.sage,
                    width = 88.dp,
                    photoHeight = 80.dp,
                    rotation = -6f
                )
                Polaroid(
                    tone = Butter,
                    width = 88.dp,
                    photoHeight = 80.dp,
                    rotation = 4f,
                    caption = if (photoCount > 3) "+ ${photoCount - 3}" else null
                )
                Polaroid(
                    tone = Peach,
                    width = 88.dp,
                    photoHeight = 80.dp,
                    rotation = -3f
                )
            }

            Spacer(Modifier.height(24.dp))

            // Nome do evento
            Tag("nome do evento", color = CozyCream)
            Spacer(Modifier.height(8.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = CozyCream.copy(alpha = 0.12f),
                border = BorderStroke(1.4.dp, CozyCream.copy(alpha = 0.4f))
            ) {
                BasicTextField(
                    value = eventName,
                    onValueChange = { if (it.length <= 60) eventName = it },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 16.dp),
                    singleLine = true,
                    cursorBrush = SolidColor(Butter),
                    textStyle = TextStyle(
                        fontFamily = FontFamily.Serif,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = CozyCream,
                        letterSpacing = (-0.4).sp
                    ),
                    decorationBox = { inner ->
                        if (eventName.isEmpty()) {
                            Text(
                                "ex: passeio no parque",
                                fontFamily = FontFamily.Serif,
                                fontStyle = FontStyle.Italic,
                                fontSize = 18.sp,
                                color = CozyCream.copy(alpha = 0.45f)
                            )
                        }
                        inner()
                    }
                )
            }
            Spacer(Modifier.height(6.dp))
            Text(
                "✿ que dia bonito ✿",
                fontFamily = FontFamily.Cursive,
                fontSize = 16.sp,
                color = Butter.copy(alpha = 0.95f)
            )

            Spacer(Modifier.height(18.dp))

            // Quando aconteceu
            Tag("quando aconteceu", color = CozyCream)
            Spacer(Modifier.height(8.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = CozyCream.copy(alpha = 0.12f),
                border = BorderStroke(1.4.dp, CozyCream.copy(alpha = 0.33f))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        today,
                        fontFamily = FontFamily.Serif,
                        fontSize = 16.sp,
                        color = CozyCream
                    )
                    Text("▤", fontSize = 18.sp, color = CozyCream, fontWeight = FontWeight.Light)
                }
            }

            Spacer(Modifier.weight(1f))

            // CTA "guardar evento" — enabled: cream sólido com check butter
            //                       disabled: outline cream sutil (claro mas visível)
            val isEnabled = eventName.isNotBlank()
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = isEnabled) { onSave(eventName.trim()) },
                shape = RoundedCornerShape(22.dp),
                color = if (isEnabled) CozyCream else CozyCream.copy(alpha = 0.06f),
                shadowElevation = if (isEnabled) 14.dp else 0.dp,
                border = if (isEnabled) null else BorderStroke(1.4.dp, CozyCream.copy(alpha = 0.55f))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 22.dp, vertical = 20.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        if (isEnabled) "guardar evento" else "digite um nome pra continuar",
                        fontFamily = FontFamily.Serif,
                        fontSize = if (isEnabled) 17.sp else 14.sp,
                        fontStyle = if (isEnabled) FontStyle.Normal else FontStyle.Italic,
                        fontWeight = if (isEnabled) FontWeight.Bold else FontWeight.Medium,
                        color = if (isEnabled) OliveDeep else CozyCream.copy(alpha = 0.75f),
                        letterSpacing = (-0.2).sp
                    )
                    if (isEnabled) {
                        Spacer(Modifier.width(12.dp))
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(androidx.compose.foundation.shape.CircleShape)
                                .background(Butter),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "✓",
                                fontSize = 15.sp,
                                color = OliveDeep,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}
