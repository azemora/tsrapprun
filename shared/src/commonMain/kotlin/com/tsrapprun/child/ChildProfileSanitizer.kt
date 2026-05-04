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

    /** ~18 anos em milissegundos (limite superior razoável). */
    private const val MAX_AGE_MS = 18L * 365L * 24L * 60L * 60L * 1000L

    /** Resultado de validação. */
    sealed class Result {
        data class Valid(val firstName: String, val birthdateMillis: Long) : Result()
        data class Invalid(val message: String) : Result()
    }

    /**
     * Sanitiza e valida o input. Retorna [Result.Valid] com dados limpos
     * ou [Result.Invalid] com mensagem amigável (sem detalhes técnicos).
     */
    fun sanitize(rawName: String, birthdateMillis: Long, nowMillis: Long): Result {
        val cleaned = cleanName(rawName)
        if (cleaned.length < MIN_NAME_LEN) {
            return Result.Invalid("Por favor, digite o primeiro nome.")
        }
        if (cleaned.length > MAX_NAME_LEN) {
            return Result.Invalid("Nome muito longo (máx. $MAX_NAME_LEN caracteres).")
        }

        // Data não pode ser no futuro
        if (birthdateMillis > nowMillis) {
            return Result.Invalid("A data de nascimento não pode estar no futuro.")
        }
        // Data não pode ser absurdamente antiga (proteção contra valores grosseiramente errados)
        val ageMs = nowMillis - birthdateMillis
        if (ageMs > MAX_AGE_MS) {
            return Result.Invalid("Data de nascimento muito antiga.")
        }

        return Result.Valid(cleaned, birthdateMillis)
    }

    /**
     * Limpa o nome:
     *  • Trim de whitespace nas pontas
     *  • Remove chars de controle (ASCII 0x00-0x1F e 0x7F)
     *  • Remove sequências tipo HTML/script (`<…>` e `${…}`)
     *  • Colapsa múltiplos espaços em um só
     *
     *  Mantém acentos, hífens, apóstrofos (ex: "Maria-José", "D'angelo").
     */
    fun cleanName(raw: String): String {
        if (raw.isEmpty()) return ""

        // 1. Remove chars de controle
        val noControl = raw.filter { it.code !in 0..31 && it.code != 0x7F }

        // 2. Remove qualquer coisa entre `<` e `>` (tags HTML/script)
        val noTags = TAG_REGEX.replace(noControl, "")

        // 3. Remove sequências de template / placeholders comuns em prompt injection
        //    (ex: ${IGNORE_PREVIOUS}, {{system_prompt}}, [INST]...)
        val noTemplates = noTags
            .replace(DOLLAR_BRACE_REGEX, "")
            .replace(DOUBLE_BRACE_REGEX, "")
            .replace(BRACKET_INST_REGEX, "")

        // 4. Colapsa espaços
        val collapsed = noTemplates.replace(MULTIWS_REGEX, " ").trim()

        return collapsed
    }

    private val TAG_REGEX = Regex("<[^>]*>")
    private val DOLLAR_BRACE_REGEX = Regex("\\$\\{[^}]*}")
    private val DOUBLE_BRACE_REGEX = Regex("\\{\\{[^}]*}}")
    private val BRACKET_INST_REGEX = Regex("\\[/?(?:INST|SYS|SYSTEM)[^]]*]", RegexOption.IGNORE_CASE)
    private val MULTIWS_REGEX = Regex("\\s+")
}
