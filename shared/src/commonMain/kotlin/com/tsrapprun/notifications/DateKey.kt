/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  DateKey.kt - Chave de Data "YYYY-MM-DD"                     ║
 * ║                                                             ║
 * ║  Helper minimalista para gerar a chave primária              ║
 * ║  das entradas do diário a partir de epoch millis.           ║
 * ║                                                             ║
 * ║  Usa cálculo aritmético próprio (algoritmo de Howard Hinnant)║
 * ║  — evita adicionar kotlinx-datetime só por um formato.      ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.notifications

/**
 * Converte epoch millis (UTC) em "YYYY-MM-DD" (UTC).
 *
 * NOTA: A data é calculada em UTC para consistência entre plataformas.
 * Para um app pessoal de diário, essa simplificação é aceitável — o
 * timezone do usuário pode mudar (viagens) e usar UTC como chave evita
 * entradas duplicadas/sumidas ao cruzar fusos.
 */
fun epochMillisToDateKey(epochMillis: Long): String {
    val daysSinceEpoch = epochMillis / 86_400_000L
    // Algoritmo de Howard Hinnant (civil_from_days) — domínio público.
    val z = daysSinceEpoch + 719_468L
    val era = (if (z >= 0) z else z - 146_096L) / 146_097L
    val doe = (z - era * 146_097L).toInt() // [0, 146096]
    val yoe = (doe - doe / 1460 + doe / 36_524 - doe / 146_096) / 365 // [0, 399]
    val y = yoe + era * 400L
    val doy = doe - (365 * yoe + yoe / 4 - yoe / 100) // [0, 365]
    val mp = (5 * doy + 2) / 153                       // [0, 11]
    val d = doy - (153 * mp + 2) / 5 + 1               // [1, 31]
    val m = if (mp < 10) mp + 3 else mp - 9            // [1, 12]
    val year = if (m <= 2) y + 1 else y
    return buildString {
        append(year)
        append('-')
        if (m < 10) append('0')
        append(m)
        append('-')
        if (d < 10) append('0')
        append(d)
    }
}

/** Atalho: chave de data do momento atual (UTC). */
fun todayDateKey(nowMillis: Long): String = epochMillisToDateKey(nowMillis)
