/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  MemoryOfTheDayRepository.kt - Diário Local Criptografado   ║
 * ║                                                             ║
 * ║  Persiste as entradas "Memória do Dia" no dispositivo.      ║
 * ║  Zero sincronização com nuvem — privacidade total.          ║
 * ║                                                             ║
 * ║  SEGURANÇA:                                                 ║
 * ║  - Android: EncryptedSharedPreferences (Keystore AES-256)   ║
 * ║  - iOS: NSFileProtectionCompleteUntilFirstUserAuth          ║
 * ║  - Chave primária é "YYYY-MM-DD" — 1 entrada por dia        ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.notifications

/**
 * Repositório das entradas de "Memória do Dia".
 * Uma única entrada por data (sobrescreve ao re-salvar o mesmo dia).
 */
expect class MemoryOfTheDayRepository {
    /** Salva (ou sobrescreve) a entrada de uma data. */
    suspend fun saveEntry(entry: MemoryEntry)

    /** Recupera a entrada de uma data específica ("YYYY-MM-DD"). */
    suspend fun getEntry(date: String): MemoryEntry?

    /** Lista todas as entradas, mais recentes primeiro. */
    suspend fun listEntries(): List<MemoryEntry>

    /** Remove a entrada de uma data. */
    suspend fun deleteEntry(date: String)
}
