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

import org.junit.Test
import java.time.LocalDate
import java.time.Month
import java.time.format.DateTimeFormatter

class WeekNumberKtTest {

    @Test
    fun toMonday() {
        val monday = LocalDate.of(2021, Month.SEPTEMBER, 20)
        val days = List(7) { i -> monday.plusDays(i.toLong()) }

        println("Monday is ${monday.format(DateTimeFormatter.ISO_DATE)}")
        days.map { it.toMonday() }.forEach {
            println("Checking ${it.format(DateTimeFormatter.ISO_DATE)}")
            assert(it == monday)
        }
    }
}