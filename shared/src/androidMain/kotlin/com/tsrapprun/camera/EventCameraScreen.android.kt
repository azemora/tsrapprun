/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  EventCameraScreen.android.kt - Câmera Contínua Android     ║
 * ║                                                             ║
 * ║  Layout: barra superior (voltar + contador + finalizar)     ║
 * ║  seguida do preview da câmera com botão de captura.         ║
 * ║  Separação clara de zonas de toque.                         ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.camera

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import kotlinx.coroutines.launch

@Composable
actual fun EventCameraScreen(
    onPhotoCaptured: suspend (ByteArray) -> String,
    onFinish: (capturedPhotoIds: List<String>) -> Unit,
    onError: (String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val capturedIds = remember { mutableStateListOf<String>() }
    var isCapturing by remember { mutableStateOf(false) }

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted: Boolean -> hasCameraPermission = granted }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    // Layout: Column para separar barra de topo do preview
    // Isso evita que o AndroidView (CameraX) intercepte toques nos botões
    Column(modifier = Modifier.fillMaxSize()) {

        // ══════════════════════════════════════════════
        // BARRA SUPERIOR — zona de toque separada do preview
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
                // Botão VOLTAR — área de toque grande
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

                // Contador de fotos
                Text(
                    text = "${capturedIds.size} foto${if (capturedIds.size != 1) "s" else ""}",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.weight(1f))

                // Botão FINALIZAR — área de toque grande
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
        // PREVIEW DA CÂMERA — ocupa o restante da tela
        // ══════════════════════════════════════════════
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            if (hasCameraPermission) {
                CameraPreview(
                    onPhotoCaptured = { bytes ->
                        if (!isCapturing) {
                            isCapturing = true
                            scope.launch {
                                try {
                                    val photoId = onPhotoCaptured(bytes)
                                    capturedIds.add(photoId)
                                } catch (e: Exception) {
                                    onError("Erro ao salvar foto")
                                } finally {
                                    isCapturing = false
                                }
                            }
                        }
                    },
                    onError = onError
                )

                // Indicador de salvando (overlay no centro)
                if (isCapturing) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(48.dp),
                            color = Color.White
                        )
                    }
                }
            } else {
                // Permissão negada
                Column(
                    modifier = Modifier.fillMaxSize().padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "Permissão de câmera necessária",
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) }) {
                        Text("Conceder permissão")
                    }
                }
            }
        }
    }
}
