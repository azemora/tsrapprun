/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  MilestoneSynthesizer.kt — Sintetiza marcos automaticamente  ║
 * ║                                                              ║
 * ║  Em vez de depender de cron/background work para criar       ║
 * ║  entradas de marcos (mesversário e semanas), as geramos      ║
 * ║  on-demand quando o app abre. Idempotente.                   ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.child

import com.tsrapprun.moments.MomentEntry
import com.tsrapprun.moments.MomentType
import com.tsrapprun.platform.newUuid
import com.tsrapprun.platform.nowMillis
import com.tsrapprun.storage.LocalPhotoStorage

object MilestoneSynthesizer {

    /** Sanity cap para evitar loop em caso de timestamp absurdo. */
    private const val MAX_WEEKS_HARD_CAP = 60
    /** Marcos semanais só até 1 mês de vida (depois conta em meses). */
    private const val MAX_WEEKS_BEFORE_MONTH = 4
    /** Mesversários só até completar 1 ano. */
    private const val MAX_MONTHS = 12

    /**
     * Cria entries para todas as semanas/mesversários que já passaram desde
     * o nascimento e que ainda não têm entry registrada.
     *
     * Retorna o número total de entries criadas (útil pra detectar
     * "tem mesversário novo, mostrar tela de anúncio").
     */
    suspend fun synthesizeFor(
        profile: ChildProfile,
        storage: LocalPhotoStorage
    ): SynthesisResult {
        val age = AgeCalculator.calculateAge(profile.birthdateMillis, nowMillis())
        val existing = storage.listMoments()
        val existingWeeks = existing
            .filter { it.type == MomentType.WEEK_OF_LIFE }
            .map { it.milestoneNumber }
            .toSet()
        val existingMonths = existing
            .filter { it.type == MomentType.MESVERSARIO }
            .map { it.milestoneNumber }
            .toSet()

        var newWeekEntries = 0
        var newMonthEntries = 0
        var latestNewMonth = 0

        // Semanas — só até a criança completar 1 mês.
        // Após 1 mesversário, marcos passam a ser mensais.
        val firstMonthEndMs = profile.birthdateMillis + SimulationMode.monthMillis
        val weeksCap = minOf(age.weeks, MAX_WEEKS_HARD_CAP, MAX_WEEKS_BEFORE_MONTH)
        for (week in 1..weeksCap) {
            if (week in existingWeeks) continue
            val periodStart = profile.birthdateMillis + (week - 1) * SimulationMode.weekMillis
            val periodEnd = profile.birthdateMillis + week * SimulationMode.weekMillis
            // Defesa adicional: se a semana terminar depois de 1 mês, para.
            if (periodEnd > firstMonthEndMs) break
            storage.saveMoment(
                MomentEntry(
                    id = newUuid(),
                    text = "Semana $week de ${profile.firstName}",
                    type = MomentType.WEEK_OF_LIFE,
                    createdAt = nowMillis(),
                    periodStart = periodStart,
                    periodEnd = periodEnd,
                    milestoneNumber = week
                )
            )
            newWeekEntries++
        }

        // Mesversários (até 12 meses)
        val monthsCap = minOf(age.months, MAX_MONTHS)
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

        return SynthesisResult(
            newWeekEntries = newWeekEntries,
            newMonthEntries = newMonthEntries,
            latestNewMonth = latestNewMonth,
            currentAge = age
        )
    }

    data class SynthesisResult(
        val newWeekEntries: Int,
        val newMonthEntries: Int,
        /** 0 se nenhum mesversário novo. */
        val latestNewMonth: Int,
        val currentAge: Age
    ) {
        val hasNewMesversario: Boolean get() = latestNewMonth > 0
    }
}
