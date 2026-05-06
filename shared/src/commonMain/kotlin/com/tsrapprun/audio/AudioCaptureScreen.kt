/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  AudioCaptureScreen.kt — gravação de áudio + transcrição     ║
 * ║                                                              ║
 * ║  Estados:                                                    ║
 * ║   • IDLE       → mic grande, "tocar pra falar"               ║
 * ║   • RECORDING  → mic pulsando, timer mm:ss, "tocar pra parar"║
 * ║   • PROCESSING → spinner, "transcrevendo..."                 ║
 * ║   • DONE       → preview do texto + "salvar" / "regravar"    ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.audio

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
import com.tsrapprun.platform.nowMillis
import com.tsrapprun.ui.chrome.Butter
import com.tsrapprun.ui.chrome.OliveDeep
import com.tsrapprun.ui.chrome.ScreenHeader
import com.tsrapprun.ui.chrome.Tag
import com.tsrapprun.ui.chrome.italicSerifText
import com.tsrapprun.ui.theme.CozyAmberDeep
import com.tsrapprun.ui.theme.CozyCream
import com.tsrapprun.ui.theme.CozyCreamDeep
import com.tsrapprun.ui.theme.CozyOlive
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AudioCaptureScreen(
    photoCount: Int,
    onConfirm: (transcription: String) -> Unit,
    onSkip: () -> Unit,
    onBack: () -> Unit
) {
    var state by remember { mutableStateOf(RecordingState.IDLE) }
    var transcription by remember { mutableStateOf("") }
    var startMs by remember { mutableStateOf(0L) }
    var elapsedMs by remember { mutableStateOf(0L) }
    val scope = rememberCoroutineScope()

    // Timer roda enquanto gravando
    LaunchedEffect(state) {
        if (state == RecordingState.RECORDING) {
            while (state == RecordingState.RECORDING) {
                elapsedMs = nowMillis() - startMs
                delay(100)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(CozyCream)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            ScreenHeader(
                chapter = "registro rápido — passo 2 de 2",
                title = italicSerifText(
                    prefix = "conte um ",
                    italic = "momento",
                    suffix = ".",
                    italicColor = CozyAmberDeep,
                    defaultColor = OliveDeep
                ),
                subtitle = if (photoCount > 0)
                    "$photoCount ${if (photoCount == 1) "foto" else "fotos"} capturada${if (photoCount == 1) "" else "s"}. agora fale o que aconteceu."
                else
                    "fale o que aconteceu — vamos transcrever pra você.",
                onBack = onBack
            )

            Spacer(Modifier.height(16.dp))

            Box(
                modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                when (state) {
                    RecordingState.IDLE -> IdleMicButton(
                        onStart = {
                            scope.launch {
                                if (AudioBridge.startRecording()) {
                                    startMs = nowMillis()
                                    elapsedMs = 0L
                                    state = RecordingState.RECORDING
                                } else {
                                    state = RecordingState.ERROR
                                }
                            }
                        }
                    )
                    RecordingState.RECORDING -> RecordingMic(
                        elapsedMs = elapsedMs,
                        onStop = {
                            state = RecordingState.PROCESSING
                            scope.launch {
                                val text = AudioBridge.stopAndTranscribe()
                                if (text.isNullOrBlank()) {
                                    state = RecordingState.ERROR
                                } else {
                                    transcription = text
                                    state = RecordingState.IDLE  // reusa pra preview
                                }
                            }
                        }
                    )
                    RecordingState.PROCESSING -> ProcessingMic()
                    RecordingState.ERROR -> ErrorMic(onRetry = { state = RecordingState.IDLE })
                }

                // Preview da transcrição quando temos texto
                if (transcription.isNotBlank() && state == RecordingState.IDLE) {
                    TranscriptionPreview(
                        text = transcription,
                        onRerecord = {
                            transcription = ""
                            elapsedMs = 0L
                        }
                    )
                }
            }

            // Footer CTA
            Box(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                if (transcription.isNotBlank()) {
                    Surface(
                        modifier = Modifier.fillMaxWidth().clickable { onConfirm(transcription) },
                        shape = RoundedCornerShape(20.dp),
                        color = OliveDeep,
                        shadowElevation = 12.dp
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 18.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = italicSerifText(
                                    italic = "revisar e salvar",
                                    italicColor = Butter,
                                    defaultColor = CozyCream,
                                    italicWeight = FontWeight.Bold
                                ),
                                fontFamily = FontFamily.Serif,
                                fontSize = 16.sp
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("→", fontSize = 18.sp, color = CozyCream, fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    // Pular áudio — vai pra criação manual
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            modifier = Modifier.clickable(onClick = onSkip),
                            shape = RoundedCornerShape(999.dp),
                            color = CozyCreamDeep,
                            border = BorderStroke(1.dp, CozyOlive.copy(alpha = 0.3f))
                        ) {
                            Text(
                                "pular áudio · escrever manualmente",
                                fontFamily = FontFamily.Serif,
                                fontStyle = FontStyle.Italic,
                                fontSize = 13.sp,
                                color = CozyOlive,
                                modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun IdleMicButton(onStart: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            modifier = Modifier
                .size(140.dp)
                .clickable(onClick = onStart),
            shape = CircleShape,
            color = CozyAmberDeep,
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
                italicColor = CozyAmberDeep,
                defaultColor = OliveDeep,
                italicWeight = FontWeight.Bold
            ),
            fontFamily = FontFamily.Serif,
            fontSize = 18.sp
        )
        Spacer(Modifier.height(6.dp))
        Text(
            "fale com calma — vou transcrever pra você",
            fontFamily = FontFamily.Serif,
            fontStyle = FontStyle.Italic,
            fontSize = 13.sp,
            color = CozyOlive.copy(alpha = 0.7f)
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
            color = CozyAmberDeep,
            shadowElevation = 18.dp,
            border = BorderStroke(4.dp, Butter)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(CozyCream)
                )
            }
        }
        Spacer(Modifier.height(20.dp))
        Text(
            "${mm.toString().padStart(2, '0')}:${ss.toString().padStart(2, '0')}",
            fontFamily = FontFamily.Monospace,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = CozyAmberDeep
        )
        Spacer(Modifier.height(6.dp))
        Text(
            "tocar pra parar",
            fontFamily = FontFamily.Serif,
            fontStyle = FontStyle.Italic,
            fontSize = 13.sp,
            color = CozyOlive
        )
    }
}

@Composable
private fun ProcessingMic() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        CircularProgressIndicator(
            modifier = Modifier.size(60.dp),
            color = CozyAmberDeep,
            strokeWidth = 4.dp
        )
        Spacer(Modifier.height(20.dp))
        Text(
            text = italicSerifText(
                italic = "transcrevendo",
                suffix = "...",
                italicColor = CozyAmberDeep,
                defaultColor = OliveDeep,
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
            modifier = Modifier
                .size(120.dp)
                .clickable(onClick = onRetry),
            shape = CircleShape,
            color = CozyCreamDeep,
            border = BorderStroke(2.dp, CozyOlive.copy(alpha = 0.4f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text("↺", fontSize = 48.sp, color = OliveDeep)
            }
        }
        Spacer(Modifier.height(16.dp))
        Text(
            "ops, deu erro · tocar pra tentar de novo",
            fontFamily = FontFamily.Serif,
            fontStyle = FontStyle.Italic,
            fontSize = 13.sp,
            color = CozyOlive,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun TranscriptionPreview(text: String, onRerecord: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Tag("o que entendemos", color = OliveDeep)
        Spacer(Modifier.height(8.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = CozyCreamDeep,
            border = BorderStroke(1.4.dp, CozyOlive.copy(alpha = 0.25f))
        ) {
            Text(
                text,
                fontFamily = FontFamily.Cursive,
                fontSize = 19.sp,
                lineHeight = 24.sp,
                color = OliveDeep,
                modifier = Modifier.padding(16.dp)
            )
        }
        Spacer(Modifier.height(12.dp))
        Surface(
            modifier = Modifier.clickable(onClick = onRerecord),
            shape = RoundedCornerShape(999.dp),
            color = CozyCream,
            border = BorderStroke(1.dp, CozyOlive.copy(alpha = 0.3f))
        ) {
            Text(
                "↺ regravar",
                fontFamily = FontFamily.Serif,
                fontSize = 12.sp,
                color = CozyOlive,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
            )
        }
    }
}
