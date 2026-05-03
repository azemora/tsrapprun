package com.tsrapprun.platform

import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarUnitDay
import platform.Foundation.NSCalendarUnitHour
import platform.Foundation.NSCalendarUnitMinute
import platform.Foundation.NSCalendarUnitMonth
import platform.Foundation.NSCalendarUnitWeekOfYear
import platform.Foundation.NSCalendarUnitWeekday
import platform.Foundation.NSCalendarUnitYear
import platform.Foundation.NSDate
import platform.Foundation.NSUUID
import platform.Foundation.dateWithTimeIntervalSince1970
import platform.Foundation.timeIntervalSince1970

actual fun newUuid(): String = NSUUID().UUIDString.lowercase()

actual fun nowMillis(): Long = (NSDate().timeIntervalSince1970 * 1000.0).toLong()

actual fun dayBoundsMillis(): Pair<Long, Long> {
    val cal = NSCalendar.currentCalendar
    val startOfToday = cal.startOfDayForDate(NSDate())
    val startOfTomorrow = cal.dateByAddingUnit(
        unit = NSCalendarUnitDay,
        value = 1,
        toDate = startOfToday,
        options = 0u
    ) ?: startOfToday
    val startMs = (startOfToday.timeIntervalSince1970 * 1000.0).toLong()
    val endMs = (startOfTomorrow.timeIntervalSince1970 * 1000.0).toLong()
    return startMs to endMs
}

actual fun weekBoundsMillis(): Pair<Long, Long> {
    val cal = NSCalendar.currentCalendar
    val now = NSDate()
    val weekday = cal.component(NSCalendarUnitWeekday, fromDate = now).toInt()
    val firstWeekday = cal.firstWeekday.toInt()
    var daysToSubtract = weekday - firstWeekday
    if (daysToSubtract < 0) daysToSubtract += 7
    val startOfToday = cal.startOfDayForDate(now)
    val startOfWeek = cal.dateByAddingUnit(
        unit = NSCalendarUnitDay,
        value = (-daysToSubtract).toLong(),
        toDate = startOfToday,
        options = 0u
    ) ?: startOfToday
    val nextWeek = cal.dateByAddingUnit(
        unit = NSCalendarUnitWeekOfYear,
        value = 1,
        toDate = startOfWeek,
        options = 0u
    ) ?: startOfWeek
    val startMs = (startOfWeek.timeIntervalSince1970 * 1000.0).toLong()
    val endMs = (nextWeek.timeIntervalSince1970 * 1000.0).toLong()
    return startMs to endMs
}

actual fun dateComponentsOf(epochMillis: Long): DateTimeComponents {
    val cal = NSCalendar.currentCalendar
    val date = NSDate.dateWithTimeIntervalSince1970(epochMillis / 1000.0)
    val units = NSCalendarUnitYear or NSCalendarUnitMonth or NSCalendarUnitDay or
        NSCalendarUnitHour or NSCalendarUnitMinute
    val components = cal.components(units, fromDate = date)
    return DateTimeComponents(
        year = components.year.toInt(),
        monthIndex = components.month.toInt() - 1,
        day = components.day.toInt(),
        hour = components.hour.toInt(),
        minute = components.minute.toInt()
    )
}
