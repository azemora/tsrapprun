/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  ReminderCaptureScreen.kt — gravação de áudio (sage)         ║
 * ║                                                              ║
 * ║  Reusa o mesmo AudioBridge do registro rápido, mas sem       ║
 * ║  câmera. Visual sage como a tela de nomear evento — feel     ║
 * ║  de "captura rápida pessoal".                                ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.reminders

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tsrapprun.audio.AudioBridge
import com.tsrapprun.audio.RecordingState
import com.tsrapprun.platform.nowMillis
import com.tsrapprun.ui.chrome.Butter
import com.tsrapprun.ui.chrome.OliveDeep
import com.tsrapprun.ui.chrome.Tag
import com.tsrapprun.ui.chrome.italicSerifText
import com.tsrapprun.ui.theme.CozyCream
import com.tsrapprun.ui.theme.CozySage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ReminderCaptureScreen(
    onConfirm: (text: String, dueAt: Long?) -> Unit,
    onBack: () -> Unit
) {
    var state by remember { mutableStateOf(RecordingState.IDLE) }
    var transcription by remember { mutableStateOf("") }
    var dueAt by remember { mutableStateOf<Long?>(null) }
    var startMs by remember { mutableStateOf(0L) }
    var elapsedMs by remember { mutableStateOf(0L) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(state) {
        if (state == RecordingState.RECORDING) {
            while (state == RecordingState.RECORDING) {
                elapsedMs = nowMillis() - startMs
                delay(100)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(CozySage)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(horizontal = 24.dp)
        ) {
            // Header sage editorial
            Row(
                modifier = Modifier.fillMaxWidth().height(40.dp).padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier.size(36.dp).clickable(onClick = onBack),
                    contentAlignment = Alignment.Center
                ) {
                    Text("‹", fontSize = 24.sp, fontWeight = FontWeight.Light, color = CozyCream)
                }
                Tag("✦ novo lembrete", color = CozyCream)
                Box(modifier = Modifier.size(36.dp))
            }

            Spacer(Modifier.height(20.dp))

            Text(
                italicSerifText(
                    prefix = "fale o que ",
                    italic = "não pode esquecer",
                    suffix = ".",
                    italicColor = Butter,
                    defaultColor = CozyCream,
                    italicWeight = FontWeight.Light
                ),
                fontSize = 30.sp,
                lineHeight = 32.sp,
                letterSpacing = (-0.7).sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Normal
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "vamos transcrever pra você. fica salvo aqui pra consultar depois.",
                fontSize = 13.sp,
                color = CozyCream.copy(alpha = 0.7f),
                lineHeight = 19.sp
            )

            // Mic
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                when (state) {
                    RecordingState.IDLE -> if (transcription.isBlank()) {
                        IdleMic(onStart = {
                            scope.launch {
                                if (AudioBridge.startRecording()) {
                                    startMs = nowMillis()
                                    elapsedMs = 0L
                                    state = RecordingState.RECORDING
                                } else {
                                    state = RecordingState.ERROR
                                }
                            }
                        })
                    } else {
                        TranscriptionPreview(
                            text = transcription,
                            dueAt = dueAt,
                            onRerecord = {
                                transcription = ""
                                dueAt = null
                                elapsedMs = 0L
                            },
                            onPickDate = { offsetDays ->
                                dueAt = if (offsetDays == null) null
                                else nowMillis() + offsetDays * 24L * 60 * 60 * 1000
                            }
                        )
                    }
                    RecordingState.RECORDING -> RecordingMic(elapsedMs = elapsedMs, onStop = {
                        state = RecordingState.PROCESSING
                        scope.launch {
                            val text = AudioBridge.stopAndTranscribe()
                            if (text.isNullOrBlank()) {
                                state = RecordingState.ERROR
                            } else {
                                val parsed = parseReminderText(text, nowMillis())
                                transcription = parsed.cleanedText
                                dueAt = parsed.dueAt
                                state = RecordingState.IDLE
                            }
                        }
                    })
                    RecordingState.PROCESSING -> ProcessingMic()
                    RecordingState.ERROR -> ErrorMic(onRetry = { state = RecordingState.IDLE })
                }
            }

            // CTA salvar (só aparece com transcription pronta)
            if (transcription.isNotBlank()) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onConfirm(transcription.trim(), dueAt) },
                    shape = RoundedCornerShape(22.dp),
                    color = CozyCream,
                    shadowElevation = 14.dp
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 18.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = italicSerifText(
                                italic = "guardar lembrete",
                                italicColor = OliveDeep,
                                defaultColor = OliveDeep,
                                italicWeight = FontWeight.Bold
                            ),
                            fontFamily = FontFamily.Serif,
                            fontSize = 17.sp,
                            color = OliveDeep
                        )
                        Spacer(Modifier.width(12.dp))
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Butter),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("✓", fontSize = 15.sp, color = OliveDeep, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
            } else {
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun IdleMic(onStart: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            modifier = Modifier.size(140.dp).clickable(onClick = onStart),
            shape = CircleShape,
            color = CozyCream,
            shadowElevation = 14.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text("🎙", fontSize = 56.sp)
            }
        }
        Spacer(Modifier.height(20.dp))
        Text(
            text = italicSerifText(
                prefix = "tocar pra ",
                italic = "falar",
                italicColor = Butter,
                defaultColor = CozyCream,
                italicWeight = FontWeight.Bold
            ),
            fontFamily = FontFamily.Serif,
            fontSize = 18.sp
        )
    }
}

@Composable
private fun RecordingMic(elapsedMs: Long, onStop: () -> Unit) {
    val infinite = rememberInfiniteTransition(label = "pulse")
    val pulse by infinite.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 700),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    val seconds = (elapsedMs / 1000).toInt()
    val mm = seconds / 60
    val ss = seconds % 60
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            modifier = Modifier
                .size(140.dp)
                .graphicsLayer { scaleX = pulse; scaleY = pulse }
                .clickable(onClick = onStop),
            shape = CircleShape,
            color = Butter,
            shadowElevation = 18.dp,
            border = BorderStroke(4.dp, CozyCream)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(OliveDeep)
                )
            }
        }
        Spacer(Modifier.height(20.dp))
        Text(
            "${mm.toString().padStart(2, '0')}:${ss.toString().padStart(2, '0')}",
            fontFamily = FontFamily.Monospace,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Butter
        )
        Spacer(Modifier.height(6.dp))
        Text(
            "tocar pra parar",
            fontFamily = FontFamily.Serif,
            fontStyle = FontStyle.Italic,
            fontSize = 13.sp,
            color = CozyCream.copy(alpha = 0.85f)
        )
    }
}

@Composable
private fun ProcessingMic() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        CircularProgressIndicator(
            modifier = Modifier.size(60.dp),
            color = Butter,
            strokeWidth = 4.dp
        )
        Spacer(Modifier.height(20.dp))
        Text(
            text = italicSerifText(
                italic = "transcrevendo",
                suffix = "...",
                italicColor = Butter,
                defaultColor = CozyCream,
                italicWeight = FontWeight.Bold
            ),
            fontFamily = FontFamily.Serif,
            fontSize = 18.sp
        )
    }
}

@Composable
private fun ErrorMic(onRetry: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            modifier = Modifier.size(120.dp).clickable(onClick = onRetry),
            shape = CircleShape,
            color = CozyCream.copy(alpha = 0.18f),
            border = BorderStroke(2.dp, CozyCream.copy(alpha = 0.4f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text("↺", fontSize = 48.sp, color = CozyCream)
            }
        }
        Spacer(Modifier.height(16.dp))
        Text(
            "ops, deu erro · tocar pra tentar de novo",
            fontFamily = FontFamily.Serif,
            fontStyle = FontStyle.Italic,
            fontSize = 13.sp,
            color = CozyCream.copy(alpha = 0.85f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun TranscriptionPreview(
    text: String,
    dueAt: Long?,
    onRerecord: () -> Unit,
    onPickDate: (offsetDays: Int?) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Tag("o que entendemos", color = CozyCream)
        Spacer(Modifier.height(10.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = CozyCream.copy(alpha = 0.12f),
            border = BorderStroke(1.4.dp, CozyCream.copy(alpha = 0.4f))
        ) {
            Text(
                text,
                fontFamily = FontFamily.Cursive,
                fontSize = 22.sp,
                lineHeight = 28.sp,
                color = CozyCream,
                modifier = Modifier.padding(18.dp)
            )
        }

        // Data — auto-detectada ou via chips
        Spacer(Modifier.height(14.dp))
        Tag("quando", color = CozyCream.copy(alpha = 0.85f))
        Spacer(Modifier.height(8.dp))
        if (dueAt != null) {
            Surface(
                modifier = Modifier.clickable { onPickDate(null) },
                shape = RoundedCornerShape(999.dp),
                color = Butter,
                border = BorderStroke(1.dp, Butter)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🗓", fontSize = 14.sp)
                    Spacer(Modifier.width(6.dp))
                    Text(
                        formatDueDate(dueAt),
                        fontFamily = FontFamily.Serif,
                        fontStyle = FontStyle.Italic,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = OliveDeep
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("✕", fontSize = 12.sp, color = OliveDeep)
                }
            }
        } else {
            // Chips manuais (Row simples — 4 chips cabem em uma linha pra phone)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally)
            ) {
                DateChip("hoje") { onPickDate(0) }
                DateChip("amanhã") { onPickDate(1) }
                DateChip("depois") { onPickDate(2) }
                DateChip("próx. semana") { onPickDate(7) }
            }
        }

        Spacer(Modifier.height(16.dp))
        Surface(
            modifier = Modifier.clickable(onClick = onRerecord),
            shape = RoundedCornerShape(999.dp),
            color = CozyCream.copy(alpha = 0.08f),
            border = BorderStroke(1.dp, CozyCream.copy(alpha = 0.4f))
        ) {
            Text(
                "↺ regravar",
                fontFamily = FontFamily.Serif,
                fontSize = 12.sp,
                color = CozyCream.copy(alpha = 0.9f),
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
private fun DateChip(label: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(999.dp),
        color = CozyCream.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, CozyCream.copy(alpha = 0.5f))
    ) {
        Text(
            label,
            fontFamily = FontFamily.Serif,
            fontStyle = FontStyle.Italic,
            fontSize = 12.sp,
            color = CozyCream,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

private fun formatDueDate(epochMillis: Long): String {
    val c = com.tsrapprun.platform.dateComponentsOf(epochMillis)
    val short = listOf("jan", "fev", "mar", "abr", "mai", "jun", "jul", "ago", "set", "out", "nov", "dez")
    val now = com.tsrapprun.platform.dateComponentsOf(com.tsrapprun.platform.nowMillis())
    val diffDays = ((epochMillis - com.tsrapprun.platform.nowMillis()) / (24L * 60 * 60 * 1000)).toInt()
    return when {
        diffDays == 0 -> "hoje"
        diffDays == 1 -> "amanhã"
        diffDays in 2..6 -> "em $diffDays dias"
        c.year == now.year -> "${c.day} ${short[c.monthIndex]}"
        else -> "${c.day} ${short[c.monthIndex]} ${c.year}"
    }
}
