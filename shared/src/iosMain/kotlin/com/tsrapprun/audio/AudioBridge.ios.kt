/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  AudioBridge.ios.kt — implementação real via Swift bridge.   ║
 * ║                                                              ║
 * ║  Usa AVAudioRecorder + SFSpeechRecognizer através de         ║
 * ║  AudioRecorderHandler (Swift). No simulador o reconhecimento ║
 * ║  pode falhar (sem áudio) — nesse caso devolve null.          ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.audio

import kotlinx.coroutines.CompletableDeferred

actual object AudioBridge {
    actual suspend fun startRecording(): Boolean {
        // Garante permissão antes de gravar.
        val permission = CompletableDeferred<Boolean>()
        IosAudioBridge.onRequestPermission { granted, _ ->
            permission.complete(granted)
        }
        if (!permission.await()) return false

        val started = CompletableDeferred<Boolean>()
        IosAudioBridge.onStart { ok, _ ->
            started.complete(ok)
        }
        return started.await()
    }

    actual suspend fun stopAndTranscribe(): String? {
        val result = CompletableDeferred<String?>()
        IosAudioBridge.onStopAndTranscribe { text, _ ->
            result.complete(text)
        }
        return result.await()
    }

    actual fun cancel() {
        IosAudioBridge.onCancel()
    }
}
