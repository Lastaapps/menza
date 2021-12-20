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

import io.kotest.matchers.ints.shouldBeInRange

data class LocalTime(val hours: Int, val minutes: Int, val seconds: Int) {

    init {
        hours shouldBeInRange 0..23
        minutes shouldBeInRange 0..59
        seconds shouldBeInRange 0..59
    }

    fun toSeconds(): Int {
        return hours * 3600 + minutes * 60 + seconds
    }

    companion object {

        fun fromSeconds(total: Int): LocalTime {
            var mTotal = total
            val hours: Int = mTotal / 3600
            mTotal -= hours * 3600
            val minutes: Int = mTotal / 60
            mTotal -= minutes * 60
            val seconds: Int = mTotal

            return LocalTime(hours, minutes, seconds)
        }
    }
}