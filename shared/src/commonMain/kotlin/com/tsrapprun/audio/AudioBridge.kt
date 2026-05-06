/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  AudioBridge.kt — captura de áudio + transcrição (expect).   ║
 * ║                                                              ║
 * ║  Implementação real (iOS): AVAudioRecorder + SFSpeech.       ║
 * ║  Stub atual: simulador grava silêncio e devolve canned text  ║
 * ║  baseada na duração — basta pra validar o fluxo de UI.       ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.audio

/** Estado da gravação observado pela UI. */
enum class RecordingState { IDLE, RECORDING, PROCESSING, ERROR }

expect object AudioBridge {
    /** Inicia gravação. Retorna true se OK; false se sem permissão/erro. */
    suspend fun startRecording(): Boolean

    /**
     * Para a gravação e devolve a transcrição (best-effort).
     * No simulador retorna texto canned para o fluxo seguir.
     * Em release com STT real, devolve o texto reconhecido.
     */
    suspend fun stopAndTranscribe(): String?

    /** Cancela a gravação atual sem transcrever. */
    fun cancel()
}

/**
 * Quebra a transcrição num par (título, nota).
 *  - 1ª frase (até "." ou "!" ou "?") vira o título
 *  - resto vira a nota
 *  - se for um único trecho curto, todo ele vira o título
 */
fun splitTranscription(text: String): Pair<String, String> {
    val cleaned = text.trim()
    if (cleaned.isEmpty()) return "" to ""
    val sentenceEnd = cleaned.indexOfAny(charArrayOf('.', '!', '?'))
    return if (sentenceEnd in 0 until cleaned.length - 1) {
        val title = cleaned.substring(0, sentenceEnd).trim()
        val rest = cleaned.substring(sentenceEnd + 1).trim()
        title to rest
    } else {
        cleaned.removeSuffix(".").removeSuffix("!").removeSuffix("?") to ""
    }
}
