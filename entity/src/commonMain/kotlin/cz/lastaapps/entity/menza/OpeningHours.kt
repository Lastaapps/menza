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

package cz.lastaapps.entity.menza

import cz.lastaapps.entity.compareInWeek
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.comparables.shouldNotBeGreaterThanOrEqualTo
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDateTime

data class OpeningHours(
    val menzaId: MenzaId,
    val name: String,
    val dayOfWeek: DayOfWeek,
    val open: LocalDateTime,
    val close: LocalDateTime,
    val mealType: String?,
) : Comparable<OpeningHours> {
    init {
        name.isNotBlank().shouldBeTrue()
        open shouldNotBeGreaterThanOrEqualTo close
    }

    override fun compareTo(other: OpeningHours): Int {
        return dayOfWeek.compareInWeek(other.dayOfWeek)
    }
}