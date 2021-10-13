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

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@JvmInline
value class WeekNumber private constructor(val week: Int) {
    init {
        assert(week > 0)
    }

    companion object {
        fun of(date: LocalDate): WeekNumber {
            return WeekNumber(
                ChronoUnit.WEEKS.between(
                    LocalDate.ofEpochDay(0),
                    date.toMonday()
                ).toInt() + 25 //fix, don't know where is the error
            )
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
    var diff = DayOfWeek.MONDAY.value - this.dayOfWeek.value
    if (diff == 0)
        return this
    while (diff > 0)
        diff -= 7

    return this.plusDays(diff.toLong())
}
