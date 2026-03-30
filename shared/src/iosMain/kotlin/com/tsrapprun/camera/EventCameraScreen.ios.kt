package com.tsrapprun.camera

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
actual fun EventCameraScreen(
    onPhotoCaptured: suspend (ByteArray) -> String,
    onFinish: (capturedPhotoIds: List<String>) -> Unit,
    onError: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Câmera de eventos iOS — em breve!", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { onFinish(emptyList()) }) { Text("Voltar") }
    }
}
