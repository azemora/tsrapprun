/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  MilestoneSynthesizer.kt — 3 fases de marcos                 ║
 * ║                                                              ║
 * ║   1. Gestação (DPP no futuro)                                ║
 * ║      → countdown semanal: "faltam N semanas pra nascer"      ║
 * ║   2. Recém-nascido (0 a 1 mês)                               ║
 * ║      → contagem diária: "dia N de vida"                      ║
 * ║   3. Bebê (1 a 12 meses)                                     ║
 * ║      → mesversário mensal                                    ║
 * ║                                                              ║
 * ║  Idempotente: usa entries existentes pra decidir o que falta.║
 * ║  Capped contra loops (40 semanas, 30 dias, 12 meses).        ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.child

import com.tsrapprun.moments.MomentEntry
import com.tsrapprun.moments.MomentType
import com.tsrapprun.platform.newUuid
import com.tsrapprun.platform.nowMillis
import com.tsrapprun.storage.LocalPhotoStorage

object MilestoneSynthesizer {

    /** Limite máximo de semanas de gestação rastreadas (humano: ~40). */
    private const val MAX_PREGNANCY_WEEKS = 40
    /** Dias rastreados após nascimento (até primeiro mesversário). */
    private const val MAX_DAYS = 30
    /** Mesversários até 1 ano. */
    private const val MAX_MONTHS = 12

    suspend fun synthesizeFor(
        profile: ChildProfile,
        storage: LocalPhotoStorage
    ): SynthesisResult {
        val now = nowMillis()
        val existing = storage.listMoments()
        val existingPregnancyWeeks = existing
            .filter { it.type == MomentType.PREGNANCY_WEEK }
            .map { it.milestoneNumber }
            .toSet()
        val existingDays = existing
            .filter { it.type == MomentType.DAY_OF_LIFE }
            .map { it.milestoneNumber }
            .toSet()
        val existingMonths = existing
            .filter { it.type == MomentType.MESVERSARIO }
            .map { it.milestoneNumber }
            .toSet()

        var newPregnancyEntries = 0
        var newDayEntries = 0
        var newMonthEntries = 0
        var latestNewMonth = 0

        val isStillPregnant = profile.isPregnancy && now < profile.birthdateMillis

        // ── FASE 1: gestação ──
        if (isStillPregnant) {
            val msUntilBirth = profile.birthdateMillis - now
            val currentWeeksRemaining = (msUntilBirth / SimulationMode.weekMillis).toInt()
            // Semana inicial registrada (no momento do cadastro)
            val initialMs = profile.birthdateMillis - profile.createdAtMillis
            val initialWeeksRemaining =
                (initialMs / SimulationMode.weekMillis).toInt().coerceAtMost(MAX_PREGNANCY_WEEKS)

            // Cria entries para cada valor de "semanas restantes" cruzado, do
            // initial (alto) até current (baixo).
            for (wr in initialWeeksRemaining downTo currentWeeksRemaining.coerceAtLeast(0)) {
                if (wr in existingPregnancyWeeks) continue
                if (wr > MAX_PREGNANCY_WEEKS) continue
                val text = pregnancyWeekText(profile.firstName, wr)
                storage.saveMoment(
                    MomentEntry(
                        id = newUuid(),
                        text = text,
                        type = MomentType.PREGNANCY_WEEK,
                        createdAt = nowMillis(),
                        periodStart = profile.birthdateMillis - (wr + 1) * SimulationMode.weekMillis,
                        periodEnd = profile.birthdateMillis - wr * SimulationMode.weekMillis,
                        milestoneNumber = wr
                    )
                )
                newPregnancyEntries++
            }
        }

        // ── FASE 2 e 3: pós-nascimento ──
        if (now >= profile.birthdateMillis) {
            val msSinceBirth = now - profile.birthdateMillis

            // Dias de vida (cap 30 e antes do primeiro mesversário)
            val daysCap = minOf((msSinceBirth / SimulationMode.dayMillis).toInt(), MAX_DAYS)
            val firstMonthEnd = profile.birthdateMillis + SimulationMode.monthMillis
            for (day in 1..daysCap) {
                if (day in existingDays) continue
                val periodStart = profile.birthdateMillis + (day - 1) * SimulationMode.dayMillis
                val periodEnd = profile.birthdateMillis + day * SimulationMode.dayMillis
                if (periodEnd > firstMonthEnd) break
                storage.saveMoment(
                    MomentEntry(
                        id = newUuid(),
                        text = "${profile.firstName} — dia $day de vida 🌱",
                        type = MomentType.DAY_OF_LIFE,
                        createdAt = nowMillis(),
                        periodStart = periodStart,
                        periodEnd = periodEnd,
                        milestoneNumber = day
                    )
                )
                newDayEntries++
            }

            // Mesversários (cap 12)
            val monthsCap = minOf((msSinceBirth / SimulationMode.monthMillis).toInt(), MAX_MONTHS)
            for (month in 1..monthsCap) {
                if (month in existingMonths) continue
                val periodStart = profile.birthdateMillis + (month - 1) * SimulationMode.monthMillis
                val periodEnd = profile.birthdateMillis + month * SimulationMode.monthMillis
                storage.saveMoment(
                    MomentEntry(
                        id = newUuid(),
                        text = "${profile.firstName} fez $month ${if (month == 1) "mês" else "meses"}!",
                        type = MomentType.MESVERSARIO,
                        createdAt = nowMillis(),
                        periodStart = periodStart,
                        periodEnd = periodEnd,
                        milestoneNumber = month
                    )
                )
                newMonthEntries++
                if (month > latestNewMonth) latestNewMonth = month
            }
        }

        val age = AgeCalculator.calculateAge(profile, now)

        return SynthesisResult(
            newPregnancyEntries = newPregnancyEntries,
            newDayEntries = newDayEntries,
            newMonthEntries = newMonthEntries,
            latestNewMonth = latestNewMonth,
            currentAge = age
        )
    }

    private fun pregnancyWeekText(name: String, weeksRemaining: Int): String = when (weeksRemaining) {
        0 -> "$name pode chegar a qualquer momento! 🌟"
        1 -> "1 semana pra $name chegar 🤍"
        else -> "faltam $weeksRemaining semanas pro $name chegar 🌱"
    }

    data class SynthesisResult(
        val newPregnancyEntries: Int,
        val newDayEntries: Int,
        val newMonthEntries: Int,
        /** 0 se nenhum mesversário novo. */
        val latestNewMonth: Int,
        val currentAge: Age
    ) {
        val hasNewMesversario: Boolean get() = latestNewMonth > 0
    }
}
