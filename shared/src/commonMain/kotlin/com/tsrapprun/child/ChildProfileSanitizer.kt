/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  ChildProfileSanitizer.kt — Validação e sanitização          ║
 * ║                                                              ║
 * ║  Endurece a entrada do usuário antes de persistir/exibir.    ║
 * ║                                                              ║
 * ║  Defesa em profundidade contra:                              ║
 * ║   • XSS (mesmo que Compose Text seja seguro)                 ║
 * ║   • Prompt injection (caso o nome seja enviado a LLM no      ║
 * ║     futuro — exclui chaves usadas como delimitadores)        ║
 * ║   • Storage corruption via chars de controle                 ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.child

object ChildProfileSanitizer {

    private const val MIN_NAME_LEN = 1
    private const val MAX_NAME_LEN = 50

    /** ~18 anos em ms (limite superior razoável para já nascido). */
    private const val MAX_AGE_MS = 18L * 365L * 24L * 60L * 60L * 1000L

    /** ~10 meses em ms (limite superior para DPP no futuro). */
    private const val MAX_PREGNANCY_FUTURE_MS = 10L * 31L * 24L * 60L * 60L * 1000L

    sealed class Result {
        data class Valid(
            val firstName: String,
            val birthdateMillis: Long,
            val isPregnancy: Boolean
        ) : Result()
        data class Invalid(val message: String) : Result()
    }

    /**
     * Sanitiza e valida o input. Considera o modo (já nasceu vs DPP) ao
     * validar a data — DPP precisa estar no futuro próximo, nascimento
     * precisa estar no passado dentro de 18 anos.
     */
    fun sanitize(
        rawName: String,
        birthdateMillis: Long,
        isPregnancy: Boolean,
        nowMillis: Long
    ): Result {
        val cleaned = cleanName(rawName)
        if (cleaned.length < MIN_NAME_LEN) {
            return Result.Invalid("Por favor, digite o primeiro nome.")
        }
        if (cleaned.length > MAX_NAME_LEN) {
            return Result.Invalid("Nome muito longo (máx. $MAX_NAME_LEN caracteres).")
        }

        if (isPregnancy) {
            // DPP precisa estar no futuro
            if (birthdateMillis <= nowMillis) {
                return Result.Invalid("A data prevista de parto deve ser no futuro.")
            }
            if ((birthdateMillis - nowMillis) > MAX_PREGNANCY_FUTURE_MS) {
                return Result.Invalid("Data prevista muito distante (limite ~10 meses).")
            }
        } else {
            // Já nasceu: data no passado, dentro de 18 anos
            if (birthdateMillis > nowMillis) {
                return Result.Invalid("A data de nascimento não pode estar no futuro.")
            }
            if ((nowMillis - birthdateMillis) > MAX_AGE_MS) {
                return Result.Invalid("Data de nascimento muito antiga.")
            }
        }

        return Result.Valid(cleaned, birthdateMillis, isPregnancy)
    }

    /**
     * Limpa o nome:
     *  • Trim de whitespace nas pontas
     *  • Remove chars de controle (ASCII 0x00-0x1F e 0x7F)
     *  • Remove sequências tipo HTML/script (`<…>`, `${…}`, `{{…}}`, `[INST]`)
     *  • Colapsa múltiplos espaços em um só
     */
    fun cleanName(raw: String): String {
        if (raw.isEmpty()) return ""
        val noControl = raw.filter { it.code !in 0..31 && it.code != 0x7F }
        val noTags = TAG_REGEX.replace(noControl, "")
        val noTemplates = noTags
            .replace(DOLLAR_BRACE_REGEX, "")
            .replace(DOUBLE_BRACE_REGEX, "")
            .replace(BRACKET_INST_REGEX, "")
        return noTemplates.replace(MULTIWS_REGEX, " ").trim()
    }

    private val TAG_REGEX = Regex("<[^>]*>")
    private val DOLLAR_BRACE_REGEX = Regex("\\$\\{[^}]*}")
    private val DOUBLE_BRACE_REGEX = Regex("\\{\\{[^}]*}}")
    private val BRACKET_INST_REGEX = Regex("\\[/?(?:INST|SYS|SYSTEM)[^]]*]", RegexOption.IGNORE_CASE)
    private val MULTIWS_REGEX = Regex("\\s+")
}
