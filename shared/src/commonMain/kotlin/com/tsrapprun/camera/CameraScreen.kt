/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  CameraScreen.kt - Tela de Câmera (expect/actual)           ║
 * ║                                                             ║
 * ║  Interface Compose compartilhada para a funcionalidade      ║
 * ║  de câmera. A implementação real é platform-specific         ║
 * ║  porque Android usa CameraX e iOS usa AVFoundation.         ║
 * ║                                                             ║
 * ║  SEGURANÇA:                                                 ║
 * ║  - Callback retorna ByteArray (memória), não arquivo        ║
 * ║  - O caller criptografa antes de persistir                  ║
 * ║  - Permissão de câmera é verificada antes de abrir          ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.camera

import androidx.compose.runtime.Composable

/**
 * Tela de câmera multiplataforma.
 *
 * Cada plataforma implementa usando seu SDK nativo:
 * - Android: CameraX + Compose AndroidView
 * - iOS: AVFoundation + UIViewControllerRepresentable
 *
 * @param onPhotoCaptured Callback com bytes da foto (em memória).
 * @param onError Callback de erro com mensagem amigável.
 * @param onClose Callback para fechar a câmera e voltar.
 */
@Composable
expect fun CameraScreen(
    onPhotoCaptured: (ByteArray) -> Unit,
    onError: (String) -> Unit,
    onClose: () -> Unit
)
