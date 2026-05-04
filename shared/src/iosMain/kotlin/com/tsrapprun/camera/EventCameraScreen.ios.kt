@file:OptIn(
    kotlinx.cinterop.ExperimentalForeignApi::class,
    kotlinx.cinterop.BetaInteropApi::class
)

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  EventCameraScreen.ios.kt — Câmera Contínua iOS              ║
 * ║                                                              ║
 * ║  UI Compose espelha o Android: top bar (voltar / contador /  ║
 * ║  finalizar), preview ocupando o restante e botão de captura  ║
 * ║  inferior.                                                   ║
 * ║                                                              ║
 * ║  AVFoundation roda em Swift via IosEventCameraBridge —       ║
 * ║  K/N 2.1.0 não expõe AVCapturePhoto.fileDataRepresentation,  ║
 * ║  então o lado iOS encapsula o ciclo de captura.              ║
 * ║                                                              ║
 * ║  No simulador (sem câmera física), bridge.hasCamera = false  ║
 * ║  e a UI mostra mensagem clara — Voltar continua funcionando. ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.camera

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.UIKitView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import platform.CoreGraphics.CGRectMake
import platform.UIKit.UIView

@Composable
actual fun EventCameraScreen(
    onPhotoCaptured: suspend (ByteArray) -> String,
    onFinish: (capturedPhotoIds: List<String>) -> Unit,
    onError: (String) -> Unit
) {
    val scope = rememberCoroutineScope()
    val capturedIds = remember { mutableStateListOf<String>() }
    var isCapturing by remember { mutableStateOf(false) }

    val hasCamera = IosEventCameraBridge.hasCamera

    DisposableEffect(Unit) {
        if (hasCamera) IosEventCameraBridge.onStart()
        onDispose { if (hasCamera) IosEventCameraBridge.onStop() }
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.Black)) {

        // ══════════════════════════════════════════════
        // BARRA SUPERIOR
        // ══════════════════════════════════════════════
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.Black,
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = { onFinish(capturedIds.toList()) },
                    modifier = Modifier.height(48.dp)
                ) {
                    Text(
                        "← Voltar",
                        fontSize = 16.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "${capturedIds.size} foto${if (capturedIds.size != 1) "s" else ""}",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = { onFinish(capturedIds.toList()) },
                    modifier = Modifier.height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        "Finalizar",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // ══════════════════════════════════════════════
        // PREVIEW
        // ══════════════════════════════════════════════
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            if (hasCamera) {
                UIKitView(
                    factory = {
                        IosEventCameraBridge.providePreviewView()
                            ?: UIView(frame = CGRectMake(0.0, 0.0, 0.0, 0.0))
                    },
                    modifier = Modifier.fillMaxSize()
                )

                if (isCapturing) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(48.dp),
                            color = Color.White
                        )
                    }
                }
            } else {
                // ── Modo simulador: foto sintética é gerada na captura ──
                Column(
                    modifier = Modifier.fillMaxSize().padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "MODO SIMULADOR",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "Sem câmera física — cada toque no botão gera uma foto sintética com contador e timestamp.",
                        color = Color.LightGray,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Use o iPhone real para fotos verdadeiras.",
                        color = Color.Gray,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }

                if (isCapturing) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(48.dp),
                            color = Color.White
                        )
                    }
                }
            }
        }

        // ══════════════════════════════════════════════
        // BOTÃO DE CAPTURA
        // ══════════════════════════════════════════════
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(vertical = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = capture@{
                    if (isCapturing) return@capture
                    isCapturing = true
                    IosEventCameraBridge.onCapture { data, errMsg ->
                        if (data == null) {
                            isCapturing = false
                            onError(errMsg ?: "Erro ao capturar foto")
                            return@onCapture
                        }
                        val bytes = data.toByteArray()
                        scope.launch {
                            try {
                                val id = onPhotoCaptured(bytes)
                                capturedIds.add(id)
                            } catch (t: Throwable) {
                                onError("Erro ao salvar foto")
                            } finally {
                                isCapturing = false
                            }
                        }
                    }
                },
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .border(width = 4.dp, color = Color.White.copy(alpha = 0.6f), shape = CircleShape),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ),
                shape = CircleShape,
                enabled = !isCapturing
            ) {}
        }
    }
}
