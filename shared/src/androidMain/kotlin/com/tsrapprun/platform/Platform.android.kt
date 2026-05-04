package com.tsrapprun.platform

import java.util.Calendar
import java.util.UUID

actual fun newUuid(): String = UUID.randomUUID().toString()

actual fun nowMillis(): Long = System.currentTimeMillis()

actual fun dayBoundsMillis(): Pair<Long, Long> {
    val cal = Calendar.getInstance()
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    val start = cal.timeInMillis
    cal.add(Calendar.DAY_OF_YEAR, 1)
    return start to cal.timeInMillis
}

actual fun weekBoundsMillis(): Pair<Long, Long> {
    val cal = Calendar.getInstance()
    cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    val start = cal.timeInMillis
    cal.add(Calendar.WEEK_OF_YEAR, 1)
    return start to cal.timeInMillis
}

actual fun epochMillisFromComponents(
    year: Int,
    monthIndex: Int,
    day: Int,
    hour: Int,
    minute: Int
): Long {
    val cal = Calendar.getInstance().apply {
        clear()
        set(Calendar.YEAR, year)
        set(Calendar.MONTH, monthIndex)
        set(Calendar.DAY_OF_MONTH, day)
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
    }
    return cal.timeInMillis
}

actual fun dateComponentsOf(epochMillis: Long): DateTimeComponents {
    val cal = Calendar.getInstance().apply { timeInMillis = epochMillis }
    return DateTimeComponents(
        year = cal.get(Calendar.YEAR),
        monthIndex = cal.get(Calendar.MONTH),
        day = cal.get(Calendar.DAY_OF_MONTH),
        hour = cal.get(Calendar.HOUR_OF_DAY),
        minute = cal.get(Calendar.MINUTE)
    )
}
