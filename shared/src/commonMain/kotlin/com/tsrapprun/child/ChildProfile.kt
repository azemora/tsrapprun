/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  ChildProfile.kt — Modelo do perfil da criança               ║
 * ║                                                              ║
 * ║  ───────  POLÍTICA DE SEGURANÇA (OWASP / LGPD / GDPR) ───── ║
 * ║                                                              ║
 * ║  A03:2021 Injection                                          ║
 * ║   • App não usa SQL — armazenamento é JSON sandboxed.        ║
 * ║   • Compose Text não interpreta HTML/JS, então XSS não       ║
 * ║     se aplica à UI. Mesmo assim, sanitizamos o nome para     ║
 * ║     remover chars de controle e tags HTML por defesa em      ║
 * ║     profundidade — útil se o nome for serializado p/ servidor║
 * ║     ou exibido em outro contexto futuro.                     ║
 * ║   • Não enviamos o nome a nenhum LLM, evitando prompt        ║
 * ║     injection. Se um dia enviarmos, sanitizar dados.         ║
 * ║                                                              ║
 * ║  A04:2021 Insecure Design                                    ║
 * ║   • Coletamos APENAS o mínimo: primeiro nome + data de       ║
 * ║     nascimento. Nada de sobrenome, RG, foto.                 ║
 * ║                                                              ║
 * ║  A05:2021 Security Misconfiguration                          ║
 * ║   • Validação estrita de schema via @Serializable +          ║
 * ║     `ignoreUnknownKeys = true` no JSON parser.               ║
 * ║                                                              ║
 * ║  A08:2021 Software & Data Integrity                          ║
 * ║   • Datas de nascimento futuras são rejeitadas.              ║
 * ║   • Datas absurdamente antigas (>18 anos) são rejeitadas.    ║
 * ║                                                              ║
 * ║  A09:2021 Logging Failures                                   ║
 * ║   • Não registramos o nome em logs do sistema (println/      ║
 * ║     Log.d). PII só vai pra storage criptografado.            ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.child

import kotlinx.serialization.Serializable

/**
 * Perfil cadastrado da criança.
 *
 * @property id Identificador único (UUID v4) — não-PII.
 * @property firstName Primeiro nome SANITIZADO (ver [ChildProfileSanitizer]).
 *                    1–50 chars, sem chars de controle nem tags HTML.
 * @property birthdateMillis Epoch millis do dia de nascimento (00:00 UTC).
 *                    Validado: não-futuro e dentro de 18 anos.
 * @property lastSeenMonthCount Último mesversário "celebrado" via tela
 *                    de anúncio. Usado para detectar mesversário novo.
 * @property lastSeenWeekCount Última semana com entry sintetizada.
 */
@Serializable
data class ChildProfile(
    val id: String,
    val firstName: String,
    val birthdateMillis: Long,
    val lastSeenMonthCount: Int = 0,
    val lastSeenWeekCount: Int = 0
)
