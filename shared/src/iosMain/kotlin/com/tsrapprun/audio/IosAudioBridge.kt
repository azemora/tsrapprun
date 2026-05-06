/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  IosAudioBridge — ponte Compose ↔ Swift para áudio + STT.    ║
 * ║                                                              ║
 * ║  Swift (AudioRecorderHandler) registra os handlers no init   ║
 * ║  do app. Compose chama via AudioBridge.ios.kt.               ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.audio

/**
 * Singleton — Swift atribui callbacks em `init`, Compose dispara.
 */
object IosAudioBridge {
    /** Solicita permissão (mic + speech). Devolve true se concedido. */
    var onRequestPermission: (callback: (Boolean, String?) -> Unit) -> Unit = {
        it(false, "Bridge não configurada")
    }

    /** Inicia gravação. Devolve true se OK; false + mensagem se falhar. */
    var onStart: (callback: (Boolean, String?) -> Unit) -> Unit = {
        it(false, "Bridge não configurada")
    }

    /**
     * Para gravação e devolve a transcrição (best-effort).
     *  - text: transcrição reconhecida (pode ser vazia se silêncio)
     *  - errorMessage: null em sucesso
     */
    var onStopAndTranscribe: (callback: (String?, String?) -> Unit) -> Unit = {
        it(null, "Bridge não configurada")
    }

    /** Cancela e descarta a gravação atual. */
    var onCancel: () -> Unit = {}

    /** True se o iOS configurou STT (bundle ID com plist permissions etc). */
    var isAvailable: Boolean = false
}
