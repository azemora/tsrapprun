/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  ChildProfile.kt — Modelo do perfil da criança               ║
 * ║                                                              ║
 * ║  ───────  POLÍTICA DE SEGURANÇA (OWASP / LGPD / GDPR) ───── ║
 * ║                                                              ║
 * ║  A03:2021 Injection                                          ║
 * ║   • App não usa SQL — armazenamento é JSON sandboxed.        ║
 * ║   • Compose Text não interpreta HTML/JS, então XSS não       ║
 * ║     se aplica à UI. Sanitizamos o nome para defesa em        ║
 * ║     profundidade (chars de controle, tags, prompt-injection).║
 * ║   • Não enviamos o nome a nenhum LLM.                        ║
 * ║                                                              ║
 * ║  A04:2021 Insecure Design                                    ║
 * ║   • Coletamos APENAS o mínimo: primeiro nome + data.         ║
 * ║                                                              ║
 * ║  A08:2021 Software & Data Integrity                          ║
 * ║   • Datas validadas pelo Sanitizer dependendo do modo:       ║
 * ║     - já nasceu: passado, dentro de 18 anos                  ║
 * ║     - DPP: futuro, dentro de ~10 meses                       ║
 * ║                                                              ║
 * ║  A09:2021 Logging Failures                                   ║
 * ║   • Nome nunca vai pra log do sistema.                       ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.child

import kotlinx.serialization.Serializable

/**
 * Perfil cadastrado da criança.
 *
 * @property id UUID v4.
 * @property firstName Primeiro nome sanitizado.
 * @property birthdateMillis Data de nascimento (real se [isPregnancy]==false,
 *           DPP — Data Provável de Parto — se true).
 * @property isPregnancy True se ainda está na gestação (DPP futura).
 * @property createdAtMillis Quando o perfil foi criado (epoch millis).
 *           Usado para limitar a contagem retroativa de marcos.
 * @property lastSeenMonthCount Última mesversário "celebrada" (anúncio).
 * @property lastSeenWeekCount Em gestação: último valor de semanas-restantes
 *           registrado. Pós-parto: não usado.
 * @property lastSeenDayCount Pós-parto < 1 mês: último dia de vida com entry.
 */
@Serializable
data class ChildProfile(
    val id: String,
    val firstName: String,
    val birthdateMillis: Long,
    val isPregnancy: Boolean = false,
    val createdAtMillis: Long = 0L,
    val lastSeenMonthCount: Int = 0,
    val lastSeenWeekCount: Int = 0,
    val lastSeenDayCount: Int = 0,
    /** Primeiro nome do pai/mãe — usado na saudação. */
    val parentFirstName: String? = null,
    /** ID da foto que serve de avatar do bebê (referencia PhotoData no storage). */
    val avatarPhotoId: String? = null
)
