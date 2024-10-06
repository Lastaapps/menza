/*
 *    Copyright 2024, Petr Laštovička as Lasta apps, All rights reserved
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

package cz.lastaapps.core.util.extensions

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import java.time.DayOfWeek.MONDAY
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

val TimeZone.Companion.CET get() = TimeZone.of("Europe/Prague")
val LocalTime.Companion.MIDNIGHT get() = LocalTime.fromSecondOfDay(0)

fun LocalDate.atMidnight() = LocalDateTime(this, LocalTime.MIDNIGHT)

/**
 * Finds the first day with the type given before this date
 */
fun LocalDate.findDayOfWeek(dof: DayOfWeek) =
    (this.dayOfWeek.value - dof.value).let {
        when {
            it == 0 -> this
            it < 0 -> this.minus(it + 7, DateTimeUnit.DAY)
            else -> this.minus(it, DateTimeUnit.DAY)
        }
    }

@Suppress("NOTHING_TO_INLINE")
inline fun LocalDate.findMonday() = findDayOfWeek(MONDAY)

/**
 * Creates an inprecise ticker that sends now() every [duration]
 */
fun Clock.durationTicker(duration: Duration = 1.minutes) =
    flow {
        while (true) {
            emit(now())
            delay(duration)
        }
    }
