/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  MemoryOfTheDayRepository.android.kt - Diário Criptografado ║
 * ║                                                             ║
 * ║  Armazenamento: EncryptedSharedPreferences (AES-256-GCM      ║
 * ║  respaldado pelo Android Keystore).                         ║
 * ║                                                             ║
 * ║  Layout: 1 chave por entrada (prefix "entry:YYYY-MM-DD")     ║
 * ║  + 1 chave "index" com lista de datas para listEntries().    ║
 * ║  Essa partição evita reescrever um JSON grande a cada save.  ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.notifications

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

actual class MemoryOfTheDayRepository(private val context: Context) {

    private val mutex = Mutex()
    private val json = Json { ignoreUnknownKeys = true; prettyPrint = false }

    private val prefs: SharedPreferences by lazy {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        EncryptedSharedPreferences.create(
            context,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    actual suspend fun saveEntry(entry: MemoryEntry): Unit = withContext(Dispatchers.IO) {
        mutex.withLock {
            val dates = loadIndex().toMutableSet().apply { add(entry.date) }
            prefs.edit()
                .putString(keyFor(entry.date), json.encodeToString(entry))
                .putStringSet(KEY_INDEX, dates)
                .apply()
        }
    }

    actual suspend fun getEntry(date: String): MemoryEntry? = withContext(Dispatchers.IO) {
        val raw = prefs.getString(keyFor(date), null) ?: return@withContext null
        runCatching { json.decodeFromString<MemoryEntry>(raw) }.getOrNull()
    }

    actual suspend fun listEntries(): List<MemoryEntry> = withContext(Dispatchers.IO) {
        val dates = loadIndex()
        dates.mapNotNull { d ->
            val raw = prefs.getString(keyFor(d), null) ?: return@mapNotNull null
            runCatching { json.decodeFromString<MemoryEntry>(raw) }.getOrNull()
        }.sortedByDescending { it.date } // "YYYY-MM-DD" ordena lexicograficamente = cronologicamente
    }

    actual suspend fun deleteEntry(date: String): Unit = withContext(Dispatchers.IO) {
        mutex.withLock {
            val dates = loadIndex().toMutableSet().apply { remove(date) }
            prefs.edit()
                .remove(keyFor(date))
                .putStringSet(KEY_INDEX, dates)
                .apply()
        }
    }

    private fun loadIndex(): Set<String> = prefs.getStringSet(KEY_INDEX, emptySet()) ?: emptySet()

    private fun keyFor(date: String) = "entry:$date"

    companion object {
        private const val PREFS_NAME = "tsr_memory_of_the_day"
        private const val KEY_INDEX = "index"
    }
}
