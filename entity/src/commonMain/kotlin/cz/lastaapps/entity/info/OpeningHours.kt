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

package cz.lastaapps.entity.info

import cz.lastaapps.entity.compareInWeek
import cz.lastaapps.entity.menza.MenzaId
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime

/**
 * Represents the open and close time for a day of week
 * and for a specific menza section/location (Cafeteria/Restaurant)
 * https://agata.suz.cvut.cz/jidelnicky/oteviraci-doby.php
 */
data class OpeningHours(
    val menzaId: MenzaId,
    val locationName: String,
    val dayOfWeek: DayOfWeek,
    val open: LocalTime,
    val close: LocalTime,
    val comment: String?,
) : Comparable<OpeningHours> {
    init {
        require(locationName.isNotBlank()) { "Location name is blank" }
        require(open <= close) { "Close time is greater than the open one; o: $open, c: $close" }
    }

    override fun compareTo(other: OpeningHours): Int {
        return dayOfWeek.compareInWeek(other.dayOfWeek)
    }
}