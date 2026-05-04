/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  SimulationMode.kt — Aceleração temporal para teste          ║
 * ║                                                              ║
 * ║  Permite encurtar a janela de testes durante desenvolvimento.║
 * ║  Em produção, FAST_FORWARD = false (tempo real).             ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
package com.tsrapprun.child

object SimulationMode {
    /**
     * Quando true:
     *  - 1 hora    = 1 semana
     *  - 4 horas   = 1 mês
     *  - 48 horas (2 dias) = 1 ano
     *
     * Útil para validar mesversários e lembretes em janela curta.
     * **Trocar para false antes de release.**
     */
    const val FAST_FORWARD = true

    /** Duração de "uma semana" em ms (1h no fast / 7d real). */
    val weekMillis: Long
        get() = if (FAST_FORWARD) 60L * 60L * 1000L else 7L * 24 * 60 * 60 * 1000

    /** Duração de "um dia" em ms (~8.57min no fast / 24h real). */
    val dayMillis: Long
        get() = weekMillis / 7

    /** Duração de "um mês" em ms (4h no fast / ~30.44d real). */
    val monthMillis: Long
        get() = if (FAST_FORWARD) 4L * 60L * 60L * 1000L else (30.44 * 24 * 60 * 60 * 1000).toLong()

    /** Duração de "um ano" em ms (48h no fast / 365d real). */
    val yearMillis: Long
        get() = if (FAST_FORWARD) 48L * 60L * 60L * 1000L else 365L * 24 * 60 * 60 * 1000
}
