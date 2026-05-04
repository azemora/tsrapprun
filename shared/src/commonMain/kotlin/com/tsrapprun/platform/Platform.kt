package com.tsrapprun.platform

expect fun newUuid(): String

expect fun nowMillis(): Long

expect fun dayBoundsMillis(): Pair<Long, Long>

expect fun weekBoundsMillis(): Pair<Long, Long>

data class DateTimeComponents(
    val year: Int,
    val monthIndex: Int,
    val day: Int,
    val hour: Int,
    val minute: Int
)

expect fun dateComponentsOf(epochMillis: Long): DateTimeComponents

/**
 * Constrói epoch millis a partir de componentes locais (calendário do
 * dispositivo). Usado para data de nascimento — recebida do usuário
 * como dia/mês/ano.
 *
 * @param monthIndex 0..11 (janeiro = 0, dezembro = 11).
 */
expect fun epochMillisFromComponents(
    year: Int,
    monthIndex: Int,
    day: Int,
    hour: Int = 0,
    minute: Int = 0
): Long
