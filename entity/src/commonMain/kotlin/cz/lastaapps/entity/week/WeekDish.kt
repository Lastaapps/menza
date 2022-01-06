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

import cz.lastaapps.entity.common.Amount
import cz.lastaapps.entity.common.CourseType
import io.kotest.matchers.string.shouldNotBeBlank
import kotlinx.datetime.LocalDate

/**
 * One dish in week menu
 * https://agata.suz.cvut.cz/jidelnicky/indexTyden.php?clPodsystem=1
 */
data class WeekDish(
    val date: LocalDate,
    val courseType: CourseType,
    val amount: Amount?,
    val name: String,
) : Comparable<WeekDish> {

    init {
        name.shouldNotBeBlank()
    }

    override fun compareTo(other: WeekDish): Int {
        return date.compareTo(other.date)
    }
}