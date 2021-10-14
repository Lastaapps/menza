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

package cz.lastaapps.entity.week

import io.kotest.matchers.ints.shouldBeGreaterThan
import kotlinx.datetime.*
import kotlin.jvm.JvmInline

@JvmInline
value class WeekNumber private constructor(val week: Int) {
    init {
        week.shouldBeGreaterThan(0)
    }

    companion object {
        fun of(date: LocalDate): WeekNumber {
            val epochStart = Instant.fromEpochMilliseconds(0L)
            val days = epochStart.toLocalDateTime(TimeZone.UTC).date.daysUntil(date) - 4

            return WeekNumber(days / 7 + 25)
        }

        /**
         * used to restore data from database
         * */
        fun restore(week: Int): WeekNumber = WeekNumber(week)
    }
}

/**
 * @return the first monday before the date given, for mondays it returns the same date
 * */
internal fun LocalDate.toMonday(): LocalDate {
    var tempDate = this
    while (tempDate.dayOfWeek != DayOfWeek.MONDAY) {
        tempDate = tempDate.minus(1, DateTimeUnit.DAY)
    }
    return tempDate
}
