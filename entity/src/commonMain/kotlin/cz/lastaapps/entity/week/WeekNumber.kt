/*
 *    Copyright 2022, Petr Laštovička as Lasta apps, All rights reserved
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

import kotlinx.datetime.*

/**
 * Represents week number used in fetching a week menu
 * Id's aren't stable on the server side, so Week number is disabled fo now
 */
@JvmInline
value class WeekNumber private constructor(val week: Int) {
    init {
        require(week >= 0) { "Week number is negative" }
    }

    companion object {
        //TODO week number not working
        fun of(date: LocalDate): WeekNumber {
            val epochStart = Instant.fromEpochMilliseconds(0L)
            val days = epochStart.toLocalDateTime(TimeZone.UTC).date.daysUntil(date) - 4

            return WeekNumber(days / 7 + 25)
        }

        val tempWeekNumber = WeekNumber(0)

        /**
         * used to restore data from database
         * */
        fun restore(week: Int): WeekNumber = WeekNumber(week)
    }
}
