/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  CameraScreen.android.kt - Tela de Câmera Android           ║
 * ║                                                             ║
 * ║  Layout: barra superior (voltar) separada do preview.       ║
 * ║  Zonas de toque claramente divididas para evitar            ║
 * ║  interferência do AndroidView (CameraX).                    ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.camera

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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

@Composable
actual fun CameraScreen(
    onPhotoCaptured: (ByteArray) -> Unit,
    onError: (String) -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasCameraPermission = permissions[Manifest.permission.CAMERA] == true
        if (!hasCameraPermission) onError("Permissão de câmera necessária para tirar fotos.")
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            val perms = mutableListOf(Manifest.permission.CAMERA)
            // API < 29 precisa de WRITE_EXTERNAL_STORAGE para salvar no álbum público
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                perms.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
            permissionLauncher.launch(perms.toTypedArray())
        }
    }

    // Layout Column: barra de topo separada do preview
    Column(modifier = Modifier.fillMaxSize()) {

        // ── Barra superior — zona de toque separada ──
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.Black,
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onClose,
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
                    "Foto avulsa",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.weight(1f))
                // Spacer para balancear o layout
                Spacer(modifier = Modifier.height(48.dp))
            }
        }

        // ── Preview da câmera ──
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            if (hasCameraPermission) {
                CameraPreview(
                    onPhotoCaptured = onPhotoCaptured,
                    onError = onError
                )
            } else {
                Column(
                    modifier = Modifier.fillMaxSize().padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "Permissão de Câmera",
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Para tirar fotos, precisamos de acesso à câmera. " +
                                "Suas fotos são criptografadas localmente.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = {
                        val perms = mutableListOf(Manifest.permission.CAMERA)
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                            perms.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        }
                        permissionLauncher.launch(perms.toTypedArray())
                    }) {
                        Text("Conceder permissão")
                    }
                }
            }
        }
    }
}
