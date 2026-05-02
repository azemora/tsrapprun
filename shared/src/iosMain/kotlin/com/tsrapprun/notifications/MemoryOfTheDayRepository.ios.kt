/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  MemoryOfTheDayRepository.ios.kt - Diário Local iOS         ║
 * ║                                                             ║
 * ║  Armazena o diário em Application Support com proteção      ║
 * ║  NSFileProtectionCompleteUntilFirstUserAuthentication.      ║
 * ║  Arquivo único JSON: memory_of_the_day.json                 ║
 * ║                                                             ║
 * ║  SEGURANÇA: Data Protection do iOS cifra em repouso com      ║
 * ║  chave derivada do passcode do usuário.                     ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.notifications

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import platform.Foundation.NSApplicationSupportDirectory
import platform.Foundation.NSData
import platform.Foundation.NSFileManager
import platform.Foundation.NSString
import platform.Foundation.NSURL
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.NSUserDomainMask
import platform.Foundation.create
import platform.Foundation.dataUsingEncoding
import platform.Foundation.writeToURL

@OptIn(ExperimentalForeignApi::class)
actual class MemoryOfTheDayRepository {

    private val mutex = Mutex()
    private val json = Json { ignoreUnknownKeys = true; prettyPrint = false }

    private val fileUrl: NSURL by lazy {
        val fm = NSFileManager.defaultManager
        val supportDir: NSURL = fm.URLsForDirectory(
            directory = NSApplicationSupportDirectory,
            inDomains = NSUserDomainMask
        ).first() as NSURL
        fm.createDirectoryAtURL(
            url = supportDir,
            withIntermediateDirectories = true,
            attributes = null,
            error = null
        )
        supportDir.URLByAppendingPathComponent(FILE_NAME)!!
    }

    actual suspend fun saveEntry(entry: MemoryEntry): Unit = withContext(Dispatchers.Default) {
        mutex.withLock {
            val current = readAll().toMutableMap()
            current[entry.date] = entry
            writeAll(current)
        }
    }

    actual suspend fun getEntry(date: String): MemoryEntry? = withContext(Dispatchers.Default) {
        readAll()[date]
    }

    actual suspend fun listEntries(): List<MemoryEntry> = withContext(Dispatchers.Default) {
        readAll().values.sortedByDescending { it.date }
    }

    actual suspend fun deleteEntry(date: String): Unit = withContext(Dispatchers.Default) {
        mutex.withLock {
            val current = readAll().toMutableMap()
            current.remove(date)
            writeAll(current)
        }
    }

    private fun readAll(): Map<String, MemoryEntry> {
        val path = fileUrl.path ?: return emptyMap()
        val fm = NSFileManager.defaultManager
        if (!fm.fileExistsAtPath(path)) return emptyMap()
        val data: NSData = fm.contentsAtPath(path) ?: return emptyMap()
        val text = NSString.create(data = data, encoding = NSUTF8StringEncoding)?.toString()
            ?: return emptyMap()
        return runCatching { json.decodeFromString<Map<String, MemoryEntry>>(text) }
            .getOrElse { emptyMap() }
    }

    /**
     * Grava o diário atomicamente.
     * A proteção de arquivo padrão do iOS (NSFileProtectionComplete*) já se aplica
     * a arquivos criados em Application Support — não precisamos especificar options.
     */
    private fun writeAll(map: Map<String, MemoryEntry>) {
        val text = json.encodeToString(map)
        val nsString = NSString.create(string = text)
        val data = nsString.dataUsingEncoding(NSUTF8StringEncoding) ?: return
        data.writeToURL(url = fileUrl, atomically = true)
    }

    companion object {
        private const val FILE_NAME = "memory_of_the_day.json"
    }
}
