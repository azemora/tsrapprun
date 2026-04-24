/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  TokenManager.kt - Gerenciamento Seguro de Tokens           ║
 * ║                                                             ║
 * ║  POLÍTICA DE SEGURANÇA:                                     ║
 * ║  - Tokens NUNCA são armazenados em texto plano              ║
 * ║  - Android: usa Android Keystore (hardware-backed)          ║
 * ║  - iOS: usa iOS Keychain (Secure Enclave)                   ║
 * ║  - Tokens expiram e são renovados automaticamente           ║
 * ║  - Ao fazer logout, TODOS os tokens são destruídos          ║
 * ║                                                             ║
 * ║  PADRÃO EXPECT/ACTUAL:                                      ║
 * ║  Este arquivo define a interface (expect).                  ║
 * ║  Cada plataforma implementa (actual) usando seu             ║
 * ║  mecanismo seguro nativo de armazenamento.                  ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.security

/**
 * Interface multiplataforma para gerenciamento seguro de tokens.
 *
 * Cada plataforma deve implementar esta interface usando seu
 * mecanismo nativo de armazenamento seguro:
 * - Android → Android Keystore + EncryptedSharedPreferences
 * - iOS → Keychain Services (Secure Enclave)
 *
 * IMPORTANTE: Esta classe NÃO deve ser instanciada diretamente.
 * Use [createTokenManager] para obter a implementação correta.
 */
expect class TokenManager {
    /**
     * Salva o ID Token do Firebase de forma segura.
     *
     * O ID Token é um JWT assinado pelo Firebase que contém:
     * - uid do usuário
     * - email
     * - claims customizados
     *
     * SEGURANÇA: Este token é armazenado criptografado no
     * Keystore/Keychain nativo e nunca exposto em logs.
     *
     * @param token O ID Token JWT do Firebase Auth.
     */
    suspend fun saveIdToken(token: String)

    /**
     * Recupera o ID Token salvo, se existir e for válido.
     *
     * @return O ID Token ou null se não existir/expirado.
     */
    suspend fun getIdToken(): String?

    /**
     * Remove TODOS os tokens e dados de sessão armazenados.
     *
     * DEVE ser chamado ao fazer logout para garantir que
     * nenhum dado de autenticação persista no dispositivo.
     *
     * SEGURANÇA: Após esta operação, é impossível recuperar
     * os tokens anteriores — eles são destruídos, não apenas
     * marcados para exclusão.
     */
    suspend fun clearAllTokens()

    /**
     * Verifica se existe uma sessão válida (token salvo).
     *
     * @return true se há um token salvo (não verifica expiração).
     *         A verificação de expiração é feita pelo Firebase Auth.
     */
    suspend fun hasValidSession(): Boolean
}
