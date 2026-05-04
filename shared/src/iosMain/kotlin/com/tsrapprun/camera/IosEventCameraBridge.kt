@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class, kotlinx.cinterop.BetaInteropApi::class)

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  IosEventCameraBridge — ponte Compose ↔ Swift para câmera   ║
 * ║                                                              ║
 * ║  K/N 2.1.0 não expõe `fileDataRepresentation()` em           ║
 * ║  AVCapturePhoto, então a captura efetiva é feita em Swift.   ║
 * ║  Esta bridge segue o padrão do IosAuthBridge: o lado iOS     ║
 * ║  registra os handlers ao iniciar o app, e a UI Compose       ║
 * ║  apenas chama-os.                                            ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.camera

import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.UIKit.UIView
import platform.posix.memcpy

/**
 * Singleton acessado tanto por Swift (configura) quanto pelo Compose iOS (consome).
 *
 * Swift atribui os lambdas em `init` do app — antes da primeira composição.
 * O Compose lê os valores e dispara os callbacks.
 */
object IosEventCameraBridge {
    /** Inicia a captura de vídeo (chamado quando a tela aparece). */
    var onStart: () -> Unit = {}

    /** Pausa a captura (chamado ao sair da tela). */
    var onStop: () -> Unit = {}

    /**
     * Solicita uma foto. Swift devolve via callback:
     * - data: NSData da imagem JPEG (ou null em erro)
     * - errorMessage: descrição amigável (ou null em sucesso)
     */
    var onCapture: (callback: (NSData?, String?) -> Unit) -> Unit = { it(null, "Câmera não inicializada") }

    /** Retorna o UIView com o preview layer pronto para ser embutido em UIKitView. */
    var providePreviewView: () -> UIView? = { null }

    /** True se Swift conseguiu configurar uma sessão com câmera real. */
    var hasCamera: Boolean = false
}

/** Converte NSData → ByteArray (cópia em memória). */
internal fun NSData.toByteArray(): ByteArray {
    val len = length.toInt()
    if (len == 0) return ByteArray(0)
    return ByteArray(len).apply {
        usePinned { pinned ->
            memcpy(pinned.addressOf(0), this@toByteArray.bytes, this@toByteArray.length)
        }
    }
}
