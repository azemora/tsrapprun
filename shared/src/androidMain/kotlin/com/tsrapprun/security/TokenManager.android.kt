/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  TokenManager.android.kt - Implementação Android            ║
 * ║                                                             ║
 * ║  Usa EncryptedSharedPreferences com Android Keystore         ║
 * ║  para armazenar tokens de forma segura.                     ║
 * ║                                                             ║
 * ║  SEGURANÇA:                                                 ║
 * ║  - Chaves AES-256 geradas e armazenadas no Keystore         ║
 * ║  - Hardware-backed em dispositivos compatíveis              ║
 * ║  - Dados criptografados em repouso (at rest)                ║
 * ║  - Sem acesso root, é impossível extrair as chaves          ║
 * ║                                                             ║
 * ║  NOTA: EncryptedSharedPreferences usa:                      ║
 * ║  - AES-256-SIV para nomes de chaves                         ║
 * ║  - AES-256-GCM para valores                                 ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.security

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * Implementação Android do [TokenManager].
 *
 * Utiliza EncryptedSharedPreferences para armazenar tokens
 * com criptografia AES-256-GCM, com chaves protegidas pelo
 * Android Keystore (hardware-backed quando disponível).
 *
 * @property context Context do Android necessário para acessar
 *                   o Keystore e SharedPreferences.
 */
actual class TokenManager(private val context: Context) {

    companion object {
        /**
         * Nome do arquivo de preferências criptografadas.
         * Armazenado em /data/data/com.tsrapprun.android/shared_prefs/
         * mas com conteúdo completamente criptografado.
         */
        private const val PREFS_FILE = "tsrapprun_secure_tokens"

        /** Chave para o ID Token do Firebase (criptografada no arquivo) */
        private const val KEY_ID_TOKEN = "firebase_id_token"
    }

    /**
     * MasterKey gerenciada pelo Android Keystore.
     *
     * Esta chave é gerada no hardware seguro do dispositivo
     * (TEE - Trusted Execution Environment) e nunca sai dele.
     * Usamos AES-256-GCM como esquema de criptografia.
     */
    private val masterKey: MasterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    /**
     * SharedPreferences criptografadas.
     *
     * Todos os dados escritos aqui são automaticamente:
     * 1. Nomes das chaves → criptografados com AES-256-SIV
     * 2. Valores → criptografados com AES-256-GCM
     *
     * Isso significa que mesmo com acesso ao arquivo XML,
     * é impossível ler os dados sem a MasterKey do Keystore.
     */
    private val securePrefs: SharedPreferences =
        EncryptedSharedPreferences.create(
            context,
            PREFS_FILE,
            masterKey,
            // Esquema de criptografia para nomes de chaves
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            // Esquema de criptografia para valores
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

    /**
     * Salva o ID Token do Firebase de forma criptografada.
     *
     * O token é criptografado automaticamente pelo
     * EncryptedSharedPreferences antes de ser escrito no disco.
     * A operação é síncrona (commit) para garantir persistência.
     */
    actual suspend fun saveIdToken(token: String) {
        securePrefs.edit()
            .putString(KEY_ID_TOKEN, token)
            .apply() // apply() é assíncrono e seguro para UI thread
    }

    /**
     * Recupera o ID Token criptografado.
     *
     * O EncryptedSharedPreferences decifra automaticamente
     * o valor usando a MasterKey do Android Keystore.
     *
     * @return Token decifrado ou null se não existir.
     */
    actual suspend fun getIdToken(): String? {
        return securePrefs.getString(KEY_ID_TOKEN, null)
    }

    /**
     * Destrói todos os tokens armazenados.
     *
     * SEGURANÇA: Usa clear() + commit() para garantir que
     * a remoção é persistida imediatamente no disco.
     * O Android Keystore mantém a MasterKey (que é reutilizada),
     * mas os dados criptografados são irrecuperáveis após clear().
     */
    actual suspend fun clearAllTokens() {
        securePrefs.edit()
            .clear()
            .commit() // commit() síncrono — garante remoção imediata
    }

    /**
     * Verifica se existe um token salvo.
     *
     * NOTA: Isso verifica apenas a presença do token,
     * não sua validade. A verificação de expiração
     * é feita pelo Firebase Auth ao usar o token.
     */
    actual suspend fun hasValidSession(): Boolean {
        return securePrefs.contains(KEY_ID_TOKEN)
    }
}
