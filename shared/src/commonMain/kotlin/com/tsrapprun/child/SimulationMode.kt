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
     * Quando true (modo teste):
     *  - 1 hora real ≈ 7 horas baby (fator 7x)
     *  - 1 dia real  = 1 semana baby (24h real → 7d baby)
     *  - 4 dias real = 1 mês baby (~28d baby)
     *  - 48 dias real ≈ 1 ano baby
     *
     * Janela mais natural pra notar mesversários ao longo de uma semana
     * de testes em vez de horas. **Trocar para false antes de release.**
     */
    const val FAST_FORWARD = true

    /** Duração de "uma hora" em ms (~8.57min no fast / 1h real). */
    val hourMillis: Long
        get() = if (FAST_FORWARD) (60L * 60L * 1000L) / 7 else 60L * 60L * 1000L

    /** Duração de "um dia" em ms (~3.43h no fast / 24h real). */
    val dayMillis: Long
        get() = if (FAST_FORWARD) (24L * 60L * 60L * 1000L) / 7 else 24L * 60 * 60 * 1000

    /** Duração de "uma semana" em ms (1d no fast / 7d real). */
    val weekMillis: Long
        get() = if (FAST_FORWARD) 24L * 60L * 60L * 1000L else 7L * 24 * 60 * 60 * 1000

    /** Duração de "um mês" em ms (4d no fast / ~30.44d real). */
    val monthMillis: Long
        get() = if (FAST_FORWARD) 4L * 24L * 60L * 60L * 1000L else (30.44 * 24 * 60 * 60 * 1000).toLong()

    /** Duração de "um ano" em ms (48d no fast / 365d real). */
    val yearMillis: Long
        get() = if (FAST_FORWARD) 48L * 24L * 60L * 60L * 1000L else 365L * 24 * 60 * 60 * 1000
}
