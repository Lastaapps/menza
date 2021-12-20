/*
 *    Copyright 2021, Petr Laštovička as Lasta apps, All rights reserved
 *
 *     This file is part of Menza.
 *
 *     Menza is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Menza is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Menza.  If not, see <https://www.gnu.org/licenses/>.
 */

package cz.lastaapps.entity

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.TimeZone

object TimeUtils {
    fun getDaysOfWeek() = daysOfWeekSorted
}

internal val CET get() = TimeZone.of("Europe/Prague")

internal val daysOfWeekSorted = listOf(
    DayOfWeek.MONDAY,
    DayOfWeek.TUESDAY,
    DayOfWeek.WEDNESDAY,
    DayOfWeek.THURSDAY,
    DayOfWeek.FRIDAY,
    DayOfWeek.SATURDAY,
    DayOfWeek.SUNDAY
)

fun DayOfWeek.compareInWeek(other: DayOfWeek): Int {
    return daysOfWeekSorted.indexOf(this).compareTo(daysOfWeekSorted.indexOf(other))
}

fun String.toCzechDayShortcutToDayOfWeek(): DayOfWeek? {
    return when (this.lowercase()) {
        "po" -> DayOfWeek.MONDAY
        "út" -> DayOfWeek.TUESDAY
        "st" -> DayOfWeek.WEDNESDAY
        "čt" -> DayOfWeek.THURSDAY
        "pá" -> DayOfWeek.FRIDAY
        "so" -> DayOfWeek.SATURDAY
        "ne" -> DayOfWeek.SUNDAY
        else -> null
    }
}
