/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  CameraScreen.ios.kt - Stub iOS                             ║
 * ║  A implementar com AVFoundation quando houver Xcode.        ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.camera

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
actual fun CameraScreen(
    onPhotoCaptured: (ByteArray) -> Unit,
    onError: (String) -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Câmera iOS — em breve!",
            style = MaterialTheme.typography.headlineSmall
        )
        Button(onClick = onClose) {
            Text("Voltar")
        }
    }
}
