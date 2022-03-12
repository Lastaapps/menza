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

import io.kotest.matchers.shouldBe
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.plus
import kotlin.test.Test

class WeekNumberKtTest {

    @Test
    fun weekNumberOf() {
        val monday = LocalDate(2021, Month.SEPTEMBER, 20)
        val days = List(7) { i -> monday.plus(i.toLong(), DateTimeUnit.DAY) }

        println("Monday in $monday")

        days.forEach {
            val weekNumber = WeekNumber.of(it)
            println("Checking $it, weekNumber is ${weekNumber.week}")
            weekNumber.week shouldBe 2723
        }
    }
}