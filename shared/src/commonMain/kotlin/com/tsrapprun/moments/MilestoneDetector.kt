/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  MilestoneDetector.kt                                        ║
 * ║                                                              ║
 * ║  Heurística simples pra detectar se um registro é um         ║
 * ║  "marco" baseado em palavras-chave de primeira-vez.          ║
 * ║                                                              ║
 * ║  Aplicado quando o usuário esquece de marcar manualmente.    ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.moments

private val milestoneKeywords = listOf(
    "primeira", "primeiro",
    "começou a", "começou ", "comecou a", "comecou ",
    "aprendeu", "aprendendo a",
    "pela primeira vez",
    "festa", "aniversário", "aniversario", "mesversário", "mesversario",
    "primeiro dia",
    "já consegue", "ja consegue", "agora consegue",
    "ensinou", "ensinando"
)

/**
 * Retorna true se o título ou nota contém indícios de ser um marco
 * (primeira-vez, aniversário, etc.)
 */
fun detectMilestone(title: String, note: String): Boolean {
    val text = (title + " " + note).lowercase()
    return milestoneKeywords.any { kw -> text.contains(kw) }
}

/**
 * MomentEntry de tipos auto (mesversário, semana de gravidez, dia da vida)
 * são marcos por natureza, mesmo sem flag explícita.
 */
fun MomentEntry.isMarco(): Boolean = isMilestone || when (type) {
    MomentType.MESVERSARIO,
    MomentType.PREGNANCY_WEEK,
    MomentType.DAY_OF_LIFE -> true
    else -> false
}
