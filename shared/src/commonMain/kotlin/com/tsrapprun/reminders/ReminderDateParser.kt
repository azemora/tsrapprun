/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  ReminderDateParser.kt — extrai data de transcrição PT-BR    ║
 * ║                                                              ║
 * ║  Heurística simples — não substitui NLP de verdade, mas      ║
 * ║  cobre os casos comuns:                                      ║
 * ║   • hoje / amanhã / depois de amanhã                         ║
 * ║   • próxima/proxima [seg|ter|qua|qui|sex|sab|dom]            ║
 * ║   • essa/esta [weekday]                                      ║
 * ║   • semana que vem / próxima semana                          ║
 * ║   • daqui a/em N dias / N semanas                            ║
 * ║                                                              ║
 * ║  Retorna texto sem o trecho de data + millis alvo.           ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.reminders

import com.tsrapprun.platform.dateComponentsOf

data class ParsedReminder(
    val cleanedText: String,
    val dueAt: Long?
)

/** Mapeia nome do dia da semana → índice (0 = dom, 1 = seg, ..., 6 = sáb). */
private val weekdayMap = mapOf(
    "domingo" to 0, "dom" to 0,
    "segunda" to 1, "segunda-feira" to 1, "seg" to 1,
    "terça" to 2, "terca" to 2, "terça-feira" to 2, "terca-feira" to 2, "ter" to 2,
    "quarta" to 3, "quarta-feira" to 3, "qua" to 3,
    "quinta" to 4, "quinta-feira" to 4, "qui" to 4,
    "sexta" to 5, "sexta-feira" to 5, "sex" to 5,
    "sábado" to 6, "sabado" to 6, "sab" to 6
)

private const val DAY_MS = 24L * 60 * 60 * 1000

/** Calcula dia da semana via Zeller (0 = dom). */
private fun weekdayOf(year: Int, month1to12: Int, day: Int): Int {
    var y = year
    var m = month1to12
    if (m < 3) { m += 12; y -= 1 }
    val k = y % 100
    val j = y / 100
    val h = (day + (13 * (m + 1)) / 5 + k + k / 4 + j / 4 + 5 * j) % 7
    // Zeller: 0 = sábado. Convert para 0 = domingo.
    return ((h + 6) % 7)
}

/** Adiciona dias mantendo a hora atual (08:00 default pra manhã). */
private fun startOfDayPlusOffset(nowMs: Long, offsetDays: Int, hour: Int = 9): Long {
    return nowMs + offsetDays * DAY_MS - (nowMs % DAY_MS) + hour * 60L * 60 * 1000
    // Aproximação — desconsidera timezone offset preciso. Funciona pra UX local.
}

fun parseReminderText(text: String, nowMs: Long): ParsedReminder {
    val lower = text.lowercase()
    val today = dateComponentsOf(nowMs)
    val todayWeekday = weekdayOf(today.year, today.monthIndex + 1, today.day)

    // ── 1) "hoje" / "amanhã" / "depois de amanhã" ──
    val patterns = listOf(
        Triple(Regex("\\bdepois de amanhã\\b|\\bdepois de amanha\\b"), 2, "depois de amanhã"),
        Triple(Regex("\\bamanhã\\b|\\bamanha\\b"), 1, "amanhã"),
        Triple(Regex("\\bhoje\\b"), 0, "hoje")
    )
    for ((regex, days, _) in patterns) {
        if (regex.containsMatchIn(lower)) {
            val cleaned = regex.replace(lower, " ").cleanWhitespace()
            return ParsedReminder(
                cleanedText = cleaned.capitalizeFirst(),
                dueAt = startOfDayPlusOffset(nowMs, days)
            )
        }
    }

    // ── 2) "daqui a N dias/semanas" ou "em N dias/semanas" ──
    val daquiRegex = Regex("\\b(?:daqui a|em)\\s+(\\d+)\\s+(dia|dias|semana|semanas)\\b")
    daquiRegex.find(lower)?.let { match ->
        val n = match.groupValues[1].toIntOrNull() ?: return@let
        val unit = match.groupValues[2]
        val days = if (unit.startsWith("semana")) n * 7 else n
        val cleaned = lower.replaceRange(match.range, " ").cleanWhitespace()
        return ParsedReminder(
            cleanedText = cleaned.capitalizeFirst(),
            dueAt = startOfDayPlusOffset(nowMs, days)
        )
    }

    // ── 3) "próxima/essa [weekday]" ──
    val weekdayRegex = Regex("\\b(próxima|proxima|próximo|proximo|essa|esta|na próxima|na proxima)\\s+(domingo|dom|segunda(-feira)?|seg|terça(-feira)?|terca(-feira)?|ter|quarta(-feira)?|qua|quinta(-feira)?|qui|sexta(-feira)?|sex|sábado|sabado|sab)\\b")
    weekdayRegex.find(lower)?.let { match ->
        val raw = match.groupValues[2]
        val target = weekdayMap[raw] ?: return@let
        var diff = (target - todayWeekday + 7) % 7
        // "próxima" significa próxima ocorrência ≥ hoje; se for hoje, joga pra próxima semana
        val keyword = match.groupValues[1]
        if (diff == 0 && (keyword.startsWith("próx") || keyword.startsWith("prox"))) diff = 7
        if (diff == 0) diff = 7  // "essa segunda" caso seja hoje, vai pra próxima
        val cleaned = lower.replaceRange(match.range, " ").cleanWhitespace()
        return ParsedReminder(
            cleanedText = cleaned.capitalizeFirst(),
            dueAt = startOfDayPlusOffset(nowMs, diff)
        )
    }

    // ── 4) só o nome do dia "na quarta", "quarta-feira" ──
    val standaloneWeekday = Regex("\\b(?:na |no )?(domingo|segunda(-feira)?|terça(-feira)?|terca(-feira)?|quarta(-feira)?|quinta(-feira)?|sexta(-feira)?|sábado|sabado)\\b")
    standaloneWeekday.find(lower)?.let { match ->
        val raw = match.groupValues[1]
        val target = weekdayMap[raw] ?: return@let
        var diff = (target - todayWeekday + 7) % 7
        if (diff == 0) diff = 7
        val cleaned = lower.replaceRange(match.range, " ").cleanWhitespace()
        return ParsedReminder(
            cleanedText = cleaned.capitalizeFirst(),
            dueAt = startOfDayPlusOffset(nowMs, diff)
        )
    }

    // ── 5) "semana que vem" / "próxima semana" ──
    val nextWeek = Regex("\\b(semana que vem|próxima semana|proxima semana)\\b")
    nextWeek.find(lower)?.let { match ->
        val cleaned = lower.replaceRange(match.range, " ").cleanWhitespace()
        return ParsedReminder(
            cleanedText = cleaned.capitalizeFirst(),
            dueAt = startOfDayPlusOffset(nowMs, 7)
        )
    }

    // Sem data detectada
    return ParsedReminder(text.trim().capitalizeFirst(), null)
}

private fun String.cleanWhitespace(): String =
    this.replace(Regex("\\s+"), " ")
        .trim()
        .removePrefix(",").removePrefix(".").trim()
        .replace(Regex("\\s+([.!?,])"), "$1")

private fun String.capitalizeFirst(): String =
    if (isBlank()) this else this[0].uppercaseChar() + substring(1)
