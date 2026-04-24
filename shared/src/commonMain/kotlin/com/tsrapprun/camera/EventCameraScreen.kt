/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  EventCameraScreen.kt - Câmera Contínua para Eventos        ║
 * ║                                                             ║
 * ║  Câmera que fica aberta para captura múltipla.              ║
 * ║  Fotos salvas uma a uma (criptografadas imediatamente).     ║
 * ║  Ao finalizar, retorna lista de IDs para nomear o evento.   ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.camera

import androidx.compose.runtime.Composable

/**
 * Câmera contínua para registro de eventos.
 *
 * @param onPhotoCaptured Chamado a cada foto. Retorna o photoId salvo.
 * @param onFinish Chamado quando o usuário toca "Finalizar".
 * @param onError Callback de erro.
 */
@Composable
expect fun EventCameraScreen(
    onPhotoCaptured: suspend (ByteArray) -> String,
    onFinish: (capturedPhotoIds: List<String>) -> Unit,
    onError: (String) -> Unit
)
