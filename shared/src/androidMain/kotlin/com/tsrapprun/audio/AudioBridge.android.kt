/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  AudioBridge.android.kt — stub Android (paridade compile).   ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.audio

import kotlinx.coroutines.delay

actual object AudioBridge {
    actual suspend fun startRecording(): Boolean = true
    actual suspend fun stopAndTranscribe(): String? {
        delay(500)
        return "primeira papinha de banana. ela amou cada colherada."
    }
    actual fun cancel() {}
}
