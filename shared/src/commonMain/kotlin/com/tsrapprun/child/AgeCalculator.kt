/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  AgeCalculator.kt — Cálculo de idade com aceleração de tempo ║
 * ║                                                              ║
 * ║  Honra [SimulationMode.FAST_FORWARD]: em modo turbo,         ║
 * ║  1min = 1 semana, 4min = 1 mês, 48min = 1 ano.               ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.child

data class Age(
    /** Tempo decorrido desde o nascimento, em ms. */
    val totalMillisAlive: Long,
    /** Semanas completas (0..). */
    val weeks: Int,
    /** Meses completos (0..). */
    val months: Int,
    /** Anos completos (0..). */
    val years: Int,
) {
    val isUnderOneYear: Boolean get() = months < 12
}

object AgeCalculator {
    fun calculateAge(birthdateMillis: Long, nowMillis: Long): Age {
        val elapsed = (nowMillis - birthdateMillis).coerceAtLeast(0)
        val weeks = (elapsed / SimulationMode.weekMillis).toInt()
        val months = (elapsed / SimulationMode.monthMillis).toInt()
        val years = (elapsed / SimulationMode.yearMillis).toInt()
        return Age(elapsed, weeks, months, years)
    }
}
