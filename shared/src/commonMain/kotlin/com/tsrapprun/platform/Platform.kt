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
