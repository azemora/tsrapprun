/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  UserData.kt - Modelo de Dados do Usuário Autenticado       ║
 * ║                                                             ║
 * ║  POLÍTICA DE SEGURANÇA:                                     ║
 * ║  - Armazena APENAS dados mínimos necessários (princípio     ║
 * ║    da minimização de dados - Art. 5(1)(c) GDPR/LGPD)       ║
 * ║  - Nenhum token de autenticação é armazenado nesta classe   ║
 * ║  - Tokens ficam no Keystore/Keychain (gerenciados pelo      ║
 * ║    TokenManager específico de cada plataforma)              ║
 * ║  - Dados são imutáveis (data class) para evitar             ║
 * ║    modificações acidentais em memória                       ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.security

import kotlinx.serialization.Serializable

/**
 * Representa os dados do usuário autenticado.
 *
 * Esta classe contém SOMENTE informações de perfil público
 * fornecidas pelo Google Sign-In. Nenhum dado sensível
 * (tokens, senhas, refresh tokens) é armazenado aqui.
 *
 * @property userId  ID único do usuário no Firebase (uid).
 *                   Usado como chave para associar dados ao usuário.
 *                   NÃO é o Google ID — é um hash gerado pelo Firebase.
 * @property displayName Nome de exibição do perfil Google.
 *                       Pode ser null se o usuário não configurou nome.
 * @property email   Email do Google. Usado apenas para exibição.
 *                   NUNCA usar como identificador único (pode mudar).
 * @property photoUrl URL da foto de perfil do Google.
 *                    Pode ser null se o usuário não tem foto.
 */
@Serializable
data class UserData(
    val userId: String,
    val displayName: String?,
    val email: String?,
    val photoUrl: String?
)
