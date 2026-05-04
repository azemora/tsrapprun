/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  AgeCalculator.kt — Cálculo de idade em fases                ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.child

enum class LifePhase {
    /** DPP no futuro. */
    PREGNANCY,
    /** Já nasceu mas tem menos de 1 mês — conta em dias. */
    NEWBORN,
    /** Entre 1 e 12 meses — conta em meses. */
    BABY,
    /** Mais de 1 ano — conta em anos. */
    TODDLER
}

data class Age(
    val phase: LifePhase,
    /** Em PREGNANCY: semanas até nascer. Senão: 0. */
    val pregnancyWeeksRemaining: Int = 0,
    /** Em NEWBORN: dias de vida. Senão: 0. */
    val daysOfLife: Int = 0,
    /** Em BABY: meses de vida (1..12). Senão: 0. */
    val months: Int = 0,
    /** Em TODDLER: anos. Senão: 0. */
    val years: Int = 0
)

/** Próximo marco a celebrar. */
data class NextMilestone(
    val label: String,         // "mesversário", "aniversário", "nascimento"
    val daysUntil: Int,         // dias (em simulação ou real)
    val targetMillis: Long      // epoch ms do alvo
)

object AgeCalculator {
    fun calculateAge(profile: ChildProfile, nowMillis: Long): Age {
        val isStillPregnant = profile.isPregnancy && nowMillis < profile.birthdateMillis
        if (isStillPregnant) {
            val ms = profile.birthdateMillis - nowMillis
            val weeks = (ms / SimulationMode.weekMillis).toInt().coerceAtLeast(0)
            return Age(phase = LifePhase.PREGNANCY, pregnancyWeeksRemaining = weeks)
        }
        val elapsed = (nowMillis - profile.birthdateMillis).coerceAtLeast(0)
        val months = (elapsed / SimulationMode.monthMillis).toInt()
        val years = (elapsed / SimulationMode.yearMillis).toInt()
        return when {
            months < 1 -> {
                val days = (elapsed / SimulationMode.dayMillis).toInt()
                Age(phase = LifePhase.NEWBORN, daysOfLife = days)
            }
            months < 12 -> Age(phase = LifePhase.BABY, months = months)
            else -> Age(phase = LifePhase.TODDLER, years = years, months = months)
        }
    }

    /**
     * Próximo marco:
     *  - Gestação → nascimento
     *  - < 1 ano → próximo mesversário
     *  - >= 1 ano → próximo aniversário
     */
    fun nextMilestone(profile: ChildProfile, nowMillis: Long): NextMilestone {
        val isStillPregnant = profile.isPregnancy && nowMillis < profile.birthdateMillis
        if (isStillPregnant) {
            val msUntil = profile.birthdateMillis - nowMillis
            return NextMilestone(
                label = "nascimento",
                daysUntil = (msUntil / SimulationMode.dayMillis).toInt().coerceAtLeast(0),
                targetMillis = profile.birthdateMillis
            )
        }
        val elapsed = (nowMillis - profile.birthdateMillis).coerceAtLeast(0)
        val months = (elapsed / SimulationMode.monthMillis).toInt()
        return if (months < 12) {
            val target = profile.birthdateMillis + (months + 1) * SimulationMode.monthMillis
            NextMilestone(
                label = "mesversário",
                daysUntil = ((target - nowMillis) / SimulationMode.dayMillis).toInt().coerceAtLeast(0),
                targetMillis = target
            )
        } else {
            val years = (elapsed / SimulationMode.yearMillis).toInt()
            val target = profile.birthdateMillis + (years + 1) * SimulationMode.yearMillis
            NextMilestone(
                label = "aniversário",
                daysUntil = ((target - nowMillis) / SimulationMode.dayMillis).toInt().coerceAtLeast(0),
                targetMillis = target
            )
        }
    }
}
